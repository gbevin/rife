/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InvalidFilePropertyElementAnnotationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InvalidFilePropertyElementAnnotationException extends ElementAnnotationErrorException
{
	private static final long serialVersionUID = 3102532509376310654L;

	private String	mMethodName = null;

	public InvalidFilePropertyElementAnnotationException(String implementationName, String siteDeclarationName, String methodName, Throwable cause)
	{
		super(implementationName, siteDeclarationName, "the FileProperty annotation on method '"+methodName+"' is invalid, these annotations can only be used on setters with an UploadedFile property type.", cause);

		mMethodName = methodName;
	}

	public String getMethodName()
	{
		return mMethodName;
	}
}
