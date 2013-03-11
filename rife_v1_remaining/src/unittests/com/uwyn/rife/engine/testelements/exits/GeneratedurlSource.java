/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GeneratedurlSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class GeneratedurlSource extends Element
{
	public final static String EXIT_NAME1 = "exit1";
	public final static String EXIT_NAME2 = "exit2";

	public String getOutput2()
	{
		return "the second";
	}
	
	public String getOutput3()
	{
		return "the third value that will be overridden";
	}
	
	public void processElement()
	{
		Template template = getHtmlTemplate("engine_exiturl_generation");
		
		setOutput("output1", "the first");
		setOutput("output3", "the third");
		
		if (getInput("switch", "").equals("overridden"))
		{
			setExitQuery(template, "exit1", new String[] {"output1", "the overridden first", "output3", "the overridden third"});
			setExitQuery(template, "exit2", new String[] {"output1", "the overridden first", "output3", "the overridden third"});
		}
		else
		{
			setExitQuery(template, "exit1");
		}
		
		print(template);
	}
}

