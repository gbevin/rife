/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TransactionSupportCheckErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class TransactionSupportCheckErrorException extends TransactionErrorException
{
	private static final long serialVersionUID = 2834697164959844045L;

	public TransactionSupportCheckErrorException(Datasource datasource, Throwable cause)
	{
		super("Error while checking the transaction support.", datasource, cause);
	}
}
