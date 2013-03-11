/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineTemplateInitializer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateInitializer;

public class EngineTemplateInitializer implements TemplateInitializer
{
	public void initialize(Template template)
	{
		// Obtain the element that's active in the current running thread and
		// obtain its context. It's not possible to store the element context
		// in the initializer since that wouldn't work with continuations.
		ElementSupport element = ElementContext.getActiveElementSupport();
		if (null == element)
		{
			return;
		}

		// check for a valid element context
		ElementContext context = element._getElementContext();
		if (null == context)
		{
			return;
		}
		
		// set an element expression var
		template.setExpressionVar("element", element);

		// process the early embedded elements
		EngineTemplateHelper.processEmbeddedElementsEarly(context, template, element);
	}
}

