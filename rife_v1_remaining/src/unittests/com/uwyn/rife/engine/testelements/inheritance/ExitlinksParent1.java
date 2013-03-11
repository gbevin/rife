/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitlinksParent1.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.Element;

public class ExitlinksParent1 extends Element
{
	public void processElement()
	{
		setOutput("output1", "a message for you");
		print("<html><body>\n");
		print("<a href=\""+getExitQueryUrl("exit1")+"\">direct link</a>");
		print("</body></html>\n");
		
		setOutput("output1", "show form");
		print("<html><body>\n");
		print("<a href=\""+getExitQueryUrl("exit1")+"\">direct link with form</a>");
		print("</body></html>\n");
	}
}

