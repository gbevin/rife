/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;

public class ExitSource extends Element
{
	public void processElement()
	{
		if (hasSubmission("activate_exit"))
		{
			setOutput("sent_value", getParameter("submitted_value"));
			exit("go_to_exit");
		}
	}
}

