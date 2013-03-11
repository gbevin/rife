/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalDest2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites;

import com.uwyn.rife.engine.Element;

public class GlobalDest2 extends Element
{
	public void processElement()
	{
		print("globaldest2-");
		if (hasInputValue("globalvar1"))
		{
			print(getInput("globalvar1"));
		}
		print("|");
		if (hasInputValue("globalvar2"))
		{
			print(getInput("globalvar2"));
		}
		print("|");
		if (hasInputValue("globalvar3"))
		{
			print(getInput("globalvar3"));
		}
		print("-");
		if (hasInputValue("destinput1"))
		{
			print(getInput("destinput1"));
		}
		print("|");
		if (hasInputValue("destinput2"))
		{
			print(getInput("destinput2"));
		}
		print("|");
		if (hasInputValue("destinput3"))
		{
			print(getInput("destinput3"));
		}
	}
}

