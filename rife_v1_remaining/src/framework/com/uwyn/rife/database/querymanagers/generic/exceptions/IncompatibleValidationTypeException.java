/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IncompatibleValidationTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;

public class IncompatibleValidationTypeException extends DatabaseException
{
	static final long serialVersionUID = -3877438782725574316L;
	
	private Class mIncompatibleType;
	private Class mExpectedType;
	
	public IncompatibleValidationTypeException(Class incompatibleType, Class expectedType)
	{
		super("Trying to validate a bean of class '"+incompatibleType.getName()+"' while this GenericQueryManager only supports beans of the '"+expectedType.getName()+"' class.");
		
		mIncompatibleType = incompatibleType;
		mExpectedType = expectedType;
	}

	public Class getExpectedType()
	{
		return mExpectedType;
	}

	public Class getIncompatibleType()
	{
		return mIncompatibleType;
	}
}
