/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: VarsInputsSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;

import com.uwyn.rife.engine.Element;

public class VarsInputsSource extends Element
{
	public void processElement()
	{
		setOutput("output1", new String [] {"output "+getInput("input1"), "outit"});
		setOutput("output2", "output "+getInput("input2"));
		exit("exit");
	}
}

