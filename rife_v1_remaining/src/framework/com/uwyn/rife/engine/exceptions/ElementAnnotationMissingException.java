/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementAnnotationMissingException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.tools.ClassUtils;

public class ElementAnnotationMissingException extends ElementAnnotationErrorException
{
	private static final long serialVersionUID = -321498340237644067L;

	private Class	mAnnotationType = null;

	public ElementAnnotationMissingException(String implementationName, String siteDeclarationName, Class annotationType, Throwable cause)
	{
		super(implementationName, siteDeclarationName, "@"+ ClassUtils.simpleClassName(annotationType)+" annotation is missing on this element.", cause);

		mAnnotationType = annotationType;
	}

	public Class getAnnotationType()
	{
		return mAnnotationType;
	}
}
