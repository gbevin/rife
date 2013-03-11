/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionGlobalSameNourl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.Template;

public class SubmissionGlobalSameNourl extends Element
{
	public void processElement()
	{
		Template t = getHtmlTemplate("engine_embedding_submission_global_same_nourl");
		if (hasSubmission("credentials"))
		{
			t.setValue("form", getParameter("login"));
			t.appendValue("form", ",");
			t.appendValue("form", getParameter("password"));
		}
		print(t);
	}
}

