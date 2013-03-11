/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InvalidOrdinalRestrictionTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.exceptions;

public class InvalidOrdinalRestrictionTypeException extends ContentManagerException
{
	private static final long serialVersionUID = -4099426985586611966L;

	private Class	mBeanClass = null;
	private String	mProperty = null;
	private String	mRestriction = null;
	private Class	mType = null;
	
	public InvalidOrdinalRestrictionTypeException(Class beanClass, String property, String restriction, Class type)
	{
		super("The property '"+property+"' of bean '"+beanClass.getName()+"' declares itself as being a restricted ordinal, but the restriction property '"+restriction+"' with type "+type.getName()+" is not a Number.");
		
		mBeanClass = beanClass;
		mProperty = property;
		mRestriction = restriction;
		mType = type;
	}
	
	public Class getBeanClass()
	{
		return mBeanClass;
	}
	
	public String getProperty()
	{
		return mProperty;
	}
	
	public String getRestriction()
	{
		return mRestriction;
	}

	public Class getType()
	{
		return mType;
	}
}
