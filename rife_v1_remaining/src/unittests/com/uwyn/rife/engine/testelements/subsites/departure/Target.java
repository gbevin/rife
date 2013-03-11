/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Target.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites.departure;

import com.uwyn.rife.engine.Element;

public class Target extends Element
{
	public void processElement()
	{
		print(""+getInput("globalvar1"));
		print(""+getInput("globalvar2"));
		print(""+getInput("globalvar3"));
		print(""+getInput("globalvar4"));
		print(""+getInput("globalvar_departure1"));
		print(""+getInput("globalvar_departure2"));
		print(""+getInput("globalvar_departure3"));
		print(""+getInput("input1"));
		print(""+getInput("input2"));
		print(""+getInput("input3"));
	}
}

