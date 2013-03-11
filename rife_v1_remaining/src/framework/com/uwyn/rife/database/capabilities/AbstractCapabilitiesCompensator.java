/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractCapabilitiesCompensator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.capabilities;

import com.uwyn.rife.database.DbConnection;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbResultSet;
import com.uwyn.rife.database.DbResultSetHandler;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Query;

public class AbstractCapabilitiesCompensator implements CapabilitiesCompensator
{
	public DbPreparedStatement getCapablePreparedStatement(Query query, DbResultSetHandler handler, DbConnection connection)
	throws DatabaseException
	{
		query.setExcludeUnsupportedCapabilities(true);
		
		if (null == handler)
		{
			return connection.getPreparedStatement(query);
		}
		
		return handler.getPreparedStatement(query, connection);
	}
	
	public DbResultSet getCapableResultSet(DbPreparedStatement statement)
	throws DatabaseException
	{
		return statement.getResultSet();
	}
}

