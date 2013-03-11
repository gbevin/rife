/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EmbedPropertiesErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.engine.exceptions.EngineException;

public class EmbedPropertiesErrorException extends EngineException
{
	private static final long serialVersionUID = -8981967025166656755L;

	private String	mDeclarationName = null;
	private String	mValue = null;

	public EmbedPropertiesErrorException(String declarationName, String value, Throwable e)
	{
		super("Unexpected error while interpreting the embed value '"+value+"' as properties.", e);

		mDeclarationName = declarationName;
		mValue = value;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public String getValue()
	{
		return mValue;
	}
}
