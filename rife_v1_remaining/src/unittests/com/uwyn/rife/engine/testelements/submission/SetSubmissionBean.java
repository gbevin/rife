/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SetSubmissionBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.HtmlBeanImpl;
import com.uwyn.rife.template.Template;

public class SetSubmissionBean extends Element
{
	public void processElement()
	{
		Template		template = getHtmlTemplate("engine_params_generation");
		HtmlBeanImpl	bean = new HtmlBeanImpl();
		
		if (hasInputValue("populated"))
		{
			bean.setWantsupdates(true);
			bean.setColors(new String[] {"orange", "red", "white"});
			bean.setFirstname("Geert");
			bean.setLastname("Bevin");
		}
		setSubmissionBean(template, bean);
		print(template);
	}
}

