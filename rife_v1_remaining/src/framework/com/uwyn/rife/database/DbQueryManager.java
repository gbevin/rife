/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DbQueryManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.exceptions.RollbackException;
import com.uwyn.rife.database.exceptions.RowProcessorErrorException;
import com.uwyn.rife.database.queries.Query;
import com.uwyn.rife.database.queries.ReadQuery;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.ReaderUser;
import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * This is a convenience class to make it easy to control the queries that
 * handle the retrieval, storage, update and removal of data in a database.
 * All queries will be executed in a connection of the <code>Datasource</code>
 * that's provided to the constructor of the <code>DbQueryManager</code>.
 * <p>A collection of convenience methods have been provided to quickly
 * execute queries in a variety of manners without having to worry about the
 * logic behind it or having to remember to close the queries at the
 * appropriate moment. These methods optionally interact with the
 * <code>DbPreparedStatementHandler</code> and <code>DbResultSetHandler</code>
 * classes to make it possible to fully customize the executed queries. The
 * following categories of worry-free methods exist:
 * <ul>
 * <li>{@linkplain #executeUpdate(Query) execute an update query directly}
 * <li>{@linkplain #executeUpdate(Query,DbPreparedStatementHandler) execute a
 * customizable update query}
 * <li>{@linkplain #executeQuery(ReadQuery,DbPreparedStatementHandler) execute a
 * customizable select query}
 * <li>{@linkplain #executeHasResultRows(ReadQuery,DbPreparedStatementHandler)
 * check the result rows of a customizable select query}
 * <li>{@linkplain #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
 * obtain the first value of a customizable select query}
 * <li>{@linkplain
 * #executeFetchFirst(ReadQuery,DbRowProcessor,DbPreparedStatementHandler) fetch
 * the first row of a customizable select query}
 * <li>{@linkplain
 * #executeFetchFirstBean(ReadQuery,Class,DbPreparedStatementHandler) fetch the
 * first bean of a customizable select query}
 * <li>{@linkplain
 * #executeFetchAll(ReadQuery,DbRowProcessor,DbPreparedStatementHandler) fetch
 * all rows of a customizable select query}
 * <li>{@linkplain
 * #executeFetchAllBeans(ReadQuery,Class,DbPreparedStatementHandler) fetch all
 * beans of a customizable select query}
 * </ul>
 * <p>Lower-level methods are also available for the sake of repetitive
 * code-reduction. To obtain execute regular statements directly,
 * use the {@link #executeQuery(ReadQuery) executeQuery} method.
 * <p>Finally, <code>since DbStatement</code> and
 * <code>DbPreparedStatement</code> instances preserve a reference to their
 * resultset, it's easy to iterate over the rows of a resultset with the
 * {@link #fetch(ResultSet,DbRowProcessor) fetch} or {@link
 * #fetchAll(ResultSet,DbRowProcessor) fetchAll} methods.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.database.DbPreparedStatement
 * @see com.uwyn.rife.database.DbStatement
 * @see com.uwyn.rife.database.DbRowProcessor
 * @see com.uwyn.rife.database.DbPreparedStatementHandler
 * @see com.uwyn.rife.database.DbResultSetHandler
 * @see com.uwyn.rife.database.DbConnectionUser
 * @since 1.0
 */
public class DbQueryManager implements Cloneable
{
	private final Datasource      mDatasource;

	/**
	 * Instantiates a new <code>DbQueryManager</code> object and ties it to
	 * the provided datasource.
	 *
	 * @param datasource the datasource that will be used to obtain database
	 * connections from
	 * @since 1.0
	 */
	public DbQueryManager(Datasource datasource)
	{
		if (null == datasource) throw new IllegalArgumentException("datasource can't be null.");
		
		mDatasource = datasource;
	}

	/**
	 * Safely and quickly executes an update statement. It relies on the
	 * wrapped {@link DbStatement#executeUpdate(String)} method, but also
	 * automatically closes the statement after its execution.
	 * <p>This method is typically used in situations where one static update
	 * query needs to be executed without any parametrization or other
	 * processing.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *int count = manager.executeUpdate("INSERT INTO person (name) VALUES ('me')");</pre>
	 *
	 * @param sql the sql query that has to be executed
	 * @return the row count for the executed query
	 * @exception DatabaseException see {@link
	 * DbStatement#executeUpdate(String)}
	 * @see DbStatement#executeUpdate(String)
	 * @see #executeUpdate(Query)
	 * @since 1.0
	 */
	public int executeUpdate(String sql)
	throws DatabaseException
	{
		if (null == sql)    throw new IllegalArgumentException("sql can't be null.");

		DbConnection connection = getConnection();
		try
		{
			DbStatement statement = connection.createStatement();
			try
			{
				return statement.executeUpdate(sql);
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly executes an update statement. It relies on the
	 * wrapped {@link DbStatement#executeUpdate(Query)} method, but also
	 * automatically closes the statement after its execution.
	 * <p>This method is typically used in situations where one static update
	 * query needs to be executed without any parametrization or other
	 * processing.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Insert insert = new Insert(datasource);
	 *insert.into("person").field("name", "me");
	 *int count = manager.executeUpdate(insert);</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the row count for the executed query
	 * @exception DatabaseException see {@link
	 * DbStatement#executeUpdate(Query)}
	 * @see DbStatement#executeUpdate(Query)
	 * @see #executeUpdate(String)
	 * @since 1.0
	 */
	public int executeUpdate(Query query)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbStatement statement = connection.createStatement();
			try
			{
				return statement.executeUpdate(query);
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}
	
	private DbPreparedStatement getPreparedStatement(Query query, DbResultSetHandler handler, DbConnection connection)
	{
		return mDatasource.getCapabilitiesCompensator().getCapablePreparedStatement(query, handler, connection);
	}
	
	private void executeQuery(DbPreparedStatement statement, DbPreparedStatementHandler handler)
	{
		if (null == handler)
		{
			statement.executeQuery();
		}
		else
		{
			handler.performQuery(statement);
		}
	}
	
	private DbResultSet getResultSet(DbPreparedStatement statement)
	{
		return mDatasource.getCapabilitiesCompensator().getCapableResultSet(statement);
	}
	
	/**
	 * Safely execute an updates statement. It relies on the wrapped {@link
	 * DbPreparedStatement#executeUpdate()} method, but also automatically
	 * closes the statement after its execution and allows customization of
	 * the prepared statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>This method is typically used when you need to fully customize a
	 * query at runtime, but still want to benefit of a safety net that
	 * ensures that the allocated statement will be closed.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Insert insert = new Insert(datasource);
	 *insert.into("person").fieldParameter("name");
	 *final String name = "me";
	 *int count = manager.executeUpdate(insert, new DbPreparedStatementHandler() {
	 *        public void setParameters(DbPreparedStatement statement)
	 *        {
	 *            statement
	 *                .setString("name", name);
	 *        }
	 *    });</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the row count for the executed query
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeUpdate()}
	 * @see DbPreparedStatement#executeUpdate()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public int executeUpdate(Query query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				if (null == handler)
				{
					return statement.executeUpdate();
				}
				
				return handler.performUpdate(statement);
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}
	
	private boolean executeHasResultRows(DbPreparedStatement statement, DbPreparedStatementHandler handler)
	{
		executeQuery(statement, handler);

		return getResultSet(statement).hasResultRows();

	}

	/**
	 * Safely and quickly verifies if a select query returns any rows. It
	 * relies on the wrapped {@link DbResultSet#hasResultRows()} method, but
	 * also automatically closes the statement after its execution.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person");
	 *boolean result = manager.executeHasResultRows(select);
	 *</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return <code>true</code> when rows were returned by the query; or
	 * <p><code>false</code> otherwise
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#hasResultRows()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#hasResultRows()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public boolean executeHasResultRows(ReadQuery query)
	throws DatabaseException
	{
		return executeHasResultRows(query, null);
	}

	/**
	 * Safely verifies if a customizable select query returns any rows. It
	 * relies on the wrapped {@link DbResultSet#hasResultRows()} method, but
	 * also automatically closes the statement after its execution and allows
	 * customization of the prepared statement through an optional instance of
	 * {@link DbPreparedStatementHandler}.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person").whereParameter("name", "=");
	 *final String name = "you";
	 *boolean result = manager.executeHasResultRows(select, new DbPreparedStatementHandler() {
	 *        public void setParameters(DbPreparedStatement statement)
	 *        {
	 *            statement
	 *                .setString("name", name);
	 *        }
	 *    });</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return <code>true</code> when rows were returned by the query; or
	 * <p><code>false</code> otherwise
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#hasResultRows()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#hasResultRows()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public boolean executeHasResultRows(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				boolean result = executeHasResultRows(statement, handler);
			
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a <code>String</code>
	 * from the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstString()} method, but also automatically closes the
	 * statement after its execution.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.field("name").from("person");
	 *String result = manager.executeGetFirstString(select);
	 *</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first <code>String</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstString()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstString()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public String executeGetFirstString(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstString(query, null);
	}

	/**
	 * Safely retrieves the first cell as a <code>String</code> from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstString()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.field("first").from("person").whereParameter("last", "=");
	 *final String last = "Smith";
	 *String result = manager.executeGetFirstString(select, new DbPreparedStatementHandler() {
	 *        public void setParameters(DbPreparedStatement statement)
	 *        {
	 *            statement
	 *                .setString("last", last);
	 *        }
	 *    });</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first <code>String</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstString()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstString()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public String executeGetFirstString(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				String result = null;
				
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstString();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a <code>boolean</code>
	 * from the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstBoolean()} method, but also automatically closes
	 * the statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first <code>boolean</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstBoolean()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstBoolean()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public boolean executeGetFirstBoolean(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstBoolean(query, null);
	}

	/**
	 * Safely retrieves the first cell as a <code>boolean</code> from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstBoolean()} method, but also automatically closes
	 * the statement after its execution and allows customization of the
	 * prepared statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first <code>boolean</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstBoolean()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstBoolean()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public boolean executeGetFirstBoolean(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				boolean result = false;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstBoolean();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a <code>byte</code> from
	 * the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstByte()} method, but also automatically closes the
	 * statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first <code>byte</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstByte()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstByte()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public byte executeGetFirstByte(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstByte(query, null);
	}

	/**
	 * Safely retrieves the first cell as a <code>byte</code> from the results
	 * of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstByte()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first <code>byte</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstByte()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstByte()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public byte executeGetFirstByte(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				byte result = -1;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstByte();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a <code>short</code>
	 * from the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstShort()} method, but also automatically closes the
	 * statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first <code>short</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstShort()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstShort()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public short executeGetFirstShort(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstShort(query, null);
	}

	/**
	 * Safely retrieves the first cell as a <code>short</code> from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstShort()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first <code>short</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstShort()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstShort()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public short executeGetFirstShort(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				short result = -1;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstShort();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a <code>int</code> from
	 * the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstInt()} method, but also automatically closes the
	 * statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first <code>int</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link DbResultSet#getFirstInt()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstInt()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public int executeGetFirstInt(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstInt(query, null);
	}

	/**
	 * Safely retrieves the first cell as a <code>int</code> from the results
	 * of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstInt()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first <code>int</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link DbResultSet#getFirstInt()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstInt()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public int executeGetFirstInt(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				int result = -1;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstInt();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a <code>long</code> from
	 * the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstLong()} method, but also automatically closes the
	 * statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first <code>long</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstLong()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstLong()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public long executeGetFirstLong(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstLong(query, null);
	}

	/**
	 * Safely retrieves the first cell as a <code>long</code> from the results
	 * of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstLong()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first <code>long</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstLong()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstLong()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public long executeGetFirstLong(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				long result = -1;
				
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstLong();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a <code>float</code>
	 * from the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstFloat()} method, but also automatically closes the
	 * statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first <code>float</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstFloat()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstFloat()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public float executeGetFirstFloat(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstFloat(query, null);
	}

	/**
	 * Safely retrieves the first cell as a <code>float</code> from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstFloat()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first <code>float</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstFloat()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstFloat()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public float executeGetFirstFloat(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				float result = -1;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstFloat();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a <code>double</code>
	 * from the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstDouble()} method, but also automatically closes the
	 * statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first <code>double</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstDouble()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstDouble()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public double executeGetFirstDouble(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstDouble(query, null);
	}

	/**
	 * Safely retrieves the first cell as a <code>double</code> from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstDouble()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first <code>double</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstDouble()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstDouble()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public double executeGetFirstDouble(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				double result = -1;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstDouble();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a <code>byte</code>
	 * array from the results of a select query. It relies on the wrapped
	 * {@link DbResultSet#getFirstBytes()} method, but also automatically
	 * closes the statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first <code>byte</code> array in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstBytes()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstBytes()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public byte[] executeGetFirstBytes(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstBytes(query, null);
	}

	/**
	 * Safely retrieves the first cell as a <code>byte</code> array from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstBytes()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first <code>byte</code> array in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstBytes()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstBytes()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public byte[] executeGetFirstBytes(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				byte[] result = null;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstBytes();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a sql <code>Date</code>
	 * from the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstDate()} method, but also automatically closes the
	 * statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first sql <code>Date</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstDate()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstDate()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Date executeGetFirstDate(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstDate(query, (DbPreparedStatementHandler)null);
	}

	/**
	 * Safely retrieves the first cell as a sql <code>Date</code> from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstDate()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first sql <code>Date</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstDate()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstDate()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Date executeGetFirstDate(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				java.sql.Date result = null;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstDate();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a sql <code>Date</code>
	 * from the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstDate(Calendar)} method, but also automatically
	 * closes the statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param cal the <code>Calendar</code> object to use in constructing the
	 * date
	 * @return the first sql <code>Date</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstDate(Calendar)}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstDate(Calendar)
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Date executeGetFirstDate(ReadQuery query, Calendar cal)
	throws DatabaseException
	{
		return executeGetFirstDate(query, cal, null);
	}

	/**
	 * Safely retrieves the first cell as a sql <code>Date</code> from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstDate(Calendar)} method, but also automatically
	 * closes the statement after its execution and allows customization of
	 * the prepared statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param cal the <code>Calendar</code> object to use in constructing the
	 * date
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first sql <code>Date</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstDate(Calendar)}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstDate(Calendar)
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Date executeGetFirstDate(ReadQuery query, Calendar cal, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				java.sql.Date result = null;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstDate(cal);
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a sql <code>Time</code>
	 * from the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstTime()} method, but also automatically closes the
	 * statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first sql <code>Time</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstTime()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstTime()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Time executeGetFirstTime(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstTime(query, (DbPreparedStatementHandler)null);
	}

	/**
	 * Safely retrieves the first cell as a sql <code>Time</code> from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstTime()} method, but also automatically closes the
	 * statement after its execution and allows customization of the prepared
	 * statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first sql <code>Time</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstTime()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstTime()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Time executeGetFirstTime(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				java.sql.Time result = null;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstTime();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a sql <code>Time</code>
	 * from the results of a select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstTime(Calendar)} method, but also automatically
	 * closes the statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param cal the <code>Calendar</code> object to use in constructing the
	 * time
	 * @return the first sql <code>Time</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstTime(Calendar)}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstTime(Calendar)
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Time executeGetFirstTime(ReadQuery query, Calendar cal)
	throws DatabaseException
	{
		return executeGetFirstTime(query, cal, null);
	}

	/**
	 * Safely retrieves the first cell as a sql <code>Time</code> from the
	 * results of a customizable select query. It relies on the wrapped {@link
	 * DbResultSet#getFirstTime(Calendar)} method, but also automatically
	 * closes the statement after its execution and allows customization of
	 * the prepared statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param cal the <code>Calendar</code> object to use in constructing the
	 * time
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first sql <code>Time</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstTime(Calendar)}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstTime(Calendar)
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Time executeGetFirstTime(ReadQuery query, Calendar cal, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				java.sql.Time result = null;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstTime(cal);
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a sql
	 * <code>Timestamp</code> from the results of a select query. It relies on
	 * the wrapped {@link DbResultSet#getFirstTimestamp()} method, but also
	 * automatically closes the statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @return the first sql <code>Timestamp</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstTimestamp()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstTimestamp()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Timestamp executeGetFirstTimestamp(ReadQuery query)
	throws DatabaseException
	{
		return executeGetFirstTimestamp(query, (DbPreparedStatementHandler)null);
	}

	/**
	 * Safely retrieves the first cell as a sql <code>Timestamp</code> from
	 * the results of a customizable select query. It relies on the wrapped
	 * {@link DbResultSet#getFirstTimestamp()} method, but also automatically
	 * closes the statement after its execution and allows customization of
	 * the prepared statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first sql <code>Timestamp</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstTimestamp()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstTimestamp()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Timestamp executeGetFirstTimestamp(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				java.sql.Timestamp result = null;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstTimestamp();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as a sql
	 * <code>Timestamp</code> from the results of a select query. It relies on
	 * the wrapped {@link DbResultSet#getFirstTimestamp(Calendar)} method, but
	 * also automatically closes the statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param cal the <code>Calendar</code> object to use in constructing the
	 * timestamp
	 * @return the first sql <code>Timestamp</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstTimestamp(Calendar)}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstTimestamp(Calendar)
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Timestamp executeGetFirstTimestamp(ReadQuery query, Calendar cal)
	throws DatabaseException
	{
		return executeGetFirstTimestamp(query, cal, null);
	}

	/**
	 * Safely retrieves the first cell as a sql <code>Timestamp</code> from
	 * the results of a customizable select query. It relies on the wrapped
	 * {@link DbResultSet#getFirstTimestamp(Calendar)} method, but also
	 * automatically closes the statement after its execution and allows
	 * customization of the prepared statement through an optional instance of
	 * {@link DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param cal the <code>Calendar</code> object to use in constructing the
	 * timestamp
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the first sql <code>Timestamp</code> in the query's resultset
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstTimestamp(Calendar)}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstTimestamp(Calendar)
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public java.sql.Timestamp executeGetFirstTimestamp(ReadQuery query, Calendar cal, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				java.sql.Timestamp result = null;
	
				if (executeHasResultRows(statement, handler))
				{
					result = getResultSet(statement).getFirstTimestamp(cal);
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as an ASCII
	 * <code>InputStream</code> from the results of a select query. It relies
	 * on the wrapped {@link DbResultSet#getFirstAsciiStream()} method, but
	 * also automatically closes the statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param user an instance of <code>InputStreamUser</code>
	 * that contains the logic that will be executed with this stream
	 * @return the return value from the <code>useInputStream</code> method of
	 * the provided <code>InputStreamUser</code> instance
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstAsciiStream()}
	 * @exception InnerClassException when errors occurs inside the
	 * <code>InputStreamUser</code>
	 * @see InputStreamUser
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstAsciiStream()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public <ResultType> ResultType executeUseFirstAsciiStream(ReadQuery query, InputStreamUser user)
	throws DatabaseException, InnerClassException
	{
		return (ResultType)executeUseFirstAsciiStream(query, user, null);
	}

	/**
	 * Safely retrieves the first cell as an ASCII <code>InputStream</code>
	 * from the results of a customizable select query. It relies on the
	 * wrapped {@link DbResultSet#getFirstAsciiStream()} method, but also
	 * automatically closes the statement after its execution and allows
	 * customization of the prepared statement through an optional instance of
	 * {@link DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param user an instance of <code>InputStreamUser</code>
	 * that contains the logic that will be executed with this stream
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the return value from the <code>useInputStream</code> method of
	 * the provided <code>InputStreamUser</code> instance
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstAsciiStream()}
	 * @exception InnerClassException when errors occurs inside the
	 * <code>InputStreamUser</code>
	 * @see InputStreamUser
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstAsciiStream()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public <ResultType> ResultType executeUseFirstAsciiStream(ReadQuery query, InputStreamUser user, DbPreparedStatementHandler handler)
	throws DatabaseException, InnerClassException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		if (null == user)	throw new IllegalArgumentException("user can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			InputStream stream = null;
			try
			{
				statement.setFetchSize(1);
				
				if (executeHasResultRows(statement, handler))
				{
					stream = getResultSet(statement).getFirstAsciiStream();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return (ResultType)user.useInputStream(stream);
			}
			finally
			{
				defensiveClose(stream);
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as an character
	 * <code>Reader</code> from the results of a select query. It relies on
	 * the wrapped {@link DbResultSet#getFirstCharacterStream()} method, but
	 * also automatically closes the statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param user an instance of <code>ReaderUser</code>
	 * that contains the logic that will be executed with this reader
	 * @return the return value from the <code>useReader</code> method of
	 * the provided <code>ReaderUser</code> instance
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstCharacterStream()}
	 * @exception InnerClassException when errors occurs inside the
	 * <code>ReaderUser</code>
	 * @see ReaderUser
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstCharacterStream()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public <ResultType> ResultType executeUseFirstCharacterStream(ReadQuery query, ReaderUser user)
	throws DatabaseException, InnerClassException
	{
		return (ResultType)executeUseFirstCharacterStream(query, user, null);
	}

	/**
	 * Safely retrieves the first cell as an character <code>Reader</code>
	 * from the results of a customizable select query. It relies on the
	 * wrapped {@link DbResultSet#getFirstCharacterStream()} method, but also
	 * automatically closes the statement after its execution and allows
	 * customization of the prepared statement through an optional instance of
	 * {@link DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param user an instance of <code>ReaderUser</code>
	 * that contains the logic that will be executed with this reader
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the return value from the <code>useReader</code> method of
	 * the provided <code>ReaderUser</code> instance
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstCharacterStream()}
	 * @exception InnerClassException when errors occurs inside the
	 * <code>ReaderUser</code>
	 * @see ReaderUser
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstCharacterStream()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public <ResultType> ResultType executeUseFirstCharacterStream(ReadQuery query, ReaderUser user, DbPreparedStatementHandler handler)
	throws DatabaseException, InnerClassException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		if (null == user)	throw new IllegalArgumentException("user can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			Reader reader = null;
			try
			{
				statement.setFetchSize(1);
				
				if (executeHasResultRows(statement, handler))
				{
					reader = getResultSet(statement).getFirstCharacterStream();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return (ResultType)user.useReader(reader);
			}
			finally
			{
				defensiveClose(reader);
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly retrieves the first cell as an binary
	 * <code>InputStream</code> from the results of a select query. It relies
	 * on the wrapped {@link DbResultSet#getFirstBinaryStream()} method, but
	 * also automatically closes the statement after its execution.
	 * <p>Refer to {@link #executeGetFirstString(ReadQuery) executeGetFirstString}
	 * for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param user an instance of <code>InputStreamUser</code>
	 * that contains the logic that will be executed with this stream
	 * @return the return value from the <code>useInputStream</code> method of
	 * the provided <code>InputStreamUser</code> instance
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstBinaryStream()}
	 * @exception InnerClassException when errors occurs inside the
	 * <code>InputStreamUser</code>
	 * @see InputStreamUser
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstBinaryStream()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public <ResultType> ResultType executeUseFirstBinaryStream(ReadQuery query, InputStreamUser user)
	throws DatabaseException, InnerClassException
	{
		return (ResultType)executeUseFirstBinaryStream(query, user, null);
	}

	/**
	 * Safely retrieves the first cell as an binary <code>InputStream</code>
	 * from the results of a customizable select query. It relies on the
	 * wrapped {@link DbResultSet#getFirstBinaryStream()} method, but also
	 * automatically closes the statement after its execution and allows
	 * customization of the prepared statement through an optional instance of
	 * {@link DbPreparedStatementHandler}.
	 * <p>Refer to {@link
	 * #executeGetFirstString(ReadQuery,DbPreparedStatementHandler)
	 * executeGetFirstString} for an example code snippet, it's 100% analogue.
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param user an instance of <code>InputStreamUser</code>
	 * that contains the logic that will be executed with this stream
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the return value from the <code>useInputStream</code> method of
	 * the provided <code>InputStreamUser</code> instance
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * DbResultSet#getFirstBinaryStream()}
	 * @exception InnerClassException when errors occurs inside the
	 * <code>InputStreamUser</code>
	 * @see InputStreamUser
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSet#getFirstBinaryStream()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public <ResultType> ResultType executeUseFirstBinaryStream(ReadQuery query, InputStreamUser user, DbPreparedStatementHandler handler)
	throws DatabaseException, InnerClassException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		if (null == user)	throw new IllegalArgumentException("user can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			InputStream stream = null;
			try
			{
				statement.setFetchSize(1);
				
				if (executeHasResultRows(statement, handler))
				{
					stream = getResultSet(statement).getFirstBinaryStream();
				}
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return (ResultType)user.useInputStream(stream);
			}
			finally
			{
				defensiveClose(stream);
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly fetches the first row from the results of a select
	 * query. It relies on the wrapped {@link
	 * #fetch(ResultSet, DbRowProcessor)} method, but automatically closes the
	 * statement after its execution.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person").where("name", "=", "me");
	 *DbRowProcessor processor = new YourProcessor();
	 *boolean result = manager.executeFetchFirst(select, processor);</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param rowProcessor a <code>DbRowProcessor</code> instance, if it's
	 * <code>null</code> no processing will be performed on the fetched row
	 * @return <code>true</code> if a row was retrieved; or
	 * <p><code>false</code> if there are no more rows .
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * #fetch(ResultSet, DbRowProcessor)}
	 * @see #fetch(ResultSet, DbRowProcessor)
	 * @see DbRowProcessor
	 * @since 1.0
	 */
	public boolean executeFetchFirst(ReadQuery query, DbRowProcessor rowProcessor)
	throws DatabaseException
	{
		return executeFetchFirst(query, rowProcessor, null);
	}

	/**
	 * Safely fetches the first row from the results of a customizable select
	 * query. It relies on the wrapped {@link
	 * #fetch(ResultSet, DbRowProcessor)} method, but also automatically
	 * closes the statement after its execution and allows customization of
	 * the prepared statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person").whereParameter("name", "=");
	 *DbRowProcessor processor = new YourProcessor();
	 *final String name = "you";
	 *boolean result = manager.executeFetchFirst(select, processor, new DbPreparedStatementHandler() {
	 *        public void setParameters(DbPreparedStatement statement)
	 *        {
	 *            statement
	 *                .setString("name", name);
	 *        }
	 *    });</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param rowProcessor a <code>DbRowProcessor</code> instance, if it's
	 * <code>null</code> no processing will be performed on the fetched row
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return <code>true</code> if a row was retrieved; or
	 * <p><code>false</code> if there are no more rows .
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * #fetch(ResultSet, DbRowProcessor)}
	 * @see #fetch(ResultSet, DbRowProcessor)
	 * @see DbRowProcessor
	 * @since 1.0
	 */
	public boolean executeFetchFirst(ReadQuery query, DbRowProcessor rowProcessor, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				statement.setFetchSize(1);
				
				executeQuery(statement, handler);
				
				boolean result = fetch(getResultSet(statement), rowProcessor);
				
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly fetches the first bean instance from the results of
	 * a select query. It relies on the wrapped {@link
	 * #executeFetchFirst(ReadQuery, DbRowProcessor)} method, but automatically
	 * uses an appropriate {@link DbBeanFetcher} instance and returns the
	 * resulting bean.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person").fields(Person.class);
	 *Person person = manager.executeFetchFirstBean(select, Person.class);</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param beanClass the class of the bean
	 * @return <code>true</code> if a row was retrieved; or
	 * <p><code>false</code> if there are no more rows .
	 * @exception DatabaseException see {@link DbBeanFetcher} and {@link
	 * #executeFetchFirst(ReadQuery, DbRowProcessor)}
	 * @see #executeFetchFirst(ReadQuery, DbRowProcessor)
	 * @see DbBeanFetcher
	 * @since 1.0
	 */
	public <BeanType> BeanType executeFetchFirstBean(ReadQuery query, Class<BeanType> beanClass)
	throws DatabaseException
	{
		return executeFetchFirstBean(query, beanClass, null);
	}

	/**
	 * Safely fetches the first bean instance from the results of a
	 * customizable select query. It relies on the wrapped {@link
	 * #executeFetchFirst(ReadQuery, DbRowProcessor)} method, but automatically
	 * uses an appropriate {@link DbBeanFetcher} instance, returns the
	 * resulting bean and allows customization of the prepared statement
	 * through an optional instance of {@link DbPreparedStatementHandler}.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person").fields(Person.class).whereParameter("name", "=");
	 *final String name = "you";
	 *Person person = manager.executeFetchFirstBean(select, Person.class, new DbPreparedStatementHandler() {
	 *        public void setParameters(DbPreparedStatement statement)
	 *        {
	 *            statement
	 *                .setString("name", name);
	 *        }
	 *    });</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param beanClass the class of the bean
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return <code>true</code> if a row was retrieved; or
	 * <p><code>false</code> if there are no more rows .
	 * @exception DatabaseException see {@link DbBeanFetcher} and {@link
	 * #executeFetchFirst(ReadQuery, DbRowProcessor)}
	 * @see #executeFetchFirst(ReadQuery, DbRowProcessor)
	 * @see DbBeanFetcher
	 * @since 1.0
	 */
	public <BeanType> BeanType executeFetchFirstBean(ReadQuery query, Class<BeanType> beanClass, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbBeanFetcher<BeanType> bean_fetcher = new DbBeanFetcher<BeanType>(getDatasource(), beanClass);
		if (executeFetchFirst(query, bean_fetcher, handler))
		{
			return bean_fetcher.getBeanInstance();
		}
		
		return null;
	}

	/**
	 * Safely and quickly fetches all the rows from the results of a select
	 * query. It relies on the wrapped {@link
	 * #fetchAll(ResultSet, DbRowProcessor)} method, but automatically closes
	 * the statement after its execution.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person").where("gender", "=", "m");
	 *DbRowProcessor processor = new YourProcessor();
	 *boolean result = manager.executeFetchAll(select, processor);</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param rowProcessor a <code>DbRowProcessor</code> instance, if it's
	 * <code>null</code> no processing will be performed on the fetched rows
	 * @return <code>true</code> if rows were retrieved; or
	 * <p><code>false</code> if there are no more rows .
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * #fetchAll(ResultSet, DbRowProcessor)}
	 * @see #fetchAll(ResultSet, DbRowProcessor)
	 * @see DbRowProcessor
	 * @since 1.0
	 */
	public boolean executeFetchAll(ReadQuery query, DbRowProcessor rowProcessor)
	throws DatabaseException
	{
		return executeFetchAll(query, rowProcessor, null);
	}

	/**
	 * Safely fetches all the rows from the results of a customizable select
	 * query. It relies on the wrapped {@link
	 * #fetchAll(ResultSet, DbRowProcessor)} method, but also automatically
	 * closes the statement after its execution and allows customization of
	 * the prepared statement through an optional instance of {@link
	 * DbPreparedStatementHandler}.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person").whereParameter("gender", "=");
	 *DbRowProcessor processor = new YourProcessor();
	 *final String name = "m";
	 *boolean result = manager.executeFetchAll(select, processor, new DbPreparedStatementHandler() {
	 *        public void setParameters(DbPreparedStatement statement)
	 *        {
	 *            statement
	 *                .setString("gender", name);
	 *        }
	 *    });</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param rowProcessor a <code>DbRowProcessor</code> instance, if it's
	 * <code>null</code> no processing will be performed on the fetched row
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return <code>true</code> if rows were retrieved; or
	 * <p><code>false</code> if there are no more rows .
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}and {@link
	 * #fetchAll(ResultSet, DbRowProcessor)}
	 * @see #fetchAll(ResultSet, DbRowProcessor)
	 * @see DbRowProcessor
	 * @since 1.0
	 */
	public boolean executeFetchAll(ReadQuery query, DbRowProcessor rowProcessor, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				executeQuery(statement, handler);
				
				boolean result = fetchAll(getResultSet(statement), rowProcessor);
	
				if (handler != null)
				{
					try
					{
						handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return result;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Safely and quickly fetches the all the bean instances from the results
	 * of a select query. It relies on the wrapped {@link
	 * #executeFetchAll(ReadQuery, DbRowProcessor)} method, but automatically
	 * uses an appropriate {@link DbBeanFetcher} instance and returns the
	 * results.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person").fields(Person.class).where("gender", "=", "m");
	 *List persons = manager.executeFetchAllBeans(select, Person.class);</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param beanClass the class of the bean
	 * @return <code>a List instance with all the beans, the list is empty if
	 * no beans could be returned</code>
	 * @exception DatabaseException see {@link DbBeanFetcher} and {@link
	 * #executeFetchAll(ReadQuery, DbRowProcessor)}
	 * @see #executeFetchAll(ReadQuery, DbRowProcessor)
	 * @see DbBeanFetcher
	 * @since 1.0
	 */
	public <BeanType> List<BeanType> executeFetchAllBeans(ReadQuery query, Class<BeanType> beanClass)
	throws DatabaseException
	{
		return executeFetchAllBeans(query, beanClass, null);
	}

	/**
	 * Safely fetches the all the bean instances from the results of a
	 * customizable select query. It relies on the wrapped {@link
	 * #executeFetchAll(ReadQuery, DbRowProcessor)} method, but automatically
	 * uses an appropriate {@link DbBeanFetcher} instance, returns the results
	 * and allows customization of the prepared statement through an optional
	 * instance of {@link DbPreparedStatementHandler}.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select.from("person").fields(Person.class).whereParameter("gender", "=");
	 *final String name = "m";
	 *List persons = manager.executeFetchAllBeans(select, Person.class, new DbPreparedStatementHandler() {
	 *        public void setParameters(DbPreparedStatement statement)
	 *        {
	 *            statement
	 *                .setString("gender", name);
	 *        }
	 *    });</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param beanClass the class of the bean
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return <code>a List instance with all the beans, the list is empty if
	 * no beans could be returned</code>
	 * @exception DatabaseException see {@link DbBeanFetcher} and {@link
	 * #executeFetchAll(ReadQuery, DbRowProcessor)}
	 * @see #executeFetchAll(ReadQuery, DbRowProcessor)
	 * @see DbBeanFetcher
	 * @since 1.0
	 */
	public <BeanType> List<BeanType> executeFetchAllBeans(ReadQuery query, Class<BeanType> beanClass, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbBeanFetcher<BeanType>     bean_fetcher = new DbBeanFetcher<BeanType>(getDatasource(), beanClass, true);
		executeFetchAll(query, bean_fetcher, handler);
		
		return bean_fetcher.getCollectedInstances();
	}

	/**
	 * Executes a customizable select statement. It relies on the wrapped
	 * {@link DbPreparedStatement#executeQuery()} method, but also
	 * automatically closes the statement after its execution and allows
	 * complete customization of the prepared statement through an optional
	 * instance of {@link DbPreparedStatementHandler}.
	 * <p>This method is typically used when you need to fully customize a
	 * query at runtime, but still want to benefit of a safety net that
	 * ensures that the allocated statement will be closed. Often another more
	 * specialized method in this class will already serve your needs, so be
	 * sure to verify that you actually need to intervene on every front.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select
	 *    .field("first")
	 *    .field("last")
	 *    .from("person")
	 *    .whereParameter("name", "=");
	 *final String name = "you";
	 *String result = (String)manager.executeQuery(select, new DbPreparedStatementHandler() {
	 *        public void setParameters(DbPreparedStatement statement)
	 *        {
	 *            statement
	 *                .setString("name", name);
	 *        }
	 *
	 *        public Object concludeResults(DbResultSet resultset)
	 *        throws SQLException
	 *        {
	 *            return resultset.getString("first")+" "+resultset.getString("last");
	 *        }
	 *    });</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code>DbPreparedStatementHandler</code>
	 * that will be used to customize the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the object that was returned by the overridden {@link
	 * DbResultSetHandler#concludeResults(DbResultSet) concludeResults}
	 * method; or
	 * <p><code>null</code> if this method wasn't overridden
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbPreparedStatementHandler
	 * @since 1.0
	 */
	public <ResultType> ResultType executeQuery(ReadQuery query, DbPreparedStatementHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				executeQuery(statement, handler);
				if (null == handler)
				{
					return null;
				}
				
				try
				{
					return (ResultType)handler.concludeResults(getResultSet(statement));
				}
				catch (SQLException e)
				{
					statement.handleException();
					throw new DatabaseException(e);
				}
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}

	/**
	 * Executes a select statement and handle the results in a custom fashion.
	 * It relies on the wrapped {@link DbPreparedStatement#executeQuery()}
	 * method, but also automatically closes the statement after its execution
	 * and allows interaction with the resultset through an optional instance
	 * of {@link DbResultSetHandler}.
	 * <p>This method is typically used when you need to interact with the
	 * results of a query, but still want to benefit of a safety net that
	 * ensures that the allocated statement will be closed. Often another more
	 * specialized method in this class will already serve your needs, so be
	 * sure to verify that there isn't another one that's better suited.
	 * <h3>Example</h3>
	 * <pre>DbQueryManager manager = new DbQueryManager(datasource);
	 *Select select = new Select(datasource);
	 *select
	 *    .field("first")
	 *    .field("last")
	 *    .from("person");
	 *String result = (String)manager.executeQuery(select, new DbResultSetHandler() {
	 *        public Object concludeResults(DbResultSet resultset)
	 *        throws SQLException
	 *        {
	 *            return resultset.getString("first")+" "+resultset.getString("last");
	 *        }
	 *    });</pre>
	 *
	 * @param query the query builder instance that needs to be executed
	 * @param handler an instance of <code><code>DbResultSetHandler</code></code>
	 * that will be used to handle the results of the query execution; or
	 * <code>null</code> if you don't want to customize it at all
	 * @return the object that was returned by the overridden {@link
	 * DbResultSetHandler#concludeResults(DbResultSet) concludeResults}
	 * method; or
	 * <p><code>null</code> if this method wasn't overridden
	 * @exception DatabaseException see {@link
	 * DbPreparedStatement#executeQuery()}
	 * @see DbPreparedStatement#executeQuery()
	 * @see DbResultSetHandler
	 * @since 1.0
	 */
	public <ResultType> ResultType executeQuery(ReadQuery query, DbResultSetHandler handler)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbConnection connection = getConnection();
		try
		{
			DbPreparedStatement statement = getPreparedStatement(query, handler, connection);
			try
			{
				executeQuery(statement, null);
				
				if (handler != null)
				{
					try
					{
						return (ResultType)handler.concludeResults(getResultSet(statement));
					}
					catch (SQLException e)
					{
						statement.handleException();
						throw new DatabaseException(e);
					}
				}
				
				return null;
			}
			finally
			{
				defensiveClose(statement);
			}
		}
		finally
		{
			connection.close();
		}
	}
	
	/**
	 * Reserves a database connection for a this particular thread for all the
	 * instructions that are executed in the provided {@link DbConnectionUser}
	 * instance.
	 * <p>This is typically used to ensure that a series of operations is done
	 * with the same connection, even though a database pool is used in the
	 * background.
	 * <h3>Example</h3>
	 * <pre>Person person;
	 *final Insert store_data = new Insert(datasource).into("person").fields(person);
	 *final Select get_last_id = new Select(datasource).from("person").field("LAST_INSERT_ID()");
	 *final DbQueryManager manager = new DbQueryManager(datasource);
	 *int id = ((Integer)manager.reserveConnection(new DbConnectionUser() {
	 *        public Integer useConnection(DbConnection connection)
	 *        {
	 *            manager.executeUpdate(store_data);
	 *            return new Integer(manager.executeGetFirstInt(get_last_id));
	 *        }
	 *    })).intValue();</pre>
	 *
	 * @param user an instance of <code>DbConnectionUser</code> that contains
	 * the logic that will be executed
	 * @return the return value from the <code>useConnection</code> method of
	 * the provided <code>DbConnectionUser</code> instance
	 * @exception DatabaseException when errors occurs during the reservation
	 * of a connection for this thread
	 * @exception InnerClassException when errors occurs inside the
	 * <code>DbConnectionUser</code>
	 * @see DbConnectionUser#useConnection(DbConnection)
	 * @since 1.0
	 */
	public <ResultType> ResultType reserveConnection(DbConnectionUser user)
	throws InnerClassException, DatabaseException
	{
		if (null == user)	throw new IllegalArgumentException("user can't be null.");

		DbConnection connection = mDatasource.getConnection();
		ConnectionPool pool = mDatasource.getPool();
		synchronized (pool)
		{
			boolean does_threadconnection_exist = pool.hasThreadConnection(Thread.currentThread());
			try
			{
				if (!does_threadconnection_exist) pool.registerThreadConnection(Thread.currentThread(), connection);
				
				return (ResultType)user.useConnection(connection);
			}
			finally
			{
				if (!does_threadconnection_exist) pool.unregisterThreadConnection(Thread.currentThread());
			}
		}
	}
	
	/**
	 * Ensures that all the instructions that are executed in the provided
	 * {@link DbTransactionUser} instance are executed inside a transaction
	 * and committed afterwards. This doesn't mean that a new transaction will
	 * always be created. If a transaction is already active, it will simply
	 * be re-used. The commit will also only be take place if a new
	 * transaction has actually been started, otherwise it's the
	 * responsibility of the enclosing code to execute the commit. If an
	 * runtime exception occurs during the execution and a new transaction has
	 * been started beforehand, it will be automatically rolled back.
	 * <p>If you need to explicitly roll back an active transaction, use the
	 * {@link DbTransactionUser#rollback() rollback} method of the
	 * <code>DbTransactionUser</code> class. If you use a regular rollback
	 * method, it's possible that you're inside a nested transaction executed
	 * and that after the rollback, other logic continues to be executed
	 * outside the transaction. Using the correct {@link
	 * DbTransactionUser#rollback() rollback} method, stops the execution of
	 * the active <code>DbTransactionUser</code> and breaks out of any number
	 * of them nesting.
	 * <p>It's recommended to always use transactions through this method
	 * since it ensures that transactional code can be re-used and enclosed in
	 * other transactional code. Correctly using the regular
	 * transaction-related methods requires great care and planning and often
	 * results in error-prone and not reusable code.
	 * <h3>Example</h3>
	 * <pre>final Insert insert = new Insert(mDatasource).into("valuelist").field("value", 232);
	 *final DbQueryManager manager = new DbQueryManager(datasource);
	 *manager.inTransaction(new DbTransactionUserWithoutResult() {
	 *        public void useTransactionWithoutResult()
	 *        throws InnerClassException
	 *        {
	 *            manager.executeUpdate(insert);
	 *            manager.executeUpdate(insert);
	 *        }
	 *    });
	 *</pre>
	 *
	 * @param user an instance of <code>DbTransactionUser</code> that contains
	 * the logic that will be executed
	 * @return the return value from the <code>useTransaction</code> method of
	 * the provided <code>DbTransactionUser</code> instance
	 * @exception DatabaseException when errors occurs during the handling of
	 * the transaction
	 * @exception InnerClassException when errors occurs inside the
	 * <code>DbTransactionUser</code>
	 * @see DbTransactionUser#useTransaction()
	 * @see DbTransactionUserWithoutResult#useTransactionWithoutResult()
	 * @since 1.0
	 */
	public <ResultType> ResultType inTransaction(DbTransactionUser user)
	throws InnerClassException, DatabaseException
	{
		boolean started_transaction = false;
		DbConnection connection = null;
		try
		{
			synchronized (mDatasource)
			{
				connection = mDatasource.getConnection();
				int isolation = user.getTransactionIsolation();
				if (isolation != -1)
				{
					connection.setTransactionIsolation(isolation);
				}
				started_transaction = connection.beginTransaction();
			}
			
			ResultType result = (ResultType)user.useTransaction();
			if (started_transaction)
			{
				connection.commit();
				if (!mDatasource.isPooled())
				{
					connection.close();
				}
			}
			return result;
		}
		catch (RollbackException e)
		{
			if (connection != null)
			{
				connection.rollback();
				if (!mDatasource.isPooled())
				{
					connection.close();
				}
			}

			if (started_transaction)
			{
				return (ResultType)null;
			}
			else
			{
				throw e;
			}
		}
		catch (RuntimeException e)
		{
			if (started_transaction &&
				connection != null)
			{
				try
				{
					if (e instanceof ControlFlowRuntimeException)
					{
						connection.commit();
					}
					else
					{
						connection.rollback();
						if (!mDatasource.isPooled())
						{
							connection.close();
						}
					}
				}
				catch (DatabaseException e2)
				{
					// nothing that can be done about this
					// the connection is probably closed since
					// a database error occurred
				}
			}
			throw e;
		}
		catch (Error e)
		{
			if (started_transaction &&
				connection != null)
			{
				try
				{
					connection.rollback();
					if (!mDatasource.isPooled())
					{
						connection.close();
					}
				}
				catch (DatabaseException e2)
				{
					// nothing that can be done about this
					// the connection is probably closed since
					// a database error occurred
				}
			}
			throw e;
		}
	}

	/**
	 * Executes a query statement in a connection of this
	 * <code>DbQueryManager</code>'s <code>Datasource</code>. Functions
	 * exactly as the wrapped {@link DbStatement#executeQuery(ReadQuery)} method.
	 * <p>Note that the statement will not be automatically closed since using
	 * this method implies that you still have to work with the resultset.
	 *
	 * @param query the query builder instance that should be executed
	 * @return the statement that has been executed
	 * @exception DatabaseException see {@link DbStatement#executeQuery(ReadQuery)}
	 * @see DbStatement#executeQuery(ReadQuery)
	 * @since 1.0
	 */
	public DbStatement executeQuery(ReadQuery query)
	throws DatabaseException
	{
		if (null == query)  throw new IllegalArgumentException("query can't be null.");
		
		DbStatement statement = getConnection().createStatement();
		statement.executeQuery(query);
		return statement;
	}
	
	/**
	 * Fetches the next row of a resultset without processing it in any way.
	 *
	 * @param resultSet a valid <code>ResultSet</code> instance
	 * @return <code>true</code> if a new row was retrieved; or
	 * <p><code>false</code> if there are no more rows .
	 * @exception DatabaseException when an error occurred during the fetch of
	 * the next row in the resultset
	 * @see #fetch(ResultSet, DbRowProcessor)
	 * @since 1.0
	 */
	public boolean fetch(ResultSet resultSet)
	throws DatabaseException
	{
		return fetch(resultSet, null);
	}

	/**
	 * Fetches the next row of a resultset and processes it through a
	 * <code>DbRowProcessor</code>.
	 *
	 * @param resultSet a valid <code>ResultSet</code> instance
	 * @param rowProcessor a <code>DbRowProcessor</code> instance, if it's
	 * <code>null</code> no processing will be performed on the fetched row
	 * @return <code>true</code> if a new row was retrieved; or
	 * <p><code>false</code> if there are no more rows .
	 * @exception DatabaseException when an error occurred during the fetch of
	 * the next row in the resultset
	 * @see #fetch(ResultSet)
	 * @see DbRowProcessor
	 * @since 1.0
	 */
	public boolean fetch(ResultSet resultSet, DbRowProcessor rowProcessor)
	throws DatabaseException
	{
		if (null == resultSet)  throw new IllegalArgumentException("resultSet can't be null.");

		try
		{
			if (resultSet.next())
			{
				if (rowProcessor != null)
				{
					rowProcessor.processRowWrapper(resultSet);
				}
				return true;
			}
		}
		catch (SQLException e)
		{
			throw new RowProcessorErrorException(e);
		}

		return false;
	}
	
	/**
	 * Fetches all the next rows of a resultset and processes it through a
	 * <code>DbRowProcessor</code>.
	 *
	 * @param resultSet a valid <code>ResultSet</code> instance
	 * @param rowProcessor a <code>DbRowProcessor</code> instance, if it's
	 * <code>null</code> no processing will be performed on the fetched rows
	 * @return <code>true</code> if rows were fetched; or
	 * <p><code>false</code> if the resultset contained no rows.
	 * @exception DatabaseException when an error occurred during the fetch of
	 * the next rows in the resultset
	 * @see DbRowProcessor
	 * @since 1.0
	 */
	public boolean fetchAll(ResultSet resultSet, DbRowProcessor rowProcessor)
	throws DatabaseException
	{
		if (null == rowProcessor)   throw new IllegalArgumentException("rowProcessor can't be null.");
		
		boolean result = false;
		
		while (fetch(resultSet, rowProcessor))
		{
			result = true;
			
			if (rowProcessor != null &&
				!rowProcessor.wasSuccessful())
			{
				break;
			}
		}

		return result;
	}

	/**
	 * Obtains a <code>DbConnection</code> of this <code>DbQueryManager</code>'s
	 * <code>Datasource</code>. Functions exactly as the wrapped {@link
	 * Datasource#getConnection()} method.
	 * @see Datasource#getConnection()
	 * @since 1.0
	 * @exception DatabaseException see {@link Datasource#getConnection()}
	 * @return the requested <code>DbConnection</code>
	 */
	public DbConnection getConnection()
	throws DatabaseException
	{
		return mDatasource.getConnection();
	}

	/**
	 * Retrieves the <code>Datasource</code> of this
	 * <code>DbQueryManager</code>.
	 *
	 * @return the requested <code>Datasource</code>
	 * @since 1.0
	 */
	public Datasource getDatasource()
	{
		return mDatasource;
	}
	
	/**
	 * Simply clones the instance with the default clone method. This creates
	 * a shallow copy of all fields and the clone will in fact just be another
	 * reference to the same underlying data. The independence of each cloned
	 * instance is consciously not respected since they rely on resources that
	 * can't be cloned.
	 * @since 1.0
	 * @return a clone of this instance
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.database").severe(ExceptionUtils.getExceptionStackTrace(e));
			return null;
			///CLOVER:ON
		}
	}
	
	private void defensiveClose(InputStream stream)
	{
		if (null == stream)
		{
			return;
		}
		
		try
		{
			stream.close();
		}
		catch (IOException e)
		{
			// couldn't close stream since it probably already has been
			// closed after an exception
			// proceed without reporting an error message.
		}
	}
	
	private void defensiveClose(Reader reader)
	{
		if (null == reader)
		{
			return;
		}
		
		try
		{
			reader.close();
		}
		catch (IOException e)
		{
			// couldn't close reader since it probably already has been
			// closed after an exception
			// proceed without reporting an error message.
		}
	}
	
	private void defensiveClose(DbStatement statement)
	{
		if (null == statement)
		{
			return;
		}
		
		try
		{
			statement.close();
		}
		catch (DatabaseException e)
		{
			// couldn't close statement since it probably already has been
			// closed after an exception
			// proceed without reporting an error message.
		}
	}
}

