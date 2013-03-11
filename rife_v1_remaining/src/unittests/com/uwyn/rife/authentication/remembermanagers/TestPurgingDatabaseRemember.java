/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPurgingDatabaseRemember.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers;

import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestPurgingDatabaseRemember extends TestCase
{
	private Datasource				mDatasource = null;
	private HierarchicalProperties	mProperties = null;
    
	public TestPurgingDatabaseRemember(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
		mProperties = new HierarchicalProperties();
		mProperties.put("datasource", mDatasource);
	}

	public void testInstantiation()
	{
		DatabaseRemember manager = new DatabaseRememberFactory().getRememberManager(mProperties);
		assertNotNull(manager);
	}
	
	public void testStartSession()
	{
		PurgingRememberManager remember = new PurgingRememberManager(new DatabaseRememberFactory().getRememberManager(mProperties));
		remember.setRememberPurgeFrequency(0);

		int		user_id = 143;
		
		String	remember_id = null;
		try
		{
			((DatabaseRemember)remember.getRememberManager()).install();
			
			remember_id = remember.createRememberId(user_id, "123.98.23.3");
			
			assertNotNull(remember_id);
			assertTrue(remember_id.length() > 0);

			assertEquals(user_id, remember.getRememberedUserId(remember_id));
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				((DatabaseRemember)remember.getRememberManager()).remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}
	
	public void testPurgeRemember()
	{
		PurgingRememberManager remember = new PurgingRememberManager(new DatabaseRememberFactory().getRememberManager(mProperties));
		remember.setRememberDuration(2000);
		remember.setRememberPurgeFrequency(1);
		remember.setRememberPurgeScale(1);

		try
		{
			((DatabaseRemember)remember.getRememberManager()).install();

			remember.eraseAllRememberIds();
			
			int	user_id1 = 143;
			String remember_id1 = remember.createRememberId(user_id1, "123.98.23.3");

			assertEquals(user_id1, remember.getRememberedUserId(remember_id1));

			Thread.sleep(2000);
			
			int	user_id2 = 143;
			String remember_id2 = remember.createRememberId(user_id2, "123.98.23.39");

			assertEquals(user_id2, remember.getRememberedUserId(remember_id2));
			assertEquals(-1, remember.getRememberedUserId(remember_id1));
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				((DatabaseRemember)remember.getRememberManager()).remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}
}
