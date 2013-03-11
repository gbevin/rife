/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDbQueryManagerImpl.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

public abstract class TestDbQueryManagerImpl extends DbQueryManager
{
	private String	mSetting = null;
	
    protected TestDbQueryManagerImpl(Datasource datasource)
    {
        super(datasource);
    }
	
	public void setSetting(String setting)
	{
		mSetting = setting;
	}
	
	public String getSetting()
	{
		return mSetting;
	}

	public abstract boolean install()
	throws DatabaseException;
	
	public abstract void store(int id, String value)
	throws DatabaseException;
	
	public abstract int count()
	throws DatabaseException;
	
	public abstract boolean remove()
	throws DatabaseException;
	
	protected boolean _install(final CreateTable createStructure)
	throws DatabaseException
	{
		assert createStructure != null;
		
		executeUpdate(createStructure);
		
		return true;
	}
	
	protected void _store(Insert store, final int id, final String value)
	throws DatabaseException
	{
		assert store != null;
		
		if (id <= 0)				throw new IllegalArgumentException("id must be positive");
		if (null == value)			throw new IllegalArgumentException("value can't be null");
		if (0 == value.length())	throw new IllegalArgumentException("value can't be empty");
		
		if (0 == executeUpdate(store, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt(1, id)
							.setString(2, value);
					}
				}))
		{
			throw new DatabaseException("Unable to store the data '"+id+"', '"+value+"'.");
		}
	}
	
	protected int _count(Select count)
	throws DatabaseException
	{
		assert count != null;
		
		return executeGetFirstInt(count);
	}
	
	protected boolean _remove(final DropTable removeStructure)
	throws DatabaseException
	{
		assert removeStructure != null;
		
		executeUpdate(removeStructure);
		
		return true;
	}
}

