/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementContextFlowGeneration.java 3917 2008-04-14 16:55:14Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.*;
import com.uwyn.rife.tools.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

abstract class ElementContextFlowGeneration
{
	static String generateExitUrl(ElementContext context, FlowLink flowlink, String pathInfo)
	throws EngineException
	{
		assert flowlink != null;

		String url = null;
		String pathinfo = null;

		ElementExecutionState element_state = context.getElementState();

		// obtain the declared flowlink target element and determine what the
		// actual target element is according to the inheritance status
		ElementInfo	flowlink_target = flowlink.getExitTarget(context.getRequestState());
		ElementInfo	context_target = null;
		if (element_state.inInheritanceStructure() &&
			!flowlink.cancelInheritance())
		{
			context_target = context.getRequestState().getTarget();
		}
		else
		{
			context_target = flowlink_target;
		}
		url = context_target.getUrl();

		// if the element defined no url and the flowlink target points to the element
		// itself when it's embedded, go up the hierarchy of embedded elements
		// to grab the first defined element url
		if (null == url)
		{
			if (!context.getRequestState().isEmbedded() &&
				!flowlink_target.getId().equals(context.getElementInfo().getId()))
			{
				throw new ExitTargetUrlMissingException(context.getElementInfo().getDeclarationName(), flowlink.getExitName(), context_target.getDeclarationName());
			}

			ElementContext embedding_context = context;
			while (null == url &&
				   embedding_context.getRequestState().isEmbedded())
			{
				embedding_context = embedding_context.getRequestState().getEmbeddingContext().getElementContext();
				if (embedding_context.getElementInfo().isPathInfoUsed())
				{
					pathinfo = embedding_context.getRequestState().getElementState().getPathInfo();
				}
				else
				{
					pathinfo = null;
				}
				if (embedding_context.getElementState().inInheritanceStructure())
				{
					url = embedding_context.getRequestState().getTarget().getUrl();
				}
				else
				{
					url = embedding_context.getElementInfo().getUrl();
				}
			}
		}

		// apply a forced pathinfo
		if (pathInfo != null &&
			pathInfo.length() > 0)
		{
			pathinfo = pathInfo;
		}

		// construct the correct servlet path and url that points to the
		// target child element, or the current element if there are no children
		StringBuilder url_buffer = new StringBuilder(context.getRequestState().getGateUrl());
		appendTargetAndPathinfo(url_buffer, url, pathinfo);
		return url_buffer.toString();
	}

	static CharSequenceDeferred generateExitQueryUrl(ElementContext context, FlowLink flowlink, String pathinfo, Map<String, String[]> outputValueMap, String[] outputValues)
	throws EngineException
	{
		assert flowlink != null;

		ElementInfo	flowlink_target = flowlink.getExitTarget(context.getRequestState());

		// process the exit url query parameters
		StateStore	state_store = flowlink_target.getStateStore();
		FlowState	state = context.collectExitParameters(flowlink, outputValueMap, outputValues);

		// process the pathinfo mappings
		if (null == pathinfo &&
			flowlink_target.isPathInfoUsed())
		{
			pathinfo = handlePathInfoMapping(flowlink_target, state);
		}

		return new CharSequenceQueryUrl(state_store, generateExitUrl(context, flowlink, pathinfo), state, context.getElementInfo(), "exit", flowlink.getExitName());
	}

	static CharSequenceDeferred generateExitFormUrl(ElementContext context, FlowLink flowlink, String pathinfo, Map<String, String[]> outputValueMap)
	throws EngineException
	{
		assert flowlink != null;

		ElementInfo	flowlink_target = flowlink.getExitTarget(context.getRequestState());

		StateStore	state_store = flowlink_target.getStateStore();

		// process the pathinfo mappings
		if (null == pathinfo &&
			flowlink_target.isPathInfoUsed())
		{
			FlowState	state = context.collectExitParameters(flowlink, outputValueMap, null);
			pathinfo = handlePathInfoMapping(flowlink_target, state);
		}

		return new CharSequenceFormUrl(state_store, generateExitUrl(context, flowlink, pathinfo));
	}

	static CharSequenceDeferred generateExitFormParameters(ElementContext context, FlowLink flowlink, Map<String, String[]> outputValueMap, String[] outputValues)
	throws EngineException
	{
		assert flowlink != null;

		ElementInfo		flowlink_target = flowlink.getExitTarget(context.getRequestState());
		FlowState	state = context.collectExitParameters(flowlink, outputValueMap, outputValues);
		return new CharSequenceFormState(flowlink_target.getStateStore(), state, FormStateType.PARAMS);
	}

	static CharSequenceDeferred generateExitFormParametersJavascript(ElementContext context, FlowLink flowlink, Map<String, String[]> outputValueMap, String[] outputValues)
	throws EngineException
	{
		assert flowlink != null;

		ElementInfo		flowlink_target = flowlink.getExitTarget(context.getRequestState());
		FlowState	state = context.collectExitParameters(flowlink, outputValueMap, outputValues);
		return new CharSequenceFormState(flowlink_target.getStateStore(), state, FormStateType.JAVASCRIPT);
	}

	static String generateSubmissionUrl(ElementContext context, String pathInfo)
	{
		ElementExecutionState element_state = context.getElementState();

		String url = null;
		String pathinfo = null;
		ElementInfo element_info = null;
		if (element_state.inInheritanceStructure())
		{
			element_info = context.getRequestState().getTarget();
		}
		else
		{
			element_info = context.getElementInfo();
		}
		url = element_info.getUrl();
		if (element_info.isPathInfoUsed())
		{
			pathinfo = context.getRequestState().getElementState().getPathInfo();
		}

		// if the element defined no url, go up the hierarchy of embedded elements
		// to grab the first defined element url
		if (null == url)
		{
			if (!context.getRequestState().isEmbedded())
			{
				if (element_state.inInheritanceStructure())
				{
					throw new SubmissionInheritanceUrlMissingException(context.getRequestState().getTarget().getDeclarationName(), context.getElementInfo().getDeclarationName());
				}
				else
				{
					throw new SubmissionUrlMissingException(context.getElementInfo().getDeclarationName());
				}
			}
			ElementContext embedding_context = context;
			while (null == url &&
				   embedding_context.getRequestState().isEmbedded())
			{
				embedding_context = embedding_context.getRequestState().getEmbeddingContext().getElementContext();

				ElementInfo context_element_info = null;
				if (embedding_context.getElementState().inInheritanceStructure())
				{
					context_element_info = embedding_context.getRequestState().getTarget();
				}
				else
				{
					context_element_info = embedding_context.getElementInfo();
				}
				url = context_element_info.getUrl();

				if (context_element_info.isPathInfoUsed())
				{
					pathinfo = embedding_context.getRequestState().getElementState().getPathInfo();
				}
			}
		}

		// apply a forced pathinfo
		if (pathInfo != null &&
			pathInfo.length() > 0)
		{
			pathinfo = pathInfo;
		}

		// construct the correct servlet path and url that points to the
		// target child element, or the current element if there are no children
		StringBuilder url_buffer = new StringBuilder(context.getRequestState().getGateUrl());
		appendTargetAndPathinfo(url_buffer, url, pathinfo);
		return url_buffer.toString();
	}

	static CharSequenceDeferred generateSubmissionQueryUrl(ElementContext context, String name, String pathinfo, String[] parameterValues, Set<Map.Entry<String, String[]>> outputEntries)
	throws EngineException
	{
		StateStore	state_store = context.getElementInfo().getStateStore();
		FlowState	state = context.collectSubmissionParameters(name, parameterValues, outputEntries);
		return new CharSequenceQueryUrl(state_store, generateSubmissionUrl(context, pathinfo), state, context.getElementInfo(), "submission", name);
	}

	static CharSequenceDeferred generateSubmissionFormUrl(ElementContext context, String pathinfo)
	throws EngineException
	{
		return new CharSequenceFormUrl(context.getElementInfo().getStateStore(), generateSubmissionUrl(context, pathinfo));
	}

	static CharSequenceDeferred generateSubmissionFormParameters(ElementContext context, String name, String[] parameterValues, Set<Map.Entry<String, String[]>> outputEntries)
	throws EngineException
	{
		FlowState	state = context.collectSubmissionParameters(name, parameterValues, outputEntries);
		return new CharSequenceFormState(context.getElementInfo().getStateStore(), state, FormStateType.PARAMS);
	}

	static CharSequenceDeferred generateSubmissionFormParametersJavascript(ElementContext context, String name, String[] parameterValues, Set<Map.Entry<String, String[]>> outputEntries)
	throws EngineException
	{
		FlowState	state = context.collectSubmissionParameters(name, parameterValues, outputEntries);
		return new CharSequenceFormState(context.getElementInfo().getStateStore(), state, FormStateType.JAVASCRIPT);
	}

	private static String handlePathInfoMapping(ElementInfo flowlinkTarget, FlowState state)
	{
		Map<String, String[]> parameters = state.getParameters();

		mapping:
		for (PathInfoMapping mapping : flowlinkTarget.getPathInfoMappings())
		{
			if (parameters.keySet().containsAll(mapping.getInputs()))
			{
				Iterator<String> inputs_it = mapping.getInputs().iterator();

				StringBuilder	builder = new StringBuilder();
				String			input_name;
				String[]		input_value;
				for (PathInfoMappingSegment segment : mapping.getSegments())
				{
					if (segment.isRegexp())
					{
						if (!inputs_it.hasNext())
						{
							continue mapping;
						}

						input_name = inputs_it.next();

						// ensure that the input has only got one value
						input_value = parameters.get(input_name);
						if (null == input_value ||
							input_value.length != 1)
						{
							continue mapping;
						}

						// ensure that the input value corresponds to the
						// regexp pattern for it
						Matcher matcher = segment.getPattern().matcher(input_value[0]);
						if (!matcher.matches())
						{
							continue mapping;
						}

						// add the url-encoded input value to the pathinfo
						builder.append(StringUtils.encodeUrl(input_value[0]));
					}
					else
					{
						builder.append(segment.getValue());
					}
				}

				// remove the input parameters that are handled by the pathinfo
				for (String input : mapping.getInputs())
				{
					parameters.remove(input);
				}

				// build the new pathinfo
				return builder.toString();
			}
		}

		return null;
	}

	private static void appendTargetAndPathinfo(StringBuilder url, String targetUrl, String pathinfo)
	{
		url.append(targetUrl);

		// append the optional pathinfo
		if (pathinfo != null &&
			pathinfo.length() > 0)
		{
			// ensure that the pathinfo is prefixed with a /
			if (url.charAt(url.length()-1) != '/')
			{
				url.append("/");
			}
			url.append(StringUtils.stripFromFront(pathinfo, "/"));
		}
	}
}