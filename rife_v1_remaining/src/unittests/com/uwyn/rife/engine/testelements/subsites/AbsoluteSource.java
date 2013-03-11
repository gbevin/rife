/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbsoluteSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites;

import com.uwyn.rife.engine.Element;

public class AbsoluteSource extends Element
{
	public void processElement()
	{
		setOutput("globalvar1", "value1absolute");
		setOutput("globalvar3", "value3absolute");
		exit("exit1");
	}
}

