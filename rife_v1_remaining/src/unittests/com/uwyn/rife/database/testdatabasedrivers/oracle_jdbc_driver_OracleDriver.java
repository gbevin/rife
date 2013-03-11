/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: oracle_jdbc_driver_OracleDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.testdatabasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.TestDbQueryManagerImpl;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

public class oracle_jdbc_driver_OracleDriver extends TestDbQueryManagerImpl
{
	private static CreateTable	sCreateStructure = null;
	private static Insert		sStore = null;
	private static Select		sCount = null;
	private static DropTable	sRemoveStructure = null;
	
	public oracle_jdbc_driver_OracleDriver(Datasource datasource)
	{
		super(datasource);

		if (null == sCreateStructure)
		{
			sCreateStructure = new CreateTable(getDatasource());
			sCreateStructure.table("TestTable")
				.column("id", int.class, CreateTable.NOTNULL)
				.column("valuecol", String.class, 32, CreateTable.NOTNULL)
				.primaryKey("ID_PK", "id");
		}
		
		if (null == sStore)
		{
			sStore = new Insert(getDatasource());
			sStore.into(sCreateStructure.getTable())
				.fieldParameter("id")
				.fieldParameter("valuecol");
		}
		
		if (null == sCount)
		{
			sCount = new Select(getDatasource());
			sCount.from(sCreateStructure.getTable())
				.field("count(*)");
		}

		if (null == sRemoveStructure)
		{
			sRemoveStructure = new DropTable(getDatasource());
			sRemoveStructure.table(sCreateStructure.getTable());
		}

		assert sCreateStructure != null;
		assert sStore != null;
		assert sCount != null;
		assert sRemoveStructure != null;

		assert sCreateStructure.getSql() != null;
		assert sStore.getSql() != null;
		assert sCount.getSql() != null;
		assert sRemoveStructure.getSql() != null;
	}
	
	public boolean install()
	throws DatabaseException
	{
		return _install(sCreateStructure);
	}
	
	public void store(int id, String value)
	throws DatabaseException
	{
		_store(sStore, id, value);
	}
	
	public int count()
	throws DatabaseException
	{
		return _count(sCount);
	}
	
	public boolean remove()
	throws DatabaseException
	{
		return _remove(sRemoveStructure);
	}
}
