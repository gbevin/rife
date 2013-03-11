/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CrudTemplateFactory.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud;

import com.uwyn.rife.crud.templates.AdminTemplateTransformer;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.Base64;
import com.uwyn.rife.tools.StringUtils;

import java.io.UnsupportedEncodingException;

public class CrudTemplateFactory extends TemplateFactory
{
	public final static String FACTORY_IDENTIFIER_PREFIX = "crud_";
	public final static String CRUD_SEPARATOR = "__crud__";

	public final static CrudTemplateFactory	CRUD_ENGINEHTML;
	public final static CrudTemplateFactory	CRUD_ENGINEXHTML;
	public final static CrudTemplateFactory	CRUD_ENGINEXML;
	public final static CrudTemplateFactory	CRUD_ENGINETXT;

	private final TemplateFactory mBase;
	
	static
	{
		TemplateFinder resource_finder = new TemplateFinder();
		CRUD_ENGINEHTML = new CrudTemplateFactory(TemplateFactory.ENGINEHTML);
		CRUD_ENGINEHTML.setResourceFinder(resource_finder);
		CRUD_ENGINEXHTML = new CrudTemplateFactory(TemplateFactory.ENGINEXHTML);
		CRUD_ENGINEXHTML.setResourceFinder(resource_finder);
		CRUD_ENGINEXML = new CrudTemplateFactory(TemplateFactory.ENGINEXML);
		CRUD_ENGINEXML.setResourceFinder(resource_finder);
		CRUD_ENGINETXT = new CrudTemplateFactory(TemplateFactory.ENGINETXT);
		CRUD_ENGINETXT.setResourceFinder(resource_finder);
	}

	public CrudTemplateFactory(TemplateFactory base)
	{
		super(FACTORY_IDENTIFIER_PREFIX + base.getIdentifier(), base);
		mBase = base;
	}
	
	public static CrudTemplateFactory getCrudFactory(String identifier)
	{
		return (CrudTemplateFactory)getFactory(FACTORY_IDENTIFIER_PREFIX+identifier);
	}

	public TemplateFactory getBase()
	{
		return mBase;
	}

	public Template get(AdminTemplateTransformer transformer)
	{
		StringBuffer template_name = new StringBuffer();
		synchronized (template_name)	// thread lock pre-allocation
		{
			String implementation = null;
			if (transformer.getImplementation() != null)
			{
				try
				{
					implementation = Base64.encodeToString(transformer.getImplementation().getBytes("UTF-8"), false);
					implementation = StringUtils.replace(implementation, "=", "_");
				}
				catch (UnsupportedEncodingException e)
				{
					// not possible, UTF-8 is always supported
				}
			}
			if (implementation != null)
			{
				template_name.append(implementation);
			}
			String differentiator = transformer.getTemplateNameDifferentiator();
			if (differentiator != null)
			{
				template_name.append(differentiator);
			}
			if (template_name.length() > 0)
			{
				template_name.append(CRUD_SEPARATOR);
			}
			template_name.append(transformer.getSupportedTemplateName());

			return get(template_name.toString(), transformer);
		}
	}
}