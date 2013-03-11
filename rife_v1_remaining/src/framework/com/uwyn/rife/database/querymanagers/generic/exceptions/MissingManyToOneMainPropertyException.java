/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingManyToOneMainPropertyException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;

public class MissingManyToOneMainPropertyException extends DatabaseException
{
	private Class mAssociationClass;
	private String mAssociationProperty;
	private Class mMainClass;
	
	static final long serialVersionUID = -860044159844481242L;
	
	public MissingManyToOneMainPropertyException(Class associationClass, String associationProperty, Class mainClass)
	{
		super("The bean '"+associationClass.getName()+"' declares a many-to-one association relationship on property '"+associationProperty+"', however no matching manyToOne constraint can be find on any property in the main bean '"+mainClass.getName()+"'.");
		
		mAssociationClass = associationClass;
		mAssociationProperty = associationProperty;
		mMainClass = mainClass;
	}
	
	public Class getAssociationClass()
	{
		return mAssociationClass;
	}
	
	public String getAssociationProperty()
	{
		return mAssociationProperty;
	}
	
	public Class getMainClass()
	{
		return mMainClass;
	}
}
