/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionBeanFormGenerationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.template.Template;

public class SubmissionBeanFormGenerationErrorException extends EngineTemplateProcessingException
{
	private static final long serialVersionUID = 2558711689356487770L;

	private String	mDeclarationName = null;
	private String	mSubmissionName = null;
	private String	mClassName = null;

	public SubmissionBeanFormGenerationErrorException(Template template, String declarationName, String submissionName, String className, Throwable cause)
	{
		super(template, "the form for the bean with class '"+className+"' of submission '"+submissionName+"' in element '"+declarationName+"' couldn't be generated.", cause);
		
		mDeclarationName = declarationName;
		mSubmissionName = submissionName;
		mClassName = className;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getSubmissionName()
	{
		return mSubmissionName;
	}
	
	public String getClassName()
	{
		return mClassName;
	}
}
