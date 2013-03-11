/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: in_co_daffodil_db_jdbc_DaffodilDBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.databasedrivers;

import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

public class in_co_daffodil_db_jdbc_DaffodilDBDriver extends generic
{
	public in_co_daffodil_db_jdbc_DaffodilDBDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableContentInfo = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentInfo())
			.columnsExcluded(DatabaseContentInfo.class, new String[] {"path"})
			.column("\"path\"", String.class, 255, CreateTable.NOTNULL)
			.column("repositoryId", int.class, CreateTable.NOTNULL)
			.defaultFunction("created", "CURRENT_TIMESTAMP")
			.check("\"path\" != ''")
			.unique(("UQ_"+RifeConfig.Cmf.getTableContentInfo()).toUpperCase(), new String[] {"repositoryId", "\"path\"", "version"})
			.foreignKey("FK_"+RifeConfig.Cmf.getTableContentInfo()+"_REPOSITORYID", RifeConfig.Cmf.getTableContentRepository(), "repositoryId", "repositoryId");
	
		mCreateContentInfoPathIndex = "CREATE INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_path ON "+RifeConfig.Cmf.getTableContentInfo()+" (\"path\")";
		mCreateContentInfoPathNameIndex = "CREATE INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_pathname ON "+RifeConfig.Cmf.getTableContentInfo()+" (\"path\", name)";

		mDropContentInfoPathIndex = "DROP INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_path OF "+RifeConfig.Cmf.getTableContentInfo();
		mDropContentInfoPathNameIndex = "DROP INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_pathname OF "+RifeConfig.Cmf.getTableContentInfo();

		mGetVersion = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentInfo())
			.field("IFNULL(MAX(version)+1, 0)")
			.whereParameter("repositoryId", "=")
			.whereParameterAnd("\"path\"", "path", "=");
			
		mGetContentInfo = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentInfo())
			.join(RifeConfig.Cmf.getTableContentRepository())
			.field(RifeConfig.Cmf.getTableContentInfo()+".*")
			.where(RifeConfig.Cmf.getTableContentInfo()+".repositoryId = "+RifeConfig.Cmf.getTableContentRepository()+".repositoryId")
			.whereParameter("\"path\"", "path", "=")
			.whereParameterAnd(RifeConfig.Cmf.getTableContentRepository()+".name", "repository", "=")
			.orderBy("version", Select.DESC);
			
		mStoreContentInfo = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentInfo())
			.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"path"})
			.fieldParameter("\"path\"", "path")
			.fieldParameter("repositoryId")
			.field("version", mGetVersion);

		mGetLatestContentInfo = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentInfo())
			.join(RifeConfig.Cmf.getTableContentRepository())
			.field(RifeConfig.Cmf.getTableContentInfo()+".*")
			.where(RifeConfig.Cmf.getTableContentInfo()+".repositoryId = "+RifeConfig.Cmf.getTableContentRepository()+".repositoryId")
			.whereParameterAnd(RifeConfig.Cmf.getTableContentRepository()+".name", "repository", "=")
			.startWhereAnd()
				.whereParameter("\"path\"", "path", "=")
				.startWhereOr()
					.whereParameter("\"path\"", "pathpart", "=")
					.whereParameterAnd(RifeConfig.Cmf.getTableContentInfo()+".name", "namepart", "=")
				.end()
			.end()
			.orderBy("version", Select.DESC)
			.limit(1);
	}
}
