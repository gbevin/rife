/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanInstanceValuesErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class BeanInstanceValuesErrorException extends EngineException
{
	private static final long serialVersionUID = 7417627480924866159L;

	private Object	mBean = null;
	
	public BeanInstanceValuesErrorException(Object bean, Throwable cause)
	{
		super("Unexpected error while trying to set the values of bean '"+String.valueOf(bean)+"'"+(bean == null ? "." : " with class '"+bean.getClass().getName()+"'."), cause);
		
		mBean = bean;
	}
	
	public Object getBean()
	{
		return mBean;
	}
}
