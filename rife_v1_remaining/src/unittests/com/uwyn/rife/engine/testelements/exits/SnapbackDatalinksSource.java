/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SnapbackDatalinksSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;

public class SnapbackDatalinksSource extends Element
{
	public void processElement()
	{
		if (hasSubmission("activate_exit"))
		{
			setOutput("param1", getParameter("param1"));
			exit("exit1");
		}
		
		print("the content of datalinks snapback source");
		print(""+getInput("input1"));
		print(""+getInput("input2"));
	}
}

