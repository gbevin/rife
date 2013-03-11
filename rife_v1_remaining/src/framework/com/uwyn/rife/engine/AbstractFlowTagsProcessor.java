/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractFlowTagsProcessor.java 3917 2008-04-14 16:55:14Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineTemplateProcessingException;
import com.uwyn.rife.engine.exceptions.ExpectedStringConstantFieldException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateEncoder;

import java.util.*;

abstract class AbstractFlowTagsProcessor<FlowTagsProcessorType extends AbstractFlowTagsProcessor, Entity>
{
	private final ElementContext		mContext;
	private final Map<String, String[]> mOutputValueMap;
	private final Template 				mTemplate;
	private final TemplateEncoder		mEncoder;

	private String	mFocusedName;
	private boolean	mProcessQueryTags;
	private boolean	mProcessFormTags;
	private boolean mReplaceExistingValues;

	private List<String>		mSetValues;
	private Map<String, String> mFormUrls;
	private Map<String, String> mFormParams;

	AbstractFlowTagsProcessor(final ElementContext context, final Map<String, String[]> outputValueMap, final Template template)
	{
		mContext = context;
		mOutputValueMap = outputValueMap;
		mTemplate = template;
		mEncoder = template.getEncoder();

		reset();
	}

	synchronized List<String> processTags()
	{
		mSetValues = new ArrayList<String>();
		mFormUrls = null;
		mFormParams = null;

		if (mFocusedName != null)
		{
			processNameTag(mFocusedName);
		}
		else
		{
			processNamedTags();
		}
		processFieldTags();

		ensurePresenceOfRequiredMatchingTags();

		return mSetValues;
	}

	ElementContext getContext()
	{
		return mContext;
	}

	Map<String, String[]> getOutputValueMap()
	{
		return mOutputValueMap;
	}

	synchronized FlowTagsProcessorType reset()
	{
		mFocusedName = null;
		mProcessFormTags = true;
		mProcessQueryTags = true;
		mReplaceExistingValues = false;
		return (FlowTagsProcessorType)this;
	}

	synchronized FlowTagsProcessorType focusOnName(final String name)
	{
		mFocusedName = name;
		return (FlowTagsProcessorType)this;
	}

	synchronized FlowTagsProcessorType onlyProcessFormTags()
	{
		mProcessQueryTags = false;
		return (FlowTagsProcessorType)this;
	}

	synchronized FlowTagsProcessorType onlyProcessQueryTags()
	{
		mProcessFormTags = false;
		return (FlowTagsProcessorType)this;
	}

	synchronized FlowTagsProcessorType replaceExistingValues()
	{
		mReplaceExistingValues = true;
		return (FlowTagsProcessorType)this;
	}

	protected abstract String getEntityName();

	protected abstract String getQueryPrefix();

	protected abstract String getFormPrefix();

	protected abstract String getParamsPrefix();

	protected abstract String getParamsjsPrefix();

	protected abstract String getFieldTag();

	protected abstract Collection<String> getNames();

	protected abstract Entity getEntity(String name);

	protected abstract CharSequenceDeferred generateEntityQueryUrl(Entity entity);

	protected abstract CharSequenceDeferred generateEntityFormUrl(Entity entity);

	protected abstract CharSequenceDeferred generateEntityFormParameters(Entity entity);

	protected abstract CharSequenceDeferred generateEntityFormParametersJavascript(Entity entity);

	private void processNamedTags()
	{
		Collection<String> names = getNames();
		if (names.size() > 0)
		{
			for (String name : names)
			{
				processNameTag(name);
			}
		}
	}

	private void processNameTag(final String name)
	{
		final Entity entity = getEntity(name);

		if (entity != null)
		{
			final String query_value_id = getQueryPrefix() + name;
			final String form_value_id = getFormPrefix() + name;
			final String params_value_id = getParamsPrefix() + name;
			final String paramsjs_value_id = getParamsjsPrefix() + name;

			if (mProcessQueryTags && mTemplate.hasValueId(query_value_id))
			{
				generateQueryUrl(entity, query_value_id);
			}
			if (mProcessFormTags)
			{
				if (mTemplate.hasValueId(form_value_id))
				{
					generateFormUrl(name, entity, form_value_id);
				}
				if (mTemplate.hasValueId(params_value_id))
				{
					generateFormParameters(name, entity, params_value_id);
				}
				if (mTemplate.hasValueId(paramsjs_value_id))
				{
					generateFormParametersJavascript(name, entity, paramsjs_value_id);
				}
			}
		}
	}

	private void processFieldTags()
	{
		if (mTemplate.hasFilteredValues(getFieldTag()))
		{
			final List<String[]> fields_filtered = mTemplate.getFilteredValues(getFieldTag());
			for (final String[] field_tag_matches : fields_filtered)
			{
				// obtain the name by retrieving the string value for
				// the field that is specified in the tag
				final String tag = field_tag_matches[0];
				final String tag_suffix = field_tag_matches[1];
				final String field_name = field_tag_matches[2];

				try
				{
					String name = EngineTemplateHelper.getFlowEntityNameFromFieldTag(mContext.getElementSupport(), tag, field_name);
					if (name != null &&
						(null == mFocusedName || name.equals(mFocusedName)))
					{
						final Entity entity = getEntity(name);
						if (entity != null)
						{
							if (mProcessQueryTags)
							{
								if (ElementContext.SUFFIX_QUERY.equals(tag_suffix))
								{
									generateQueryUrl(entity, tag);
								}
							}
							if (mProcessFormTags)
							{
								if (ElementContext.SUFFIX_FORM.equals(tag_suffix))
								{
									generateFormUrl(name, entity, tag);
								}
								else if (ElementContext.SUFFIX_PARAMS.equals(tag_suffix))
								{
									generateFormParameters(name, entity, tag);
								}
								else if (ElementContext.SUFFIX_PARAMSJS.equals(tag_suffix))
								{
									generateFormParametersJavascript(name, entity, tag);
								}
							}
						}
					}
				}
				catch (ClassNotFoundException e)
				{
					throw new EngineTemplateProcessingException(mTemplate, "couldn't process the " + getEntityName() + " field value tag '" + tag + "', the specified class couldn't be found.", e);
				}
				catch (NoSuchFieldException e)
				{
					throw new EngineTemplateProcessingException(mTemplate, "couldn't process the " + getEntityName() + " field value tag '" + tag + "', the field couldn't be found.", e);
				}
				catch (IllegalAccessException e)
				{
					throw new EngineTemplateProcessingException(mTemplate, "couldn't process the " + getEntityName() + " field value tag '" + tag + "', the field isn't accessible.", e);
				}
				catch (ExpectedStringConstantFieldException e)
				{
					throw new EngineTemplateProcessingException(mTemplate, "couldn't process the " + getEntityName() + " field value tag '" + tag + "' since the referenced field is not a public final static String constant.", e);
				}
			}
		}
	}

	private void generateQueryUrl(final Entity entity, final String queryValueId)
	{
		if (mReplaceExistingValues || !mTemplate.isValueSet(queryValueId))
		{
			mTemplate.setValue(queryValueId, generateEntityQueryUrl(entity).encoder(mEncoder));
			mSetValues.add(queryValueId);
		}
	}

	private void generateFormUrl(final String name, final Entity entity, final String formValueId)
	{
		registerFormUrlTag(name, formValueId);

		if (mReplaceExistingValues || !mTemplate.isValueSet(formValueId))
		{
			mTemplate.setValue(formValueId, generateEntityFormUrl(entity).encoder(mEncoder));
			mSetValues.add(formValueId);
		}
	}

	private void generateFormParameters(final String name, final Entity entity, final String paramsValueId)
	{
		registerFormParamTag(name, paramsValueId);

		if (mReplaceExistingValues || !mTemplate.isValueSet(paramsValueId))
		{
			mTemplate.setValue(paramsValueId, generateEntityFormParameters(entity));
			mSetValues.add(paramsValueId);
		}
	}

	private void generateFormParametersJavascript(final String name, final Entity entity, final String paramsjsValueId)
	{
		registerFormParamTag(name, paramsjsValueId);

		if (mReplaceExistingValues || !mTemplate.isValueSet(paramsjsValueId))
		{
			mTemplate.setValue(paramsjsValueId, generateEntityFormParametersJavascript(entity));
			mSetValues.add(paramsjsValueId);
		}
	}

	private void registerFormUrlTag(final String name, final String tag)
	{
		if (null == mFormUrls)
		{
			mFormUrls = new LinkedHashMap<String, String>();
		}
		mFormUrls.put(name, tag);
	}

	private void registerFormParamTag(final String name, final String tag)
	{
		if (null == mFormParams)
		{
			mFormParams = new LinkedHashMap<String, String>();
		}
		mFormParams.put(name, tag);
	}

	private void ensurePresenceOfRequiredMatchingTags()
	{
		// verify that all form tags have matching param tags and
		// that for all the param tags a corresponding form tag is present too
		if (mFormUrls != null &&
			mFormParams != null)
		{
			// remove all the entries that are present in both maps
			for (Iterator<String> urls_it = mFormUrls.keySet().iterator(); urls_it.hasNext(); )
			{
				String name = urls_it.next();

				if (mFormParams.remove(name) != null)
				{
					urls_it.remove();
				}
			}

			for (Iterator<String> params_it = mFormParams.keySet().iterator(); params_it.hasNext();)
			{
				String name = params_it.next();

				if (mFormUrls.remove(name) != null)
				{
					params_it.remove();
				}
			}
		}
		if (mFormUrls != null &&
			mFormUrls.size() > 0)
		{
			Map.Entry<String, String> first_entry = mFormUrls.entrySet().iterator().next();
			throw new EngineTemplateProcessingException(mTemplate, "the form URL tag '" + first_entry.getValue()+ "' was specified for " + getEntityName() + " '" + first_entry.getKey()+ "' without a matching parameter tag, both tags are needed.");
		}
		if (mFormParams != null &&
			mFormParams.size() > 0)
		{
			Map.Entry<String, String> first_entry = mFormParams.entrySet().iterator().next();
			throw new EngineTemplateProcessingException(mTemplate, "the parameters tag '" + first_entry.getValue()+ "' was specified for " + getEntityName() + " '" + first_entry.getKey()+ "' without a matching form URL tag, both tags are needed.");
		}
	}
}