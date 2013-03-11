/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExpectedMimeTypeConstraintException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.exceptions;

public class ExpectedMimeTypeConstraintException extends ContentManagerException
{
	private static final long serialVersionUID = 6107469107496719973L;

	private Class	mBeanClass = null;
	private String	mProperty = null;
	
	public ExpectedMimeTypeConstraintException(Class beanClass, String property)
	{
		super("The constrained property '"+property+"' of bean '"+beanClass.getName()+"' should have been constrained by an 'mimeType' constraint, but it wasn't.");
		
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
