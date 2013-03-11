/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Multiple.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class Multiple extends Element
{
	public void processElement()
	{
		if (hasSubmission("login"))
		{
			print(getParameter("login")+","+getParameter("password")+","+StringUtils.join(getParameterValues("language"),"|"));
		}
		else if (hasSubmission("register"))
		{
			print(getParameter("login")+","+getParameter("password")+","+getParameter("firstname")+","+getParameter("lastname"));
		}
		else
		{
			print("<html><body>\n");
			print("<form name=\"login\" action=\""+getSubmissionQueryUrl("login")+"\" method=\"post\">\n");
			print("<input name=\"login\" type=\"text\">\n");
			print("<input name=\"password\" type=\"password\">\n");
			print("<select name=\"language\" multiple=\"1\">\n");
			print("<option value=\"fr\">french</option>\n");
			print("<option value=\"nl\">dutch</option>\n");
			print("</select>\n");
			print("<input type=\"submit\">\n");
			print("</form>\n");
			print("<form name=\"register\" action=\""+getSubmissionQueryUrl("register")+"\" method=\"post\">\n");
			print("<input name=\"login\" type=\"text\">\n");
			print("<input name=\"password\" type=\"password\">\n");
			print("<input name=\"firstname\" type=\"text\">\n");
			print("<input name=\"lastname\" type=\"text\">\n");
			print("<input type=\"submit\">\n");
			print("</form>\n");
			print("</body></html>\n");
		}
	}
}

