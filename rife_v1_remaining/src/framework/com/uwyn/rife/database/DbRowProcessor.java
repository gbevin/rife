/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DbRowProcessor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.exceptions.RowProcessorErrorException;
import com.uwyn.rife.tools.ExceptionUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * This abstract base class should be used to implement classes that process one
 * row in a database query resulset. The <code>fetch</code> method of a
 * <code>DbQueryManager</code> requires an instance of a
 * <code>DbRowProcessor</code> and calls its <code>processRow</code>
 * method each time it is called.
 * <p>
 * The <code>DbRowProcessor</code> instance can then work with the result set
 * and extract all needed data. It is free to implement any logic to be
 * able to return the retrieved data in an acceptable form to the user.
 * <p>
 * A class that extends <code>DbRowProcessor</code> can for example take a
 * <code>Template</code> instance as the argument of its constructor and
 * progressively fill in each resulting row in a HTML table. This, without
 * having to maintain the query results in memory to be able to provide it to a
 * seperate method which is responsible for the handling of the output. Using a
 * <code>DbRowProcessor</code> thus allows for perfect seperation and
 * abstraction of result processing without having to be burdened with possible
 * large memory usage or large object allocation.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see #processRow(ResultSet resultSet)
 * @see com.uwyn.rife.database.DbQueryManager
 * @since 1.0
 */
public abstract class DbRowProcessor implements Cloneable
{
	private boolean mSuccessful = false;

	/**
	 * This method has to be implemented by each class that extends the
	 * <code>DbRowProcessor</code> class. It has to contain all the logic that
	 * should be executed for each row of a resultset.
	 *
	 * @param resultSet the <code>ResultSet</code> instance that was provided to
	 * the <code>DbQueryManager</code>'s <code>fetch</code> method.
	 *
	 * @return <code>true</code> if the processing is considered successful; or
	 * <p>
	 * <code>false</code> if the processing is considered failed.
	 * <p>
	 * Note: this return value is purely indicative and unless the user does
	 * checks with the <code>wasSuccessful()</code> method, it will have no
	 * influence on anything.
	 *
	 * @throws SQLException when a database error occurs, it's thus not
	 * necessary to catch all the possible <code>SQLException</code>s inside
	 * this method. They'll be caught higher up and be transformed in
	 * <code>DatabaseException</code>s.
	 *
	 * @see DbQueryManager#fetch(ResultSet, DbRowProcessor)
	 * @see #wasSuccessful()
	 *
	 * @since 1.0
	 */
	public abstract boolean processRow(ResultSet resultSet)
	throws SQLException;

	/**
	 * Indicates whether the processing of the row was successful.
	 *
	 * @return <code>true</code> if the processing was successful; or
	 * <p>
	 * <code>false</code> if the processing was unsuccessful.
	 *
	 * @since 1.0
	 */
	public final boolean wasSuccessful()
	{
		return mSuccessful;
	}

	/**
	 * This method wraps around the actual {@link #processRow(ResultSet)} method
	 * to ensure that the success status is reset at each iteration and that the
	 * possible <code>SQLException</code>s are caught correctly.
	 * <p>
	 * This is the method that's called internally by the <code>fetch()</code>
	 * method of a <code>DbQueryManager</code>. It is not meant to be used by
	 * the user.
	 *
	 * @param resultSet a <code>ResultSet</code> instance that was returned
	 * after a query's execution.
	 *
	 * @throws DatabaseException when a database access error occurred during
	 * the processing of the resultset row
	 *
	 * @see #processRow(ResultSet)
	 * @see DbQueryManager#fetch(ResultSet, DbRowProcessor)
	 *
	 * @since 1.0
	 */
	final void processRowWrapper(ResultSet resultSet)
	throws DatabaseException
	{
		if (null == resultSet)	throw new IllegalArgumentException("resultSet can't be null.");
		
		mSuccessful = false;
		try
		{
			mSuccessful = processRow(resultSet);
		}
		catch (SQLException e)
		{
			mSuccessful = false;
			throw new RowProcessorErrorException(e);
		}
	}
	
	/**
	 * Simply clones the instance with the default clone method since this
	 * class contains no object member variables.
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
