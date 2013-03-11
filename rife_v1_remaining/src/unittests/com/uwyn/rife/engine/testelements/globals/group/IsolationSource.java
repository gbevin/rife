/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IsolationSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals.group;

import com.uwyn.rife.engine.Element;

public class IsolationSource extends Element
{
	public void processElement()
	{
		setOutput("globalvar1", "value1");
		setOutput("globalvar2", "value2");
		setOutput("globalvar5", "value5");
		
		if (getInput("switch").equals("1"))
		{
			exit("exit1");
		}
		else if (getInput("switch").equals("2"))
		{
			exit("exit2");
		}
	}
}

