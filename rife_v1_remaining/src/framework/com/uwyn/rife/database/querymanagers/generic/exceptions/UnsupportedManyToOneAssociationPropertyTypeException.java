/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnsupportedManyToOneAssociationPropertyTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;

public class UnsupportedManyToOneAssociationPropertyTypeException extends DatabaseException
{
	private Class mBeanClass;
	private String mPropertyName;
	private Class mType;
	
	static final long serialVersionUID = 221066901914357366L;
	
	public UnsupportedManyToOneAssociationPropertyTypeException(Class beanClass, String propertyName, Class type)
	{
		super("The bean '"+beanClass.getName()+"' declares a many-to-one association relationship on property '"+propertyName+"', however the property's type '"+type.getName()+"' is not supported. Only java.util.Collection, java.util.Set and java.util.List can be used.");
		
		mBeanClass = beanClass;
		mPropertyName = propertyName;
		mType = type;
	}
	
	public Class getBeanClass()
	{
		return mBeanClass;
	}
	
	public String getPropertyName()
	{
		return mPropertyName;
	}
	
	public Class getType()
	{
		return mType;
	}
}
