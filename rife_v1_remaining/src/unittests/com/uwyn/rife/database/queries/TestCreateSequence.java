/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCreateSequence.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbConnection;
import com.uwyn.rife.database.exceptions.DatabaseException;

public abstract class TestCreateSequence extends TestQuery
{
	public TestCreateSequence(String name)
	{
		super(name);
	}
	
	public void execute(Datasource datasource, CreateSequence query)
	{
		DbConnection connection = null;
		DropSequence drop_sequence = new DropSequence(datasource);
		
		try
		{
			connection = datasource.getConnection();
		}
		catch (DatabaseException e)
		{
			throw new RuntimeException(e);
		}

		try
		{
			connection.beginTransaction();

			// try to execute the table creation
			connection.createStatement().executeUpdate(query);

			// it wass succesful, remove the table again
			drop_sequence.name(query.getName());
			connection.createStatement().executeUpdate(drop_sequence);
		}
		catch (DatabaseException e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			// clean up foreign key table
			try
			{
				connection.rollback();
			}
			catch (DatabaseException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
