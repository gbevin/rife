/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParampassParent2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.Element;

public class ParampassParent2 extends Element
{
	public void processElement()
	{
		if (getInput("globalvar1").equals("stage1a") &&
			getInput("globalvar2").equals("stage2a"))
		{
			if (hasSubmission("activatechildparent2"))
			{
				if (getParameter("activationparent2").equals("go to child"))
				{
					setOutput("globalvar1", "stage1b");
					setOutput("globalvar2", "stage2b");
					setOutput("trigger", "ok");
				}
			}
			
			print("<html><body>\n");
			print("<form name=\"formparent2\" action=\""+getSubmissionFormUrl()+"\" method=\"post\">\n");
			print(getSubmissionFormParameters("activatechildparent2"));
			print("<input name=\"activationparent2\" type=\"text\">\n");
			print("<input type=\"submit\">\n");
			print("</form>\n");
			print("</body></html>\n");
		}
	}
	
	public boolean childTriggered(String name, String[] values)
	{
		if (name.equals("trigger") &&
			values[0].equals("ok"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}

