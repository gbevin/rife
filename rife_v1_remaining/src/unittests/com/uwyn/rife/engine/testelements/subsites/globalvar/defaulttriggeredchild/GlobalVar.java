/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalVar.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites.globalvar.defaulttriggeredchild;

import com.uwyn.rife.engine.Element;

public class GlobalVar extends Element
{
	public void processElement()
	{
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
	}
}

