/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GlobalvarSiblingInjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;

public class GlobalvarSiblingInjection extends Element
{
	private String	mGlobalvar1;
	private boolean	mClear = false;
	
	public void setGlobalvar1(String value)
	{
		mGlobalvar1 = value;
	}
	
	public void setClear(boolean value)
	{
		mClear = value;
	}
	
	public void processElement()
	{
		print(getElementInfo().getId()+": "+mGlobalvar1);
		if (mClear)
		{
			clearOutput("globalvar1");
		}
	}
}

