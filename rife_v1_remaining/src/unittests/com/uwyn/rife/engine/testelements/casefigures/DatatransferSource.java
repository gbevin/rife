/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatatransferSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.casefigures;

import com.uwyn.rife.engine.Element;

public class DatatransferSource extends Element
{
	public void processElement()
	{
		setOutput("output1", "value1");
		setOutput("output2", new String[] {"value2a", "value2b", "value2c"});
		setOutput("output3", "value3");
		setOutput("output4", new String[] {"value4a", "value4b"});
		
		if (getInput("switch").equals("1"))
		{
			exit("exit1");
		}
		if (getInput("switch").equals("2"))
		{
			exit("exit2");
		}
		
		print("this shouldn't print anything");
	}
}

