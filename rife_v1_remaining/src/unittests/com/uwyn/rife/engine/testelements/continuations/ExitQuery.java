/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitQuery.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class ExitQuery extends Element
{
	public void processElement()
	{
		Template template = getHtmlTemplate("engine_continuation_exit_query");
		
		int total = 0;
		while (total < 50)
		{
			template.setValue("subtotal", total);
			print(template);
			pause();
			total += getInputInt("answer", 0);
		}
		
		print("got a total of "+total);
	}
}

