/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDbPreparedStatement.java 3920 2008-04-14 19:58:18Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.*;
import com.uwyn.rife.database.queries.*;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

public class TestDbPreparedStatement extends TestCase
{
	private Datasource		mDatasource = null;

	public TestDbPreparedStatement(Datasource datasource, String datasourceName, String name)
	{
		super(name);
		mDatasource = datasource;
	}

	public void setUp()
	{
		// create the temporary table
		CreateTable query_create = new CreateTable(mDatasource);
		query_create.table("parametersbean")
			.columns(BeanImpl.class)
			.column("notbeanInt", int.class)
			.precision("propertyString", 255)
			.precision("propertyStringbuffer", 255)
			.precision("propertyChar", 1)
			.precision("propertyDouble", 7, 2)
			.precision("propertyFloat", 8, 3)
			.precision("propertyBigDecimal", 16, 6);
		DbStatement statement = mDatasource.getConnection().createStatement();
		try
		{
			try
			{
				statement.executeUpdate(query_create);
			}
			catch (DatabaseException e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			try
			{
				statement.close();
			}
			catch (DatabaseException e)
			{
				// do nothing
			}
		}
	}

	public void tearDown()
	{
		try
		{
			DbConnection connection = mDatasource.getConnection();

			// drop temporary table
			DropTable query_drop = new DropTable(mDatasource);
			query_drop.table("parametersbean");
			connection.createStatement().executeUpdate(query_drop);
			
			connection.close();
		}
		catch (DatabaseException e)
		{
			fail(ExceptionUtils.getExceptionStackTrace(e));
		}
	}
	
	public void testInstationSql()
	{
		try
		{
			String sql = "DELETE FROM parametersbean";
			DbPreparedStatement statement_delete = mDatasource.getConnection().getPreparedStatement(sql);
			assertEquals(sql, statement_delete.getSql());
			assertNull(statement_delete.getQuery());
			statement_delete.executeUpdate();
			statement_delete.close();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testInstationQuery()
	{
		try
		{
			Delete query_delete = new Delete(mDatasource);
			query_delete
				.from("parametersbean");
			DbPreparedStatement statement_delete = mDatasource.getConnection().getPreparedStatement(query_delete);
			assertEquals(query_delete.getSql(), statement_delete.getSql());
			assertEquals(query_delete, statement_delete.getQuery());
			statement_delete.executeUpdate();
			statement_delete.close();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteQuery()
	{
		try
		{
			Select query_select = new Select(mDatasource);
			query_select
				.from("parametersbean");
			DbPreparedStatement statement_select = mDatasource.getConnection().getPreparedStatement(query_select);
			statement_select.executeQuery();
			assertNotNull(statement_select.getResultSet());
			statement_select.close();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteQueryException()
	{
		try
		{
			Select query_select = new Select(mDatasource);
			query_select
				.from("inexistenttable");

			DbPreparedStatement statement_select = null;
			try
			{
				statement_select = mDatasource.getConnection().getPreparedStatement(query_select);

				try
				{
					statement_select.executeQuery();
					fail();
				}
				catch (ExecutionErrorException e)
				{
					assertSame(mDatasource, e.getDatasource());
					assertEquals(query_select.getSql(), e.getSql());
				}
				assertNull(statement_select.getResultSet());
			}
			catch (PreparedStatementCreationErrorException e)
			{
				assertSame(mDatasource, e.getDatasource());
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteUpdate()
	{
		try
		{
			Delete query_delete = new Delete(mDatasource);
			query_delete
				.from("parametersbean");
			DbPreparedStatement statement_select = mDatasource.getConnection().getPreparedStatement(query_delete);
			statement_select.executeUpdate();
			statement_select.close();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testExecuteUpdateException()
	{
		try
		{
			Delete query_delete = new Delete(mDatasource);
			query_delete
				.from("inexistenttable");
			DbPreparedStatement statement_update = null;

			try
			{
				statement_update = mDatasource.getConnection().getPreparedStatement(query_delete);
				try
				{
					statement_update.executeUpdate();
					fail();
				}
				catch (ExecutionErrorException e)
				{
					assertSame(mDatasource, e.getDatasource());
					assertEquals(query_delete.getSql(), e.getSql());
				}
			}
			catch (PreparedStatementCreationErrorException e)
			{
				assertSame(mDatasource, e.getDatasource());
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testNotParametrized()
	{
		try
		{
			String sql = "SELECT * FROM parametersbean WHERE propertyString = ?";
			DbPreparedStatement statement_select = mDatasource.getConnection().getPreparedStatement(sql);
			try
			{
				statement_select.setString("propertyString", "ok");
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(e instanceof NoParametrizedQueryException);
				assertSame(statement_select, ((NoParametrizedQueryException)e).getPreparedStatement());
			}
			statement_select.close();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testNoParameters()
	{
		try
		{
			Select query_select = new Select(mDatasource);
			query_select.from("parametersbean");
			DbPreparedStatement statement_select = mDatasource.getConnection().getPreparedStatement(query_select);
			try
			{
				statement_select.setString("propertyString", "ok");
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(e instanceof NoParametersException);
				assertSame(statement_select, ((NoParametersException)e).getPreparedStatement());
			}
			statement_select.close();
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testAddBatch()
	{
		CreateTable query_create = new CreateTable(mDatasource);
		query_create
			.table("batchtest")
			.column("intcol", int.class);
		try
		{
			DbPreparedStatement statement_create = mDatasource.getConnection().getPreparedStatement(query_create);
			statement_create.executeUpdate();
			statement_create.close();

			Insert query_insert = new Insert(mDatasource);
			query_insert
				.into(query_create.getTable())
				.fieldParameter("intcol");
			DbPreparedStatement statement_insert = mDatasource.getConnection().getPreparedStatement(query_insert);
			int first = 1;
			int second = 5;
			int third = 9;
			int fourth = 12;
			statement_insert.setInt("intcol", first);
			statement_insert.addBatch();
			statement_insert.setInt("intcol", second);
			statement_insert.addBatch();
			statement_insert.setInt("intcol", third);
			statement_insert.addBatch();
			statement_insert.setInt("intcol", fourth);
			statement_insert.addBatch();
			statement_insert.executeBatch();
			statement_insert.close();

			Select query_select = new Select(mDatasource);
			query_select
				.from(query_create.getTable());
			DbStatement statement_select = mDatasource.getConnection().createStatement();
			statement_select.executeQuery(query_select);
			boolean got_first = false;
			boolean got_second = false;
			boolean got_third = false;
			boolean got_fourth = false;
			ResultSet resultset = statement_select.getResultSet();
			int result = -1;
			while (resultset.next())
			{
				result = resultset.getInt("intcol");
				if (first == result)
				{
					if (got_first)
					{
						assertTrue("Got "+first+" more than once", false);
					}
					got_first = true;
				}
				else if (second == result)
				{
					if (got_second)
					{
						assertTrue("Got "+second+" more than once", false);
					}
					got_second = true;
				}
				else if (third == result)
				{
					if (got_third)
					{
						assertTrue("Got "+third+" more than once", false);
					}
					got_third = true;
				}
				else if (fourth == result)
				{
					if (got_fourth)
					{
						assertTrue("Got "+fourth+" more than once", false);
					}
					got_fourth = true;
				}
				else
				{
					assertTrue("Unknown value : "+result, false);
				}
			}
			statement_select.close();

			assertTrue(got_first);
			assertTrue(got_second);
			assertTrue(got_third);
			assertTrue(got_fourth);
		}
		catch (SQLException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				DropTable query_drop = new DropTable(mDatasource);
				query_drop
					.table(query_create.getTable());
				DbPreparedStatement statement_drop = mDatasource.getConnection().getPreparedStatement(query_drop);
				statement_drop.executeUpdate();
				statement_drop.close();
			}
			catch (DatabaseException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}

	public void testGetMetaData()
	{

		try
		{
			Select query_select = new Select(mDatasource);
			query_select
				.from("parametersbean")
				.whereParameter("propertyString", "=");
			DbPreparedStatement statement_select = mDatasource.getConnection().getPreparedStatement(query_select);
			statement_select.setString("propertyString", "ok");
			ResultSetMetaData metadata = null;
			metadata = statement_select.getMetaData();
			assertNotNull(metadata);
			statement_select.close();
		}
		catch (DatabaseException e)
		{
			if (e.getCause() != null)
			{
				// mysql
				if (e.getCause().getClass().getName().equals("com.mysql.jdbc.NotImplemented"))
				{
					return;
				}
				// oracle
				if (e.getCause().getClass().getName().equals("java.sql.SQLException") &&
					e.getCause().getMessage().indexOf("statement handle not executed: getMetaData") != -1)
				{
					return;
				}
				// mckoi
				if (e.getCause().getClass().getName().equals("com.mckoi.database.jdbc.MSQLException") &&
					e.getCause().getMessage().startsWith("Not Supported"))
				{
					return;
				}
			}

			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testGetParameterMetaData()
	{

		try
		{
			Select query_select = new Select(mDatasource);
			query_select
				.from("parametersbean")
				.whereParameter("propertyString", "=");
			DbPreparedStatement statement_select = mDatasource.getConnection().getPreparedStatement(query_select);
			statement_select.setString("propertyString", "ok");
			ParameterMetaData metadata = null;
			try
			{
				metadata = statement_select.getParameterMetaData();
				assertNotNull(metadata);
			}
			catch (AbstractMethodError e)
			{
				assertTrue(mDatasource.getDriver().equals("oracle.jdbc.driver.OracleDriver") ||
						   mDatasource.getDriver().equals("org.apache.derby.jdbc.EmbeddedDriver"));
			}
			statement_select.close();
		}
		catch (DatabaseException e)
		{
			if (e.getCause() != null)
			{
				if (e.getCause().getClass().getName().equals("com.mysql.jdbc.NotImplemented"))
				{
					return;
				}
				if (e.getCause().getClass().getName().equals("org.postgresql.util.PSQLException") &&
					e.getCause().getMessage().equals("This method is not yet implemented."))
				{
					return;
				}
				if (e.getCause().getClass().getName().equals("com.mckoi.database.jdbc.MSQLException") &&
					e.getCause().getMessage().startsWith("Not Supported"))
				{
					return;
				}
			}

			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testSetBeanNull()
	{
		try
		{
			// insert some data
			Insert query_insert = new Insert(mDatasource);
			query_insert.into("parametersbean")
				.fieldsParameters(BeanImpl.class);
			DbPreparedStatement statement_insert = mDatasource.getConnection().getPreparedStatement(query_insert);
			try
			{
				statement_insert.setBean(null);
				fail();
			}
			catch (IllegalArgumentException e)
			{
				assertTrue(true);
			}
			finally
			{
				statement_insert.close();
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testSetBeanError()
	{
		try
		{
			// insert some data
			Insert query_insert = new Insert(mDatasource);
			query_insert.into("parametersbean")
				.fieldsParameters(BeanImpl.class);
			DbPreparedStatement statement_insert = mDatasource.getConnection().getPreparedStatement(query_insert);
			try
			{
				statement_insert.setBean(BeanErrorImpl.getPopulatedBean());
				fail();
			}
			catch (DatabaseException e)
			{
				assertTrue(e.getCause() instanceof BeanUtilsException);
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testSetBean()
	{
		try
		{
			// insert some data
			Insert query_insert = new Insert(mDatasource);
			query_insert.into("parametersbean")
				.fieldsParameters(BeanImpl.class)
				.fieldParameter("notbeanInt");
			DbPreparedStatement statement_insert = mDatasource.getConnection().getPreparedStatement(query_insert);
			try
			{
				try
				{
					statement_insert.setBean(null);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				statement_insert.setBean(BeanImpl.getPopulatedBean());
				statement_insert.setInt("notbeanInt", 23);
				statement_insert.executeUpdate();
	
				// retrieve the data
				BeanManager bean_manager = new BeanManager();
				BeanImpl retrieved_bean = bean_manager.fetchBean();
				BeanImpl new_bean = BeanImpl.getPopulatedBean();
				assertEquals(retrieved_bean.getPropertyString(), new_bean.getPropertyString());
				assertEquals(retrieved_bean.getPropertyStringbuffer().toString(), new_bean.getPropertyStringbuffer().toString());
	
				// don't compare milliseconds since each db stores it differently
				assertEquals((retrieved_bean.getPropertyDate().getTime()/1000)*1000, (new_bean.getPropertyDate().getTime()/1000)*1000);
				assertEquals((retrieved_bean.getPropertyCalendar().getTime().getTime()/1000)*1000, (new_bean.getPropertyCalendar().getTime().getTime()/1000)*1000);
				assertEquals((retrieved_bean.getPropertyTimestamp().getTime()/1000)*1000, (new_bean.getPropertyTimestamp().getTime()/1000)*1000);
	
				assertEquals(retrieved_bean.getPropertySqlDate().toString(), new_bean.getPropertySqlDate().toString());
				assertEquals(retrieved_bean.getPropertyTime().toString(), new_bean.getPropertyTime().toString());
				assertEquals(retrieved_bean.getPropertyChar(), new_bean.getPropertyChar());
				assertEquals(retrieved_bean.getPropertyCharacterObject(), new_bean.getPropertyCharacterObject());
				assertEquals(retrieved_bean.isPropertyBoolean(), new_bean.isPropertyBoolean());
				assertEquals(retrieved_bean.getPropertyBooleanObject(), new_bean.getPropertyBooleanObject());
				assertEquals(retrieved_bean.getPropertyByte(), new_bean.getPropertyByte());
				assertEquals(retrieved_bean.getPropertyByteObject(), new_bean.getPropertyByteObject());
				assertEquals(retrieved_bean.getPropertyDouble(), new_bean.getPropertyDouble(), 0.01);
				assertEquals(retrieved_bean.getPropertyDoubleObject().doubleValue(), new_bean.getPropertyDoubleObject().doubleValue(), 0.01);
				assertEquals(retrieved_bean.getPropertyFloat(), new_bean.getPropertyFloat(), 0.01);
				assertEquals(retrieved_bean.getPropertyFloatObject().floatValue(), new_bean.getPropertyFloatObject().floatValue(), 0.01);
				assertEquals(retrieved_bean.getPropertyInt(), new_bean.getPropertyInt());
				assertEquals(retrieved_bean.getPropertyIntegerObject(), new_bean.getPropertyIntegerObject());
				assertEquals(retrieved_bean.getPropertyLong(), new_bean.getPropertyLong());
				assertEquals(retrieved_bean.getPropertyLongObject(), new_bean.getPropertyLongObject());
				assertEquals(retrieved_bean.getPropertyShort(), new_bean.getPropertyShort());
				assertEquals(retrieved_bean.getPropertyShortObject(), new_bean.getPropertyShortObject());
				assertEquals(retrieved_bean.getPropertyBigDecimal(), new_bean.getPropertyBigDecimal());
			}
			finally
			{
				statement_insert.close();
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testSetBeanNulls()
	{
		try
		{
			// insert some data
			Insert query_insert = new Insert(mDatasource);
			query_insert.into("parametersbean")
				.fieldsParameters(BeanImpl.class);
			DbPreparedStatement statement_insert = mDatasource.getConnection().getPreparedStatement(query_insert);
			try
			{
				BeanImpl null_bean = BeanImpl.getNullBean();
				// each database has its oddities here, sadly
				Calendar cal = Calendar.getInstance();
				cal.set(2002, 5, 18, 15, 26, 14);
				cal.set(Calendar.MILLISECOND, 764);
				if (mDatasource.getDriver().equals("org.postgresql.Driver"))
				{
					// postgres doesn't handle null chars
					null_bean.setPropertyChar(' ');
				}
				else if (mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
				{
					// mysql automatically set the current time to timestamps
					null_bean.setPropertyDate(cal.getTime());
					null_bean.setPropertyCalendar(cal);
					null_bean.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
				}
				statement_insert.setBean(null_bean);
				statement_insert.executeUpdate();
	
				// retrieve the data
				BeanManager bean_manager = new BeanManager();
				BeanImpl retrieved_bean = bean_manager.fetchBean();
				BeanImpl new_bean = BeanImpl.getNullBean();
				// apply the database oddities
				if (mDatasource.getDriver().equals("org.postgresql.Driver"))
				{
					// postgres doesn't handle null chars
					new_bean.setPropertyChar(' ');
				}
				else if (mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
				{
					// mysql automatically set the current time to timestamps
					new_bean.setPropertyDate(cal.getTime());
					new_bean.setPropertyCalendar(cal);
					new_bean.setPropertyTimestamp(new Timestamp(cal.getTime().getTime()));
				}
				assertEquals(retrieved_bean.getPropertyString(), new_bean.getPropertyString());
				assertEquals(retrieved_bean.getPropertyStringbuffer(), new_bean.getPropertyStringbuffer());
				// don't compare milliseconds since each db stores it differently
				if (mDatasource.getAliasedDriver().equals("com.mysql.jdbc.Driver"))
				{
					// don't compare milliseconds since each db stores it differently
					assertEquals((retrieved_bean.getPropertyDate().getTime()/1000)*1000, (new_bean.getPropertyDate().getTime()/1000)*1000);
					assertEquals((retrieved_bean.getPropertyCalendar().getTime().getTime()/1000)*1000, (new_bean.getPropertyCalendar().getTime().getTime()/1000)*1000);
					assertEquals((retrieved_bean.getPropertyTimestamp().getTime()/1000)*1000, (new_bean.getPropertyTimestamp().getTime()/1000)*1000);
				}
				else
				{
					assertEquals(retrieved_bean.getPropertyDate(), new_bean.getPropertyDate());
					assertEquals(retrieved_bean.getPropertyCalendar(), new_bean.getPropertyCalendar());
					assertEquals(retrieved_bean.getPropertyTimestamp(), new_bean.getPropertyTimestamp());
				}
				assertEquals(retrieved_bean.getPropertySqlDate(), new_bean.getPropertySqlDate());
				assertEquals(retrieved_bean.getPropertyTime(), new_bean.getPropertyTime());
				assertEquals(retrieved_bean.getPropertyChar(), new_bean.getPropertyChar());
				assertEquals(retrieved_bean.getPropertyCharacterObject(), new_bean.getPropertyCharacterObject());
				assertEquals(retrieved_bean.isPropertyBoolean(), new_bean.isPropertyBoolean());
				assertEquals(retrieved_bean.getPropertyBooleanObject(), new_bean.getPropertyBooleanObject());
				assertEquals(retrieved_bean.getPropertyByte(), new_bean.getPropertyByte());
				assertEquals(retrieved_bean.getPropertyByteObject(), new_bean.getPropertyByteObject());
				assertEquals(retrieved_bean.getPropertyDouble(), new_bean.getPropertyDouble(), 0.01);
				assertEquals(retrieved_bean.getPropertyDoubleObject().doubleValue(), new_bean.getPropertyDoubleObject().doubleValue(), 0.01);
				assertEquals(retrieved_bean.getPropertyFloat(), new_bean.getPropertyFloat(), 0.01);
				assertEquals(retrieved_bean.getPropertyFloatObject().floatValue(), new_bean.getPropertyFloatObject().floatValue(), 0.01);
				assertEquals(retrieved_bean.getPropertyInt(), new_bean.getPropertyInt());
				assertEquals(retrieved_bean.getPropertyIntegerObject(), new_bean.getPropertyIntegerObject());
				assertEquals(retrieved_bean.getPropertyLong(), new_bean.getPropertyLong());
				assertEquals(retrieved_bean.getPropertyLongObject(), new_bean.getPropertyLongObject());
				assertEquals(retrieved_bean.getPropertyShort(), new_bean.getPropertyShort());
				assertEquals(retrieved_bean.getPropertyShortObject(), new_bean.getPropertyShortObject());
				assertEquals(retrieved_bean.getPropertyBigDecimal(), new_bean.getPropertyBigDecimal());
			}
			finally
			{
				statement_insert.close();
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testSetNamedParameters()
	{
		try
		{
			// insert some data
			Insert query_insert = new Insert(mDatasource);
			query_insert.into("parametersbean")
				.fieldsParameters(BeanImpl.class);
			DbPreparedStatement statement_insert = mDatasource.getConnection().getPreparedStatement(query_insert);
			try
			{
				Calendar cal = Calendar.getInstance();
				cal.set(2002, 5, 18, 15, 26, 14);
				cal.set(Calendar.MILLISECOND, 764);
				statement_insert.setString("propertyString", "someotherstring");
				statement_insert.setString("propertyStringbuffer", "someotherstringbuff");
				statement_insert.setTimestamp("propertyDate", new Timestamp(cal.getTime().getTime()));
				statement_insert.setTimestamp("propertyCalendar", new Timestamp(cal.getTime().getTime()));
				statement_insert.setDate("propertySqlDate", new java.sql.Date(cal.getTime().getTime()));
				statement_insert.setTime("propertyTime", new Time(cal.getTime().getTime()));
				statement_insert.setTimestamp("propertyTimestamp", new Timestamp(cal.getTime().getTime()));
				statement_insert.setString("propertyChar", "v");
				statement_insert.setString("propertyCharacterObject", "r");
				statement_insert.setBoolean("propertyBoolean", true);
				statement_insert.setBoolean("propertyBooleanObject", false);
				statement_insert.setByte("propertyByte", (byte)89);
				statement_insert.setByte("propertyByteObject", (byte)34);
				statement_insert.setDouble("propertyDouble", 53348.34d);
				statement_insert.setDouble("propertyDoubleObject", 143298.692d);
				statement_insert.setFloat("propertyFloat", 98634.2f);
				statement_insert.setFloat("propertyFloatObject", 8734.7f);
				statement_insert.setInt("propertyInt", 545);
				statement_insert.setInt("propertyIntegerObject", 968);
				statement_insert.setLong("propertyLong", 34563L);
				statement_insert.setLong("propertyLongObject", 66875L);
				statement_insert.setShort("propertyShort", (short)43);
				statement_insert.setShort("propertyShortObject", (short)68);
				statement_insert.setBigDecimal("propertyBigDecimal", new BigDecimal("219038743.392874"));
				statement_insert.setString("propertyEnum", SomeEnum.VALUE_TWO.toString());
				
				statement_insert.executeUpdate();
	
				// retrieve the data
				BeanManager bean_manager = new BeanManager();
				BeanImpl retrieved_bean = bean_manager.fetchBean();
				BeanImpl new_bean = BeanImpl.getPopulatedBean();
				assertEquals(retrieved_bean.getPropertyString(), new_bean.getPropertyString());
				assertEquals(retrieved_bean.getPropertyStringbuffer().toString(), new_bean.getPropertyStringbuffer().toString());
	
				// don't compare milliseconds since each db stores it differently
				assertEquals((retrieved_bean.getPropertyDate().getTime()/1000)*1000, (new_bean.getPropertyDate().getTime()/1000)*1000);
				assertEquals((retrieved_bean.getPropertyCalendar().getTime().getTime()/1000)*1000, (new_bean.getPropertyCalendar().getTime().getTime()/1000)*1000);
				assertEquals((retrieved_bean.getPropertyTimestamp().getTime()/1000)*1000, (new_bean.getPropertyTimestamp().getTime()/1000)*1000);
	
				assertEquals(retrieved_bean.getPropertySqlDate().toString(), new_bean.getPropertySqlDate().toString());
				assertEquals(retrieved_bean.getPropertyTime().toString(), new_bean.getPropertyTime().toString());
				assertEquals(retrieved_bean.getPropertyChar(), new_bean.getPropertyChar());
				assertEquals(retrieved_bean.getPropertyCharacterObject(), new_bean.getPropertyCharacterObject());
				assertEquals(retrieved_bean.isPropertyBoolean(), new_bean.isPropertyBoolean());
				assertEquals(retrieved_bean.getPropertyBooleanObject(), new_bean.getPropertyBooleanObject());
				assertEquals(retrieved_bean.getPropertyByte(), new_bean.getPropertyByte());
				assertEquals(retrieved_bean.getPropertyByteObject(), new_bean.getPropertyByteObject());
				assertEquals(retrieved_bean.getPropertyDouble(), new_bean.getPropertyDouble(), 0.01);
				assertEquals(retrieved_bean.getPropertyDoubleObject().doubleValue(), new_bean.getPropertyDoubleObject().doubleValue(), 0.01);
				assertEquals(retrieved_bean.getPropertyFloat(), new_bean.getPropertyFloat(), 0.01);
				assertEquals(retrieved_bean.getPropertyFloatObject().floatValue(), new_bean.getPropertyFloatObject().floatValue(), 0.01);
				assertEquals(retrieved_bean.getPropertyInt(), new_bean.getPropertyInt());
				assertEquals(retrieved_bean.getPropertyIntegerObject(), new_bean.getPropertyIntegerObject());
				assertEquals(retrieved_bean.getPropertyLong(), new_bean.getPropertyLong());
				assertEquals(retrieved_bean.getPropertyLongObject(), new_bean.getPropertyLongObject());
				assertEquals(retrieved_bean.getPropertyShort(), new_bean.getPropertyShort());
				assertEquals(retrieved_bean.getPropertyShortObject(), new_bean.getPropertyShortObject());
				assertEquals(retrieved_bean.getPropertyBigDecimal(), new_bean.getPropertyBigDecimal());
			}
			finally
			{
				statement_insert.close();
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalParameterName()
	{
		try
		{
			Insert query_insert = new Insert(mDatasource);
			query_insert
				.into("parametersbean")
				.fieldParameter("intcol");
			DbPreparedStatement statement_insert = null;

			try
			{
				statement_insert = mDatasource.getConnection().getPreparedStatement(query_insert);
				try
				{
					statement_insert.setInt(null, 1);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setInt("", 1);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				statement_insert.close();
			}
			catch (PreparedStatementCreationErrorException e)
			{
				assertSame(mDatasource, e.getDatasource());
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testInexistantParameterName()
	{
		try
		{
			Insert query_insert = new Insert(mDatasource);
			query_insert
				.into("parametersbean")
				.fieldParameter("intcol");
			DbPreparedStatement statement_insert = null;

			try
			{
				statement_insert = mDatasource.getConnection().getPreparedStatement(query_insert);
				try
				{
					statement_insert.setInt("doesntexist", 1);
					fail();
				}
				catch (ParameterDoesntExistException e)
				{
					assertSame(statement_insert, e.getPreparedStatement());
					assertEquals("doesntexist", e.getParameterName());
				}
				statement_insert.close();
			}
			catch (PreparedStatementCreationErrorException e)
			{
				assertSame(mDatasource, e.getDatasource());
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testIllegalArgumentTypedParameters()
	{
		try
		{
			Insert query_insert = new Insert(mDatasource);
			query_insert
				.into("parametersbean")
				.fieldParameter("intcol");
			DbPreparedStatement statement_insert = null;

			try
			{
				statement_insert = mDatasource.getConnection().getPreparedStatement(query_insert);
				try
				{
					statement_insert.setDoubles(null, 1d);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setShorts(null, (short)1);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setNulls(null, Types.INTEGER);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setNulls(null, Types.INTEGER, "INT");
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setBooleans(null, true);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setBytes(null, (byte)1);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setDates(null, new Date(0));
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setDates(null, new Date(0), Calendar.getInstance());
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setInts(null, 1);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setLongs(null, 1L);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setFloats(null, 1f);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setBigDecimals(null, new BigDecimal(String.valueOf(1)));
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setStrings(null, "1");
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setBytes((int[])null, new byte[] {(byte)1});
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setObjects(null, "1", Types.VARCHAR, 0);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setObjects(null, "1", Types.VARCHAR);
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setTimes(null, new Time(0));
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setTimes(null, new Time(0), Calendar.getInstance());
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setTimestamps(null, new Timestamp(0));
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setTimestamps(null, new Timestamp(0), Calendar.getInstance());
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					statement_insert.setObjects(null, "1");
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				try
				{
					try
					{
						statement_insert.setURLs(null, new URL("http://www.uwyn.com"));
					}
					catch (MalformedURLException e)
					{
						assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
					}
					fail();
				}
				catch (IllegalArgumentException e)
				{
					assertTrue(true);
				}
				statement_insert.close();
			}
			catch (PreparedStatementCreationErrorException e)
			{
				assertSame(mDatasource, e.getDatasource());
			}
		}
		catch (DatabaseException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	class BeanManager extends DbQueryManager
	{
		public BeanManager()
		{
			super(mDatasource);
		}

		protected BeanImpl fetchBean()
		throws DatabaseException
		{
			Select query_select = new Select(getDatasource());
			query_select
				.from("parametersbean")
				.fields(BeanImpl.class);
			DbBeanFetcher<BeanImpl> fetcher = new DbBeanFetcher<BeanImpl>(getDatasource(), BeanImpl.class);

			DbStatement statement = executeQuery(query_select);
			fetch(statement.getResultSet(), fetcher);
			BeanImpl bean = fetcher.getBeanInstance();
			statement.close();

			return bean;
		}
	}
}
