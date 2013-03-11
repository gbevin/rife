/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TransactionRollbackErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class TransactionRollbackErrorException extends TransactionErrorException
{
	private static final long serialVersionUID = 2809082434948067632L;

	public TransactionRollbackErrorException(Datasource datasource, Throwable cause)
	{
		super("Error while rolling back the transaction.", datasource, cause);
	}
}
