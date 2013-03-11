/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: IsolationDestination.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals.group;

import com.uwyn.rife.engine.Element;

public class IsolationDestination extends Element
{
	public void processElement()
	{
		print(getInput("globalvar1", "null")+"|"+getInput("globalvar2", "null")+"|"+getInput("globalvar5", "null"));
	}
}

