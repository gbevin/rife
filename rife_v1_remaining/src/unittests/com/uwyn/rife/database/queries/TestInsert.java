/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestInsert.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.BeanImpl;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.exceptions.DatabaseException;

public abstract class TestInsert extends TestQuery
{
	public TestInsert(String name)
	{
		super(name);
	}
	
	public DbQueryManager setupQuery(Datasource datasource)
	{
		DbQueryManager manager = new DbQueryManager(datasource);

		CreateTable createtable = new CreateTable(datasource);
		createtable.table("tablename")
			.columns(BeanImpl.class)
			.column("nullColumn", String.class)
			.precision("propertyBigDecimal", 18, 9)
			.precision("propertyChar", 10)
			.precision("propertyDouble", 12, 3)
			.precision("propertyFloat", 13, 2)
			.precision("propertyString", 255)
			.precision("propertyStringbuffer", 100)
			.precision("nullColumn", 255);
		
		try
		{
			manager.executeUpdate(createtable);
			
			createtable.table("table2");
			manager.executeUpdate(createtable);
		}
		catch (DatabaseException e)
		{
			cleanupQuery(manager);
			throw new RuntimeException(e);
		}
		
		return manager;
	}
	
	private void cleanupQuery(DbQueryManager manager)
	{
		// clean up nicely
		DropTable drop_table = new DropTable(manager.getDatasource());
		try
		{
			drop_table.table("tablename");
			manager.executeUpdate(drop_table);
			
			drop_table.clear();
			drop_table.table("table2");
			manager.executeUpdate(drop_table);
		}
		catch (DatabaseException e)
		{
			System.out.println(e.toString());
		}
	}
	
	public boolean execute(Insert query)
	{
		boolean			success = false;
		DbQueryManager	manager = setupQuery(query.getDatasource());
		
		try
		{
			// try to execute insert statement
			if (manager.executeUpdate(query) > 0)
			{
				success = true;
			}
		}
		catch (DatabaseException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			cleanupQuery(manager);
		}
		
		return success;
	}
	
	public boolean execute(Insert query, DbPreparedStatementHandler handler)
	{
		boolean			success = false;
		DbQueryManager	manager = setupQuery(query.getDatasource());
		
		try
		{
			if (manager.executeUpdate(query, handler) > 0)
			{
				success = true;
			}
		}
		catch (DatabaseException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			cleanupQuery(manager);
		}
		
		return success;
	}
}
