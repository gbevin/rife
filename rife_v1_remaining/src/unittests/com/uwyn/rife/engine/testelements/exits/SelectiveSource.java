/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SelectiveSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;

public class SelectiveSource extends Element
{
	public void processElement()
	{
		if (getInput("switch").equals("1"))
		{
			exit("exit1");
		}
		if (getInput("switch").equals("2"))
		{
			exit("exit2");
		}
		if (getInput("switch").equals("3"))
		{
			exit("exit3");
		}
		print("this shouldn't print anything");
	}
}

