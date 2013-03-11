/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDbConnection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestDbConnection extends TestCase
{
    private Datasource  mDatasource = null;
    
	public TestDbConnection(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	public void testConnection()
	{
        DbConnection connection = null;
        try
        {
            connection = mDatasource.getConnection();
            assertFalse(connection.isClosed());
        }
        catch (DatabaseException e)
        {
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
        }
        finally
        {
            if (null != connection)
            {
                try
                {
                    connection.close();
                }
                catch (DatabaseException e)
                {
		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
                }
            }
        }
	}

	public void testDriverNameMapping()
	throws Exception
	{
        DbConnection connection = mDatasource.getConnection();
        try
        {
        	String name = connection.getMetaData().getDriverName();
        	assertEquals(name+" : "+Datasource.sDriverNames.get(name)+" "+mDatasource.getAliasedDriver(), Datasource.sDriverNames.get(name), mDatasource.getAliasedDriver());
        }
        finally
        {
            if (null != connection)
            {
                try
                {
                    connection.close();
                }
                catch (DatabaseException e)
                {
		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
                }
            }
        }
	}

	public void testClose()
	{
		DbConnection connection = null;
		try
		{
			connection = mDatasource.getConnection();
            assertFalse(connection.isClosed());
			connection.close();
			if (mDatasource.isPooled())
			{
	            assertFalse(connection.isClosed());
			}
			else
			{
				assertTrue(connection.isClosed());
			}
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetMetaData()
	{
		DbConnection connection = null;
		try
		{
			connection = mDatasource.getConnection();
			assertNotNull(connection.getMetaData());
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			if (null != connection)
			{
				try
				{
					connection.close();
				}
				catch (DatabaseException e)
				{
		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
				}
			}
		}
	}

	public void testGetStatement()
	{
		DbConnection connection = null;
		try
		{
			connection = mDatasource.getConnection();
			DbStatement statement1 = connection.createStatement();
			assertNotNull(statement1);
			DbStatement statement2 = connection.createStatement();
			assertNotNull(statement2);
			assertTrue(statement1 != statement2);
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			if (null != connection)
			{
				try
				{
					connection.close();
				}
				catch (DatabaseException e)
				{
		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
				}
			}
		}
	}

	public void testGetPreparedStatement()
	{
		DbConnection connection = null;
		try
		{
			connection = mDatasource.getConnection();
			String sql = "CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))";
			DbPreparedStatement prepared_statement1 = connection.getPreparedStatement(sql);
			assertNotNull(prepared_statement1);
			DbPreparedStatement prepared_statement2 = connection.getPreparedStatement(sql);
			assertNotNull(prepared_statement2);
			assertTrue(prepared_statement1 != prepared_statement2);
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			if (null != connection)
			{
				try
				{
					connection.close();
				}
				catch (DatabaseException e)
				{
		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
				}
			}
		}
	}

	public void testGetPreparedStatementQueryBuilder()
	{
		DbConnection connection = null;
		try
		{
			connection = mDatasource.getConnection();
			CreateTable create = new CreateTable(mDatasource);
			create
				.table("tbltest")
				.column("id", int.class)
				.column("stringcol", String.class, 255);
			DbPreparedStatement prepared_statement1 = connection.getPreparedStatement(create);
			assertNotNull(prepared_statement1);
			DbPreparedStatement prepared_statement2 = connection.getPreparedStatement(create);
			assertNotNull(prepared_statement2);
			assertTrue(prepared_statement1 != prepared_statement2);
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			if (null != connection)
			{
				try
				{
					connection.close();
				}
				catch (DatabaseException e)
				{
		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
				}
			}
		}
	}

	public void testTransactionBeginCommitRollback()
	{
		DbConnection connection = mDatasource.getConnection();

		DbPreparedStatement prepared_statement_create = null;
		DbPreparedStatement prepared_statement_drop = null;
		prepared_statement_create = connection.getPreparedStatement("CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))");
		prepared_statement_create.executeUpdate();

		prepared_statement_drop = connection.getPreparedStatement("DROP TABLE tbltest");

		DbPreparedStatement prepared_statement_insert = null;
		DbPreparedStatement prepared_statement_select = null;
		try
		{
			prepared_statement_insert = connection.getPreparedStatement("INSERT INTO tbltest VALUES (232, 'somestring')");
			prepared_statement_select = connection.getPreparedStatement("SELECT * FROM tbltest");

			if (connection.supportsTransactions() &&
				false == mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
			{
				assertTrue(connection.beginTransaction());
				assertEquals(1, prepared_statement_insert.executeUpdate());
				prepared_statement_select.executeQuery();
				assertTrue(prepared_statement_select.getResultSet().hasResultRows());
				assertTrue(connection.rollback());
				assertFalse(connection.commit());

				prepared_statement_select.executeQuery();
				assertFalse(prepared_statement_select.getResultSet().hasResultRows());

				assertTrue(connection.beginTransaction());
				assertEquals(1, prepared_statement_insert.executeUpdate());
				prepared_statement_select.executeQuery();
				assertTrue(prepared_statement_select.getResultSet().hasResultRows());
				assertTrue(connection.commit());
				assertFalse(connection.rollback());

				prepared_statement_select.executeQuery();
				assertTrue(prepared_statement_select.getResultSet().hasResultRows());
			}
			else
			{
				// FIXME: write tests with non transactional database
			}
		}
		catch (Exception e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			prepared_statement_insert.close();
			prepared_statement_select.close();
			
			assertTrue(connection.beginTransaction());
			prepared_statement_drop.executeUpdate();
			assertTrue(connection.commit());
			assertFalse(connection.rollback());

			try
			{
				prepared_statement_select = connection.getPreparedStatement("SELECT * FROM tbltest");
				prepared_statement_select.executeQuery();
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}

			connection.close();
		}
	}

	public void testTransactionThreadValidity()
	{
		DbConnection connection = null;
		try
		{
			connection = mDatasource.getConnection();
			if (connection.supportsTransactions() &&
				false == mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
			{
				assertTrue(false == connection.isTransactionValidForThread());
				assertTrue(true == connection.beginTransaction());
				assertTrue(true == connection.isTransactionValidForThread());
				ThreadImpl other_thread = new ThreadImpl(connection);
				other_thread.start();
				while (other_thread.isAlive())
				{
					synchronized (other_thread)
					{
						try
						{
							other_thread.wait(3600);
						}
						catch (InterruptedException e)
						{
							other_thread.interrupt();
							other_thread.stop();
							throw new RuntimeException("testTransactionThreadValidity failed for "+mDatasource.getAliasedDriver()+", timeout", e);
						}
					}
				}
				assertTrue(true == connection.isTransactionValidForThread());
				assertTrue(true == connection.rollback());
				assertTrue(false == connection.commit());
				assertTrue(false == connection.isTransactionValidForThread());
			}
			else
			{
				// FIXME: write tests with non transactional database
			}
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			if (null != connection)
			{
				try
				{
					connection.close();
				}
				catch (DatabaseException e)
				{
		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
				}
			}
		}
	}

//	public void testTransactionTimeoutBegin()
//	{
//		DbConnection connection = null;
//		DbPreparedStatement prepared_statement_create = null;
//		DbPreparedStatement prepared_statement_insert = null;
//		DbPreparedStatement prepared_statement_select = null;
//		DbPreparedStatement prepared_statement_drop = null;
//		try
//		{
//			connection = mDatasource.getConnection();
//			prepared_statement_create = connection.getPreparedStatement("CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))");
//			prepared_statement_drop = connection.getPreparedStatement("DROP TABLE tbltest");
//			prepared_statement_create.executeUpdate();
//
//			prepared_statement_insert = connection.getPreparedStatement("INSERT INTO tbltest VALUES (232, 'somestring')");
//			prepared_statement_select = connection.getPreparedStatement("SELECT * FROM tbltest");
//
//			if (connection.supportsTransactions() &&
//				false == mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
//			{
//				assertTrue(true == connection.beginTransaction());
//				prepared_statement_insert.executeUpdate();
//				try
//				{
//					Thread.sleep(RifeConfig.Database.getTransactionTimeout()*1000+100);
//				}
//				catch (InterruptedException e)
//				{
//		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//				}
//
//				try
//				{
//					connection.beginTransaction();
//					fail();
//				}
//				catch (TransactionTimedOutException e)
//				{
//					assertTrue(true);
//				}
//
//				assertTrue(false == connection.commit());
//				assertTrue(false == connection.rollback());
//				try
//				{
//					prepared_statement_select.executeQuery();
//					assertTrue(false == prepared_statement_select.hasResultRows());
//				}
//				catch (DatabaseException e)
//				{
//					assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//				}
//			}
//			else
//			{
//				// FIXME: write tests with non transactional database
//			}
//		}
//		catch (DatabaseException e)
//		{
//            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//		}
//		finally
//		{
//			if (null != connection)
//			{
//				try
//				{
//					prepared_statement_drop.executeUpdate();
//					connection.close();
//				}
//				catch (DatabaseException e)
//				{
//		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//				}
//			}
//		}
//	}

//	public void testTransactionTimeoutCommit()
//	{
//		DbConnection connection = null;
//		DbPreparedStatement prepared_statement_create = null;
//		DbPreparedStatement prepared_statement_insert = null;
//		DbPreparedStatement prepared_statement_select = null;
//		DbPreparedStatement prepared_statement_drop = null;
//		try
//		{
//			connection = mDatasource.getConnection();
//			prepared_statement_create = connection.getPreparedStatement("CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))");
//			prepared_statement_drop = connection.getPreparedStatement("DROP TABLE tbltest");
//			prepared_statement_create.executeUpdate();
//
//			prepared_statement_insert = connection.getPreparedStatement("INSERT INTO tbltest VALUES (232, 'somestring')");
//			prepared_statement_select = connection.getPreparedStatement("SELECT * FROM tbltest");
//
//			if (connection.supportsTransactions() &&
//				false == mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
//			{
//				assertTrue(true == connection.beginTransaction());
//				prepared_statement_insert.executeUpdate();
//				try
//				{
//					Thread.sleep(RifeConfig.Database.getTransactionTimeout()*1000+100);
//				}
//				catch (InterruptedException e)
//				{
//		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//				}
//
//				try
//				{
//					connection.commit();
//					fail();
//				}
//				catch (TransactionTimedOutException e)
//				{
//					assertTrue(true);
//				}
//
//				assertTrue(false == connection.rollback());
//				try
//				{
//					prepared_statement_select.executeQuery();
//					assertTrue(false == prepared_statement_select.hasResultRows());
//				}
//				catch (DatabaseException e)
//				{
//					assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//				}
//			}
//			else
//			{
//				// FIXME: write tests with non transactional database
//			}
//		}
//		catch (DatabaseException e)
//		{
//            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//		}
//		finally
//		{
//			if (null != connection)
//			{
//				try
//				{
//					prepared_statement_drop.executeUpdate();
//					connection.close();
//				}
//				catch (DatabaseException e)
//				{
//		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//				}
//			}
//		}
//	}
//
//	public void testTransactionTimeoutRollback()
//	{
//		DbConnection connection = null;
//		DbPreparedStatement prepared_statement_create = null;
//		DbPreparedStatement prepared_statement_insert = null;
//		DbPreparedStatement prepared_statement_select = null;
//		DbPreparedStatement prepared_statement_drop = null;
//		try
//		{
//			connection = mDatasource.getConnection();
//			prepared_statement_create = connection.getPreparedStatement("CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))");
//			prepared_statement_drop = connection.getPreparedStatement("DROP TABLE tbltest");
//			prepared_statement_create.executeUpdate();
//
//			prepared_statement_insert = connection.getPreparedStatement("INSERT INTO tbltest VALUES (232, 'somestring')");
//			prepared_statement_select = connection.getPreparedStatement("SELECT * FROM tbltest");
//
//			if (connection.supportsTransactions() &&
//				false == mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
//			{
//				assertTrue(true == connection.beginTransaction());
//				prepared_statement_insert.executeUpdate();
//				try
//				{
//					Thread.sleep(RifeConfig.Database.getTransactionTimeout()*1000+100);
//				}
//				catch (InterruptedException e)
//				{
//		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//				}
//
//				try
//				{
//					connection.rollback();
//					fail();
//				}
//				catch (TransactionTimedOutException e)
//				{
//					assertTrue(true);
//				}
//
//				assertTrue(false == connection.commit());
//				try
//				{
//					prepared_statement_select.executeQuery();
//					assertTrue(false == prepared_statement_select.hasResultRows());
//				}
//				catch (DatabaseException e)
//				{
//					assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//				}
//			}
//			else
//			{
//				// FIXME: write tests with non transactional database
//			}
//		}
//		catch (DatabaseException e)
//		{
//            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//		}
//		finally
//		{
//			if (null != connection)
//			{
//				try
//				{
//					prepared_statement_drop.executeUpdate();
//					connection.close();
//				}
//				catch (DatabaseException e)
//				{
//		            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
//				}
//			}
//		}
//	}

	class ThreadImpl extends Thread
	{
		private DbConnection mConnection = null;

		public ThreadImpl(DbConnection connection)
		{
			mConnection = connection;
		}

		public void run()
		{
			try
			{
				assertTrue(false == mConnection.isTransactionValidForThread());
				assertTrue(false == mConnection.beginTransaction());
				assertTrue(false == mConnection.commit());
				assertTrue(false == mConnection.rollback());
			}
			catch (DatabaseException e)
			{
	            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
			finally
			{
				synchronized (this)
				{
					this.notifyAll();
				}
			}
		}
	}
}
