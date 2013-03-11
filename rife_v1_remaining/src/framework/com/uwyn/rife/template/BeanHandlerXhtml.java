/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanHandlerXhtml.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.site.FormBuilder;
import com.uwyn.rife.site.FormBuilderXhtml;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import java.util.Map;

public class BeanHandlerXhtml extends AbstractBeanHandler
{
	private FormBuilder	mFormBuilder = new FormBuilderXhtml();
	
	BeanHandlerXhtml()
	{
	}
	
	public static BeanHandlerXhtml getInstance()
	{
		return BeanHandlerXhtmlSingleton.INSTANCE;
	}
	
	public MimeType getMimeType()
	{
		return MimeType.APPLICATION_XHTML;
	}
	
	public FormBuilder getFormBuilder()
	{
		return mFormBuilder;
	}
	
	protected Map<String, Object> getPropertyValues(Template template, Object bean, String prefix)
	throws BeanUtilsException
	{
		return BeanUtils.getPropertyValues(bean, null, null, prefix);
	}
}

