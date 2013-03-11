/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExplicitEvaluation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.roleuser;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class ExplicitEvaluation extends Element
{
	public void processElement()
	{
		Template template = getHtmlTemplate(getPropertyString("template_name"));
		template.setExpressionVar("login", "this will not be set");
		template.setExpressionVar("roleadmin", getInput("roleadmin", ""));
		evaluateExpressionRoleUserTags(template, getInput("evaluate"));
		print(template.getContent());
	}
}

