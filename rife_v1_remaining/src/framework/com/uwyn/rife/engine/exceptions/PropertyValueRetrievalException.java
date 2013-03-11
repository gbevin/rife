/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PropertyValueRetrievalException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class PropertyValueRetrievalException extends EngineException
{
	static final long serialVersionUID = 6223832962760620854L;

	private String	mDeclarationName = null;
	private String	mPropertyName = null;

	public PropertyValueRetrievalException(String declarationName, String propertyName, Throwable e)
	{
		super("An error occured while retrieving a value for the property '"+propertyName+"' of element '"+declarationName+"'.", e);

		mDeclarationName = declarationName;
		mPropertyName = propertyName;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public String getPropertyName()
	{
		return mPropertyName;
	}
}
