/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateFactoryUnknownException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.ioc.exceptions;

import com.uwyn.rife.ioc.exceptions.PropertyValueException;

public class TemplateFactoryUnknownException extends PropertyValueException
{
	private static final long serialVersionUID = 7268695167543687153L;
	
	private String	mType = null;
	
	public TemplateFactoryUnknownException(String type)
	{
		super("The template factory with type '" + type + "' isn't known by the system.");
		
		mType = type;
	}
	
	public String getType()
	{
		return mType;
	}
}
