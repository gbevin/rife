/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExplicitProcessing.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class ExplicitProcessing extends Element
{
	public void processElement()
	{
		Template template = getHtmlTemplate("engine_embedding_explicitprocessing");
		
		setExitQuery(template, "early", new String[] {"late", "no"});
		setExitQuery(template, "late", new String[] {"late", "yes"});
		
		if (hasInputValue("late"))
		{
			setOutput("var2", "value2");
		
			if ("yes".equals(getInput("late")))
			{
				setExitQuery(template, "result");
			}
			else
			{
				processEmbeddedElement(template, ".EXPLICIT_PROCESSING_EMBEDDED");
				setExitQuery(template, "result");
			}
		}
		
		print(template);
	}
}

