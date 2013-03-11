/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TryCatch.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class TryCatch extends Element
{
	public void processElement()
	{
		Template template = getHtmlTemplate("engine_continuation_trycatch");
		try
		{
			template.setValue("title", "start");
			print(template);
			pause();

			boolean do_throw = getParameterBoolean("throw", false);
			if (do_throw)
			{
				throw new RuntimeException(" : throw done");
			}
			
			template.appendValue("title", " : throw not done");
			print(template);
			pause();
		}
		catch (RuntimeException e)
		{
			template.appendValue("title", e.getMessage());
			template.appendValue("title", " catch");
			print(template);
			pause();
		}
		finally
		{
			template.appendValue("title", " : finally done");
			print(template);
			pause();
		}
		
		template.appendValue("title", " : all done");
		print(template);
	}
}
