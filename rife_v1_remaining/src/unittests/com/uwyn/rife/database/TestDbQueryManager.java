/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDbQueryManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.ReaderUser;
import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import junit.framework.TestCase;

public class TestDbQueryManager extends TestCase
{
    private Datasource  mDatasource = null;
    
	public TestDbQueryManager(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}

	public void setUp()
	{
	}

	public void tearDown()
	{
		try
		{
			DbConnection connection = mDatasource.getConnection();

			// drop the test table
			DbStatement statement = connection.createStatement();
			try
			{
				try
				{
					statement.executeUpdate(new DropTable(mDatasource).table("tbltest"));
				}
				catch (DatabaseException e) { /* don't do anything */ }
			}
			finally
			{
				try
				{
					statement.close();
				}
				catch (DatabaseException e) { /* don't do anything */ }
			}
			
			connection.close();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalInstantiation()
	{
		try
		{
			new DbQueryManager(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testInstantiation()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		assertNotNull(manager);
		assertSame(manager.getDatasource(), mDatasource);
	}

	public void testIllegalExecuteUpdateSql()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeUpdate((String)null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testTransactionUserCommit()
	{
		final DbQueryManager manager = new DbQueryManager(mDatasource);
		String create = "CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))";
		manager.executeUpdate(create);
		try
		{
			final String insert = "INSERT INTO tbltest VALUES (232, 'somestring')";
			final Select select = new Select(mDatasource).from("tbltest").field("count(*)");

			if (manager.getConnection().supportsTransactions() &&
				!mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver") &&
				!mDatasource.getAliasedDriver().equals("org.apache.derby.jdbc.EmbeddedDriver") &&
				!mDatasource.getAliasedDriver().equals("in.co.daffodil.db.jdbc.DaffodilDBDriver"))
			{
				manager.inTransaction(new DbTransactionUserWithoutResult() {
						public void useTransactionWithoutResult()
						throws InnerClassException
						{
							manager.executeUpdate(insert);
							assertEquals(1, manager.executeGetFirstInt(select));

							manager.inTransaction(new DbTransactionUserWithoutResult() {
									public void useTransactionWithoutResult()
									throws InnerClassException
									{
										manager.inTransaction(new DbTransactionUserWithoutResult() {
												public void useTransactionWithoutResult()
												throws InnerClassException
												{
													manager.executeUpdate(insert);
													assertEquals(2, manager.executeGetFirstInt(select));
												}
											});

										manager.executeUpdate(insert);
										assertEquals(3, manager.executeGetFirstInt(select));
									}
								});

							assertEquals(3, manager.executeGetFirstInt(select));

							// ensure that the transaction isn't committed yet
							// since this should only happen after the last transaction user
							Thread other_thread = new Thread() {
									public void run()
									{
										// HsqlDB only has read-uncommitted transactionisolation
										if ("org.hsqldb.jdbcDriver".equals(mDatasource.getAliasedDriver()))
										{
											assertEquals(3, manager.executeGetFirstInt(select));
										}
										// all the rest should be fully isolated
										else
										{
											assertEquals(0, manager.executeGetFirstInt(select));
										}

										synchronized (this)
										{
											this.notifyAll();
										}
									}};

							other_thread.start();
							while (other_thread.isAlive())
							{
								synchronized (other_thread)
								{
									try
									{
										other_thread.wait();
									}
									catch (InterruptedException e)
									{
									}
								}
							}
						}
					});
				assertEquals(3, manager.executeGetFirstInt(select));
			}
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testTransactionUserRecommendedRollback()
	{
		final DbQueryManager manager = new DbQueryManager(mDatasource);
		String create = "CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))";
		manager.executeUpdate(create);
		try
		{
			final String insert = "INSERT INTO tbltest VALUES (232, 'somestring')";
			final Select select = new Select(mDatasource).from("tbltest").field("count(*)");

			if (manager.getConnection().supportsTransactions() &&
				false == mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
			{
				manager.inTransaction(new DbTransactionUserWithoutResult() {
						public void useTransactionWithoutResult()
						throws InnerClassException
						{
							manager.executeUpdate(insert);
							assertEquals(1, manager.executeGetFirstInt(select));

							manager.inTransaction(new DbTransactionUserWithoutResult() {
									public void useTransactionWithoutResult()
									throws InnerClassException
									{
										manager.inTransaction(new DbTransactionUserWithoutResult() {
												public void useTransactionWithoutResult()
												throws InnerClassException
												{
													manager.executeUpdate(insert);
													rollback();
												}
											});

										manager.executeUpdate(insert);
										fail();
									}
								});

							fail();
						}
					});
				assertEquals(0, manager.executeGetFirstInt(select));
			}
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testTransactionUserRuntimeException()
	{
		final DbQueryManager manager = new DbQueryManager(mDatasource);
		String create = "CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))";
		manager.executeUpdate(create);
		try
		{
			final String insert = "INSERT INTO tbltest VALUES (232, 'somestring')";
			final Select select = new Select(mDatasource).from("tbltest").field("count(*)");

			if (manager.getConnection().supportsTransactions() &&
				false == mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
			{
				try
				{
					manager.inTransaction(new DbTransactionUserWithoutResult() {
							public void useTransactionWithoutResult()
							throws InnerClassException
							{
								manager.executeUpdate(insert);
								assertEquals(1, manager.executeGetFirstInt(select));

								manager.inTransaction(new DbTransactionUserWithoutResult() {
										public void useTransactionWithoutResult()
										throws InnerClassException
										{
											manager.inTransaction(new DbTransactionUserWithoutResult() {
													public void useTransactionWithoutResult()
													throws InnerClassException
													{
														manager.executeUpdate(insert);
														throw new RuntimeException("something happened");
													}
												});

											manager.executeUpdate(insert);
											fail();
										}
									});

								fail();
							}
						});

					fail();
				}
				catch (RuntimeException e)
				{
					assertEquals("something happened", e.getMessage());
				}

				assertEquals(0, manager.executeGetFirstInt(select));
			}
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testTransactionUserRegularRollback()
	{
		final DbQueryManager manager = new DbQueryManager(mDatasource);
		String create = "CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))";
		manager.executeUpdate(create);
		try
		{
			final String insert = "INSERT INTO tbltest VALUES (232, 'somestring')";
			final Select select = new Select(mDatasource).from("tbltest").field("count(*)");

			if (manager.getConnection().supportsTransactions() &&
				false == mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
			{
				manager.inTransaction(new DbTransactionUserWithoutResult() {
						public void useTransactionWithoutResult()
						throws InnerClassException
						{
							manager.executeUpdate(insert);
							assertEquals(1, manager.executeGetFirstInt(select));

							manager.getConnection().rollback();
						}
					});
				assertEquals(0, manager.executeGetFirstInt(select));
			}
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testTransactionUserCommittingException()
	{
		final DbQueryManager manager = new DbQueryManager(mDatasource);
		String create = "CREATE TABLE tbltest (id INTEGER, stringcol VARCHAR(255))";
		manager.executeUpdate(create);
		try
		{
			final String insert = "INSERT INTO tbltest VALUES (232, 'somestring')";
			final Select select = new Select(mDatasource).from("tbltest").field("count(*)");

			if (manager.getConnection().supportsTransactions() &&
				false == mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
			{
				try
				{
					manager.inTransaction(new DbTransactionUserWithoutResult() {
							public void useTransactionWithoutResult()
							throws InnerClassException
							{
								manager.executeUpdate(insert);
								assertEquals(1, manager.executeGetFirstInt(select));

								manager.inTransaction(new DbTransactionUserWithoutResult() {
										public void useTransactionWithoutResult()
										throws InnerClassException
										{
											manager.inTransaction(new DbTransactionUserWithoutResult() {
													public void useTransactionWithoutResult()
													throws InnerClassException
													{
														manager.executeUpdate(insert);
														throw new TestComittingRuntimeException("something happened");
													}
												});

											manager.executeUpdate(insert);
											fail();
										}
									});

								fail();
							}
						});

					fail();
				}
				catch (RuntimeException e)
				{
					assertEquals("something happened", e.getMessage());
				}

				assertEquals(2, manager.executeGetFirstInt(select));
			}
		}
		catch (DatabaseException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public class TestComittingRuntimeException extends RuntimeException implements ControlFlowRuntimeException
	{
		public TestComittingRuntimeException(String message)
		{
			super(message);
		}
	}

	public void testExecuteUpdateSqlSuccess()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("column1", String.class, 50);
			manager.executeUpdate(create_query.getSql());

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext");
			assertEquals(1, manager.executeUpdate(insert_query.getSql()));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteUpdateBuilder()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeUpdate((Query)null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteUpdateBuilderSuccess()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("column1", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext");
			assertEquals(1, manager.executeUpdate(insert_query));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteUpdateBuilderError()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("column1", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column2", "sometext");
			try
			{
				manager.executeUpdate(insert_query);
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteUpdateHandler()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeUpdate(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteUpdateHandler()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.fieldParameter("name");
			assertEquals(1, manager.executeUpdate(insert_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("name", "me");
									 }
								}));

			assertEquals("me", manager.executeGetFirstString(new Select(mDatasource).from("tbltest")));

			manager.executeUpdate(new Delete(mDatasource).from("tbltest"));

			insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("name", "me2");
			assertEquals(1, manager.executeUpdate(insert_query, null));

			assertEquals("me2", manager.executeGetFirstString(new Select(mDatasource).from("tbltest")));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteHasResultRows()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeHasResultRows((Select)null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteReadQueryString()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").field("name", "me"));

			assertTrue(manager.executeHasResultRows(new ReadQueryString("SELECT name FROM tbltest WHERE name = 'me'")));

			assertTrue(manager.executeHasResultRows(new ReadQueryString("SELECT name FROM tbltest WHERE name = ?"), new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString(1, "me");
									 }
								}));

			manager.executeUpdate(new Delete(mDatasource).from("tbltest"));

			assertFalse(manager.executeHasResultRows(new ReadQueryString("SELECT name FROM tbltest WHERE name = 'me'")));

			assertFalse(manager.executeHasResultRows(new ReadQueryString("SELECT name FROM tbltest WHERE name = ?"), new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString(1, "me");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteReadQueryTemplate()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").field("name", "me"));

			Template template1 = TemplateFactory.SQL.get("readquery_blocks");
			template1.setValue("name", template1.getEncoder().encode("me"));
			assertTrue(manager.executeHasResultRows(new ReadQueryTemplate(template1, "query1")));

			Template template2 = TemplateFactory.SQL.get("readquery_content");
			template2.setValue("name", template2.getEncoder().encode("me"));
			assertTrue(manager.executeHasResultRows(new ReadQueryTemplate(template2)));

			assertTrue(manager.executeHasResultRows(new ReadQueryTemplate(template1, "query2"), new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString(1, "me");
									 }
								}));

			manager.executeUpdate(new Delete(mDatasource).from("tbltest"));

			assertFalse(manager.executeHasResultRows(new ReadQueryTemplate(template1, "query1")));

			assertFalse(manager.executeHasResultRows(new ReadQueryTemplate(template1, "query2"), new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString(1, "me");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteHasResultRows()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").field("name", "me"));

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			assertTrue(manager.executeHasResultRows(select_query));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.whereParameter("name", "=");
			assertTrue(manager.executeHasResultRows(select_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("name", "me");
									 }
								}));

			manager.executeUpdate(new Delete(mDatasource).from("tbltest"));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			assertFalse(manager.executeHasResultRows(select_query));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.whereParameter("name", "=");
			assertFalse(manager.executeHasResultRows(select_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("name", "me");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteHasResultRowsConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeHasResultRows(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstString()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstString(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstString()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("firstcol", String.class, 50).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("firstcol")
				.where("lastcol", "=", "Doe");

			assertNull(manager.executeGetFirstString(select_query));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "John", "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "Piet", "lastcol", "Smith"}));

			assertEquals("John", manager.executeGetFirstString(select_query));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("firstcol")
				.whereParameter("lastcol", "=");
			assertEquals("Piet", manager.executeGetFirstString(select_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("lastcol", "Smith");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstStringConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstString(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstBoolean()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstBoolean(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstBoolean()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", boolean.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertEquals(false, manager.executeGetFirstBoolean(select_query));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Boolean(true), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Boolean(false), "lastcol", "Smith"}));

			assertEquals(true, manager.executeGetFirstBoolean(select_query));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(false, manager.executeGetFirstBoolean(select_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("lastcol", "Smith");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstBooleanConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstBoolean(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							resultSet.getBoolean("unknown");
							return null;
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstByte()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstByte(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstByte()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", byte.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertEquals(-1, manager.executeGetFirstByte(select_query));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Byte((byte)12), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Byte((byte)23), "lastcol", "Smith"}));

			assertEquals(12, manager.executeGetFirstByte(select_query));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(23, manager.executeGetFirstByte(select_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("lastcol", "Smith");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstByteConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstByte(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							resultSet.getByte("unknown");
							return null;
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstShort()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstShort(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstShort()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", short.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertEquals(-1, manager.executeGetFirstShort(select_query));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Short((short)98), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Short((short)243), "lastcol", "Smith"}));

			assertEquals(98, manager.executeGetFirstShort(select_query));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(243, manager.executeGetFirstShort(select_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("lastcol", "Smith");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstShortConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstShort(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							resultSet.getShort("unknown");
							return null;
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstInt()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstInt(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstInt()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", int.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertEquals(-1, manager.executeGetFirstInt(select_query));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Integer(827), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Integer(154), "lastcol", "Smith"}));

			assertEquals(827, manager.executeGetFirstInt(select_query));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(154, manager.executeGetFirstInt(select_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("lastcol", "Smith");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstIntConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstInt(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							resultSet.getInt("unknown");
							return null;
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstLong()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstLong(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstLong()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", long.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertEquals(-1, manager.executeGetFirstLong(select_query));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Long(92873), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Long(14873), "lastcol", "Smith"}));

			assertEquals(92873, manager.executeGetFirstLong(select_query));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(14873, manager.executeGetFirstLong(select_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("lastcol", "Smith");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstLongConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstLong(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							resultSet.getLong("unknown");
							return null;
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstFloat()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstFloat(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstFloat()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", float.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertEquals(-1.0f, manager.executeGetFirstFloat(select_query));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Float(12.4), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Float(23.5), "lastcol", "Smith"}));

			assertEquals(12.4f, manager.executeGetFirstFloat(select_query));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(23.5f, manager.executeGetFirstFloat(select_query, new DbPreparedStatementHandler() {
									 public void setParameters(DbPreparedStatement statement)
									 {
										 statement
											 .setString("lastcol", "Smith");
									 }
								}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstFloatConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstFloat(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							resultSet.getFloat("unknown");
							return null;
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstDouble()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstDouble(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstDouble()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", double.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertEquals(-1.0d, manager.executeGetFirstDouble(select_query));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Double(287.52), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new Double(1984.328), "lastcol", "Smith"}));

			assertEquals(287.52d, manager.executeGetFirstDouble(select_query), 0.001);

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(1984.328d, manager.executeGetFirstDouble(select_query, new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("lastcol", "Smith");
					 }
				}), 0.001);
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstDoubleConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstDouble(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							resultSet.getDouble("unknown");
							return null;
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstBytes()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstBytes(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstBytes()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			if (mDatasource.getAliasedDriver().equals("oracle.jdbc.driver.OracleDriver"))
			{
				create_query.table("tbltest").column("datacol", String.class).column("lastcol", String.class, 50);
			}
			else
			{
				create_query.table("tbltest").column("datacol", Blob.class).column("lastcol", String.class, 50);
			}
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertNull(manager.executeGetFirstBytes(select_query));

			Insert insert = new Insert(mDatasource).into("tbltest").fieldParameter("datacol").fieldParameter("lastcol");
			manager.executeUpdate(insert, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						if (mDatasource.getAliasedDriver().equals("oracle.jdbc.driver.OracleDriver"))
						{
							statement.setString("datacol", "abc");
						}
						else
						{
							statement.setBytes("datacol", "abc".getBytes());
						}
						statement.setString("lastcol", "Doe");
					}
				});
			manager.executeUpdate(insert, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						if (mDatasource.getAliasedDriver().equals("oracle.jdbc.driver.OracleDriver"))
						{
							statement.setString("datacol", "def");
						}
						else
						{
							statement.setBytes("datacol", "def".getBytes());
						}
						statement.setString("lastcol", "Smith");
					}
				});

			byte[] result = null;
			result = manager.executeGetFirstBytes(select_query);
			assertTrue(Arrays.equals(new byte[] {97,98,99}, result));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			result = manager.executeGetFirstBytes(select_query, new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("lastcol", "Smith");
					 }
				});
			assertTrue(Arrays.equals(new byte[] {100,101,102}, result));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstBytesConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstBytes(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							resultSet.getBytes("unknown");
							return null;
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstDate()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstDate(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		try
		{
			manager.executeGetFirstDate(null, (Calendar)null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstDate()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", java.sql.Date.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertNull(manager.executeGetFirstDate(select_query));
			assertNull(manager.executeGetFirstDate(select_query, Calendar.getInstance()));

			Calendar cal1 = Calendar.getInstance();
			cal1.set(2003, 11, 12, 0, 0, 0);
			cal1.set(Calendar.MILLISECOND, 0);
			Calendar cal2 = Calendar.getInstance();
			cal2.set(2004, 2, 7, 0, 0, 0);
			cal2.set(Calendar.MILLISECOND, 0);
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new java.sql.Date(cal1.getTimeInMillis()), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new java.sql.Date(cal2.getTimeInMillis()), "lastcol", "Smith"}));

			assertEquals(cal1.getTimeInMillis(), manager.executeGetFirstDate(select_query).getTime());
			assertEquals(cal1.getTimeInMillis(), manager.executeGetFirstDate(select_query, Calendar.getInstance()).getTime());

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(cal2.getTimeInMillis(), manager.executeGetFirstDate(select_query, new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("lastcol", "Smith");
					 }
				}).getTime());
			assertEquals(cal2.getTimeInMillis(), manager.executeGetFirstDate(select_query, Calendar.getInstance(), new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("lastcol", "Smith");
					 }
				}).getTime());
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstDateConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstDate(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getDate("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
			try
			{
				manager.executeGetFirstDate(select_query, Calendar.getInstance(), new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getDate("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstTime()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstTime(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		try
		{
			manager.executeGetFirstTime(null, (Calendar)null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstTime()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", java.sql.Time.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertNull(manager.executeGetFirstTime(select_query));
			assertNull(manager.executeGetFirstTime(select_query, Calendar.getInstance()));

			Calendar cal1 = Calendar.getInstance();
			cal1.set(1970, 0, 1, 12, 5, 12);
			cal1.set(Calendar.MILLISECOND, 0);
			Calendar cal2 = Calendar.getInstance();
			cal2.set(1970, 0, 1, 23, 34, 27);
			cal2.set(Calendar.MILLISECOND, 0);
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new java.sql.Time(cal1.getTimeInMillis()), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new java.sql.Time(cal2.getTimeInMillis()), "lastcol", "Smith"}));

			assertEquals(cal1.getTimeInMillis(), manager.executeGetFirstTime(select_query).getTime());
			assertEquals(cal1.getTimeInMillis(), manager.executeGetFirstTime(select_query, Calendar.getInstance()).getTime());

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(cal2.getTimeInMillis(), manager.executeGetFirstTime(select_query, new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("lastcol", "Smith");
					 }
				}).getTime());
			assertEquals(cal2.getTimeInMillis(), manager.executeGetFirstTime(select_query, Calendar.getInstance(), new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("lastcol", "Smith");
					 }
				}).getTime());
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstTimeConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstTime(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getTime("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
			try
			{
				manager.executeGetFirstTime(select_query, Calendar.getInstance(), new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getTime("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstTimestamp()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeGetFirstTimestamp(null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		try
		{
			manager.executeGetFirstTimestamp(null, (Calendar)null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstTimestamp()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("datacol", java.sql.Timestamp.class).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.where("lastcol", "=", "Doe");

			assertNull(manager.executeGetFirstTimestamp(select_query));
			assertNull(manager.executeGetFirstTimestamp(select_query, Calendar.getInstance()));

			Calendar cal1 = Calendar.getInstance();
			cal1.set(2003, 11, 12, 8, 10, 8);
			cal1.set(Calendar.MILLISECOND, 0);
			Calendar cal2 = Calendar.getInstance();
			cal2.set(2004, 2, 7, 21, 34, 12);
			cal2.set(Calendar.MILLISECOND, 0);
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new java.sql.Timestamp(cal1.getTimeInMillis()), "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new Object[] {"datacol", new java.sql.Timestamp(cal2.getTimeInMillis()), "lastcol", "Smith"}));

			assertEquals(cal1.getTimeInMillis(), manager.executeGetFirstTimestamp(select_query).getTime());
			assertEquals(cal1.getTimeInMillis(), manager.executeGetFirstTimestamp(select_query, Calendar.getInstance()).getTime());

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol")
				.whereParameter("lastcol", "=");
			assertEquals(cal2.getTimeInMillis(), manager.executeGetFirstTimestamp(select_query, new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("lastcol", "Smith");
					 }
				}).getTime());
			assertEquals(cal2.getTimeInMillis(), manager.executeGetFirstTimestamp(select_query, Calendar.getInstance(), new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("lastcol", "Smith");
					 }
				}).getTime());
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstTimestampConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeGetFirstTimestamp(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getTimestamp("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
			try
			{
				manager.executeGetFirstTimestamp(select_query, Calendar.getInstance(), new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getTimestamp("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstAsciiStream()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeUseFirstAsciiStream(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		try
		{
			manager.executeUseFirstAsciiStream(new Select(mDatasource).from("tbltest"), null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstAsciiStream()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("firstcol", String.class, 50).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("firstcol")
				.where("lastcol", "=", "Doe");

			manager.executeUseFirstAsciiStream(select_query, new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNull(stream);

						return null;
					}
				});

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "John", "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "Piet", "lastcol", "Smith"}));

			manager.executeUseFirstAsciiStream(select_query, new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNotNull(stream);
						try
						{
							assertEquals("John", FileUtils.readString(stream));
						}
						catch (FileUtilsErrorException e)
						{
							assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
						}

						return null;
					}
				});

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("firstcol")
				.whereParameter("lastcol", "=");
			manager.executeUseFirstAsciiStream(select_query, new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNotNull(stream);
						try
						{
							assertEquals("Piet", FileUtils.readString(stream));
						}
						catch (FileUtilsErrorException e)
						{
							assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
						}

						return null;
					}
				}, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("lastcol", "Smith");
					}
				});
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstAsciiStreamConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeUseFirstAsciiStream(select_query, new InputStreamUser() {
						public Object useInputStream(InputStream stream)
						throws InnerClassException
						{
							assertNotNull(stream);

							return null;
						}
					}, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstCharacterStream()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeUseFirstCharacterStream(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		try
		{
			manager.executeUseFirstCharacterStream(new Select(mDatasource).from("tbltest"), null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstCharacterStream()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("firstcol", String.class, 50).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("firstcol")
				.where("lastcol", "=", "Doe");

			manager.executeUseFirstCharacterStream(select_query, new ReaderUser() {
					public Object useReader(Reader reader)
					throws InnerClassException
					{
						assertNull(reader);

						return null;
					}
				});

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "John", "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "Piet", "lastcol", "Smith"}));

			manager.executeUseFirstCharacterStream(select_query, new ReaderUser() {
					public Object useReader(Reader reader)
					throws InnerClassException
					{
						assertNotNull(reader);

							try
							{
								assertEquals("John", FileUtils.readString(reader));
							}
							catch (FileUtilsErrorException e)
							{
								assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
							}

						return null;
					}
				});

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("firstcol")
				.whereParameter("lastcol", "=");
			manager.executeUseFirstCharacterStream(select_query, new ReaderUser() {
				public Object useReader(Reader reader)
				throws InnerClassException
				{
					assertNotNull(reader);

					try
					{
						assertEquals("Piet", FileUtils.readString(reader));
					}
					catch (FileUtilsErrorException e)
					{
						assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
					}

					return null;
				}
			}, new DbPreparedStatementHandler() {
				 public void setParameters(DbPreparedStatement statement)
				 {
					 statement
						 .setString("lastcol", "Smith");
				 }
			});
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstCharacterStreamConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeUseFirstCharacterStream(select_query, new ReaderUser() {
						public Object useReader(Reader reader)
						throws InnerClassException
						{
							assertNotNull(reader);

							return null;
						}
					}, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteGetFirstBinaryStream()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeUseFirstBinaryStream(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		try
		{
			manager.executeUseFirstBinaryStream(new Select(mDatasource).from("tbltest"), null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteGetFirstBinaryStream()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			if (mDatasource.getAliasedDriver().equals("org.apache.derby.jdbc.EmbeddedDriver") ||
				mDatasource.getAliasedDriver().equals("com.mckoi.JDBCDriver"))
			{
				create_query.table("tbltest").column("firstcol", Blob.class).column("lastcol", String.class, 50);
			}
			else
			{
				create_query.table("tbltest").column("firstcol", String.class).column("lastcol", String.class, 50);
			}
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("firstcol")
				.where("lastcol", "=", "Doe");

			manager.executeUseFirstBinaryStream(select_query, new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNull(stream);

						return null;
					}
				});

			manager.executeUpdate(new Insert(mDatasource)
									  .into("tbltest")
									  .fieldParameter("firstcol")
									  .field("lastcol", "Doe"), new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						if (mDatasource.getAliasedDriver().equals("org.apache.derby.jdbc.EmbeddedDriver") ||
							mDatasource.getAliasedDriver().equals("com.mckoi.JDBCDriver") ||
							mDatasource.getAliasedDriver().equals("org.h2.Driver"))
						{
							try
							{
								statement.setBytes("firstcol", "John".getBytes("UTF-8"));
							}
							catch (UnsupportedEncodingException e)
							{
								assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
							}
						}
						else
						{
							statement.setString("firstcol", "John");
						}
					}
				});
			manager.executeUpdate(new Insert(mDatasource)
									  .into("tbltest")
									  .fieldParameter("firstcol")
									  .field("lastcol", "Smith"), new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						if (mDatasource.getAliasedDriver().equals("org.apache.derby.jdbc.EmbeddedDriver") ||
							mDatasource.getAliasedDriver().equals("com.mckoi.JDBCDriver") ||
							mDatasource.getAliasedDriver().equals("org.h2.Driver"))
						{
							try
							{
								statement.setBytes("firstcol", "Piet".getBytes("UTF-8"));
							}
							catch (UnsupportedEncodingException e)
							{
								assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
							}
						}
						else
						{
							statement.setString("firstcol", "Piet");
						}
					}
				});

			manager.executeUseFirstBinaryStream(select_query, new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNotNull(stream);
						try
						{
							assertEquals("John", FileUtils.readString(stream, "UTF-8"));
						}
						catch (FileUtilsErrorException e)
						{
							assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
						}

						return null;
					}
				});

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("firstcol")
				.whereParameter("lastcol", "=");
			manager.executeUseFirstBinaryStream(select_query, new InputStreamUser() {
					public Object useInputStream(InputStream stream)
					throws InnerClassException
					{
						assertNotNull(stream);
						try
						{
							assertEquals("Piet", FileUtils.readString(stream));
						}
						catch (FileUtilsErrorException e)
						{
							assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
						}

						return null;
					}
				}, new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("lastcol", "Smith");
					 }
				});
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteGetFirstBinaryStreamConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("name")
				.where("name", "=", "me");
			try
			{
				manager.executeUseFirstBinaryStream(select_query, new InputStreamUser() {
						public Object useInputStream(InputStream stream)
						throws InnerClassException
						{
							assertNotNull(stream);

							return null;
						}
					}, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteFetchFirst()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeFetchFirst(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteFetchFirst()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("datacol", String.class, 50)
				.column("valuecol", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("datacol", "sometext")
				.field("valuecol", "thevalue");
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol");

			DbRowProcessorSuccess processor = null;

			processor = new DbRowProcessorSuccess();
			assertTrue(manager.executeFetchFirst(select_query, processor));
			assertEquals(processor.getCounter(), 1);
			assertTrue(manager.executeFetchFirst(select_query, processor));
			assertEquals(processor.getCounter(), 2);

			select_query
				.whereParameter("valuecol", "=");

			processor = new DbRowProcessorSuccess();
			assertTrue(manager.executeFetchFirst(select_query, processor, new DbPreparedStatementHandler() {
								 public void setParameters(DbPreparedStatement statement)
								 {
									 statement
										 .setString("valuecol", "thevalue");
								 }
							}));
			assertEquals(processor.getCounter(), 1);

			processor = new DbRowProcessorSuccess();
			assertFalse(manager.executeFetchFirst(select_query, processor, new DbPreparedStatementHandler() {
								 public void setParameters(DbPreparedStatement statement)
								 {
									 statement
										 .setString("valuecol", "not present");
								 }
							}));
			assertEquals(processor.getCounter(), 0);
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteFetchFirstConcludeError()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("datacol", String.class, 50)
				.column("valuecol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol");

			try
			{
				manager.executeFetchFirst(select_query, null, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteFetchFirstBean()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeFetchFirstBean(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteFetchFirstBean()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").columns(BeanImplConstrained.class);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest").fields(BeanImplConstrained.getPopulatedBean());
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest");

			BeanImplConstrained bean = null;

			bean = manager.executeFetchFirstBean(select_query, BeanImplConstrained.class);
			assertNotNull(bean);

			BeanImplConstrained bean_populated = BeanImplConstrained.getPopulatedBean();
			assertEquals(bean.getPropertyString(), bean_populated.getPropertyString());
			assertEquals(bean.getPropertyStringbuffer().toString(), bean_populated.getPropertyStringbuffer().toString());
			// don't compare milliseconds since each db stores it differently
			assertEquals((bean.getPropertyDate().getTime()/1000)*1000, (bean_populated.getPropertyDate().getTime()/1000)*1000);
			assertEquals((bean.getPropertyCalendar().getTime().getTime()/1000)*1000, (bean_populated.getPropertyCalendar().getTime().getTime()/1000)*1000);
			assertEquals((bean.getPropertyTimestamp().getTime()/1000)*1000, (bean_populated.getPropertyTimestamp().getTime()/1000)*1000);
			assertEquals(bean.getPropertySqlDate().toString(), bean_populated.getPropertySqlDate().toString());
			assertEquals(bean.getPropertyTime().toString(), bean_populated.getPropertyTime().toString());
			assertEquals(bean.isPropertyBoolean(), bean_populated.isPropertyBoolean());
			assertEquals(bean.getPropertyChar(), bean_populated.getPropertyChar());
			assertFalse(bean.getPropertyByte() == bean_populated.getPropertyByte()); // byte is not saved
			assertEquals(bean.getPropertyDouble(), bean_populated.getPropertyDouble(), 0.001);
			assertEquals(bean.getPropertyFloat(), bean_populated.getPropertyFloat(), 0.001);
			assertEquals(bean.getPropertyDoubleObject().doubleValue(), bean_populated.getPropertyDoubleObject().doubleValue(), 0.01);
			assertEquals(bean.getPropertyFloatObject().floatValue(), bean_populated.getPropertyFloatObject().floatValue(), 0.01);
			assertEquals(bean.getPropertyInt(), bean_populated.getPropertyInt());
			assertFalse(bean.getPropertyLong() == bean_populated.getPropertyLong()); // long is not persistent
			assertEquals(bean.getPropertyShort(), bean_populated.getPropertyShort());
			assertEquals(bean.getPropertyBigDecimal(), bean_populated.getPropertyBigDecimal());

			select_query
				.whereParameter("propertyString", "=");

			bean = manager.executeFetchFirstBean(select_query, BeanImplConstrained.class, new DbPreparedStatementHandler() {
							 public void setParameters(DbPreparedStatement statement)
							 {
								 statement
									 .setString("propertyString", "someotherstring");
							 }
						});
			assertNotNull(bean);
			assertEquals(bean.getPropertyString(), bean_populated.getPropertyString());
			assertEquals(bean.getPropertyStringbuffer().toString(), bean_populated.getPropertyStringbuffer().toString());
			assertEquals((bean.getPropertyDate().getTime()/1000)*1000, (bean_populated.getPropertyDate().getTime()/1000)*1000);
			assertEquals((bean.getPropertyCalendar().getTime().getTime()/1000)*1000, (bean_populated.getPropertyCalendar().getTime().getTime()/1000)*1000);
			assertEquals((bean.getPropertyTimestamp().getTime()/1000)*1000, (bean_populated.getPropertyTimestamp().getTime()/1000)*1000);
			assertEquals(bean.getPropertySqlDate().toString(), bean_populated.getPropertySqlDate().toString());
			assertEquals(bean.getPropertyTime().toString(), bean_populated.getPropertyTime().toString());
			assertEquals(bean.isPropertyBoolean(), bean_populated.isPropertyBoolean());
			assertEquals(bean.getPropertyChar(), bean_populated.getPropertyChar());
			assertFalse(bean.getPropertyByte() == bean_populated.getPropertyByte()); // byte is not saved
			assertEquals(bean.getPropertyDouble(), bean_populated.getPropertyDouble(), 0.001);
			assertEquals(bean.getPropertyFloat(), bean_populated.getPropertyFloat(), 0.001);
			assertEquals(bean.getPropertyDoubleObject().doubleValue(), bean_populated.getPropertyDoubleObject().doubleValue(), 0.01);
			assertEquals(bean.getPropertyFloatObject().floatValue(), bean_populated.getPropertyFloatObject().floatValue(), 0.01);
			assertEquals(bean.getPropertyInt(), bean_populated.getPropertyInt());
			assertFalse(bean.getPropertyLong() == bean_populated.getPropertyLong()); // long is not persistent
			assertEquals(bean.getPropertyShort(), bean_populated.getPropertyShort());
			assertEquals(bean.getPropertyBigDecimal(), bean_populated.getPropertyBigDecimal());

			bean = manager.executeFetchFirstBean(select_query, BeanImplConstrained.class, new DbPreparedStatementHandler() {
							 public void setParameters(DbPreparedStatement statement)
							 {
								 statement
									 .setString("propertyString", "not present");
							 }
						});
			assertNull(bean);
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteFetchFirstBeanConcludeError()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").columns(BeanImplConstrained.class);
			manager.executeUpdate(create_query);

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest");

			try
			{
				manager.executeFetchFirstBean(select_query, BeanImplConstrained.class, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteFetchAll()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeFetchAll(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteFetchAll()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("datacol", String.class, 50)
				.column("valuecol", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("datacol", "sometext1")
				.field("valuecol", "thevalue1");
			assertEquals(1, manager.executeUpdate(insert_query));
			insert_query.clear();
			insert_query.into("tbltest")
				.field("datacol", "sometext2")
				.field("valuecol", "thevalue2");
			assertEquals(1, manager.executeUpdate(insert_query));
			insert_query.clear();
			insert_query.into("tbltest")
				.field("datacol", "sometext2b")
				.field("valuecol", "thevalue2");
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol");

			DbRowProcessorSuccess processor = null;

			processor = new DbRowProcessorSuccess();
			assertTrue(manager.executeFetchAll(select_query, processor));
			assertEquals(processor.getCounter(), 2); // limited to maximum 2 by the rowprocessor

			select_query
				.whereParameter("valuecol", "=");

			processor = new DbRowProcessorSuccess();
			assertTrue(manager.executeFetchAll(select_query, processor, new DbPreparedStatementHandler() {
								 public void setParameters(DbPreparedStatement statement)
								 {
									 statement
										 .setString("valuecol", "thevalue2");
								 }
							}));
			assertEquals(processor.getCounter(), 2);

			processor = new DbRowProcessorSuccess();
			assertFalse(manager.executeFetchAll(select_query, processor, new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("valuecol", "not present");
					 }
				}));
			assertEquals(processor.getCounter(), 0);
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteFetchAllConcludeError()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("datacol", String.class, 50)
				.column("valuecol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("datacol");

			try
			{
				DbRowProcessorSuccess processor = new DbRowProcessorSuccess();
				manager.executeFetchAll(select_query, processor, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteFetchAllBeans()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeFetchAllBeans(null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteFetchAllBeans()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").columns(BeanImplConstrained.class);
			manager.executeUpdate(create_query);

			BeanImplConstrained bean = null;
			Insert insert_query = new Insert(mDatasource);
			bean = BeanImplConstrained.getPopulatedBean();
			bean.setPropertyString("someotherstring");
			bean.setPropertyStringbuffer(new StringBuffer("someotherstringbuf1"));
			insert_query.into("tbltest").fields(bean);
			assertEquals(1, manager.executeUpdate(insert_query));
			insert_query.clear();
			bean = BeanImplConstrained.getPopulatedBean();
			bean.setPropertyString("one");
			bean.setPropertyStringbuffer(new StringBuffer("someotherstringbuf2"));
			insert_query.into("tbltest").fields(bean);
			assertEquals(1, manager.executeUpdate(insert_query));
			insert_query.clear();
			bean = BeanImplConstrained.getPopulatedBean();
			bean.setPropertyString("tw''o");
			bean.setPropertyStringbuffer(new StringBuffer("someotherstringbuf3"));
			insert_query.into("tbltest").fields(bean);
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest");

			BeanImplConstrained bean_populated = BeanImplConstrained.getPopulatedBean();
			List<BeanImplConstrained> beans = null;

			beans = manager.executeFetchAllBeans(select_query, BeanImplConstrained.class);
			assertNotNull(beans);
			assertEquals(beans.size(), 3);
			for (BeanImplConstrained bean2 : beans)
			{
				assertTrue(bean2.getPropertyString().equals("someotherstring") || bean2.getPropertyString().equals("one") || bean2.getPropertyString().equals("tw''o"));
				assertTrue(bean2.getPropertyStringbuffer().toString().equals("someotherstringbuf1") || bean2.getPropertyStringbuffer().toString().equals("someotherstringbuf2") || bean2.getPropertyStringbuffer().toString().equals("someotherstringbuf3"));
				// don't compare milliseconds since each db stores it differently
				assertEquals((bean2.getPropertyDate().getTime()/1000)*1000, (bean_populated.getPropertyDate().getTime()/1000)*1000);
				assertEquals((bean2.getPropertyCalendar().getTime().getTime()/1000)*1000, (bean_populated.getPropertyCalendar().getTime().getTime()/1000)*1000);
				assertEquals((bean2.getPropertyTimestamp().getTime()/1000)*1000, (bean_populated.getPropertyTimestamp().getTime()/1000)*1000);
				assertEquals(bean2.getPropertySqlDate().toString(), bean_populated.getPropertySqlDate().toString());
				assertEquals(bean2.getPropertyTime().toString(), bean_populated.getPropertyTime().toString());
				assertEquals(bean2.isPropertyBoolean(), bean_populated.isPropertyBoolean());
				assertEquals(bean2.getPropertyChar(), bean_populated.getPropertyChar());
				assertFalse(bean2.getPropertyByte() == bean_populated.getPropertyByte()); // byte is not saved
				assertEquals(bean2.getPropertyDouble(), bean_populated.getPropertyDouble(), 0.001);
				assertEquals(bean2.getPropertyFloat(), bean_populated.getPropertyFloat(), 0.001);
				assertEquals(bean2.getPropertyDoubleObject().doubleValue(), bean_populated.getPropertyDoubleObject().doubleValue(), 0.01);
				assertEquals(bean2.getPropertyFloatObject().floatValue(), bean_populated.getPropertyFloatObject().floatValue(), 0.01);
				assertEquals(bean2.getPropertyInt(), bean_populated.getPropertyInt());
				assertFalse(bean2.getPropertyLong() == bean_populated.getPropertyLong()); // long is not persistent
				assertEquals(bean2.getPropertyShort(), bean_populated.getPropertyShort());
				assertEquals(bean2.getPropertyBigDecimal(), bean_populated.getPropertyBigDecimal());
			}

			select_query
				.whereParameter("propertyString", "=");

			beans = manager.executeFetchAllBeans(select_query, BeanImplConstrained.class, new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("propertyString", "one");
					 }
				});
			assertNotNull(beans);
			assertEquals(beans.size(), 1);
			BeanImplConstrained bean2 = beans.get(0);
			assertEquals(bean2.getPropertyString(), "one");
			assertEquals(bean2.getPropertyStringbuffer().toString(), "someotherstringbuf2");
			// don't compare milliseconds since each db stores it differently
			assertEquals((bean2.getPropertyDate().getTime()/1000)*1000, (bean_populated.getPropertyDate().getTime()/1000)*1000);
			assertEquals((bean2.getPropertyCalendar().getTime().getTime()/1000)*1000, (bean_populated.getPropertyCalendar().getTime().getTime()/1000)*1000);
			assertEquals((bean2.getPropertyTimestamp().getTime()/1000)*1000, (bean_populated.getPropertyTimestamp().getTime()/1000)*1000);
			assertEquals(bean2.getPropertySqlDate().toString(), bean_populated.getPropertySqlDate().toString());
			assertEquals(bean2.getPropertyTime().toString(), bean_populated.getPropertyTime().toString());
			assertEquals(bean2.isPropertyBoolean(), bean_populated.isPropertyBoolean());
			assertEquals(bean2.getPropertyChar(), bean_populated.getPropertyChar());
			assertFalse(bean2.getPropertyByte() == bean_populated.getPropertyByte()); // byte is not saved
			assertEquals(bean2.getPropertyDouble(), bean_populated.getPropertyDouble(), 0.001);
			assertEquals(bean2.getPropertyFloat(), bean_populated.getPropertyFloat(), 0.001);
			assertEquals(bean2.getPropertyDoubleObject().doubleValue(), bean_populated.getPropertyDoubleObject().doubleValue(), 0.01);
			assertEquals(bean2.getPropertyFloatObject().floatValue(), bean_populated.getPropertyFloatObject().floatValue(), 0.01);
			assertEquals(bean2.getPropertyInt(), bean_populated.getPropertyInt());
			assertFalse(bean2.getPropertyLong() == bean_populated.getPropertyLong()); // long is not persistent
			assertEquals(bean2.getPropertyShort(), bean_populated.getPropertyShort());
			assertEquals(bean2.getPropertyBigDecimal(), bean_populated.getPropertyBigDecimal());

			beans = manager.executeFetchAllBeans(select_query, BeanImplConstrained.class, new DbPreparedStatementHandler() {
					 public void setParameters(DbPreparedStatement statement)
					 {
						 statement
							 .setString("propertyString", "not present");
					 }
				});
			assertNotNull(beans);
			assertEquals(beans.size(), 0);
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteFetchAllBeansConcludeError()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").columns(BeanImplConstrained.class);
			manager.executeUpdate(create_query);

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest");

			try
			{
				manager.executeFetchAllBeans(select_query, BeanImplConstrained.class, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteQueryDbPreparedStatementHandler()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeQuery((Select)null, (DbPreparedStatementHandler)null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteQueryDbPreparedStatementHandler()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("firstcol", String.class, 50).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.where("lastcol", "=", "Doe");

			assertNull(manager.executeQuery(select_query, (DbPreparedStatementHandler)null));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "John", "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "Piet", "lastcol", "Smith"}));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.whereParameter("lastcol", "=");
			assertEquals("Piet Smith", manager.executeQuery(select_query, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("lastcol", "Smith");
					}

					public Object concludeResults(DbResultSet resultSet)
						throws SQLException
					{
						if (resultSet.next())
						{
							return resultSet.getString("firstcol")+" "+resultSet.getString("lastcol");
						}

						return null;
					}
				}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteQueryDbPreparedStatementHandlerConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest");
			try
			{
				manager.executeQuery(select_query, new DbPreparedStatementHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteQueryDbResultSetHandler()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeQuery((Select)null, (DbResultSetHandler)null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteQueryDbResultSetHandler()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("firstcol", String.class, 50).column("lastcol", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.where("lastcol", "=", "Doe");

			assertNull(manager.executeQuery(select_query, (DbResultSetHandler)null));

			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "John", "lastcol", "Doe"}));
			manager.executeUpdate(new Insert(mDatasource).into("tbltest").fields(new String[] {"firstcol", "Piet", "lastcol", "Smith"}));

			select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.where("lastcol", "=", "Doe");
			assertEquals("John Doe", manager.executeQuery(select_query, new DbResultSetHandler() {
					public Object concludeResults(DbResultSet resultSet)
						throws SQLException
					{
						if (resultSet.next())
						{
							return resultSet.getString("firstcol")+" "+resultSet.getString("lastcol");
						}

						return null;
					}
				}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteQueryDbResultSetHandlerConcludeErrors()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest").column("name", String.class, 50);
			manager.executeUpdate(create_query);

			Select select_query = null;
			select_query = new Select(mDatasource);
			select_query.from("tbltest");
			try
			{
				manager.executeQuery(select_query, new DbResultSetHandler() {
						public Object concludeResults(DbResultSet resultSet)
							throws SQLException
						{
							return resultSet.getString("unknown");
						}
					});
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testReserveConnection()
	{
		final DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			assertEquals("test", manager.reserveConnection(new DbConnectionUser() {
					public Object useConnection(final DbConnection connection)
					{
						assertSame(manager.getConnection(), connection);
						new Thread() {
							public void run()
							{
								assertNotSame(manager.getConnection(), connection);
							}
						}.start();
						return "test";
					}
				}));
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalExecuteQuerySql()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.executeQuery((ReadQuery)null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testExecuteQueryBuilderSuccess()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("column1", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext");
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("column1");
			DbStatement statement = manager.executeQuery(select_query);
			try
			{
				assertNotNull(statement);
			}
			finally
			{
				statement.close();
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteQueryBuilderError()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("column1", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext");
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("column2");
			try
			{
				manager.executeQuery(select_query);
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalFetch()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.fetch((ResultSet)null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testFetchSuccess()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("column1", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext");
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("column1");

			DbRowProcessorSuccess processor = new DbRowProcessorSuccess();
			DbStatement statement = null;
			DbResultSet resultset = null;
			try
			{
				statement = manager.executeQuery(select_query);
				resultset = statement.getResultSet();
				assertTrue(manager.fetch(resultset, processor));
				assertEquals(processor.getCounter(), 1);
				assertFalse(manager.fetch(resultset, processor));
				assertEquals(processor.getCounter(), 1);
			}
			finally
			{
				statement.close();
			}

			statement = manager.executeQuery(select_query);
			try
			{
				resultset = statement.getResultSet();
				assertTrue(manager.fetch(resultset));
				assertFalse(manager.fetch(resultset));
			}
			finally
			{
				statement.close();
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testFetchError()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("column1", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext");
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("column1");

			DbStatement statement1 = manager.executeQuery(select_query);
			try
			{
				manager.fetch(statement1.getResultSet(), new DbRowProcessorError());
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
			finally
			{
				statement1.close();
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalFetchAll()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			manager.fetchAll((ResultSet)null, null);
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
	}

	public void testFetchAllSuccess()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("column1", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext");
			assertEquals(1, manager.executeUpdate(insert_query));

			insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext2");
			assertEquals(1, manager.executeUpdate(insert_query));

			insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext2");
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("column1");

			DbRowProcessorSuccess processor = new DbRowProcessorSuccess();
			DbStatement statement = manager.executeQuery(select_query);
			try
			{
				assertTrue(manager.fetchAll(statement.getResultSet(), processor));
			}
			finally
			{
				statement.close();
			}
			assertEquals(processor.getCounter(), 2);
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testFetchAllError()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);
		try
		{
			CreateTable create_query = new CreateTable(mDatasource);
			create_query.table("tbltest")
				.column("column1", String.class, 50);
			manager.executeUpdate(create_query);

			Insert insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext");
			assertEquals(1, manager.executeUpdate(insert_query));

			insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext2");
			assertEquals(1, manager.executeUpdate(insert_query));

			insert_query = new Insert(mDatasource);
			insert_query.into("tbltest")
				.field("column1", "sometext2");
			assertEquals(1, manager.executeUpdate(insert_query));

			Select select_query = new Select(mDatasource);
			select_query.from("tbltest")
				.field("column1");

			try
			{
				DbStatement statement = manager.executeQuery(select_query);
				try
				{
					manager.fetchAll(statement.getResultSet(), new DbRowProcessorError());
				}
				finally
				{
					statement.close();
				}
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(true);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testClone()
	{
		DbQueryManager manager = new DbQueryManager(mDatasource);

		CreateTable create_query = new CreateTable(mDatasource);
		create_query.table("tbltest").column("column1", String.class, 50);
		manager.executeUpdate(create_query);

		DbQueryManager manager2 = (DbQueryManager)manager.clone();
		DbPreparedStatement statement = null;
		try
		{
			statement = manager2.getConnection().getPreparedStatement(new Insert(mDatasource).into("tbltest").fieldParameter("column1"));
			assertNotNull(statement);
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			statement.close();
		}
	}
	
	class DbRowProcessorSuccess extends DbRowProcessor
	{
		private int	mCounter = 0;
		
		public DbRowProcessorSuccess()
		{
		}

		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			if (2 == mCounter)
			{
				return false;
			}
			
			mCounter++;
			return true;
		}
		
		public int getCounter()
		{
			return mCounter;
		}
	}
	
	class DbRowProcessorError extends DbRowProcessor
	{
		public DbRowProcessorError()
		{
		}

		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			resultSet.getString("inexistant_column");
			return false;
		}
	}
}
