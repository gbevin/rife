/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mckoi_JDBCDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.testdatabasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

public class com_mckoi_JDBCDriver extends generic
{
	public com_mckoi_JDBCDriver(Datasource datasource)
	{
		super(datasource);

		mStore = new Insert(getDatasource())
			.into(mCreateStructure.getTable())
			.fieldParameter("id")
			.fieldParameter("valuecol");

		mCount = new Select(getDatasource())
			.from(mCreateStructure.getTable())
			.field("count(*)");
	}
	
	public void store(int id, String value)
	throws DatabaseException
	{
		_store(mStore, id, value);
	}
	
	public int count()
	throws DatabaseException
	{
		return _count(mCount);
	}
}
