/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDropSequence.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbConnection;
import com.uwyn.rife.database.exceptions.DatabaseException;

public abstract class TestDropSequence extends TestQuery
{
	public TestDropSequence(String name)
	{
		super(name);
	}
	
	public void execute(Datasource datasource, DropSequence query)
	{
		try
		{
			DbConnection connection = datasource.getConnection();
			connection.beginTransaction();
			CreateSequence create_sequence = new CreateSequence(datasource);
			create_sequence.name(query.getName());
			connection.createStatement().executeUpdate(create_sequence);
			
			connection.createStatement().executeUpdate(query);
			connection.rollback();
		}
		catch (DatabaseException e)
		{
			throw new RuntimeException(e);
		}
	}
}
