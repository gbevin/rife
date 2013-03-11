/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Normal.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.outputs;

import com.uwyn.rife.engine.Element;

public class Normal extends Element
{
	public void processElement()
	{
		setOutput("output1", "value1");
		setOutput("output2", "value2");
		setOutput("output3", new String[] {"value3a", "value3b", "value3c"});
		
		print("the response");
	}
}

