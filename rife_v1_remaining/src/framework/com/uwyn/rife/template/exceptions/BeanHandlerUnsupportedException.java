/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanHandlerUnsupportedException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.template.Template;

public class BeanHandlerUnsupportedException extends TemplateException
{
	private static final long serialVersionUID = 7166917351543261088L;
	
	private Template	mTemplate = null;
	private Object		mBean = null;
	
	public BeanHandlerUnsupportedException(Template template, Object bean)
	{
		super("The template '"+template.getClass().getName()+"' doesn't support the handling of bean values. This was attempted for bean '"+String.valueOf(bean)+"'"+(bean == null ? "." : " with class '"+bean.getClass().getName()+"'."));
		
		mTemplate = template;
		mBean = bean;
	}
	
	public Template getTemplate()
	{
		return mTemplate;
	}
	
	public Object getBean()
	{
		return mBean;
	}
}
