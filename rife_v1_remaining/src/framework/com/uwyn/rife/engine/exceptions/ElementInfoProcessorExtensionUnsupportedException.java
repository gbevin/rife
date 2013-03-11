/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementInfoProcessorExtensionUnsupportedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementInfoProcessorExtensionUnsupportedException extends EngineException
{
	private static final long serialVersionUID = 7758764770913709485L;

	private String	mDeclarationName = null;
	private String	mExtension = null;

	public ElementInfoProcessorExtensionUnsupportedException(String declarationName, String extension)
	{
		super("The element '"+declarationName+"' uses an unsupported element info processor extension: '"+extension+"'.");
		
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
