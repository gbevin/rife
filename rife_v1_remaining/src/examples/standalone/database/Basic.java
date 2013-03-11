/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Basic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package database;

import com.uwyn.rife.database.*;

import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Basic extends DbQueryManager
{
	public Basic(Datasource datasource)
	{
		super(datasource);
	}
	
	public static void main(String[] args)
	{
		Rep.initialize("rep/unittests_participants.xml");
		
		Basic example = new Basic(Datasources.getRepInstance().getDatasource("unittestspgsql"));
		try
		{
			example.doIt();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}
	
	public void doIt()
	throws DatabaseException
	{
		CreateTable	create = new CreateTable(getDatasource());
		create
			.table("example")
			.column("firstname", String.class, 50)
			.column("lastname", String.class, 50);
		executeUpdate(create);
		
		Insert	insert = new Insert(getDatasource());
		insert
			.into(create.getTable())
			.fieldParameter("firstname")
			.fieldParameter("lastname");
		DbPreparedStatement	insert_stmt = getConnection().getPreparedStatement(insert);
		insert_stmt.setString("firstname", "John");
		insert_stmt.setString("lastname", "Doe");
		insert_stmt.executeUpdate();
		insert_stmt.clearParameters();
		insert_stmt.setString("firstname", "Jane");
		insert_stmt.setString("lastname", "TheLane");
		insert_stmt.executeUpdate();
		
		Select	select = new Select(getDatasource());
		select
			.from(create.getTable())
			.orderBy("firstname");
		DbStatement select_stmt = executeQuery(select);
		Processor	processor = new Processor();
		while (fetch(select_stmt.getResultSet(), processor) &&
			   processor.wasSuccessful())
		{
			System.out.println(processor.getFirstname()+" "+processor.getLastname());
		}

		DropTable drop = new DropTable(getDatasource());
		drop.table(create.getTable());
		executeUpdate(drop);
	}
	
	class Processor extends DbRowProcessor
	{
		private String mFirstname = null;
		private String mLastname = null;
		
		public String getFirstname()
		{
			return mFirstname;
		}
		
		public String getLastname()
		{
			return mLastname;
		}
		
		public boolean processRow(ResultSet resultSet)
		throws SQLException
		{
			mFirstname = resultSet.getString("firstname");
			mLastname = resultSet.getString("lastname");
			
			return true;
		}
		
	}
}
