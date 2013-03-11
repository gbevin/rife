/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowlinkSpecificSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;

public class FlowlinkSpecificSource extends Element
{
	public void processElement()
	{
		setOutput("output1", "output1 value");
		setOutput("output2", "output2 value");
		setOutput("output3", "output3 value");
		
		if (getInput("type", "").equals("directlink"))
		{
			print("<html><body>");
			print("<a href=\""+getExitQueryUrl(getInput("exitselector"))+"\">link</a>");
			print("</body></html>");
		}
		else
		{
			exit(getInput("exitselector"));
		}
	}
}

