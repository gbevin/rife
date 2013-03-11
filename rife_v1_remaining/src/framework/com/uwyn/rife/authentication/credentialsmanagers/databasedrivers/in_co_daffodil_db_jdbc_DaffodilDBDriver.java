/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: in_co_daffodil_db_jdbc_DaffodilDBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers.databasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.Select;

public class in_co_daffodil_db_jdbc_DaffodilDBDriver extends generic
{
	public in_co_daffodil_db_jdbc_DaffodilDBDriver(Datasource datasource)
	{
		super(datasource);
		
		mGetFreeUserId = new Select(getDatasource())
			.field("IFNULL(MAX(userId)+1, 0) as freeUserId")
			.from(mCreateTableUser.getTable());
	}
}
