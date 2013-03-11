/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropTable.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.exceptions.DatabaseException;

public abstract class TestDropTable extends TestQuery
{
	public TestDropTable(String name)
	{
		super(name);
	}
	
	public void execute(DropTable query)
	{
		try
		{
			DbQueryManager manager = new DbQueryManager(query.getDatasource());
			CreateTable create_table = new CreateTable(query.getDatasource());
			create_table.column("firstcolumn", int.class);
			
			for (String table : query.getTables())
			{
				create_table.table(table);
				manager.executeUpdate(create_table);
			}
			manager.executeUpdate(query);
		}
		catch (DatabaseException e)
		{
			throw new RuntimeException(e);
		}
	}
}
