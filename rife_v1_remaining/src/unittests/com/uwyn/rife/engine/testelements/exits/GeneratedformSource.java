/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GeneratedformSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class GeneratedformSource extends Element
{
	public final static String EXIT_NAME1 = "exit1";
	public final static String EXIT_NAME2 = "exit2";

	public void processElement()
	{
		if (getInputBoolean("setoutputs"))
		{
			setOutput("output1", "another first");
			setOutput("output2", "another second");
			setOutput("output3", "another third");
		}
		
		Template template = getHtmlTemplate("engine_exitform_generation");
		
		print(template);
	}
}

