/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanSettingErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

public class BeanSettingErrorException extends TemplateException
{
	private static final long serialVersionUID = 518347938529572778L;
	
	private Object	mBean = null;
	
	public BeanSettingErrorException(Object bean, Throwable cause)
	{
		super("Unexpected error while trying to set the values of bean '"+String.valueOf(bean)+"'"+(bean == null ? "." : " with class '"+bean.getClass().getName()+"'."), cause);
		
		mBean = bean;
	}
	
	public Object getBean()
	{
		return mBean;
	}
}
