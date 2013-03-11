/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_firebirdsql_jdbc_FBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.CreateTable;

public class org_firebirdsql_jdbc_FBDriver extends generic
{
	public org_firebirdsql_jdbc_FBDriver(Datasource datasource)
	{
		super(datasource);
		
		String table = RifeConfig.Resources.getTableResources();
		
		mCreateTable = new CreateTable(getDatasource())
			.table(table)
			.column(COLUMN_NAME, String.class, 250, "CHARACTER SET ISO8859_1", CreateTable.NOTNULL)
			.column(COLUMN_CONTENT, String.class)
			.column(COLUMN_MODIFIED, java.sql.Timestamp.class)
			.primaryKey(COLUMN_NAME);
	}
}
