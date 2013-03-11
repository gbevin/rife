/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_apache_derby_jdbc_EmbeddedDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.textstoredrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.CreateTable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class org_apache_derby_jdbc_EmbeddedDriver extends generic
{
	public org_apache_derby_jdbc_EmbeddedDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableContent = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreText())
			.column("contentId",  int.class, CreateTable.NOTNULL)
			.column(getContentSizeColumnName(), int.class, CreateTable.NOTNULL)
			.column("content", Clob.class)
			.primaryKey("PK_CONTENTTEXT", "contentId")
			.foreignKey("FK_CONTENTTEXT", RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");
	}
	
	protected void outputContentColumn(ResultSet resultSet, OutputStream os)
	throws SQLException
	{
		Clob clob = resultSet.getClob("content");
		Reader 	text_reader = clob.getCharacterStream();
		char[]	buffer = new char[512];
		int size = 0;
		try
		{
			while ((size = text_reader.read(buffer)) != -1)
			{
				os.write(new String(buffer).getBytes("UTF-8"), 0, size);
			}
			
			os.flush();
		}
		catch (IOException e)
		{
			// don't do anything, the client has probably disconnected
		}
	}
}
