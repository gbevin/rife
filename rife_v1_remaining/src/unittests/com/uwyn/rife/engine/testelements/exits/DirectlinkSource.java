/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DirectlinkSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;

public class DirectlinkSource extends Element
{
	public void processElement()
	{
		setOutput("output1", "this is");
		setOutput("output2", "great");
		setOutput("output3", "this is not linked");
		print("<html><body>");
		print("<a href=\""+getExitQueryUrl("exit1")+"\">direct link</a>");
		print("</body></html>");
	}
}

