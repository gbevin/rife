/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalDestSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.subsites.globalexit.isolation;

import com.uwyn.rife.engine.Element;

public class GlobalDestSource extends Element
{
	public void processElement()
	{
		if (hasInputValue("sourceinput1"))
		{
			setOutput("sourceoutput1", getInputValues("sourceinput1"));
		}
		if (hasInputValue("sourceinput2"))
		{
			setOutput("sourceoutput2", getInputValues("sourceinput2"));
		}
		if (hasInputValue("sourceinput3"))
		{
			setOutput("sourceoutput3", getInputValues("sourceinput3"));
		}
		
		if (hasInputValue("sourceinput1") &&
			getInput("sourceinput1").equals("request value1 dest1"))
		{
			exit("globalexit1");
		}
		else if (hasInputValue("sourceinput1") &&
				 getInput("sourceinput1").equals("request value1 dest2"))
		{
			exit("globalexit2");
		}
		else if (hasInputValue("sourceinput1") &&
				 getInput("sourceinput1").equals("request value1 dest3"))
		{
			exit("globalexit3");
		}
	}
}

