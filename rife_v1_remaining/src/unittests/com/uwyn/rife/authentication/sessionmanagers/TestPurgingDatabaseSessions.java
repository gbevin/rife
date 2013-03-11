/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPurgingDatabaseSessions.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestPurgingDatabaseSessions extends TestCase
{
	private Datasource 				mDatasource = null;
	private HierarchicalProperties	mProperties = null;
    
	public TestPurgingDatabaseSessions(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
		mProperties = new HierarchicalProperties();
		mProperties.put("datasource", mDatasource);
	}

	public void testInstantiation()
	{
		PurgingSessionManager manager = new PurgingSessionManager(new DatabaseSessionsFactory().getManager(mProperties));
		assertNotNull(manager);
	}
	
	public void testStartSession()
	{
		PurgingSessionManager sessions = new PurgingSessionManager(new DatabaseSessionsFactory().getManager(mProperties));
		sessions.setSessionPurgeFrequency(0);

		int		user_id = 143;
		String	host_ip = "189.38.987.43";
		
		String	auth_id = null;
		try
		{
			((DatabaseSessions)sessions.getSessionManager()).install();
			
			auth_id = sessions.startSession(user_id, host_ip, false);

			assertEquals(1, sessions.countSessions());
			
			assertNotNull(auth_id);
			assertTrue(auth_id.length() > 0);
		}
		catch (SessionManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				((DatabaseSessions)sessions.getSessionManager()).remove();
			}
			catch (SessionManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}
	
	public void testPurgeSessions()
	{
		PurgingSessionManager sessions = new PurgingSessionManager(new DatabaseSessionsFactory().getManager(mProperties));
		sessions.setSessionDuration(2000);
		sessions.setSessionPurgeFrequency(1);
		sessions.setSessionPurgeScale(1);

		int		user_id = 9478;
		String	host_ip = "98.232.12.456";
		
		try
		{
			((DatabaseSessions)sessions.getSessionManager()).install();

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
		finally
		{
			try
			{
				((DatabaseSessions)sessions.getSessionManager()).remove();
			}
			catch (SessionManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}
}
