/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPurgingMemorySessions.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers;

import junit.framework.TestCase;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.tools.ExceptionUtils;

public class TestPurgingMemorySessions extends TestCase
{
	public TestPurgingMemorySessions(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		PurgingSessionManager sessions = null;
		
		sessions = new PurgingSessionManager(new MemorySessions());
		
		assertNotNull(sessions);
	}
	
	public void testStartSession()
	{
		PurgingSessionManager sessions = new PurgingSessionManager(new MemorySessions());
		sessions.setSessionPurgeFrequency(0);
		try
		{
			sessions.eraseAllSessions();
		}
		catch (SessionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		int		user_id = 143;
		String	host_ip = "189.38.987.43";
		
		String	auth_id = null;
		try
		{
			auth_id = sessions.startSession(user_id, host_ip, false);
			
			assertNotNull(auth_id);
			assertTrue(auth_id.length() > 0);
			
			assertEquals(1, sessions.countSessions());
		}
		catch (SessionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testPurgeSessions()
	{
		PurgingSessionManager sessions = new PurgingSessionManager(new MemorySessions());
		sessions.setSessionDuration(2000);
		sessions.setSessionPurgeFrequency(1);
		sessions.setSessionPurgeScale(1);

		int		user_id = 9478;
		String	host_ip = "98.232.12.456";
		
		try
		{
			sessions.eraseAllSessions();
			assertEquals(0, sessions.countSessions());

			sessions.startSession(user_id, host_ip, false);
			assertEquals(1, sessions.countSessions());

			Thread.sleep(2010);
			
			sessions.startSession(user_id, host_ip, false);
			assertEquals(1, sessions.countSessions());
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (SessionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
}
