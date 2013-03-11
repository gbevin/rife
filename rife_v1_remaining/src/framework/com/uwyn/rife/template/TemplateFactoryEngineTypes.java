/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateFactoryEngineTypes.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.engine.ElementContext;
import com.uwyn.rife.engine.EngineTemplateInitializer;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.site.ValidationBuilder;

import static com.uwyn.rife.template.TemplateFactory.*;

public class TemplateFactoryEngineTypes
{
	public static TemplateFactory	ENGINEHTML = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"enginehtml", CONFIGS_XML, "text/html", ".html",
		new String[]
		{
			ValidationBuilder.TAG_ERRORS,
			ValidationBuilder.TAG_ERRORMESSAGE,
			TAG_LANG,
			TAG_OGNL_CONFIG,
			ElementContext.TAG_OGNL_ROLEUSER,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
			ElementContext.TAG_MVEL_ROLEUSER,
		    TAG_MVEL,
			TAG_GROOVY_CONFIG,
			ElementContext.TAG_GROOVY_ROLEUSER,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			ElementContext.TAG_JANINO_ROLEUSER,
			TAG_JANINO
		},
		new String[]
		{
			ValidationBuilder.TAG_MARK,
			ValidationBuilder.TAG_ERRORS,
			ElementContext.TAG_ELEMENT,
		    ElementContext.TAG_PROPERTY,
			ElementContext.TAG_EXITFIELD,
			ElementContext.TAG_SUBMISSIONFIELD,
			TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerXhtml.getInstance(), EncoderHtml.getInstance(),
		new EngineTemplateInitializer());
	
	public static TemplateFactory	ENGINEXHTML = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"enginexhtml", CONFIGS_XML, "text/html", ".xhtml",
		new String[]
		{
			ValidationBuilder.TAG_ERRORS,
			ValidationBuilder.TAG_ERRORMESSAGE,
			TAG_LANG,
			TAG_OGNL_CONFIG,
			ElementContext.TAG_OGNL_ROLEUSER,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
			ElementContext.TAG_MVEL_ROLEUSER,
		    TAG_MVEL,
			TAG_GROOVY_CONFIG,
			ElementContext.TAG_GROOVY_ROLEUSER,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			ElementContext.TAG_JANINO_ROLEUSER,
			TAG_JANINO
		},
		new String[]
		{
			ValidationBuilder.TAG_MARK,
			ValidationBuilder.TAG_ERRORS,
			ElementContext.TAG_ELEMENT,
		    ElementContext.TAG_PROPERTY,
			ElementContext.TAG_EXITFIELD,
			ElementContext.TAG_SUBMISSIONFIELD,
		    TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerXhtml.getInstance(), EncoderHtml.getInstance(),
		new EngineTemplateInitializer());
	
	public static	TemplateFactory	ENGINEXML = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"enginexml", CONFIGS_XML, "application/xml", ".xml",
		new String[]
		{
			ValidationBuilder.TAG_ERRORS,
			ValidationBuilder.TAG_ERRORMESSAGE,
			TAG_LANG,
			TAG_OGNL_CONFIG,
			ElementContext.TAG_OGNL_ROLEUSER,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
			ElementContext.TAG_MVEL_ROLEUSER,
		    TAG_MVEL,
			TAG_GROOVY_CONFIG,
			ElementContext.TAG_GROOVY_ROLEUSER,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			ElementContext.TAG_JANINO_ROLEUSER,
			TAG_JANINO
		},
		new String[]
		{
			ValidationBuilder.TAG_MARK,
			ValidationBuilder.TAG_ERRORS,
			ElementContext.TAG_ELEMENT,
		    ElementContext.TAG_PROPERTY,
			ElementContext.TAG_EXITFIELD,
			ElementContext.TAG_SUBMISSIONFIELD,
			TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerXml.getInstance(), EncoderXml.getInstance(),
		new EngineTemplateInitializer());
	
	public static	TemplateFactory	ENGINETXT = new TemplateFactory(ResourceFinderClasspath.getInstance(),
		"enginetxt", TemplateFactory.CONFIGS_TXT, "text/plain", ".txt",
		new String[]
		{
			TAG_LANG,
			TAG_OGNL_CONFIG,
			ElementContext.TAG_OGNL_ROLEUSER,
			TAG_OGNL,
		    TAG_MVEL_CONFIG,
			ElementContext.TAG_MVEL_ROLEUSER,
		    TAG_MVEL,
			TAG_GROOVY_CONFIG,
			ElementContext.TAG_GROOVY_ROLEUSER,
			TAG_GROOVY,
			TAG_JANINO_CONFIG,
			ElementContext.TAG_JANINO_ROLEUSER,
			TAG_JANINO
		},
		new String[]
		{
			ElementContext.TAG_ELEMENT,
			ElementContext.TAG_PROPERTY,																		
			ElementContext.TAG_EXITFIELD,
			ElementContext.TAG_SUBMISSIONFIELD,
			TAG_CONFIG,
			TAG_L10N,
			TAG_RENDER
		},
		BeanHandlerPlain.getInstance(), null,
		new EngineTemplateInitializer());
}
