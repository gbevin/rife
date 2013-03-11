/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Conditional.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class Conditional extends Element
{
	public void processElement()
	{
		boolean stop = getParameterBoolean("stop", false);
		boolean answer = getParameterBoolean("answer", false);
		
		Template template = getHtmlTemplate("engine_continuation_conditional");
		if (stop)
		{
			template.setValue("title", "stopping");
			print(template);
			return;
		}
		
		if (answer)
		{
			template.setValue("title", "pauzing");
			print(template);
			pause();
		}
		
		template.appendValue("title", "printing");
		print(template);
	}
}

