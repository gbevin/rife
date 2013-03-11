/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InvalidUseOfElementSubmissionHandlerAnnotationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class InvalidUseOfElementSubmissionHandlerAnnotationException extends ElementAnnotationErrorException
{
	private static final long serialVersionUID = 4403393138736067398L;

	private String	mMethodName = null;

	public InvalidUseOfElementSubmissionHandlerAnnotationException(String implementationName, String siteDeclarationName, String methodName, Throwable cause)
	{
		super(implementationName, siteDeclarationName, "the user of @SubmissionHandler annotations on method '\"+methodName+\"' is invalid, these annotations can only be added to submission method handlers which start with the 'do' prefix, return void and have no arguments.", cause);

		mMethodName = methodName;
	}

	public String getMethodName()
	{
		return mMethodName;
	}
}
