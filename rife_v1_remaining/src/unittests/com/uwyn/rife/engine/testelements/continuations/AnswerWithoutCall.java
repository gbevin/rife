/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AnswerWithoutCall.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class AnswerWithoutCall extends Element
{
	public void processElement()
	{
		String		global1 = getInput("global1");
		String[]	global2 = getInputValues("global2");
		String		global3 = getInput("global3");
		
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
		
		answer();
	}
}

