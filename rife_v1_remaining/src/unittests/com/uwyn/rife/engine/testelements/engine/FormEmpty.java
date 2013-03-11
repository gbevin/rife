/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FormEmpty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.site.ConstrainedBeanImpl;
import com.uwyn.rife.template.Template;

public class FormEmpty extends Element
{
	public void processElement()
	{
		if (getInputBoolean("prefix"))
		{
			Template template = getHtmlTemplate("formbuilder_form_prefix");
			
			generateEmptyForm(template, ConstrainedBeanImpl.class, "prefix_");
			
			if (getInputBoolean("remove"))
			{
				removeForm(template, ConstrainedBeanImpl.class, "prefix_");
			}
			
			print(template.getContent());
		}
		else
		{
			Template template = getHtmlTemplate("formbuilder_fields");
			
			generateEmptyForm(template, ConstrainedBeanImpl.class);
			
			if (getInputBoolean("remove"))
			{
				removeForm(template, ConstrainedBeanImpl.class);
			}
			
			print(template.getContent());
		}
	}
}

