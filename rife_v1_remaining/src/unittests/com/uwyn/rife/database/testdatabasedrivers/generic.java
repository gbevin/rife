/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.testdatabasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.TestDbQueryManagerImpl;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

public class generic extends TestDbQueryManagerImpl
{
	protected CreateTable	mCreateStructure = null;
	protected Insert		mStore = null;
	protected Select		mCount = null;
	protected DropTable		mRemoveStructure = null;
	
	public generic(Datasource datasource)
	{
		super(datasource);
		
		mCreateStructure = new CreateTable(getDatasource())
			.table("TestTable")
			.column("id", int.class, CreateTable.NOTNULL)
			.column("valuecol", String.class, 32, CreateTable.NOTNULL)
			.primaryKey("ID_PK", "id");

		mStore = new Insert(getDatasource())
			.into(mCreateStructure.getTable())
			.fieldParameter("id")
			.fieldParameter("valuecol");

		mCount = new Select(getDatasource())
			.from(mCreateStructure.getTable())
			.field("count(*)");
		
		mRemoveStructure = new DropTable(getDatasource())
			.table(mCreateStructure.getTable());
	}
	
	public boolean install()
	throws DatabaseException
	{
		return _install(mCreateStructure);
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
	
	public boolean remove()
	throws DatabaseException
	{
		return _remove(mRemoveStructure);
	}
}
