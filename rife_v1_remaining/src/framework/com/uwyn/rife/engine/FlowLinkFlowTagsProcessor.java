/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowLinkFlowTagsProcessor.java 3917 2008-04-14 16:55:14Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.template.Template;

import java.util.Collection;
import java.util.Map;

class FlowLinkFlowTagsProcessor extends AbstractFlowTagsProcessor<FlowLinkFlowTagsProcessor, FlowLink>
{
	private static final String ENTITY_NAME = "exit";

	private final String	mPathinfo;
	private final String[]	mOutputValues;

	FlowLinkFlowTagsProcessor(final ElementContext context, final Map<String, String[]> outputValueMap, final Template template, final String pathinfo, final String[] outputValues)
	{
		super(context, outputValueMap, template);

		mPathinfo = pathinfo;
		mOutputValues = outputValues;
	}

	protected String getEntityName()
	{
		return ENTITY_NAME;
	}

	protected String getQueryPrefix()
	{
		return ElementContext.PREFIX_EXIT_QUERY;
	}

	protected String getFormPrefix()
	{
		return ElementContext.PREFIX_EXIT_FORM;
	}

	protected String getParamsPrefix()
	{
		return ElementContext.PREFIX_EXIT_PARAMS;
	}

	protected String getParamsjsPrefix()
	{
		return ElementContext.PREFIX_EXIT_PARAMSJS;
	}

	protected String getFieldTag()
	{
		return ElementContext.TAG_EXITFIELD;
	}

	protected Collection<String> getNames()
	{
		return getContext().getElementInfo().getExitNames();
	}

	protected FlowLink getEntity(final String name)
	{
		return getContext().getElementInfo().getFlowLink(name);
	}

	protected CharSequenceDeferred generateEntityQueryUrl(final FlowLink flowLink)
	{
		return ElementContextFlowGeneration.generateExitQueryUrl(getContext(), flowLink, mPathinfo, getOutputValueMap(), mOutputValues);
	}

	protected CharSequenceDeferred generateEntityFormUrl(final FlowLink flowLink)
	{
		return ElementContextFlowGeneration.generateExitFormUrl(getContext(), flowLink, mPathinfo, getOutputValueMap());
	}

	protected CharSequenceDeferred generateEntityFormParameters(final FlowLink flowLink)
	{
		return ElementContextFlowGeneration.generateExitFormParameters(getContext(), flowLink, getOutputValueMap(), mOutputValues);
	}

	protected CharSequenceDeferred generateEntityFormParametersJavascript(final FlowLink flowLink)
	{
		return ElementContextFlowGeneration.generateExitFormParametersJavascript(getContext(), flowLink, getOutputValueMap(), mOutputValues);
	}
}