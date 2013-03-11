/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SuccessiveParent1Exit.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.Element;

public class SuccessiveParent1Exit extends Element
{
	public void processElement()
	{
		if (hasSubmission("activatechildparent1exit"))
		{
			if (getParameter("activationparent1exit").equals("go to child"))
			{
				setOutput("triggerparent1exit", "ok");
			}
		}

		print("<html><body>\n");
		print("<form name=\"formparent1exit\" action=\""+getSubmissionFormUrl()+"\" method=\"post\">\n");
		print(getSubmissionFormParameters("activatechildparent1exit"));
		print("<input name=\"activationparent1exit\" type=\"text\">\n");
		print("<input type=\"submit\">\n");
		print("</form>\n");
		print("</body></html>\n");
	}
	
	public boolean childTriggered(String name, String[] values)
	{
		if (name.equals("triggerparent1exit") &&
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

