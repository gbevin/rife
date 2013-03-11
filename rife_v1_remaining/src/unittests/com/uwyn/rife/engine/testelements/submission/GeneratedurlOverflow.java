/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GeneratedurlOverflow.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.tools.StringUtils;

public class GeneratedurlOverflow extends Element
{
	public final static String SUBMISSION_NAME1 = "submission5";

	public void processElement()
	{
		if (hasSubmission("submission1"))
		{
			print(getParameter("parameter"));
		}
		else
		{
			Template template = getHtmlTemplate("engine_submissionurl_generation");
			
			setSubmissionQuery(template, "submission1", new String[] {"parameter", StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", 74)+"01234567890"});
			
			print(template);
		}
	}
}

