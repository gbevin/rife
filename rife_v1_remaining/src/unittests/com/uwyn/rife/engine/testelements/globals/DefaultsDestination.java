/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DefaultsDestination.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class DefaultsDestination extends Element
{
	public void processElement()
	{
		String		input1 = getInput("input1");
		String[]	input2 = getInputValues("input2");
		String		input3 = getInput("input3");
		
		if (input1 != null)
		{
			print(input1);
		}
		if (input2 != null)
		{
			print(StringUtils.join(input2, "-"));
		}
		if (input3 != null)
		{
			print(input3);
		}

		String		global1 = getInput("global1");
		String[]	global2 = getInputValues("global2");
		String		global3 = getInput("global3");
		String		global4 = getInput("global4");
		
		if (global1 != null)
		{
			print(global1);
		}
		if (global2 != null)
		{
			print(StringUtils.join(global2, "-"));
		}
		if (global3 != null)
		{
			print(global3);
		}
		if (global4 != null)
		{
			print(global4);
		}
	}
}

