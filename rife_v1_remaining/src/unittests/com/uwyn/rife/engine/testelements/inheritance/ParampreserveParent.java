/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParampreserveParent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.Element;

public class ParampreserveParent extends Element
{
	public void processElement()
	{
		if (hasSubmission("activatechild"))
		{
			if (getParameter("param1").equals("go to child"))
			{
				setOutput("trigger", new String[] {"?ok?", "!ok!"});
			}
		}
		
		print("<html><body>\n");
		print("<form action=\""+getSubmissionFormUrl()+"\" method=\"post\">\n");
		print(getSubmissionFormParameters("activatechild"));
		print("<input name=\"param1\" type=\"text\">\n");
		print("<input type=\"submit\">\n");
		print("</form>\n");
		print("</body></html>\n");
	}
	
	public boolean childTriggered(String name, String[] values)
	{
		if (name.equals("trigger") &&
			values[0].equals("?ok?") &&
			values[1].equals("!ok!"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}

