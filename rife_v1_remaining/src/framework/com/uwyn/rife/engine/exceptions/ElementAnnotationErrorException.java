/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementAnnotationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementAnnotationErrorException extends EngineException
{
	private static final long serialVersionUID = 2174216337945033177L;

	private String	mImplementationName = null;
	private String	mSiteDeclarationName = null;

	public ElementAnnotationErrorException(String implementationName, String siteDeclarationName, String message, Throwable cause)
	{
		super("Error while processing the annotations of element with implementation '"+implementationName+"' in site '"+siteDeclarationName+"' : "+message, cause);

		mImplementationName = implementationName;
		mSiteDeclarationName = siteDeclarationName;
	}

	public String getImplementationName()
	{
		return mImplementationName;
	}

	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
}
