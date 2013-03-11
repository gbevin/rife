/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractLogoutTemplate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.engine.exceptions.PropertyRequiredException;
import com.uwyn.rife.engine.exceptions.UnsupportedTemplateTypeException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;

public abstract class AbstractLogoutTemplate extends AbstractLogout
{
	protected String	mTemplateName = null;
	
	protected AbstractLogoutTemplate()
	{
	}
	
	protected void setTemplateName(String name)
	{
		mTemplateName = name;
	}
	
	protected void init()
	{
	}
	
	protected void entrance(Template template)
	{
	}

	protected void loggedOut(Template template)
	{
	}
	
	public void processElement()
	{
		assert mSessionManager != null;
		
		init();
		
		if (!hasProperty("template_name") &&
			null == mTemplateName)
		{
			throw new PropertyRequiredException(getDeclarationName(), "template_name");
		}
		if (!hasProperty("authvar_name"))
		{
			throw new PropertyRequiredException(getDeclarationName(), "authvar_name");
		}
		
		// obtain the optional template_type property
		String template_type = null;
		if (hasProperty("template_type"))
		{
			template_type = getPropertyString("template_type");
		}
		else
		{
			template_type = "enginehtml";
		}
		
		// obtain the optional template_encoding property
		String template_encoding = null;
		if (hasProperty("template_encoding"))
		{
			template_encoding = getPropertyString("template_encoding");
		}

		// obtain the mandatory template_name property
		String template_name = null;
		if (mTemplateName != null)
		{
			template_name = mTemplateName;
		}
		else
		{
			template_name = getPropertyString("template_name");
		}
		
		TemplateFactory template_factory = null;
		Template		template = null;
		
		template_factory = TemplateFactory.getFactory(template_type);
		if (null == template_factory)
		{
			throw new UnsupportedTemplateTypeException(template_type);
		}
		template = template_factory.get(template_name, template_encoding, null);
		
		entrance(template);
		
		performLogout();
		
		loggedOut(template);
		
		print(template);
	}
}
