/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementAlreadyMappedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementAlreadyMappedException extends EngineException
{
	private static final long serialVersionUID = 7379024707238867441L;

	private String	mDeclarationName = null;
	private String	mUrl = null;

	public ElementAlreadyMappedException(String declarationName, String url)
	{
		super("The element '"+declarationName+"' has already been mapped to url '"+url+"'.");
		
		mDeclarationName = declarationName;
		mUrl = url;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getUrl()
	{
		return mUrl;
	}
}
