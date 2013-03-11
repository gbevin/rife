/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_firebirdsql_jdbc_FBDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.databasedrivers;

import com.uwyn.rife.cmf.ContentRepository;
import com.uwyn.rife.cmf.dam.ContentStore;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.InstallContentErrorException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.RemoveContentErrorException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;

public class org_firebirdsql_jdbc_FBDriver extends generic
{
	public org_firebirdsql_jdbc_FBDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableContentRepository = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentRepository())
			.columnsExcluded(ContentRepository.class, new String[] {"name"})
			.column("repositoryId", int.class)
			.column("name", String.class, 100, "CHARACTER SET ISO8859_1")
			.primaryKey("PK_"+RifeConfig.Cmf.getTableContentRepository(), "repositoryId");

		mCreateTableContentInfo = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentInfo())
			.columnsExcluded(DatabaseContentInfo.class, new String[] {"path"})
			.column("repositoryId", int.class, CreateTable.NOTNULL)
			.column("path", String.class, 184, "CHARACTER SET ISO8859_1")
			.defaultFunction("created", "CURRENT_TIMESTAMP")
			.unique(("UQ_"+RifeConfig.Cmf.getTableContentInfo()).toUpperCase(), new String[] {"repositoryId", "path", "version"})
			.foreignKey("FK_"+RifeConfig.Cmf.getTableContentInfo()+"_REPOSITORYID", RifeConfig.Cmf.getTableContentRepository(), "repositoryId", "repositoryId");
	}
	
	public boolean install()
	throws ContentManagerException
	{
		int poolsize = getDatasource().getPoolsize();
		
		getDatasource().setPoolsize(0);
		try
		{
			try
			{
				executeUpdate(mCreateSequenceContentRepository);
				executeUpdate(mCreateSequenceContentInfo);
				executeUpdate(mCreateTableContentRepository);
				executeUpdate(mCreateTableContentInfo);
				executeUpdate(mCreateTableContentAttribute);
				executeUpdate(mCreateTableContentProperty);
				
				for (ContentStore store : mStores)
				{
					store.install();
				}

				executeUpdate(mCreateContentInfoPathIndex);
			}
			catch (DatabaseException e)
			{
				throw new InstallContentErrorException(e);
			}
		}
		finally
		{
			getDatasource().setPoolsize(poolsize);
		}
		
		createRepository(ContentRepository.DEFAULT);
		
		return true;
	}
	
	public boolean remove()
	throws ContentManagerException
	{
		int poolsize = getDatasource().getPoolsize();
		
		getDatasource().setPoolsize(0);
		try
		{
			try
			{
				executeUpdate(mDropContentInfoPathIndex);
			}
			catch (DatabaseException e)
			{
				throw new RemoveContentErrorException(e);
			}
			return _remove(mDropSequenceContentRepository, mDropSequenceContentInfo,
						   mDropTableContentRepository, mDropTableContentInfo, mDropTableContentAttribute, mDropTableContentProperties);
		}
		finally
		{
			getDatasource().setPoolsize(poolsize);
		}
	}

	protected String getValueColumnName()
	{
		return "attrvalue";
	}
}
