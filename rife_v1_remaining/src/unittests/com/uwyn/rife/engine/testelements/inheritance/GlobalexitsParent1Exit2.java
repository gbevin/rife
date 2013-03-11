/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalexitsParent1Exit2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.Element;

public class GlobalexitsParent1Exit2 extends Element
{
	public void processElement()
	{
		if (hasSubmission("activateexitparent1exit2"))
		{
			if (getParameter("activationparent1exit2").equals("go to child"))
			{
				setOutput("output1", "ok");
				exit("globalexit3");
			}
		}

		print("<html><body>\n");
		print("<form name=\"formparent1exit2\" action=\""+getSubmissionFormUrl()+"\" method=\"post\">\n");
		print(getSubmissionFormParameters("activateexitparent1exit2"));
		print("<input name=\"activationparent1exit2\" type=\"text\">\n");
		print("<input type=\"submit\">\n");
		print("</form>\n");
		print("</body></html>\n");
	}
}

