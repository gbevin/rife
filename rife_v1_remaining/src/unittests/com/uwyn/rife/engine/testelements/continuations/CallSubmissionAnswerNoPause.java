/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallSubmissionAnswerNoPause.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.continuations;

import com.uwyn.rife.engine.Element;

public class CallSubmissionAnswerNoPause extends Element
{
	public void processElement()
	{
		if (hasSubmission("yes"))
		{
			answer(true);
		}
		if (hasSubmission("no"))
		{
			answer(false);
		}
		print("<a href=\""+getSubmissionQueryUrl("yes")+"\">yes</a>");
		print("<a href=\""+getSubmissionQueryUrl("no")+"\">no</a>");
	}
}

