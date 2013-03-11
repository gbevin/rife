/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Parent1.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites.inheritance;

import com.uwyn.rife.engine.Element;

public class Parent1 extends Element
{
	public void processElement()
	{
		print("inheritance parent 1");
	}
	
	public boolean childTriggered(String name, String[] values)
	{
		if (name.equals("globalvar_inheritance1"))
		{
			return true;
		}
		return false;
	}
}

