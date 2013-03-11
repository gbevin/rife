/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TransactionTimedOutException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import com.uwyn.rife.database.Datasource;

public class TransactionTimedOutException extends TransactionErrorException
{
	private static final long serialVersionUID = 6277363843403636905L;

	public TransactionTimedOutException(Datasource datasource)
	{
		super("The transaction timed out.", datasource, null);
	}
}
