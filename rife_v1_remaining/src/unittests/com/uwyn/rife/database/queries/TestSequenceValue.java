/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSequenceValue.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbConnection;
import com.uwyn.rife.database.DbStatement;
import com.uwyn.rife.database.exceptions.DatabaseException;

public abstract class TestSequenceValue extends TestQuery
{
	public TestSequenceValue(String name)
	{
		super(name);
	}
	
	public int execute(Datasource datasource, SequenceValue query)
	{
		int result = -1;
		
		try
		{
			DbConnection connection = datasource.getConnection();
			
			CreateSequence create_sequence = new CreateSequence(datasource);
			create_sequence.name(query.getName());
			connection.createStatement().executeUpdate(create_sequence);

			DbStatement statement = connection.createStatement();
			statement.executeQuery(new SequenceValue(datasource).name(query.getName()).next());
			statement = connection.createStatement();
			statement.executeQuery(query);
			if (statement.getResultSet().hasResultRows())
			{
				result = statement.getResultSet().getFirstInt();
			}
			
			connection.createStatement().executeUpdate(new DropSequence(datasource).name(query.getName()));
		}
		catch (DatabaseException e)
		{
			throw new RuntimeException(e);
		}
		
		return result;
	}
}
