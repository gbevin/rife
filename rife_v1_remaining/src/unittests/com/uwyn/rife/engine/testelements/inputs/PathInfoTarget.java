/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PathInfoTarget.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inputs;

import com.uwyn.rife.engine.Element;

public class PathInfoTarget extends Element
{
	public void processElement()
	{
		print(getPathInfo()+"\n");
		print(getInput("exitinput1"));
		print(","+getInput("exitinput2"));
		print(","+getInput("exitinput3"));
		print(","+getInput("exitinput4"));
		print(","+getInput("exitinput5"));
	}
}

