/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SiteProcessorExtensionUnsupportedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SiteProcessorExtensionUnsupportedException extends EngineException
{
	private static final long serialVersionUID = 3617560531286823879L;

	private String	mDeclarationName = null;
	private String	mExtension = null;

	public SiteProcessorExtensionUnsupportedException(String declarationName, String extension)
	{
		super("The site '"+declarationName+"' uses an unsupported site processor extension '"+extension+"'.");
		
		mDeclarationName = declarationName;
		mExtension = extension;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getExtension()
	{
		return mExtension;
	}
}
