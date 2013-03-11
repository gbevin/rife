/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitDataflowExit.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.Element;

public class ExitDataflowExit extends Element
{
	public void processElement()
	{
		if (getInput("input1").equals("the first data") &&
			getInput("input2").equals("some more data") &&
			getInput("input3").equals("the last data"))
		{
			setOutput("trigger", "ok");
		}
		else
		{
			print("child not triggered");
		}
	}
	
	public boolean childTriggered(String name, String[] values)
	{
		if (name.equals("trigger") &&
			values[0].equals("ok"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}

