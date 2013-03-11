/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Normal.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inputs;

import com.uwyn.rife.engine.Element;

public class Normal extends Element
{
	public void processElement()
	{
		String input1 = getInput("input1");
		String input2 = getInput("input2");
		String input3 = getInput("input3");
		
		print("another response");
		
		if (input1 != null)
		{
			print(input1);
		}
		if (input2 != null)
		{
			print(input2);
		}
		if (input3 != null)
		{
			print(input3);
		}
	}
}

