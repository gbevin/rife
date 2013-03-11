/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PurgingSessionManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers;

import com.uwyn.rife.authentication.ListSessions;
import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.config.RifeConfig;
import java.util.Random;

public class PurgingSessionManager implements SessionManager
{
	private int		mSessionPurgeFrequency = RifeConfig.Authentication.getSessionPurgeFrequency();
	private int		mSessionPurgeScale = RifeConfig.Authentication.getSessionPurgeScale();

	private final Random	mRandom = new Random();
	
	private SessionManager	mSessionManager = null;

	public PurgingSessionManager(SessionManager sessionManager)
	{
		if (null == sessionManager)	throw new IllegalArgumentException("sessionManager can't be null");

		mSessionManager = sessionManager;
	}
	
	public SessionManager getSessionManager()
	{
		return mSessionManager;
	}
	
	public int getSessionPurgeFrequency()
	{
		return mSessionPurgeFrequency;
	}
	
	public void setSessionPurgeFrequency(int frequency)
	{
		mSessionPurgeFrequency = frequency;
	}
	
	public int getSessionPurgeScale()
	{
		return mSessionPurgeScale;
	}
	
	public void setSessionPurgeScale(int scale)
	{
		mSessionPurgeScale = scale;
	}
	
	public String startSession(long userId, String hostIp, boolean remembered)
	throws SessionManagerException
	{
		int purge_decision = -1;
		synchronized (mRandom)
		{
			purge_decision = mRandom.nextInt(mSessionPurgeScale);
		}
		if (purge_decision <= mSessionPurgeFrequency)
		{
			purgeSessions();
		}
		
		return mSessionManager.startSession(userId, hostIp, remembered);
	}
	
	public void setSessionDuration(final long milliseconds)
	{
		mSessionManager.setSessionDuration(milliseconds);
	}
	
	public long getSessionDuration()
	{
		return mSessionManager.getSessionDuration();
	}
	
	public boolean getRestrictHostIp()
	{
		return mSessionManager.getRestrictHostIp();
	}
	
	public void setRestrictHostIp(boolean flag)
	{
		mSessionManager.setRestrictHostIp(flag);
	}
	
	public void eraseAllSessions()
	throws SessionManagerException
	{
		mSessionManager.eraseAllSessions();
	}
	
	public boolean isSessionValid(final String authId, final String hostIp)
	throws SessionManagerException
	{
		return mSessionManager.isSessionValid(authId, hostIp);
	}
	
	public boolean continueSession(final String authId)
	throws SessionManagerException
	{
		return mSessionManager.continueSession(authId);
	}
	
	public long getSessionUserId(final String authId)
	throws SessionManagerException
	{
		return mSessionManager.getSessionUserId(authId);
	}
	
	public void purgeSessions()
	throws SessionManagerException
	{
		mSessionManager.purgeSessions();
	}
	
	public boolean eraseSession(String authId)
	throws SessionManagerException
	{
		return mSessionManager.eraseSession(authId);
	}
	
	public boolean wasRemembered(String authId)
	throws SessionManagerException
	{
		return mSessionManager.wasRemembered(authId);
	}
	
	public boolean eraseUserSessions(long userId)
	throws SessionManagerException
	{
		return mSessionManager.eraseUserSessions(userId);
	}
	
	public long countSessions()
	throws SessionManagerException
	{
		return mSessionManager.countSessions();
	}
	
	public boolean listSessions(ListSessions processor)
	throws SessionManagerException
	{
		return mSessionManager.listSessions(processor);
	}
}

