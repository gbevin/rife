/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_apache_derby_jdbc_EmbeddedDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.imagestoredrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.CreateTable;
import java.sql.Blob;

public class org_apache_derby_jdbc_EmbeddedDriver extends generic
{
	public org_apache_derby_jdbc_EmbeddedDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableContent = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreImage())
			.column("contentId", int.class, CreateTable.NOTNULL)
			.column(getContentSizeColumnName(), int.class, CreateTable.NOTNULL)
			.column("content", Blob.class)
			.primaryKey("PK_CONTENTIMAGE", "contentId")
			.foreignKey("FK_CONTENTIMAGE", RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");
	}
}
