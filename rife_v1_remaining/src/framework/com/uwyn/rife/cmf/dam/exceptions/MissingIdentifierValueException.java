/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MissingIdentifierValueException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.exceptions;

public class MissingIdentifierValueException extends ContentManagerException
{
	private static final long serialVersionUID = -1157200832197263833L;

	private Class	mBeanClass = null;
	private String	mIdentifierName = null;
	
	public MissingIdentifierValueException(Class beanClass, String identifierName)
	{
		super("The instance of bean '"+beanClass.getName()+"' should have value for the identifier '"+identifierName+"'.");
		
		mBeanClass = beanClass;
		mIdentifierName = identifierName;
	}
	
	public Class getBeanClass()
	{
		return mBeanClass;
	}
	
	public String getIdentifierName()
	{
		return mIdentifierName;
	}
}
