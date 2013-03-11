/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InterfaceParent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.inheritance;

import com.uwyn.rife.engine.ElementAware;
import com.uwyn.rife.engine.ElementChildTrigger;
import com.uwyn.rife.engine.ElementSupport;

public class InterfaceParent implements ElementAware
{
	private ElementSupport	mElement = null;
	
	public void noticeElement(ElementSupport element)
	{
		mElement = element;
		mElement.setChildTrigger(new ElementChildTrigger()
			{
				public boolean childTriggered(String name, String[] values)
				{
					if (name.equals("trigger") &&
						values[0].equals("ok"))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			});
	}
	
	public void processElement()
	{
		if (mElement.hasSubmission("activatechild"))
		{
			mElement.setOutput("trigger", "ok");
		}
		
		mElement.print("<html><body><a href=\""+mElement.getSubmissionQueryUrl("activatechild")+"\">activate child</a></body></html>");
	}
}

