/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EmbedderInputs.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;

public class EmbedderInputs extends Element
{
	public void processElement()
	{
		if (hasSubmission("submission"))
		{
			print("embedded ");
			print(getParameter("var6", "no var6"));
			print(",");
			
			print("submitted");
		}
		else
		{
			setOutput("var3", "embedded value 3");
			setOutput("var4", "embedded value 4");
			setOutput("var5", "embedded value 5");
			print("<form action=\""+getSubmissionFormUrl()+"\">"+getSubmissionFormParameters("submission")+"<input type=\"submit\" /></form>");
		}
	}
}

