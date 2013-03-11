/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SqlNull.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.types;

public class SqlNull
{
	public static final SqlNull	NULL = new SqlNull();
	
	private SqlNull()
	{
	}

	public String toString()
	{
		return "NULL";
	}
}
