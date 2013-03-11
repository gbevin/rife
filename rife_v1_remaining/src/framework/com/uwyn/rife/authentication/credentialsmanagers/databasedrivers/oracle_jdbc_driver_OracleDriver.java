/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: oracle_jdbc_driver_OracleDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.databasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.Select;

public class oracle_jdbc_driver_OracleDriver extends generic
{
	public oracle_jdbc_driver_OracleDriver(Datasource datasource)
	{
		super(datasource);
		
		mGetFreeUserId = new Select(getDatasource())
			.field("NVL(MAX(userId)+1, 0) as freeUserId")
			.from(mCreateTableUser.getTable());
	}
}
