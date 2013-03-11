/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParampassParent3.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.Element;

public class ParampassParent3 extends Element
{
	public void processElement()
	{
		if (hasSubmission("activatechildparent3"))
		{
			if (getParameter("activationparent3").equals("go to child"))
			{
				setOutput("globalvar1", "stage1a");
				setOutput("globalvar2", "stage2a");
				setOutput("trigger", "ok");
			}
		}
		
		print("<html><body>\n");
		print("<form name=\"formparent3\" action=\""+getSubmissionFormUrl()+"\" method=\"post\">\n");
		print(getSubmissionFormParameters("activatechildparent3"));
		print("<input name=\"activationparent3\" type=\"text\">\n");
		print("<input type=\"submit\">\n");
		print("</form>\n");
		print("</body></html>\n");
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

