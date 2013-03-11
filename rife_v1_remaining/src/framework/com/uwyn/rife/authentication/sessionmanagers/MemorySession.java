/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MemorySession.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers;

public class MemorySession
{
	private String	mAuthId = null;
	private long	mUserId = -1;
	private String	mHostIp = null;
	private long	mStart = -1;
	private boolean	mRemembered = false;
	
	public MemorySession(String authId, long userId, String hostIp, boolean remembered)
	{
		setAuthId(authId);
		setUserId(userId);
		setHostIp(hostIp);
		setRemembered(remembered);
		mStart = System.currentTimeMillis();
	}
	
	public void setAuthId(String authId)
	{
		assert authId != null;
		assert authId.length() > 0;

		mAuthId = authId;
	}
	
	public String getAuthId()
	{
		return mAuthId;
	}
	
	public void setUserId(long userId)
	{
		assert userId >= 0;

		mUserId = userId;
	}
	
	public long getUserId()
	{
		return mUserId;
	}
	
	public void setHostIp(String hostIp)
	{
		assert hostIp != null;
		assert hostIp.length() > 0;

		mHostIp = hostIp;
	}
	
	public String getHostIp()
	{
		return mHostIp;
	}
	
	public void setStart(long start)
	{
		mStart = start;
	}
	
	public long getStart()
	{
		return mStart;
	}
	
	public void setRemembered(boolean remembered)
	{
		mRemembered = remembered;
	}
	
	public boolean getRemembered()
	{
		return mRemembered;
	}
	
	public int hashCode()
	{
		return mAuthId.hashCode();
	}

	public boolean equals(Object object)
	{
		if (object instanceof MemorySession)
		{
			MemorySession other_session = (MemorySession)object;
			if (null != other_session &&
				other_session.getAuthId().equals(getAuthId()))
			{
				return true;
			}
		}

		return false;
	}
}

