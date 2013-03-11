/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: NormalOutjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.outputs;

import com.uwyn.rife.engine.Element;

public class NormalOutjection extends Element
{
	public long getOutput1()
	{
		return 11124;
	}

	public String getOutput2()
	{
		return "value2";
	}

	public String[] getOutput3()
	{
		return new String[] {"value3a", "value3b", "value3c"};
	}

	public int[] getOutput4()
	{
		return new int[] {870, 411, 419};
	}

	public String getOutput5()
	{
		return "value5";
	}

	public void processElement()
	{
		setOutput("output5", "programmatic value5");
		print(getHtmlTemplate("engine_outputs_normal_outjection"));
	}
}
