/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExpectedStringConstantFieldException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ExpectedStringConstantFieldException extends Exception
{
	private static final long serialVersionUID = -3878035095820948418L;

	private final String mClassName;
	private final String mFieldName;

	public ExpectedStringConstantFieldException(final String className, final String fieldName)
	{
		super("Expected the field '" + fieldName + "' in class '" + className + "' to have 'public final static' modifiers but it didn't.");

		mClassName = className;
		mFieldName = fieldName;
	}

	public String getClassName()
	{
		return mClassName;
	}

	public String getFieldName()
	{
		return mFieldName;
	}
}
