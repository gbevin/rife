/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PrintTemplate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.elements;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.annotations.Elem;
import com.uwyn.rife.engine.exceptions.PropertyRequiredException;
import com.uwyn.rife.engine.exceptions.UnsupportedTemplateTypeException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;

@Elem
public class PrintTemplate extends Element
{
	public Template getTemplate()
	{
		// obtain the optional template_type property
		String template_type = null;
		if (hasProperty("type"))
		{
			template_type = getPropertyString("type");
		}
		// for backwards compatibility
		else if (hasProperty("template_type"))
		{
			template_type = getPropertyString("template_type");
		}
		else
		{
			template_type = "enginehtml";
		}
		
		// obtain the optional template_encoding property
		String template_encoding = null;
		if (hasProperty("encoding"))
		{
			template_encoding = getPropertyString("encoding");
		}
		// for backwards compatibility
		else if (hasProperty("template_encoding"))
		{
			template_encoding = getPropertyString("template_encoding");
		}
		
		// obtain the mandatory template_name property
		String template_name = null;
		if (hasProperty("name"))
		{
			template_name = getPropertyString("name");
		}
		// for backwards compatibility
		else if (hasProperty("template_name"))
		{
			template_name = getPropertyString("template_name");
		}
		else
		{
			throw new PropertyRequiredException(getDeclarationName(), "name");
		}
		
		// get a template instance and print it
		TemplateFactory template_factory = null;
		Template		template = null;
		
		template_factory = TemplateFactory.getFactory(template_type);
		if (null == template_factory)
		{
			throw new UnsupportedTemplateTypeException(template_type);
		}
		template = template_factory.get(template_name, template_encoding, null);
		
		return template;
	}
	
	public void processElement()
	{
		print(getTemplate());
	}
}

