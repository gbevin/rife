/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseRemember.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers;

import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.ArrayList;
import junit.framework.TestCase;

public class TestDatabaseRemember extends TestCase
{
	private Datasource				mDatasource = null;
	private HierarchicalProperties	mProperties = null;
    
	public TestDatabaseRemember(Datasource datasource, String datasourceName, String name)
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
	
	public void testInstall()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);

		try
		{
			assertTrue(true == remember.install());
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testRemove()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);

		try
		{
			assertTrue(true == remember.remove());
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	 }
	
	public void testCreateRememberId()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);

		int		user_id = 143;
		
		String	remember_id = null;
		try
		{
			remember.install();
			
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
				remember.remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}

	public void testGetRememberedUserId()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);

		try
		{
			remember.install();

			ArrayList<String> remember_ids1 = new ArrayList<String>();
			ArrayList<String> remember_ids2 = new ArrayList<String>();
			ArrayList<String> remember_ids3 = new ArrayList<String>();
			remember_ids1.add(remember.createRememberId(232, "123.98.23.3"));
			remember_ids1.add(remember.createRememberId(232, "123.98.23.32"));
			remember_ids2.add(remember.createRememberId(23, "123.98.23.3"));
			remember_ids3.add(remember.createRememberId(53, "123.98.23.3"));
			remember_ids3.add(remember.createRememberId(53, "123.98.23.3"));
			remember_ids1.add(remember.createRememberId(232, "123.98.23.34"));
			remember_ids2.add(remember.createRememberId(23, "123.98.23.3"));
			
			for (String remember_id : remember_ids1)
			{
				assertEquals(232, remember.getRememberedUserId(remember_id));
			}
			
			for (String remember_id : remember_ids2)
			{
				assertEquals(23, remember.getRememberedUserId(remember_id));
			}
			
			for (String remember_id : remember_ids3)
			{
				assertEquals(53, remember.getRememberedUserId(remember_id));
			}
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				remember.remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}

	public void testEraseRememberId()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);

		int		user_id = 93;
		
		try
		{
			remember.install();

			String	remember_id = null;
			remember_id = remember.createRememberId(user_id, "123.98.23.3");
			assertEquals(user_id, remember.getRememberedUserId(remember_id));
			assertTrue(remember.eraseRememberId(remember_id));
			assertEquals(-1, remember.getRememberedUserId(remember_id));
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				remember.remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}
	
	public void testEraseUnknownSession()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);
		
		String	remember_id = "unknown";
		try
		{
			remember.install();

			assertTrue(false == remember.eraseRememberId(remember_id));
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				remember.remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}

	public void testEraseAllRememberIds()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);

		try
		{
			remember.install();

			ArrayList<String> remember_ids = new ArrayList<String>();
			remember_ids.add(remember.createRememberId(232, "123.98.23.3"));
			remember_ids.add(remember.createRememberId(232, "123.98.23.34"));
			remember_ids.add(remember.createRememberId(23, "123.98.23.3"));
			remember_ids.add(remember.createRememberId(53, "123.98.23.3"));
			remember_ids.add(remember.createRememberId(53, "123.98.23.3"));
			remember_ids.add(remember.createRememberId(232, "123.98.23.31"));
			remember_ids.add(remember.createRememberId(23, "123.98.23.3"));
			
			for (String remember_id : remember_ids)
			{
				assertTrue(remember.getRememberedUserId(remember_id) != -1);
			}
			
			remember.eraseAllRememberIds();

			for (String remember_id : remember_ids)
			{
				assertEquals(-1, remember.getRememberedUserId(remember_id));
			}
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				remember.remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}

	public void testEraseUserRememberIds()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);

		try
		{
			remember.install();

			ArrayList<String> remember_ids = new ArrayList<String>();
			remember_ids.add(remember.createRememberId(8433, "123.98.23.3"));
			remember_ids.add(remember.createRememberId(8433, "123.98.23.33"));
			remember_ids.add(remember.createRememberId(8432, "123.98.23.31"));
			remember_ids.add(remember.createRememberId(8431, "123.98.23.3"));
			
			for (String remember_id : remember_ids)
			{
				assertTrue(remember.getRememberedUserId(remember_id) != -1);
			}

			assertTrue(remember.eraseUserRememberIds(8433));

			assertTrue(remember.getRememberedUserId(remember_ids.get(0)) == -1);
			assertTrue(remember.getRememberedUserId(remember_ids.get(1)) == -1);
			assertTrue(remember.getRememberedUserId(remember_ids.get(2)) != -1);
			assertTrue(remember.getRememberedUserId(remember_ids.get(3)) != -1);
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				remember.remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}

	public void testEraseUnkownUserRememberIds()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);

		try
		{
			remember.install();

			ArrayList<String> remember_ids = new ArrayList<String>();
			remember_ids.add(remember.createRememberId(8432, "123.98.23.3"));
			remember_ids.add(remember.createRememberId(8431, "123.98.23.3"));
			
			for (String remember_id : remember_ids)
			{
				assertTrue(remember.getRememberedUserId(remember_id) != -1);
			}

			assertFalse(remember.eraseUserRememberIds(8433));

			for (String remember_id : remember_ids)
			{
				assertTrue(remember.getRememberedUserId(remember_id) != -1);
			}
		}
		catch (RememberManagerException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				remember.remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}
	
	public void testPurgeRememberIds()
	{
		DatabaseRemember remember = new DatabaseRememberFactory().getRememberManager(mProperties);
		remember.setRememberDuration(2000);

		int		user_id = 9478;
		
		try
		{
			remember.install();

			remember.eraseAllRememberIds();

			String remember_id = remember.createRememberId(user_id, "123.98.23.3");
			
			remember.purgeRememberIds();
			
			assertEquals(remember.getRememberedUserId(remember_id), user_id);

			Thread.sleep(2010);
			
			remember.purgeRememberIds();
			
			assertEquals(remember.getRememberedUserId(remember_id), -1);
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
				remember.remove();
			}
			catch (RememberManagerException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}
}
