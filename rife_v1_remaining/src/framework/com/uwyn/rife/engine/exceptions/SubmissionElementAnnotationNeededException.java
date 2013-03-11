/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionElementAnnotationNeededException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.tools.ClassUtils;

public class SubmissionElementAnnotationNeededException extends ElementAnnotationErrorException
{
	private static final long serialVersionUID = 3871651033062876268L;

	private Class	mAnnotationType = null;

	public SubmissionElementAnnotationNeededException(String implementationName, String siteDeclarationName, Class annotationType, Throwable cause)
	{
		super(implementationName, siteDeclarationName, ClassUtils.simpleClassName(annotationType)+" annotations can only be used if a submission has been declared beforehand.", cause);

		mAnnotationType = annotationType;
	}

	public Class getAnnotationType()
	{
		return mAnnotationType;
	}
}
