/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingManyToOneTableException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class MissingManyToOneTableException extends DatabaseException
{
	private static final long serialVersionUID = 9024147800617136452L;
	
	private Class	mConstrainedClass = null;
	private String	mPropertyName = null;
	
	public MissingManyToOneTableException(Class constrainedClass, String propertyName)
	{
		super("The property '"+propertyName+"' of '"+constrainedClass.getName()+"' has a manyToOne constraint, however the associated table name is missing. This can be provided by giving either the table name or the associated class when the constraint is declared.");
		
		mConstrainedClass = constrainedClass;
		mPropertyName = propertyName;
	}

	public Class getConstrainedClass()
	{
		return mConstrainedClass;
	}

	public String getPropertyName()
	{
		return mPropertyName;
	}
}
