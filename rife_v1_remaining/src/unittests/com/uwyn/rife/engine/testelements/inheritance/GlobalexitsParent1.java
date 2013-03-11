/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalexitsParent1.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.Element;

public class GlobalexitsParent1 extends Element
{
	public void processElement()
	{
		if (hasSubmission("activateexitparent1"))
		{
			if (getParameter("activationparent1").equals("go to exit"))
			{
				exit("globalexit1");
			}
		}
		
		print("<html><body>\n");
		print("<form name=\"formparent1\" action=\""+getSubmissionFormUrl()+"\" method=\"post\">\n");
		print(getSubmissionFormParameters("activateexitparent1"));
		print("<input name=\"activationparent1\" type=\"text\">\n");
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

