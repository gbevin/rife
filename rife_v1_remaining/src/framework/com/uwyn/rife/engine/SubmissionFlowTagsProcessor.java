/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionFlowTagsProcessor.java 3917 2008-04-14 16:55:14Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.template.Template;

import java.util.Collection;
import java.util.Map;

class SubmissionFlowTagsProcessor extends AbstractFlowTagsProcessor<SubmissionFlowTagsProcessor, Submission>
{
	private static final String ENTITY_NAME = "submission";

	private final String	mPathinfo;
	private final String[]	mParameterValues;

	SubmissionFlowTagsProcessor(final ElementContext context, final Map<String, String[]> outputValueMap, final Template template, final String pathinfo, final String[] parameterValues)
	{
		super(context, outputValueMap, template);

		mPathinfo = pathinfo;
		mParameterValues = parameterValues;
	}

	protected String getEntityName()
	{
		return ENTITY_NAME;
	}

	protected String getQueryPrefix()
	{
		return ElementContext.PREFIX_SUBMISSION_QUERY;
	}

	protected String getFormPrefix()
	{
		return ElementContext.PREFIX_SUBMISSION_FORM;
	}

	protected String getParamsPrefix()
	{
		return ElementContext.PREFIX_SUBMISSION_PARAMS;
	}

	protected String getParamsjsPrefix()
	{
		return ElementContext.PREFIX_SUBMISSION_PARAMSJS;
	}

	protected String getFieldTag()
	{
		return ElementContext.TAG_SUBMISSIONFIELD;
	}

	protected Collection<String> getNames()
	{
		return getContext().getElementInfo().getSubmissionNames();
	}

	protected Submission getEntity(final String name)
	{
		return getContext().getElementInfo().getSubmission(name);
	}

	protected CharSequenceDeferred generateEntityQueryUrl(final Submission submission)
	{
		return ElementContextFlowGeneration.generateSubmissionQueryUrl(getContext(), submission.getName(), mPathinfo, mParameterValues, getOutputValueMap().entrySet());
	}

	protected CharSequenceDeferred generateEntityFormUrl(final Submission submission)
	{
		return ElementContextFlowGeneration.generateSubmissionFormUrl(getContext(), mPathinfo);
	}

	protected CharSequenceDeferred generateEntityFormParameters(final Submission submission)
	{
		return ElementContextFlowGeneration.generateSubmissionFormParameters(getContext(), submission.getName(), mParameterValues, getOutputValueMap().entrySet());
	}

	protected CharSequenceDeferred generateEntityFormParametersJavascript(final Submission submission)
	{
		return ElementContextFlowGeneration.generateSubmissionFormParametersJavascript(getContext(), submission.getName(), mParameterValues, getOutputValueMap().entrySet());
	}
}