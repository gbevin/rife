/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Source.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;

import com.uwyn.rife.engine.Element;

public class Source extends Element
{
	public void processElement()
	{
		setOutput("globalvar1", "value1");
		setOutput("globalvar2", new String[] {"value2a", "value2b", "value2c"});
		
		if (hasInputValue("globalvar1") &&
			getInput("globalvar1").equals("reflective"))
		{
			print("reflected"+getInput("reflected")+getInput("reflected_overridden")+getInput("not_reflected"));
			return;
		}
		
		if (getInput("switch").equals("1"))
		{
			exit("exit1");
		}
		else if (getInput("switch").equals("2"))
		{
			exit("exit2");
		}
		else if (getInput("switch").equals("3"))
		{
			exit("globalexit1");
		}
		else if (getInput("switch").equals("4"))
		{
			setOutput("globalvar1", "reflective");
			setOutput("reflected_overridden", "reflected_value_overridden");
			exit("globalexit2");
		}
	}
}

