/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CookiesOutjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.embedding.embedded;

import com.uwyn.rife.engine.Element;

public class CookiesOutjection extends Element
{
	private String mCookie1;
	private String mCookie2;
	private String mCookie4;
	private String mCookie5;
	
	public String getCookie1()
	{
		return mCookie1;
	}
	
	public String getCookie2()
	{
		return mCookie2;
	}
	
	public String getCookie4()
	{
		return mCookie4;
	}
	
	public String getCookie5()
	{
		return mCookie5;
	}
	
	public void processElement()
	{
		if (hasSubmission("submission"))
		{
			mCookie1 = "embedded value 1";
			mCookie2 = "embedded value 2";
			mCookie4 = "embedded value 4";
			mCookie5 = "embedded value 5";
			print("submitted");
		}
		else
		{
			print("<form action=\""+getSubmissionFormUrl()+"\">"+getSubmissionFormParameters("submission")+"<input type=\"submit\" /></form>");
		}
	}
}

