/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: HomeTemplateTransformer.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.templates;

import com.uwyn.rife.crud.CrudPropertyNames;
import com.uwyn.rife.crud.elements.admin.CrudElement;
import com.uwyn.rife.template.Template;

public class HomeTemplateTransformer extends AdminTemplateTransformer
{
	public HomeTemplateTransformer(CrudElement element)
	{
		super(element);
	}
	
	public String getSupportedTemplateName()
	{
		return mElement.getPropertyString(CrudPropertyNames.TEMPLATE_NAME_HOME, buildGroupedTemplateName("home"));
	}

	public void transformTemplate(Template t)
	{
	}
}

