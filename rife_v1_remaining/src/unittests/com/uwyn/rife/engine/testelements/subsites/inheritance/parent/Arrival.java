/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Arrival.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites.inheritance.parent;

import com.uwyn.rife.engine.Element;

public class Arrival extends Element
{
	public void processElement()
	{
		print("parent subsite arrival");
	}
	
	public boolean childTriggered(String name, String[] values)
	{
		if (name.equals("parent_subsite_trigger"))
		{
			return true;
		}
		return false;
	}
}

