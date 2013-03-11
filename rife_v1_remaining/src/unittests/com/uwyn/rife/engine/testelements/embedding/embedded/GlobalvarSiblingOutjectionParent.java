/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalvarSiblingOutjectionParent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;

public class GlobalvarSiblingOutjectionParent extends Element
{
	public String getGlobalvar1()
	{
		return "inheritancevalue";
	}

	public void processElement()
	{
		print(getElementInfo().getId()+": ");
	}

	public boolean childTriggered(String name, String[] values)
	{
		return true;
	}
}

