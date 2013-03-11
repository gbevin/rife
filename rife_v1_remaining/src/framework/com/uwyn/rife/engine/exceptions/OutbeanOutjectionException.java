/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutbeanOutjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class OutbeanOutjectionException extends EngineException
{
	private static final long serialVersionUID = -1121267931088809398L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;
	private String	mBeanName = null;

	public OutbeanOutjectionException(String declarationName, Class elementClass, String beanName, Throwable e)
	{
		super("An error occurred while outjecting the outbean '"+beanName+"' from element '"+declarationName+"' with class '"+elementClass.getName()+"'.", e);

		mDeclarationName = declarationName;
		mElementClass = elementClass;
		mBeanName = beanName;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public Class getElementClass()
	{
		return mElementClass;
	}
	
	public String getBeanName()
	{
		return mBeanName;
	}
}
