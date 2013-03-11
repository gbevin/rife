/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalvarsTarget.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.precedence;

import com.uwyn.rife.engine.Element;

public class GlobalvarsTarget extends Element
{
	public void processElement()
	{
		print("This is the globalvars target content"+
			":"+getInput("globalvar1")+
			":"+getInput("globalvar2")+
			":"+getInput("globalvar3")+
			":"+getInput("globalvar4")+
			":"+getInput("globalvar5")+
			":"+getInput("globalvar6"));
	}
}

