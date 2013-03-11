/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DbTransactionUserWithoutResult.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.tools.exceptions.InnerClassException;

/**
 * Convenience class that offers the same facilities as the
 * <code>DbTransactionUser</code> class, but makes it easier to work with
 * transactions that don't return any results.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see DbTransactionUser
 * @since 1.0
 */
public abstract class DbTransactionUserWithoutResult<DataType> extends DbTransactionUser<Object, DataType>
{
	public DbTransactionUserWithoutResult()
	{
	}
	
	public DbTransactionUserWithoutResult(DataType data)
	{
		super(data);
	}
	
	/**
	 * Has been implemented to return a <code>null</code> reference and
	 * delegate the logic to the <code>useTransactionWithoutResult()</code>
	 * method.
	 *
	 * @since 1.0
	 */
	public Object useTransaction()
	throws InnerClassException
	{
		useTransactionWithoutResult();
		return null;
	}

	/**
	 * Should be implemented by all extending classes.
	 *
	 * @since 1.0
	 */
	public abstract void useTransactionWithoutResult() throws InnerClassException;
}
