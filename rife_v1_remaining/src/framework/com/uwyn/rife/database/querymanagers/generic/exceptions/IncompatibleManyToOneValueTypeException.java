/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IncompatibleManyToOneValueTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;

public class IncompatibleManyToOneValueTypeException extends DatabaseException
{
	static final long serialVersionUID = -7028083340881018568L;
	
	private Class mBeanClass;
	private String mPropertyName;
	private Class mPropertyType;
	private Class mAssociatedType;
	
	public IncompatibleManyToOneValueTypeException(Class beanClass, String propertyName, Class propertyType, Class associatedType)
	{
		super("The bean '"+beanClass.getName()+"' declares a many-to-one relationship on property '"+propertyName+"', however the property's type '"+propertyType.getName()+"' is not assignable from the associated class '"+associatedType.getName()+"' that has been declared through constraints.");
		
		mBeanClass = beanClass;
		mPropertyName = propertyName;
		mPropertyType = propertyType;
		mAssociatedType = associatedType;
	}
	
	public Class getBeanClass()
	{
		return mBeanClass;
	}
	
	public String getPropertyName()
	{
		return mPropertyName;
	}
	
	public Class getPropertyType()
	{
		return mPropertyType;
	}
	
	public Class getAssociatedType()
	{
		return mAssociatedType;
	}
}
