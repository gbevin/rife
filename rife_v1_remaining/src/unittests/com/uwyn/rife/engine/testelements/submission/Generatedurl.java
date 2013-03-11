/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Generatedurl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class Generatedurl extends Element
{
	public final static String SUBMISSION_NAME1 = "submission5";
	public final static String SUBMISSION_NAME2 = "submission6";

	public void processElement()
	{
		if (hasSubmission("submission1"))
		{
			print(getParameter("parameter"));
		}
		else if (hasSubmission("submission2") &&
				 !isParameterEmpty("login") &&
				 !isParameterEmpty("password"))
		{
			print(getParameter("login")+","+getParameter("password"));
		}
		else if (hasSubmission("submission3") &&
				 !isParameterEmpty("login") &&
				 !isParameterEmpty("password"))
		{
			print(getParameter("login")+","+getParameter("password"));
		}
		else if (hasSubmission("submission4"))
		{
			print("submission4");
		}
		else if (hasSubmission("submission5") &&
				 !isParameterEmpty("login") &&
				 !isParameterEmpty("password"))
		{
			print(getParameter("login")+","+getParameter("password"));
		}
		else if (hasSubmission("submission6"))
		{
			print("submission6");
		}
		else
		{
			Template template = getHtmlTemplate("engine_submissionurl_generation");
			
			setSubmissionQuery(template, "submission1", new String[] {"parameter", "thevalue"});
			setSubmissionForm(template, "submission3", new String[] {"password", "it is"});
			setSubmissionForm(template, "submission5", new String[] {"login", "one more"});

			print(template);
			return;
		}
	}
}

