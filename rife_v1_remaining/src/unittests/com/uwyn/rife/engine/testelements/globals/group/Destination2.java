/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Destination2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals.group;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class Destination2 extends Element
{
	public void processElement()
	{
		print(getInput("globalvar1")+","+StringUtils.join(getInputValues("globalvar2"),"|")+","+getInput("globalvar3")+","+StringUtils.join(getInputValues("globalvar4"),"|"));
	}
}

