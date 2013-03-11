/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SendGetHidden.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;

public class SendGetHidden extends Element
{
	public void processElement()
	{
		if (hasSubmission("login"))
		{
			print(getParameter("login")+","+getParameter("password"));
		}
		else
		{
			print("<html><body>\n");
			print("<form action=\""+getSubmissionFormUrl()+"\" method=\"get\">\n");
			print(getSubmissionFormParameters("login"));
			print("<input name=\"login\" type=\"text\">\n");
			print("<input name=\"password\" type=\"password\">\n");
			print("<input type=\"submit\">\n");
			print("</form>\n");
			print("</body></html>\n");
		}
	}
}

