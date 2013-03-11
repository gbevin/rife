/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Globalvar.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;

public class Globalvar extends Element
{
	public void processElement()
	{
		if (hasSubmission("submission"))
		{
			setOutput("var1", "embedded value 1");
			clearOutput("var2");
			setOutput("var4", "embedded value 4");
			setOutput("var5", "embedded value 5");
			print("submitted");
		}
		else
		{
			print("<form action=\""+getSubmissionFormUrl()+"\">"+getSubmissionFormParameters("submission")+"<input type=\"submit\" /></form>");
		}
	}
}

