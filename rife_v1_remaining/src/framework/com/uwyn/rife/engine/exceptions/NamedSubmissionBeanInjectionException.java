/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NamedSubmissionBeanInjectionException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class NamedSubmissionBeanInjectionException extends EngineException
{
	private static final long serialVersionUID = 7624841539921259537L;

	private String	mDeclarationName = null;
	private Class	mElementClass = null;
	private String	mSubmissionBeanName = null;

	public NamedSubmissionBeanInjectionException(String declarationName, Class elementClass, String beanName, Throwable e)
	{
		super("An error occurred while injecting the value for the named submission bean '"+beanName +"' of element '"+declarationName+"' into class '"+elementClass.getName()+"'.", e);

		mDeclarationName = declarationName;
		mElementClass = elementClass;
		mSubmissionBeanName = beanName;
	}

	public String getDeclarationName()
	{
		return mDeclarationName;
	}

	public Class getElementClass()
	{
		return mElementClass;
	}

	public String getSubmissionBeanName()
	{
		return mSubmissionBeanName;
	}
}
