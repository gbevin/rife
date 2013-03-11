/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Source.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites.departure;

import com.uwyn.rife.engine.Element;

public class Source extends Element
{
	public void processElement()
	{
		setOutput("globalvar4", "source value1");
		setOutput("globalvar_departure1", "source value2");
		setOutput("globalvar_departure2", "source value3");
		setOutput("globalvar_departure3", "source value4");
		setOutput("output1", "set by source");
		setOutput("output2", getInput("globalvar_departure1"));
		exit("target");
	}
}

