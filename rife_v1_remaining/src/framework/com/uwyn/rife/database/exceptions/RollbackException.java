/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RollbackException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

public class RollbackException extends DatabaseException
{
	private static final long serialVersionUID = -8696265689207175989L;

	public RollbackException()
	{
		super("Causes a transaction user to trigger a rollback.");
	}
}
