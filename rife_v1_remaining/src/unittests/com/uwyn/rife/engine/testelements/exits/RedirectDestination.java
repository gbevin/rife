/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RedirectDestination.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.exits;

import com.uwyn.rife.engine.Element;

public class RedirectDestination extends Element
{
	public void processElement()
	{
		print(getInput("input1")+getInput("input2"));
		setProhibitRawAccess(false);
		print(getHttpServletRequest().getRequestURI());
		print(getHttpServletRequest().getQueryString());
	}
}

