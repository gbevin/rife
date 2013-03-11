/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSelect.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.BeanImpl;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.exceptions.DatabaseException;

public abstract class TestSelect extends TestQuery
{
	public TestSelect(String name)
	{
		super(name);
	}

	public DbQueryManager setupQuery(Datasource datasource)
	{
		DbQueryManager manager = new DbQueryManager(datasource);
		
		CreateTable createtable = new CreateTable(datasource);
		createtable.table("tablename")
			.columns(BeanImpl.class)
			.precision("propertyBigDecimal", 18, 9)
			.precision("propertyChar", 1)
			.precision("propertyDouble", 12, 3)
			.precision("propertyDoubleObject", 12, 3)
			.precision("propertyFloat", 13, 2)
			.precision("propertyFloatObject", 13, 2)
			.precision("propertyString", 255)
			.precision("propertyStringbuffer", 100);
		
		try
		{
			// prepare table and data
			manager.executeUpdate(createtable);
			
			createtable.table("table2");
			manager.executeUpdate(createtable);
			
			createtable.table("table3");
			manager.executeUpdate(createtable);
			
			Insert insert = new Insert(datasource);
			insert.into("tablename")
				.fields(BeanImpl.getPopulatedBean());
			manager.executeUpdate(insert);
			
			insert.into("table2");
			manager.executeUpdate(insert);
			
			insert.into("table3");
			manager.executeUpdate(insert);
			
			insert.clear();
			insert.into("tablename")
				.fields(BeanImpl.getNullBean());
			manager.executeUpdate(insert);
			
			BeanImpl impl = BeanImpl.getPopulatedBean();
			insert.clear();
			impl.setPropertyInt(3);
			insert.into("tablename")
				.fields(impl);
			manager.executeUpdate(insert);
			insert.clear();
			impl.setPropertyInt(4);
			insert.into("tablename")
				.fields(impl);
			manager.executeUpdate(insert);
			insert.clear();
			impl.setPropertyInt(5);
			insert.into("tablename")
				.fields(impl);
			manager.executeUpdate(insert);

			insert.into("table2");
			manager.executeUpdate(insert);
			
			insert.into("table3");
			manager.executeUpdate(insert);
		}
		catch (DatabaseException e)
		{
			cleanupQuery(manager);
			throw new RuntimeException(e);
		}
		
		return manager;
	}

	public void cleanupQuery(DbQueryManager manager)
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
			
			drop_table.clear();
			drop_table.table("table3");
			manager.executeUpdate(drop_table);
		}
		catch (DatabaseException e)
		{
			System.out.println(e.toString());
		}
	}
	
	public boolean execute(Select query)
	{
		boolean			success = false;
		DbQueryManager	manager = setupQuery(query.getDatasource());
		
		try
		{
			success = manager.executeHasResultRows(query);
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
	
	public boolean execute(Select query, DbPreparedStatementHandler handler)
	{
		boolean			success = false;
		DbQueryManager	manager = setupQuery(query.getDatasource());
		
		try
		{
			success = manager.executeHasResultRows(query, handler);
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

