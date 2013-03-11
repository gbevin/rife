/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_hsqldb_jdbcDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.databasedrivers;

import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

public class org_hsqldb_jdbcDriver extends generic
{
	public org_hsqldb_jdbcDriver(Datasource datasource)
	{
		super(datasource);

		mGetVersion = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentInfo())
			.field("MAX(version)+1")
			.whereParameter("repositoryId", "=")
			.whereParameterAnd("path", "=");

		mStoreContentInfo = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentInfo())
			.fieldsParameters(DatabaseContentInfo.class)
			.fieldParameter("repositoryId")
			.fieldSubselect(mGetVersion)
			.fieldCustom("version", "COALESCE(("+mGetVersion.getSql()+"), 0)");
	}
}
