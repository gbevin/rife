/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UrltransferSourceParent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.casefigures;

import com.uwyn.rife.engine.Element;

public class UrltransferSourceParent extends Element
{
	public void processElement()
	{
		if (hasSubmission("submission1"))
		{
			child();
		}
		
		print("<a href=\""+getSubmissionQueryUrl("submission1")+"\">sourceparent</a>");
	}
}

