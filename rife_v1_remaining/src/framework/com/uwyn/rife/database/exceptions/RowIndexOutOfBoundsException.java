/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RowIndexOutOfBoundsException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.exceptions;

import java.sql.SQLException;

public class RowIndexOutOfBoundsException extends SQLException
{
	private static final long serialVersionUID = 3132609745592263804L;

	public RowIndexOutOfBoundsException()
	{
		super("Row index out of bounds.");
	}
}
