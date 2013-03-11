/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_postgresql_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.testdatabasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.DropTable;

public class org_postgresql_Driver extends generic
{
	public org_postgresql_Driver(Datasource datasource)
	{
		super(datasource);

		mCreateStructure = new CreateTable(getDatasource())
			.table("TestTable")
			.column("id", int.class, CreateTable.NOTNULL)
			.column("valuecol", String.class, 32, CreateTable.NOTNULL)
			.primaryKey("ID_PK", "id");

		mRemoveStructure = new DropTable(getDatasource())
			.table(mCreateStructure.getTable());
	}
	
	public boolean install()
	throws DatabaseException
	{
		return _install(mCreateStructure);
	}
	
	public boolean remove()
	throws DatabaseException
	{
		return _remove(mRemoveStructure);
	}
}
