/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PropertyValueErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.ioc.exceptions.PropertyValueException;

public class PropertyValueErrorException extends EngineException
{
	private static final long serialVersionUID = 4400535247294963445L;
	
	private String	mDeclarationName = null;

	public PropertyValueErrorException(String declarationName, PropertyValueException e)
	{
		super("An error occured while retrieving a value for a property of element '"+declarationName+"'.", e);

		mDeclarationName = declarationName;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}
}
