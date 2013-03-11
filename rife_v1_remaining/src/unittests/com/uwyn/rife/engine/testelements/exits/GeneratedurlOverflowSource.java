/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GeneratedurlOverflowSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.tools.StringUtils;

public class GeneratedurlOverflowSource extends Element
{
	public final static String EXIT_NAME1 = "exit1";
	public final static String EXIT_NAME2 = "exit2";

	public void processElement()
	{
		Template template = getHtmlTemplate("engine_exiturl_generation");
		
		setOutput("output1", "the first");
		setOutput("output2", StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", 76)+"012345678");
		
		setExitQuery(template, "exit1");
		
		print(template);
	}
}

