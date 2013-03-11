/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitDataflowParent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.Element;

public class ExitDataflowParent extends Element
{
	public void processElement()
	{
		if (getInput("input1").equals("validoutputs"))
		{
			setOutput("output1", "the first data");
			setOutput("output2", "some more data");
			setOutput("output3", "the last data");
		}
		else
		{
			setOutput("output1", "invalid data");
			setOutput("output2", "invalid data");
			setOutput("output3", "invalid data");
		}
		exit("exit1");
	}
}

