/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: in_co_daffodil_db_jdbc_DaffodilDBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.textstoredrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.CreateTable;
import java.sql.Clob;

public class in_co_daffodil_db_jdbc_DaffodilDBDriver extends generic
{
	public in_co_daffodil_db_jdbc_DaffodilDBDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableContent = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreText())
			.column("contentId",  int.class, CreateTable.NOTNULL)
			.column(getContentSizeColumnName(), int.class, CreateTable.NOTNULL)
			.column("content", Clob.class)
			.primaryKey(("PK_"+RifeConfig.Cmf.getTableContentStoreText()).toUpperCase(), "contentId")
			.foreignKey(("FK_"+RifeConfig.Cmf.getTableContentStoreText()).toUpperCase(), RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");
	}
	
	protected String getContentSizeColumnName()
	{
		return "contentsize";
	}
}
