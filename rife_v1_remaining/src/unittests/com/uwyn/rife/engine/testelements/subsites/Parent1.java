/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Parent1.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites;

import com.uwyn.rife.engine.Element;

public class Parent1 extends Element
{
	public void processElement()
	{
		print("parent1");
	}
	
	public boolean childTriggered(String name, String[] values)
	{
		if (name.equals("globalvar1") &&
			!values[0].equals("default value1"))
		{
			return true;
		}
		return false;
	}
}

