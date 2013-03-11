/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Home.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.elements.admin;

import com.uwyn.rife.crud.templates.AdminTemplateTransformer;
import com.uwyn.rife.crud.templates.HomeTemplateTransformer;
import com.uwyn.rife.template.Template;

public class Home extends CrudElement
{
	public AdminTemplateTransformer getTransformer()
	{
		return new HomeTemplateTransformer(this);
	}

	public void processElement()
	{
		Template t = getTemplate();
		
		print(t);
	}
}

