/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_apache_derby_jdbc_EmbeddedDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.remembermanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;

public class org_apache_derby_jdbc_EmbeddedDriver extends generic
{
	public org_apache_derby_jdbc_EmbeddedDriver(Datasource datasource)
	{
		super(datasource);

		mCreateRememberMomentIndex = "CREATE INDEX "+RifeConfig.Authentication.getTableRemember()+"_IDX ON "+RifeConfig.Authentication.getTableRemember()+" (moment)";
		mRemoveRememberMomentIndex = "DROP INDEX "+RifeConfig.Authentication.getTableRemember()+"_IDX";
	}
}
