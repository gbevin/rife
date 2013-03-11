/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingManyToManyTypeInformationException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.exceptions;

import com.uwyn.rife.database.exceptions.DatabaseException;

public class MissingManyToManyTypeInformationException extends DatabaseException
{
	private Class mBeanClass;
	private String mPropertyName;
	
	static final long serialVersionUID = -1852595871902260744L;
	
	public MissingManyToManyTypeInformationException(Class beanClass, String propertyName)
	{
		super("The bean '"+beanClass.getName()+"' declares a many-to-many relationship on property '"+propertyName+"', however the type of the associated class hasn't been specified. This can either be done during the declaration of the manyToMany constraint or by specifying the property's collection elements through generics.");
		
		mBeanClass = beanClass;
		mPropertyName = propertyName;
	}
	
	public Class getBeanClass()
	{
		return mBeanClass;
	}
	
	public String getPropertyName()
	{
		return mPropertyName;
	}
}
