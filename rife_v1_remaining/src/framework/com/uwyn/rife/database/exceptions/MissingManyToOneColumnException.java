/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingManyToOneColumnException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class MissingManyToOneColumnException extends DatabaseException
{
	private static final long serialVersionUID = 1390166791727531269L;
	
	private Class	mConstrainedClass = null;
	private String	mPropertyName = null;
	
	public MissingManyToOneColumnException(Class constrainedClass, String propertyName)
	{
		super("The property '"+propertyName+"' of '"+constrainedClass.getName()+"' has a manyToOne constraint, however the column of the associated table is missing. This can be provided when the constraint is declared.");
		
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
