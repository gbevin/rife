/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DbTransactionUser.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.RollbackException;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.util.logging.Logger;

/**
 * By extending this class it's possible to provide the logic that should be
 * executed by the {@link DbQueryManager#inTransaction(DbTransactionUser) inTransaction}
 * method in the {@link DbQueryManager} class.
 * <p>This class has both a default constructor and one that can take a data
 * object. This can be handy when using it as an extending anonymous inner
 * class when you need to use variables inside the inner class that are
 * cumbersome to change to <code>final</code> in the enclosing class.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see DbQueryManager#inTransaction(DbTransactionUser)
 * @since 1.0
 */
public abstract class DbTransactionUser<ResultType, DataType> implements Cloneable
{
	protected DataType  mData = null;
	
	public DbTransactionUser()
	{
	}
	
	public DbTransactionUser(DataType data)
	{
		mData = data;
	}
	
	public DataType getData()
	{
		return mData;
	}
	
	/**
	 * Should be overridden if the transaction has to be executed in another
	 * isolation level.
	 * 
	 * @return <code>-1</code> when the active isolation level should be
	 * preserved; or
	 * <p>a level constant from {@link java.sql.Connection Connection} if the
	 * isolation needs to be changed.
	 * @since 1.0
	 */
	public int getTransactionIsolation()
	{
		return -1;
	}
	
	/**
	 * Should be used to roll back ongoing transactions, otherwise enclosing
	 * transaction users might not be interrupted and subsequent modification
	 * can still happen outside the transaction.
	 * 
	 * @exception RollbackException indicates that a rollback should happen
	 * and all further transaction logic interrupted.
	 * @since 1.0
	 */
	public void rollback()
	throws RollbackException
	{
		throw new RollbackException();
	}
	
	/**
	 * Calling this method makes it possible to throw a checked exception from
	 * within this class.
	 * <p>To catch it you should surround the {@link
	 * DbQueryManager#inTransaction(DbTransactionUser) inTransaction} with a
	 * <code>try-catch</code> block that catching
	 * <code>InnerClassException</code>. The original exception is then
	 * available through <code>getCause()</code> and can for example be
	 * rethrown.
	 * 
	 * @exception InnerClassException when a checked exception needs to be
	 * thrown from within this class and caught outside the caller.
	 * @since 1.0
	 */
	public void throwException(Exception exception)
	throws InnerClassException
	{
		throw new InnerClassException(exception);
	}
	
	/**
	 * Should be implemented by all extending classes.
	 * 
	 * @since 1.0
	 */
	public abstract ResultType useTransaction() throws InnerClassException;

	/**
	 * Simply clones the instance with the default clone method since this
	 * class contains no member variables.
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
