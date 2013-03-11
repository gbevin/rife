/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Throw.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class Throw extends Element
{
	public void processElement()
	{
		Template template = getHtmlTemplate("engine_continuation_throw");
		
		print(template);
		pause();
		
		boolean do_throw = getParameterBoolean("throw", false);
		
		template.setValue("title", "do throw = "+do_throw);

		try
		{
			if (do_throw)
			{
				throw new Exception(" : throw message");
			}
		}
		catch (Exception e)
		{
			template.appendValue("title", e.getMessage());
		}
		finally
		{
			template.appendValue("title", " : finally message");
		}
		
		print(template);
		pause();
		
		template.appendValue("title", " : all done");
		print(template);
	}
}

