/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnserializableOutputValueException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.tools.exceptions.SerializationUtilsErrorException;

public class UnserializableOutputValueException extends EngineException
{
	private static final long serialVersionUID = -7317663625061951368L;
	
	private String	mDeclarationName = null;
	private String	mOutputName = null;
	private Object	mValue = null;

	public UnserializableOutputValueException(String declarationName, String outputName, Object value, SerializationUtilsErrorException cause)
	{
		super("The value '"+value+"' for the output '"+outputName+"' of element '"+declarationName+"' can't be serialized to a string.", cause);
		
		mDeclarationName = declarationName;
		mOutputName = outputName;
		mValue = value;
	}
	
	public String getDeclarationName()
	{
		return mDeclarationName;
	}
	
	public String getOutputName()
	{
		return mOutputName;
	}
	
	public Object getValue()
	{
		return mValue;
	}
}
