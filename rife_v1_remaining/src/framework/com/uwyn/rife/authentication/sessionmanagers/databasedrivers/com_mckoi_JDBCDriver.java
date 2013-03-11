/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mckoi_JDBCDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionmanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;

public class com_mckoi_JDBCDriver extends generic
{
	public com_mckoi_JDBCDriver(Datasource datasource)
	{
		super(datasource);

		mRemoveAuthenticationSessStartIndex = "DROP INDEX "+RifeConfig.Authentication.getTableAuthentication()+"_IDX ON "+RifeConfig.Authentication.getTableAuthentication();
	}
}
