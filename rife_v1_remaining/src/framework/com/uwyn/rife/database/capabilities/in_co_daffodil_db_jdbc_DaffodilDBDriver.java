/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: in_co_daffodil_db_jdbc_DaffodilDBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.capabilities;

import com.uwyn.rife.database.DbConnection;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbResultSet;
import com.uwyn.rife.database.DbResultSetHandler;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Query;

public class in_co_daffodil_db_jdbc_DaffodilDBDriver extends AbstractCapabilitiesCompensator
{
	private LimitOffsetCompensator	mLimitOffsetCompensator = new LimitOffsetCompensator();
	
	public DbPreparedStatement getCapablePreparedStatement(Query query, DbResultSetHandler handler, DbConnection connection)
	throws DatabaseException
	{
		query.setExcludeUnsupportedCapabilities(true);
		
		// either create a new prepared statement or get it from the handler
		DbPreparedStatement statement = null;
		if (null == handler)
		{
			statement = connection.getPreparedStatement(query);
		}
		else
		{
			statement = handler.getPreparedStatement(query, connection);
		}

		mLimitOffsetCompensator.handleCapablePreparedStatement(statement);
		
		return statement;
	}
	
	public DbResultSet getCapableResultSet(DbPreparedStatement statement)
	throws DatabaseException
	{
		mLimitOffsetCompensator.handleCapableResultSet(statement);

		return statement.getResultSet();
	}
}

