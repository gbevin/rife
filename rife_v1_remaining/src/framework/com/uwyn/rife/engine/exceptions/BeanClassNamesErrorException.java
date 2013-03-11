/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanClassNamesErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class BeanClassNamesErrorException extends EngineException
{
	private static final long serialVersionUID = 397667713488556202L;

	private Class	mBeanClass = null;
	
	public BeanClassNamesErrorException(Class beanClass, Throwable cause)
	{
		super("Unexpected error while trying to work with the property names of bean with class '"+beanClass.getName()+"'.", cause);
		
		mBeanClass = beanClass;
	}
	
	public Class getBeanClass()
	{
		return mBeanClass;
	}
}
