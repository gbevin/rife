/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RendererWrongTypeException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.template.ValueRenderer;
import com.uwyn.rife.template.Template;

public class RendererWrongTypeException extends TemplateException
{
	private static final long serialVersionUID = -9116436595859491104L;
	
	private Template	mTemplate = null;
	private String		mRendererClassname = null;
	
	public RendererWrongTypeException(Template template, String rendererClassname)
	{
		super("The renderer '"+rendererClassname+"' of template '"+template.getName()+"' doesn't implement '"+ValueRenderer.class.getName()+"'.", null);
		
		mTemplate = template;
		mRendererClassname = rendererClassname;
	}
	
	public String getRendererClassname()
	{
		return mRendererClassname;
	}
	
	public Template getTemplate()
	{
		return mTemplate;
	}
}
