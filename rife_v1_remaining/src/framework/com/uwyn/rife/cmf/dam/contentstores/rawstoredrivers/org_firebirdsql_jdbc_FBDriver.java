/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_firebirdsql_jdbc_FBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.rawstoredrivers;

import com.uwyn.rife.database.*;
import com.uwyn.rife.database.queries.*;
import java.sql.*;

public class org_firebirdsql_jdbc_FBDriver extends generic
{
	public org_firebirdsql_jdbc_FBDriver(Datasource datasource)
	{
		super(datasource);
	}
	
	protected String getContentSizeColumnName()
	{
		return "contentsize";
	}
	
	protected DbPreparedStatement getStreamPreparedStatement(Query query, DbConnection connection)
	{
		DbPreparedStatement statement = connection.getPreparedStatement(query);
		statement.setFetchDirection(ResultSet.FETCH_FORWARD);
		statement.setFetchSize(1);
		return statement;
	}
}
