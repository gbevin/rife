/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InvalidOrdinalTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.exceptions;

public class InvalidOrdinalTypeException extends ContentManagerException
{
	private static final long serialVersionUID = 931403852026897190L;

	private Class	mBeanClass = null;
	private String	mProperty = null;
	
	public InvalidOrdinalTypeException(Class beanClass, String property)
	{
		super("The property '"+property+"' of bean '"+beanClass.getName()+"' declares itself as being an ordinal, but it is not an integer.");
		
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
