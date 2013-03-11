/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TargetChildMiddleParent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inputs;

import com.uwyn.rife.engine.Element;

public class TargetChildMiddleParent extends Element
{
	public void processElement()
	{
		if (!hasInputValue("middleparentinput"))
		{
			setOutput("overridden", "middleparentvalue");
			setOutput("aninput", "middleparentvalue");
			child();
		}
		print(getInput("globalvar"));
		print(",");
		print(getInput("middleparentinput"));
		print(",");
		print(getInput("overridden"));
		print(",");
		print(getInput("aninput"));
	}
}

