/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InvalidUseOfElementPropertyAnnotationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InvalidUseOfElementPropertyAnnotationException extends ElementAnnotationErrorException
{
	private static final long serialVersionUID = -4487585264263683699L;

	private String	mMethodName = null;

	public InvalidUseOfElementPropertyAnnotationException(String implementationName, String siteDeclarationName, String methodName, Throwable cause)
	{
		super(implementationName, siteDeclarationName, "the use of property annotations on method '"+methodName+"' is invalid, these annotations can only be used on setters and getters.", cause);

		mMethodName = methodName;
	}

	public String getMethodName()
	{
		return mMethodName;
	}
}
