/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Form.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.engine;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.site.ConstrainedBeanImpl;
import com.uwyn.rife.template.Template;

public class Form extends Element
{
	public void processElement()
	{
		ConstrainedBeanImpl bean = new ConstrainedBeanImpl();
		bean.setHidden("canyouseeme");
		bean.setAnotherhidden("I can't see you");
		bean.setLogin("ikke");
		bean.setAnotherlogin("jullie");
		bean.setPassword("secret");
		bean.setAnotherpassword("real secret");
		bean.setComment("één comment");
		bean.setAnothercomment("this comment");
		bean.setQuestion(ConstrainedBeanImpl.Question.a2);
		bean.setAnotherquestion("a3");
		bean.setCustomquestion("a1");
		bean.setAnothercustomquestion("a2");
		bean.setOptions(new int[] {2});
		bean.setOtheroptions(new int[] {2, 0});
		bean.setCustomoptions(new int[] {1});
		bean.setOthercustomoptions(new int[] {2});
		bean.setInvoice(true);
		bean.setOnemoreinvoice(false);
		bean.setColors(new String[] {"red", "green"});
		bean.setMorecolors(new String[] {"black"});
		
		if (getInputBoolean("prefix"))
		{
			bean.setYourcolors(new String[] {"orange", "brown"});
			
			Template template = getHtmlTemplate("formbuilder_form_prefix");
			
			generateForm(template, bean, "prefix_");
			
			if (getInputBoolean("remove"))
			{
				removeForm(template, ConstrainedBeanImpl.class, "prefix_");
			}
			
			print(template.getContent());
		}
		else
		{
			bean.setYourcolors(new String[] {"brown"});
			
			Template template = getHtmlTemplate("formbuilder_fields");
			
			generateForm(template, bean);
			
			if (getInputBoolean("remove"))
			{
				removeForm(template, ConstrainedBeanImpl.class);
			}
			
			print(template.getContent());
		}
	}
}

