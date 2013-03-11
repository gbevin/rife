/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.sessionmanagers;

import com.uwyn.rife.authentication.ListSessions;
import com.uwyn.rife.authentication.SessionManager;

/**
 * Dummy session manager class; this is just a mock we can use to test
 * that our session factory is being used.
 */
public class CustomSessionManager implements SessionManager
{
	private String mId;
	
	public CustomSessionManager() { }
	
	public CustomSessionManager(String id)
	{
		mId = id;
	}
	public String getId()
	{
		return mId;
	}

	public boolean continueSession(String authId) { return false; }
	public long countSessions() { return 0; }
	public void eraseAllSessions() { }
	public boolean eraseSession(String authId) { return false; }
	public boolean eraseUserSessions(long userId) { return false; }
	public boolean getRestrictHostIp() { return false; }
	public long getSessionDuration() { return 0; }
	public long getSessionUserId(String authId) { return 0; }
	public boolean isSessionValid(String authId, String hostIp) { return false; }
	public boolean listSessions(ListSessions processor) { return false; }
	public void purgeSessions() { }
	public void setRestrictHostIp(boolean flag) { }
	public void setSessionDuration(long milliseconds) { }
	public String startSession(long userId, String hostIp, boolean remembered) { return null; }
	public boolean wasRemembered(String authId) { return false; }
}
