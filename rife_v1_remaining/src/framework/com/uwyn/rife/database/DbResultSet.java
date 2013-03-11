/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DbResultSet.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import java.sql.*;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.exceptions.MissingResultsException;
import com.uwyn.rife.database.exceptions.RowIndexOutOfBoundsException;
import com.uwyn.rife.tools.ExceptionUtils;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

public abstract class DbResultSet implements ResultSet, Cloneable
{
	protected DbStatement	mStatement = null;
	protected ResultSet		mResultSet = null;
	protected boolean		mFirstRowSkew = false;
	protected boolean		mHasResultRows = false;

	DbResultSet(DbStatement statement, ResultSet resultSet)
	{
		assert statement != null;
		assert resultSet != null;
		
		mStatement = statement;
		mResultSet = resultSet;
	}
	
	public final boolean next()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			mFirstRowSkew = false;
			return true;
		}
		else if (mResultSet.next())
		{
			mHasResultRows = true;
			mFirstRowSkew = false;
			return true;
		}
		
		return false;
	}
	
	public final boolean previous()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		if (mResultSet.previous())
		{
			mHasResultRows = true;
			mFirstRowSkew = false;
			return true;
		}
		
		return false;
	}
	
	public final boolean absolute(int row)
	throws SQLException
	{
		if (mResultSet.absolute(row))
		{
			mHasResultRows = true;
			mFirstRowSkew = false;
			return true;
		}
		
		return false;
	}
	
	public final boolean relative(int rows)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			if (mResultSet.relative(rows-1))
			{
				mFirstRowSkew = false;
			}
		}
		else if (mResultSet.relative(rows))
		{
			mHasResultRows = true;
			mFirstRowSkew = false;
			return true;
		}
		
		return false;
	}
	
	public final void beforeFirst()
	throws SQLException
	{
		mFirstRowSkew = false;
		mResultSet.beforeFirst();
	}
	
	public final boolean first()
	throws SQLException
	{
		if (mResultSet.first())
		{
			mHasResultRows = true;
			mFirstRowSkew = false;
			return true;
		}
		
		return false;
	}
	
	public final boolean last()
	throws SQLException
	{
		if (mResultSet.last())
		{
			mHasResultRows = true;
			mFirstRowSkew = false;
			return true;
		}
		
		return false;
	}
	
	public final void afterLast()
	throws SQLException
	{
		mResultSet.afterLast();
	}
	
	public final void moveToInsertRow()
	throws SQLException
	{
		mResultSet.moveToInsertRow();
	}
	
	public final void moveToCurrentRow()
	throws SQLException
	{
		mResultSet.moveToCurrentRow();
	}
	
	public final boolean isBeforeFirst()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			return true;
		}
		
		return mResultSet.isBeforeFirst();
	}
	
	public final boolean isFirst()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			return false;
		}
		
		return mResultSet.isFirst();
	}
	
	public final boolean isLast()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			return false;
		}
		
		return mResultSet.isLast();
	}
	
	public final boolean isAfterLast()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			return false;
		}
		
		return mResultSet.isAfterLast();
	}
	
	public final int getRow()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			return mResultSet.getRow()-1;
		}
		else
		{
			return mResultSet.getRow();
		}
	}
	
	public final void refreshRow()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.refreshRow();
	}
	
	public final void insertRow()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.insertRow();
	}
	
	public final void updateRow()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateRow();
	}
	
	public final void deleteRow()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.deleteRow();
	}
	
	public final boolean rowInserted()
	throws SQLException
	{
		return mResultSet.rowInserted();
	}
	
	public final boolean rowUpdated()
	throws SQLException
	{
		return mResultSet.rowUpdated();
	}
	
	public final boolean rowDeleted()
	throws SQLException
	{
		return mResultSet.rowDeleted();
	}
	
	public final void close()
	throws SQLException
	{
		mStatement = null;
		if (mResultSet != null)
		{
			mResultSet.close();
		}
		mFirstRowSkew = false;
		mHasResultRows = false;
	}
	
	public final boolean wasNull()
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.wasNull();
	}
	
	public final void setFetchDirection(int direction)
	throws SQLException
	{
		mResultSet.setFetchDirection(direction);
	}
	
	public final void setFetchSize(int rows)
	throws SQLException
	{
		mResultSet.setFetchSize(rows);
	}
	
	public final void cancelRowUpdates()
	throws SQLException
	{
		mResultSet.cancelRowUpdates();
	}
	
	public final ResultSetMetaData getMetaData()
	throws SQLException
	{
		return mResultSet.getMetaData();
	}
	
	public final int getConcurrency()
	throws SQLException
	{
		return mResultSet.getConcurrency();
	}
	
	public final int getFetchDirection()
	throws SQLException
	{
		return mResultSet.getFetchDirection();
	}
	
	public final int getFetchSize()
	throws SQLException
	{
		return mResultSet.getFetchSize();
	}
	
	public final void clearWarnings()
	throws SQLException
	{
		mResultSet.clearWarnings();
	}
	
	public final SQLWarning getWarnings()
	throws SQLException
	{
		return mResultSet.getWarnings();
	}
	
	public final String getCursorName()
	throws SQLException
	{
		return mResultSet.getCursorName();
	}
	
	public final Statement getStatement()
	throws SQLException
	{
		return mResultSet.getStatement();
	}
	
	public final int getType()
	throws SQLException
	{
		return mResultSet.getType();
	}
	
	public final int findColumn(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.findColumn(columnName);
	}
	
	/**
	 * Determines if there are rows available in the <code>ResultSet</code>
	 * object that was returned by an <code>execute</code> method.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return <code>true</code> if there are result rows available; or
	 * <p>
	 * <code>false</code> if no <code>ResultSet</code> object was available or
	 * it didn't have any result rows.
	 *
	 * @throws DatabaseException if a database access error occurs
	 *
	 * @since 1.0
	 */
	public boolean hasResultRows()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null)
			{
				if (mHasResultRows)
				{
					return true;
				}
				
				if (mFirstRowSkew)
				{
					return true;
				}
				
				if (next())
				{
					mFirstRowSkew = true;
					mHasResultRows = true;
					return true;
				}
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		return false;
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a string. This method works both when
	 * the <code>next</code> method has never been called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>String</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public String getFirstString()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getString(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a boolean. This method works both when
	 * the <code>next</code> method has never been called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>boolean</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public boolean getFirstBoolean()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getBoolean(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a byte. This method works both when
	 * the <code>next</code> method has never been called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>byte</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public byte getFirstByte()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getByte(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a short. This method works both when
	 * the <code>next</code> method has never been called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>short</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public short getFirstShort()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getShort(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as an integer. This method works both when
	 * the <code>next</code> method has never been called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>int</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public int getFirstInt()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getInt(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a long. This method works both when
	 * the <code>next</code> method has never been called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>long</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public long getFirstLong()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getLong(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a float. This method works both when
	 * the <code>next</code> method has never been called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>float</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public float getFirstFloat()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getFloat(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a double. This method works both when
	 * the <code>next</code> method has never been called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>String</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public double getFirstDouble()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getDouble(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a big decimal. This method works both when
	 * the <code>next</code> method has never been called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>BigDecimal</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public BigDecimal getFirstBigDecimal()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getBigDecimal(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}

	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as an array of bytes. This method works
	 * both when the <code>next</code> method has never been called or once been
	 * called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>byte[]</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public byte[] getFirstBytes()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getBytes(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a sql date. This method works both
	 * when the <code>next</code> method has never been called or once been
	 * called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>java.sql.Date</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public java.sql.Date getFirstDate()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getDate(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
    /**
     * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a sql date. This method uses the given
	 * calendar to construct an appropriate millisecond value for the date if
	 * the underlying database does not store timezone information.
	 * This method works both when the <code>next</code> method has never been
	 * called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
     *
     * @param cal the <code>java.util.Calendar</code> object
     * to use in constructing the date
	 *
     * @return the first <code>java.sql.Date</code> object in the resultsn;
     * if the value is SQL <code>NULL</code>,
     * the value returned is <code>null</code> in the Java programming language
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
     * @since 1.0
     */
	public java.sql.Date getFirstDate(Calendar cal)
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getDate(1, cal);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a sql time. This method works both
	 * when the <code>next</code> method has never been called or once been
	 * called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>java.sql.Time</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public java.sql.Time getFirstTime()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getTime(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
    /**
     * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a sql time. This method works both
	 * when the <code>next</code> method has never been called or once been
	 * called. This method uses the given calendar to construct an appropriate
	 * millisecond value for the time if the underlying database does not store
     * timezone information.
	 * This method works both when the <code>next</code> method has never been
	 * called or once been called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
     *
     * @param cal the <code>java.util.Calendar</code> object to use in
	 * constructing the time
	 *
	 * @return the first <code>java.sql.Time</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public java.sql.Time getFirstTime(Calendar cal)
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getTime(1, cal);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}

	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a sql timestamo. This method works both
	 * when the <code>next</code> method has never been called or once been
	 * called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>java.sql.Timestamp</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public java.sql.Timestamp getFirstTimestamp()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getTimestamp(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}

    /**
     * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a sql timestamp. This method uses the
	 * given calendar to construct an appropriate millisecond value for the
	 * timestamp if the underlying database does not store timezone information.
	 * This method works both when the <code>next</code> method has never been
	 * called or once been called.
	 * <p>
	 * It is perfectly usable after the <code>hasResultRows</code> method or
	 * alone where catching the <code>MissingResultsException</code> is used to
	 * indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
     *
     * @param cal the <code>java.util.Calendar</code> object to use in
	 * constructing the date
	 *
	 * @return the first <code>java.sql.Timestamp</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public java.sql.Timestamp getFirstTimestamp(Calendar cal)
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getTimestamp(1, cal);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as an ascii stream. This method works both
	 * when the <code>next</code> method has never been called or once been
	 * called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>java.io.InputStream</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public InputStream getFirstAsciiStream()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getAsciiStream(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a character stream. This method works
	 * both when the <code>next</code> method has never been called or once been
	 * called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>java.io.Reader</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public Reader getFirstCharacterStream()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getCharacterStream(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	/**
	 * Retrieves the first field of the first row of this
	 * <code>DbResultSet</code> object as a binary stream. This method works hiboth
	 * when the <code>next</code> method has never been called or once been
	 * called.
	 * <p>
	 * Therefore, it's thus perfectly usable after the <code>hasResultRows</code>
	 * method or alone where catching the <code>MissingResultsException</code>
	 * is used to indicate the absence of results.
	 * <p>
	 * If an exception is thrown, the related <code>DbStatement</code> is
	 * automatically closed and an ongoing transaction will be automatically
	 * rolled back if it belongs to the executing thread.
	 *
	 * @return the first <code>java.io.InputStream</code> object in the results.
	 *
	 * @throws DatabaseException if a database access error occurs. If there
	 * are no results available the thrown exception is
	 * {@link MissingResultsException}.
	 *
	 * @see #hasResultRows
	 *
	 * @since 1.0
	 */
	public InputStream getFirstBinaryStream()
	throws DatabaseException
	{
		try
		{
			if (mResultSet != null &&
				(isFirst() || (isBeforeFirst() && next())))
			{
				return getBinaryStream(1);
			}
		}
		catch (SQLException e)
		{
			mStatement.handleException();
			throw new DatabaseException(e);
		}
		
		throw new MissingResultsException(mStatement.getConnection().getDatasource());
	}
	
	public final String getString(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getString(columnIndex);
	}
	
	public final String getString(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getString(columnName);
	}
	
	public final boolean getBoolean(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBoolean(columnIndex);
	}
	
	public final boolean getBoolean(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBoolean(columnName);
	}
	
	public final byte getByte(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getByte(columnIndex);
	}
	
	public final byte getByte(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getByte(columnName);
	}
	
	public final short getShort(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getShort(columnIndex);
	}
		
	public final short getShort(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getShort(columnName);
	}
	
	public final int getInt(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getInt(columnIndex);
	}
	
	public final int getInt(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getInt(columnName);
	}
	
	public final long getLong(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getLong(columnIndex);
	}

	public final long getLong(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getLong(columnName);
	}
 	
	public final float getFloat(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getFloat(columnIndex);
	}
	
	public final float getFloat(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getFloat(columnName);
	}
	
	public final double getDouble(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getDouble(columnIndex);
	}
	
	public final double getDouble(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getDouble(columnName);
	}
	
	public final BigDecimal getBigDecimal(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBigDecimal(columnIndex);
	}
	
	public final BigDecimal getBigDecimal(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBigDecimal(columnName);
	}
	
	public final BigDecimal getBigDecimal(int columnIndex, int scale)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBigDecimal(columnIndex, scale);
	}
	
	public final BigDecimal getBigDecimal(String columnName, int scale)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBigDecimal(columnName, scale);
	}
	
	public final byte[] getBytes(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBytes(columnIndex);
	}
	
	public final byte[] getBytes(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBytes(columnName);
	}
	
	public final Date getDate(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getDate(columnIndex);
	}
	
	public final Date getDate(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getDate(columnName);
	}
	
	public final Date getDate(int columnIndex, Calendar cal)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getDate(columnIndex, cal);
	}
	
	public final Date getDate(String columnName, Calendar cal)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getDate(columnName, cal);
	}
	
	public final Time getTime(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getTime(columnIndex);
	}
	
	public final Time getTime(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getTime(columnName);
	}
	
	public final Time getTime(int columnIndex, Calendar cal)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getTime(columnIndex, cal);
	}
	
	public final Time getTime(String columnName, Calendar cal)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getTime(columnName, cal);
	}

	public final Timestamp getTimestamp(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getTimestamp(columnIndex);
	}
	
	public final Timestamp getTimestamp(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getTimestamp(columnName);
	}
	
	public final Timestamp getTimestamp(int columnIndex, Calendar cal)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getTimestamp(columnIndex, cal);
	}
	
	public final Timestamp getTimestamp(String columnName, Calendar cal)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getTimestamp(columnName, cal);
	}
		
	public final InputStream getAsciiStream(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getAsciiStream(columnIndex);
	}
	
	public final InputStream getAsciiStream(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getAsciiStream(columnName);
	}
	
	public final InputStream getUnicodeStream(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getUnicodeStream(columnIndex);
	}
	
	public final InputStream getUnicodeStream(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getUnicodeStream(columnName);
	}
	
	public final Reader getCharacterStream(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getCharacterStream(columnIndex);
	}
	
	public final Reader getCharacterStream(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getCharacterStream(columnName);
	}
	
	public final InputStream getBinaryStream(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBinaryStream(columnIndex);
	}
	
	public final InputStream getBinaryStream(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBinaryStream(columnName);
	}
	
	public final Ref getRef(String colName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getRef(colName);
	}
	
	public final Ref getRef(int i)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getRef(i);
	}
	
	public final Object getObject(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getObject(columnIndex);
	}
	
	public final Object getObject(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getObject(columnName);
	}
	
	public final Object getObject(int i, Map map)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getObject(i, map);
	}
	
	public final Object getObject(String colName, Map map)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getObject(colName, map);
	}
	
	public final Blob getBlob(int i)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBlob(i);
	}
	
	public final Blob getBlob(String colName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getBlob(colName);
	}
	
	public final Clob getClob(int i)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getClob(i);
	}
	
	public final Clob getClob(String colName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getClob(colName);
	}
	
	public final Array getArray(String colName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getArray(colName);
	}
	
	public final Array getArray(int i)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getArray(i);
	}
	
	public final URL getURL(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getURL(columnIndex);
	}
	
	public final URL getURL(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getURL(columnName);
	}
	
	public final void updateNull(int columnIndex)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNull(columnIndex);
	}
	
	public final void updateNull(String columnName)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNull(columnName);
	}
	
	public final void updateString(int columnIndex, String x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateString(columnIndex, x);
	}
	
	public final void updateString(String columnName, String x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateString(columnName, x);
	}
	
	public final void updateBoolean(int columnIndex, boolean x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBoolean(columnIndex, x);
	}
	
	public final void updateBoolean(String columnName, boolean x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBoolean(columnName, x);
	}
	
	public final void updateByte(int columnIndex, byte x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateByte(columnIndex, x);
	}
	
	public final void updateByte(String columnName, byte x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateByte(columnName, x);
	}
	
	public final void updateShort(int columnIndex, short x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateShort(columnIndex, x);
	}
	
	public final void updateShort(String columnName, short x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateShort(columnName, x);
	}
	
	public final void updateInt(int columnIndex, int x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateInt(columnIndex, x);
	}
	
	public final void updateInt(String columnName, int x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateInt(columnName, x);
	}
	
	public final void updateLong(int columnIndex, long x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateLong(columnIndex, x);
	}
	
	public final void updateLong(String columnName, long x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateLong(columnName, x);
	}
	
	public final void updateFloat(int columnIndex, float x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateFloat(columnIndex, x);
	}
	
	public final void updateFloat(String columnName, float x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateFloat(columnName, x);
	}
	
	public final void updateDouble(int columnIndex, double x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateDouble(columnIndex, x);
	}
	
	public final void updateDouble(String columnName, double x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateDouble(columnName, x);
	}
	
	public final void updateBigDecimal(int columnIndex, BigDecimal x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBigDecimal(columnIndex, x);
	}
	
	public final void updateBigDecimal(String columnName, BigDecimal x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBigDecimal(columnName, x);
	}
	
	public final void updateBytes(int columnIndex, byte[] x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBytes(columnIndex, x);
	}
	
	public final void updateBytes(String columnName, byte[] x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBytes(columnName, x);
	}
	
	public final void updateDate(int columnIndex, Date x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateDate(columnIndex, x);
	}
	
	public final void updateDate(String columnName, Date x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateDate(columnName, x);
	}
	
	public final void updateTime(int columnIndex, Time x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateTime(columnIndex, x);
	}
	
	public final void updateTime(String columnName, Time x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateTime(columnName, x);
	}
	
	public final void updateTimestamp(int columnIndex, Timestamp x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateTimestamp(columnIndex, x);
	}
	
	public final void updateTimestamp(String columnName, Timestamp x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateTimestamp(columnName, x);
	}
	
	public final void updateAsciiStream(int columnIndex, InputStream x, int length)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateAsciiStream(columnIndex, x, length);
	}
	
	public final void updateAsciiStream(String columnName, InputStream x, int length)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateAsciiStream(columnName, x, length);
	}
	
	public final void updateCharacterStream(int columnIndex, Reader x, int length)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateCharacterStream(columnIndex, x, length);
	}
	
	public final void updateCharacterStream(String columnName, Reader reader, int length)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateCharacterStream(columnName, reader, length);
	}
	
	public final void updateBinaryStream(int columnIndex, InputStream x, int length)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBinaryStream(columnIndex, x, length);
	}
	
	public final void updateBinaryStream(String columnName, InputStream x, int length)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBinaryStream(columnName, x, length);
	}
	
	public final void updateRef(int columnIndex, Ref x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateRef(columnIndex, x);
	}
	
	public final void updateRef(String columnName, Ref x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateRef(columnName, x);
	}
	
	public final void updateObject(int columnIndex, Object x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateObject(columnIndex, x);
	}
	
	public final void updateObject(String columnName, Object x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateObject(columnName, x);
	}
	
	public final void updateObject(int columnIndex, Object x, int scale)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateObject(columnIndex, x, scale);
	}
	
	public final void updateObject(String columnName, Object x, int scale)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateObject(columnName, x, scale);
	}
	
	public final void updateBlob(int columnIndex, Blob x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBlob(columnIndex, x);
	}
	
	public final void updateBlob(String columnName, Blob x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBlob(columnName, x);
	}
	
	public final void updateClob(int columnIndex, Clob x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateClob(columnIndex, x);
	}
	
	public final void updateClob(String columnName, Clob x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateClob(columnName, x);
	}
	
	public final void updateArray(int columnIndex, Array x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateArray(columnIndex, x);
	}
	
	public final void updateArray(String columnName, Array x)
	throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateArray(columnName, x);
	}
	
	/**
	 * Simply clones the instance with the default clone method. This creates a
	 * shallow copy of all fields and the clone will in fact just be another
	 * reference to the same underlying data. The independence of each cloned
	 * instance is consciously not respected since they rely on resources
	 * that can't be cloned.
	 *
	 * @since 1.0
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// this should never happen
			Logger.getLogger("com.uwyn.rife.database").severe(ExceptionUtils.getExceptionStackTrace(e));
			return null;
		}
	}
}
