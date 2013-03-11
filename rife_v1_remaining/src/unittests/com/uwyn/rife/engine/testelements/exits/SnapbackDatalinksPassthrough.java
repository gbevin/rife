/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SnapbackDatalinksPassthrough.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;

public class SnapbackDatalinksPassthrough extends Element
{
	public void processElement()
	{
		print("this is the output of datalinks snapback passthrough");
		print(""+getInput("param1"));
		setOutput("output1", "passthrough value 1");
		setOutput("output2", "passthrough value 2");
		setOutput("output3", "passthrough value 3");
		exit("exit2");
	}
}

