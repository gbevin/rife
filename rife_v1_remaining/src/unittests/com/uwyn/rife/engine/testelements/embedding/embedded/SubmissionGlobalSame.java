/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionGlobalSame.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;

public class SubmissionGlobalSame extends Element
{
	public void processElement()
	{
		if (hasSubmission("credentials"))
		{
			print(""+getParameter("login"));
			print(",");
			print(""+getParameter("password"));
			print(",");
			print(""+getParameter("language"));
			return;
		}
		print(getHtmlTemplate("engine_embedding_embedded_submission_same"));
	}
}

