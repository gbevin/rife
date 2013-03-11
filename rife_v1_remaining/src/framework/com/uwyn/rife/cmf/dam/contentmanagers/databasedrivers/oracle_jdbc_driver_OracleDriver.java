/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: oracle_jdbc_driver_OracleDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.Select;

public class oracle_jdbc_driver_OracleDriver extends generic
{
	public oracle_jdbc_driver_OracleDriver(Datasource datasource)
	{
		super(datasource);

		mGetLatestContentInfo = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentInfo())
			.join(RifeConfig.Cmf.getTableContentRepository())
			.field(RifeConfig.Cmf.getTableContentInfo()+".*")
			.where(RifeConfig.Cmf.getTableContentInfo()+".repositoryId = "+RifeConfig.Cmf.getTableContentRepository()+".repositoryId")
			.whereParameterAnd(RifeConfig.Cmf.getTableContentRepository()+".name", "repository", "=")
			.startWhereAnd()
				.whereParameter("path", "=")
				.startWhereOr()
					.whereParameter("path", "pathpart", "=")
					.whereParameterAnd(RifeConfig.Cmf.getTableContentInfo()+".name", "namepart", "=")
				.end()
			.end()
			.orderBy("version", Select.DESC);
	}
}
