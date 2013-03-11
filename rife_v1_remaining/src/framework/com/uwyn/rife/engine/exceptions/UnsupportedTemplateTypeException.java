/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedTemplateTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class UnsupportedTemplateTypeException extends EngineException
{
	private static final long serialVersionUID = 3619128112449075123L;
	
	private String	mType = null;

	public UnsupportedTemplateTypeException(String type)
	{
		super("The template type '"+type+"' is not supported.");
		
		mType = type;
	}
	
	public String getType()
	{
		return mType;
	}
}
