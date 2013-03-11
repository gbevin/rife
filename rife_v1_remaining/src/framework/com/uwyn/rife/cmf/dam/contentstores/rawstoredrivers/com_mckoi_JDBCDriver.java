/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mckoi_JDBCDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.rawstoredrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbConnection;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.queries.Query;
import java.sql.ResultSet;

public class com_mckoi_JDBCDriver extends generic
{
	public com_mckoi_JDBCDriver(Datasource datasource)
	{
		super(datasource);
	}
	
	protected DbPreparedStatement getStreamPreparedStatement(Query query, DbConnection connection)
	{
		DbPreparedStatement statement = connection.getPreparedStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
		statement.setFetchDirection(ResultSet.FETCH_FORWARD);
		statement.setFetchSize(1);
		return statement;
	}
}
