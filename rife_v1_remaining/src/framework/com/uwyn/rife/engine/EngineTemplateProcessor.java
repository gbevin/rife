/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineTemplateProcessor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.SubmissionBeanFormGenerationErrorException;
import com.uwyn.rife.site.FormBuilder;
import com.uwyn.rife.template.BeanHandler;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateEncoder;
import com.uwyn.rife.template.exceptions.TemplateException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class EngineTemplateProcessor
{
	private final ElementContext				mContext;
	private final Template						mTemplate;
	private final TemplateEncoder				mEncoder;

	private Map<String, String[]>				mOutputValueMap;

	EngineTemplateProcessor(final ElementContext context, final Template template)
	{
		mContext = context;
		mTemplate = template;
		mEncoder = template.getEncoder();
	}

	synchronized List<String> processTemplate()
	throws TemplateException, EngineException
	{
		initialize();

		final List<String> set_values = new ArrayList<String>();

		EngineTemplateHelper.evaluateExpressionRoleUserTags(mContext, set_values, mTemplate, null);

		processExitFlowTags(set_values);

		processSubmissionFlowTags(set_values);

		processPropertyTags(set_values);

		processInputValues(set_values);

		processOutputValues(set_values);

		processGlobalVariableValues(set_values);

		processIncookieValues(set_values);

		processOutcookieValues(set_values);

		processApplicationTags(set_values);

		processSubmissionParametersAndBeans(set_values);

		processEmbeddedElementsLate(set_values);

		return set_values;
	}

	private void initialize()
	{
		// pre-obtain the output entries, since they process the outjection logic
		mOutputValueMap = mContext.getOutputs().aggregateValues();
	}

	private void processExitFlowTags(final List<String> setValues)
	{
		setValues.addAll(new FlowLinkFlowTagsProcessor(mContext, mOutputValueMap, mTemplate, null, null).processTags());
	}

	private void processSubmissionFlowTags(final List<String> setValues)
	{
		setValues.addAll(new SubmissionFlowTagsProcessor(mContext, mOutputValueMap, mTemplate, null, null).processTags());
	}

	private void processPropertyTags(final List<String> setValues)
	{
		if (mTemplate.hasFilteredValues(ElementContext.TAG_PROPERTY))
		{
			String	property_value_id = null;
			String	property_value = null;
	
			for (String[] property_tag : mTemplate.getFilteredValues(ElementContext.TAG_PROPERTY))
			{
				property_value_id = ElementContext.PREFIX_PROPERTY + property_tag[1];
				if (mTemplate.hasValueId(property_value_id) &&
					!mTemplate.isValueSet(property_value_id))
				{
					property_value = mContext.getElementInfo().getPropertyString(property_tag[1]);
					if (property_value != null)
					{
						mTemplate.setValue(property_value_id, mEncoder.encode(property_value));
						setValues.add(property_value_id);
					}
				}
			}
		}
	}

	private void processInputValues(final List<String> setValues)
	{
		String		input_value_id = null;
		String[]	input_values = null;
	
		ElementExecutionState element_state = mContext.getElementState();
		for (Map.Entry<String, String[]> input_entry : element_state.getInputEntries())
		{
			if (mContext.getElementInfo().containsInput(input_entry.getKey()) &&
				element_state.hasInputValue(input_entry.getKey()))
			{
				input_value_id = ElementContext.PREFIX_INPUT + input_entry.getKey();
				input_values = input_entry.getValue();
				if (mTemplate.hasValueId(input_value_id) &&
					!mTemplate.isValueSet(input_value_id))
				{
					mTemplate.setValue(input_value_id, mEncoder.encode(input_values[0]));
					setValues.add(input_value_id);
				}
	
				setValues.addAll(EngineTemplateHelper.selectInputParameter(mTemplate, input_entry.getKey(), input_values));
			}
		}
	}

	private void processOutputValues(final List<String> setValues)
	{
		String	output_value_id = null;
	
		for (Map.Entry<String, String[]> output_entry : mOutputValueMap.entrySet())
		{
			if (output_entry.getValue() != null)
			{
				output_value_id = ElementContext.PREFIX_OUTPUT + output_entry.getKey();
				if (mTemplate.hasValueId(output_value_id) &&
					!mTemplate.isValueSet(output_value_id))
				{
					mTemplate.setValue(output_value_id, mEncoder.encode(output_entry.getValue()[0]));
					setValues.add(output_value_id);
				}
	
				setValues.addAll(EngineTemplateHelper.selectOutputParameter(mTemplate, output_entry.getKey(), output_entry.getValue()));
			}
		}
	}

	private void processGlobalVariableValues(final List<String> setValues)
	{
		String input_value_id;
		String output_value_id;
	
		ElementExecutionState element_state = mContext.getElementState();
		for (String globalvar_name : mContext.getElementInfo().getGlobalVarNames())
		{
			if(element_state.hasInputValue(globalvar_name))
			{
				input_value_id = ElementContext.PREFIX_INPUT + globalvar_name;
				if (mTemplate.hasValueId(input_value_id) &&
					!mTemplate.isValueSet(input_value_id))
				{
					mTemplate.setValue(input_value_id, mEncoder.encode(element_state.getInput(globalvar_name)));
					setValues.add(input_value_id);
				}
	
				setValues.addAll(EngineTemplateHelper.selectInputParameter(mTemplate, globalvar_name, element_state.getInputValues(globalvar_name)));
			}
	
			String[] global_output_value = mOutputValueMap.get(globalvar_name);
			if (global_output_value != null)
			{
				output_value_id = ElementContext.PREFIX_OUTPUT + globalvar_name;
				if (mTemplate.hasValueId(output_value_id) &&
					!mTemplate.isValueSet(output_value_id))
				{
					mTemplate.setValue(output_value_id, mEncoder.encode(global_output_value[0]));
					setValues.add(output_value_id);
				}
	
				setValues.addAll(EngineTemplateHelper.selectOutputParameter(mTemplate, globalvar_name, global_output_value));
			}
		}
	}

	private void processOutcookieValues(final List<String> setValues)
	{
		String	outcookie_value_id = null;
		for (Map.Entry<String, String> outcookie_entry : mContext.getOutcookies().aggregateValues().entrySet())
		{
			if (mContext.getElementInfo().containsOutcookiePossibility(outcookie_entry.getKey()))
			{
				outcookie_value_id = ElementContext.PREFIX_OUTCOOKIE + outcookie_entry.getKey();
				if (mTemplate.hasValueId(outcookie_value_id) &&
					!mTemplate.isValueSet(outcookie_value_id))
				{
					mTemplate.setValue(outcookie_value_id, mEncoder.encode(outcookie_entry.getValue()));
					setValues.add(outcookie_value_id);
				}
			}
		}
	}

	private void processIncookieValues(final List<String> setValues)
	{
		String	incookie_value_id = null;
		for (Map.Entry<String, String> incookie_entry : mContext.getIncookieEntries())
		{
			if (mContext.getElementInfo().containsIncookie(incookie_entry.getKey()) ||
				mContext.getElementInfo().containsGlobalCookie(incookie_entry.getKey()))
			{
				incookie_value_id = ElementContext.PREFIX_INCOOKIE + incookie_entry.getKey();
				if (mTemplate.hasValueId(incookie_value_id) &&
					!mTemplate.isValueSet(incookie_value_id))
				{
					mTemplate.setValue(incookie_value_id, mEncoder.encode(incookie_entry.getValue()));
					setValues.add(incookie_value_id);
				}
			}
		}
	}

	private void processApplicationTags(final List<String> setValues)
	{
		// set the webapp root
		if (mTemplate.hasValueId(ElementContext.ID_WEBAPP_ROOTURL) &&
			!mTemplate.isValueSet(ElementContext.ID_WEBAPP_ROOTURL))
		{
			mTemplate.setValue(ElementContext.ID_WEBAPP_ROOTURL, mContext.getRequestState().getWebappRootUrl(-1));
			setValues.add(ElementContext.ID_WEBAPP_ROOTURL);
		}
	
		// set the server root
		if (mTemplate.hasValueId(ElementContext.ID_SERVER_ROOTURL) &&
			!mTemplate.isValueSet(ElementContext.ID_SERVER_ROOTURL))
		{
			mTemplate.setValue(ElementContext.ID_SERVER_ROOTURL, mContext.getRequestState().getServerRootUrl(-1));
			setValues.add(ElementContext.ID_SERVER_ROOTURL);
		}
	}

	private void processSubmissionParametersAndBeans(final List<String> setValues)
	{
		ElementExecutionState element_state = mContext.getElementState();

		String[]	submission_param_values = null;
		String		submission_param_value_id = null;

		for (Submission submission : mContext.getElementInfo().getSubmissions())
		{
			// create the parameters
			for (String submission_param_name : submission.getParameterNames())
			{
				if (element_state.hasRequestParameterValue(submission_param_name))
				{
					submission_param_values = element_state.getRequestParameterValues(submission_param_name);

					submission_param_value_id = ElementContext.PREFIX_PARAM +submission_param_name;
					if (mTemplate.hasValueId(submission_param_value_id) &&
						!mTemplate.isValueSet(submission_param_value_id))
					{
						mTemplate.setValue(submission_param_value_id, mEncoder.encode(submission_param_values[0]));
						setValues.add(submission_param_value_id);
					}

					setValues.addAll(EngineTemplateHelper.selectSubmissionParameter(mTemplate, submission_param_name, submission_param_values));
				}
			}

			// create the bean forms
			BeanHandler bean_handler = mTemplate.getBeanHandler();
			if (bean_handler != null)
			{
				FormBuilder form_builder = bean_handler.getFormBuilder();
				if (form_builder != null)
				{
					for (BeanDeclaration bean : submission.getBeans())
					{
						try
						{
							Map<String, String[]> parameters = null;
							if (mContext.hasSubmission() &&
								submission.getName().equals(mContext.getSubmission()))
							{
								parameters = element_state.getRequestParameters();
							}
							form_builder.generateForm(mTemplate, bean.getBeanClass(), parameters, bean.getPrefix());
						}
						catch (Throwable e)
						{
							throw new SubmissionBeanFormGenerationErrorException(mTemplate, mContext.getElementInfo().getDeclarationName(), submission.getName(), bean.getClassname(), e);
						}
					}
				}
			}
		}
	}

	private void processEmbeddedElementsLate(final List<String> setValues)
	{
		// process the embedded elements
		if (mTemplate.hasFilteredValues(ElementContext.TAG_ELEMENT))
		{
			ElementSupport embedding_element = mContext.getElementSupport();

			List<String[]>	element_tags = mTemplate.getFilteredValues(ElementContext.TAG_ELEMENT);
			for (String[] captured_groups : element_tags)
			{
				// only embed the element if the value hasn't been set in the
				// template yet and is declared as late
				if (!mTemplate.isValueSet(captured_groups[0]) &&
					captured_groups[2].equals("+"))
				{
					mContext.processEmbeddedElement(captured_groups[0], mTemplate, embedding_element, captured_groups[3], captured_groups[4], null);
					setValues.add(captured_groups[0]);
				}
			}
		}
	}
}