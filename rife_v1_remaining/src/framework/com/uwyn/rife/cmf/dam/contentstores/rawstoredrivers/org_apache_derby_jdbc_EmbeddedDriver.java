/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_apache_derby_jdbc_EmbeddedDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.rawstoredrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

public class org_apache_derby_jdbc_EmbeddedDriver extends generic
{
	public org_apache_derby_jdbc_EmbeddedDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableContentInfo = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreRawInfo())
			.column("contentId", int.class, CreateTable.NOTNULL)
			.column(getContentSizeColumnName(), int.class, CreateTable.NOTNULL)
			.primaryKey("PK_CONTENTRAW", "contentId")
			.foreignKey("FK_CONTENTRAW", RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");

		mCreateTableContentChunk = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreRawChunk())
			.column("contentId", int.class, CreateTable.NOTNULL)
			.column("ordinal", int.class, CreateTable.NOTNULL)
			.column("chunk", Blob.class)
			.primaryKey("PK_CONTENTCHUNK", new String[] {"contentId", "ordinal"})
			.foreignKey("FK_CONTENTCHUNK", RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");
	}
	
	protected int storeChunks(Insert storeContentChunk, final int id, InputStream data)
	throws IOException
	{
		return storeChunksNoStream(storeContentChunk, id, data);
	}
}
