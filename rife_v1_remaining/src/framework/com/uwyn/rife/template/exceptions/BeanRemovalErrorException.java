/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanRemovalErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class BeanRemovalErrorException extends TemplateException
{
	private static final long serialVersionUID = -9178572416144354823L;
	
	private Object	mBean = null;
	
	public BeanRemovalErrorException(Object bean, Throwable cause)
	{
		super("Unexpected error while trying to remove the values and properties of bean '"+String.valueOf(bean)+"'"+(bean == null ? "." : " with class '"+bean.getClass().getName()+"'."), cause);
		
		mBean = bean;
	}
	
	public Object getBean()
	{
		return mBean;
	}
}
