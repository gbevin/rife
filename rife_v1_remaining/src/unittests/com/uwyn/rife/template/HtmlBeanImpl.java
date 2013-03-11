/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: HtmlBeanImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.site.Validation;

public class HtmlBeanImpl extends Validation
{
	private boolean		mWantsupdates = false;
	private String[]	mColors = null;
	private String		mFirstname = null;
	private String		mLastname = null;
	
	public HtmlBeanImpl()
	{
	}
	
	public void setWantsupdates(boolean wantsupdates)
	{
		mWantsupdates = wantsupdates;
	}
	
	public boolean getWantsupdates()
	{
		return mWantsupdates;
	}
	
	public void setColors(String[] colors)
	{
		mColors = colors;
	}
	
	public String[] getColors()
	{
		return mColors;
	}
	
	public void setFirstname(String firstname)
	{
		mFirstname = firstname;
	}
	
	public String getFirstname()
	{
		return mFirstname;
	}
	
	public void setLastname(String lastname)
	{
		mLastname = lastname;
	}
	
	public String getLastname()
	{
		return mLastname;
	}
}

