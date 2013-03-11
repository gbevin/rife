/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.remembermanagers;

import com.uwyn.rife.authentication.RememberManager;

/**
 * Dummy custom remember manager class; just a mock we use to make sure
 * we can instantiate custom remember managers. */
public class CustomRememberManager implements RememberManager
{
	private String mId;
	
	public CustomRememberManager(String id)
	{
		mId = id;
	}
	
	public String getId()
	{
		return mId;
	}
	
	public String createRememberId(long userId, String hostIp) { return null; }
	public void eraseAllRememberIds() { }
	public boolean eraseRememberId(String rememberId) { return false; }
	public boolean eraseUserRememberIds(long userId) { return false; }
	public long getRememberDuration() { return 0; }
	public long getRememberedUserId(String rememberId) { return 0; }
	public void purgeRememberIds() { }
	public void setRememberDuration(long milliseconds) { }
}
