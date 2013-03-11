/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mckoi_JDBCDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;

public class com_mckoi_JDBCDriver extends generic
{
	public com_mckoi_JDBCDriver(Datasource datasource)
	{
		super(datasource);

		mRemoveRememberMomentIndex = "DROP INDEX "+RifeConfig.Authentication.getTableRemember()+"_moment_IDX ON "+RifeConfig.Authentication.getTableRemember();
	}
}
