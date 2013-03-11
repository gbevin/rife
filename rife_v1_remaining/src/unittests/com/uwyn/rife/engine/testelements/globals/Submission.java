/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Submission.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class Submission extends Element
{
	public void processElement()
	{
		if (hasSubmission("submissionglobals"))
		{
			print(getInput("globalvar1"));
			print(",");
			print(StringUtils.join(getInputValues("globalvar2"), "|"));
			print(",");
			print(getParameter("param1"));
		}
		else
		{
			// setting the outputs here will override globalvar input values
			setOutput("globalvar1", "value1");
			setOutput("globalvar2", new String[] {"value2a", "value2b", "value2c"});
			
			print("<form action=\""+getSubmissionFormUrl()+"\" method=\"post\">");
			print(getSubmissionFormParameters("submissionglobals", new String[] {"param1", "one param"}));
			print("</form>");
		}
	}
}

