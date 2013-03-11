/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RendererNotFoundException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template.exceptions;

import com.uwyn.rife.template.Template;

public class RendererNotFoundException extends TemplateException
{
	private static final long serialVersionUID = 8271213893937731347L;
	
	private Template	mTemplate = null;
	private String		mRendererClassname = null;
	
	public RendererNotFoundException(Template template, String rendererClassname, Throwable cause)
	{
		super("Couldn't find the renderer '"+rendererClassname+"' of template '"+template.getName()+"'.", cause);
		
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
