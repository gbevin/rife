/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InjectionDest1.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.globals;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.tools.StringUtils;

public class InjectionDest1 extends Element
{
	private String		mGlobalvar1;
	private String[]	mGlobalvar2;
	
	public void setGlobalvar1(String globalvar1)	{ mGlobalvar1 = globalvar1; }
	public void setGlobalvar2(String[] globalvar2)	{ mGlobalvar2 = globalvar2; }

	public void processElement()
	{
		print(StringUtils.join(mGlobalvar2,"|")+","+mGlobalvar1);
	}
}

