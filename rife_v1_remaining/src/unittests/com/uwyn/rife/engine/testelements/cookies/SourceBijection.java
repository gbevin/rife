/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SourceBijection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.cookies;

import com.uwyn.rife.engine.Element;

public class SourceBijection extends Element
{
	private String mCookie1;
	private String mCookie2;
	private String mCookie3;
	private String mCookie4;
	
	public void setCookie1(String value)
	{
		mCookie1 = value;
	}
	
	public void setCookie2(String value)
	{
		mCookie2 = value;
	}
	
	public String getCookie3()
	{
		return mCookie3;
	}
	
	public String getCookie4()
	{
		return mCookie4;
	}
	
	public void processElement()
	{
		if (mCookie1 != null &&
			mCookie2 != null &&
			hasCookie("cookie3"))
		{
			mCookie3 = mCookie1;
			mCookie4 = mCookie2;
		}
		
		print("source");
	}
}

