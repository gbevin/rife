/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnknownConstrainedPropertyException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.exceptions;

public class UnknownConstrainedPropertyException extends ContentManagerException
{
	private static final long serialVersionUID = 1443710217884057575L;
	
	private Class	mBeanClass = null;
	private String	mProperty = null;
	
	public UnknownConstrainedPropertyException(Class beanClass, String property)
	{
		super("The property '"+property+"' of bean '"+beanClass.getName()+"' can't be found as a constrained property.");
		
		mBeanClass = beanClass;
		mProperty = property;
	}
	
	public Class getBeanClass()
	{
		return mBeanClass;
	}
	
	public String getProperty()
	{
		return mProperty;
	}
}
