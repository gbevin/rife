/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UnknownOrdinalRestrictionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.exceptions;

public class UnknownOrdinalRestrictionException extends ContentManagerException
{
	private static final long serialVersionUID = -1656164424552067374L;

	private Class	mBeanClass = null;
	private String	mProperty = null;
	private String	mRestriction = null;
	
	public UnknownOrdinalRestrictionException(Class beanClass, String property, String restriction)
	{
		super("The property '"+property+"' of bean '"+beanClass.getName()+"' declares itself as being a restricted ordinal, but the restriction property '"+restriction+"' can't be found.");
		
		mBeanClass = beanClass;
		mProperty = property;
		mRestriction = restriction;
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
}
