/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedElementAnnotationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.tools.ClassUtils;

public class UnsupportedElementAnnotationErrorException extends ElementAnnotationErrorException
{
	private static final long serialVersionUID = 2062131617719477259L;

	private Class	mAnnotationType = null;

	public UnsupportedElementAnnotationErrorException(String implementationName, String siteDeclarationName, Class annotationType, String message, Throwable cause)
	{
		super(implementationName, siteDeclarationName, "@"+ClassUtils.simpleClassName(annotationType)+" annotation isn't supported "+message, cause);

		mAnnotationType = annotationType;
	}

	public Class getAnnotationType()
	{
		return mAnnotationType;
	}
}
