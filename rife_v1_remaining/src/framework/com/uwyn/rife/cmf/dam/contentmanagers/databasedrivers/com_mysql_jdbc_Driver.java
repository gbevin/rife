/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mysql_jdbc_Driver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.databasedrivers;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentRepository;
import com.uwyn.rife.cmf.dam.ContentStore;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.InstallContentErrorException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.RemoveContentErrorException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.UnknownContentRepositoryException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.UnsupportedMimeTypeException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.*;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Query;
import com.uwyn.rife.datastructures.Pair;
import com.uwyn.rife.tools.exceptions.InnerClassException;

import java.sql.Statement;
import java.sql.Types;
import java.util.Map;

public class com_mysql_jdbc_Driver extends generic
{
	private static final Object	sVersionMonitor = new Object();

	public com_mysql_jdbc_Driver(Datasource datasource)
	{
		super(datasource);

		mCreateTableContentRepository = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentRepository())
			.columns(ContentRepository.class)
			.column("repositoryId", int.class)
			.customAttribute("repositoryId", "AUTO_INCREMENT")
			.primaryKey("PK_"+RifeConfig.Cmf.getTableContentRepository(), "repositoryId");

		mCreateTableContentInfo = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentInfo())
			.columns(DatabaseContentInfo.class)
			.column("repositoryId", int.class, CreateTable.NOTNULL)
			.customAttribute("contentId", "AUTO_INCREMENT")
			.unique(("UQ_"+RifeConfig.Cmf.getTableContentInfo()).toUpperCase(), new String[] {"repositoryId", "path", "version"})
			.foreignKey("FK_"+RifeConfig.Cmf.getTableContentInfo()+"_REPOSITORYID", RifeConfig.Cmf.getTableContentRepository(), "repositoryId", "repositoryId");

		mDropContentInfoPathIndex = "DROP INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_path ON "+RifeConfig.Cmf.getTableContentInfo();

		mDropContentInfoPathNameIndex = "DROP INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_pathname ON "+RifeConfig.Cmf.getTableContentInfo();
		
		mStoreContentRepository = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentRepository())
			.fieldsParameters(ContentRepository.class);
			
		mStoreContentInfo = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentInfo())
			.fieldsParametersExcluded(DatabaseContentInfo.class, new String[] {"contentId"})
			.fieldParameter("repositoryId")
			.fieldParameter("version")
			.fieldParameter("created");
	}
	
	public boolean install()
	throws ContentManagerException
	{
		try
		{
			executeUpdate(mCreateTableContentRepository);
			executeUpdate(mCreateTableContentInfo);
			executeUpdate(mCreateTableContentAttribute);
			executeUpdate(mCreateTableContentProperty);

			createRepository(ContentRepository.DEFAULT);

			for (ContentStore store : mStores)
			{
				store.install();
			}

			executeUpdate(mCreateContentInfoPathIndex);
			executeUpdate(mCreateContentInfoPathNameIndex);
		}
		catch (DatabaseException e)
		{
			throw new InstallContentErrorException(e);
		}
	
	   return true;
	}
	
	public boolean remove()
	throws ContentManagerException
	{
		try
		{
			executeUpdate(mDropContentInfoPathNameIndex);
			executeUpdate(mDropContentInfoPathIndex);

			for (ContentStore store : mStores)
			{
				store.remove();
			}

			executeUpdate(mDropTableContentProperties);
			executeUpdate(mDropTableContentAttribute);
			executeUpdate(mDropTableContentInfo);
			executeUpdate(mDropTableContentRepository);
		}
		catch (DatabaseException e)
		{
			throw new RemoveContentErrorException(e);
		}
	
	   return true;
	}
	
	public boolean createRepository(final String name)
	throws ContentManagerException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty");
		
		Boolean result = null;

		try
		{
			result = inTransaction(new DbTransactionUser() {
					public Boolean useTransaction() throws InnerClassException
					{
						// store the content
						return executeUpdate(mStoreContentRepository, new DbPreparedStatementHandler() {
								public void setParameters(DbPreparedStatement statement)
								{
									statement
										.setString("name", name);
								}
							}) > 0;
					}
				});
		}
		catch (InnerClassException e)
		{
			throw (ContentManagerException)e.getCause();
		}
		
		return result != null && result.booleanValue();
	}

	public boolean storeContent(String location, final Content content, final ContentTransformer transformer)
	throws ContentManagerException
	{
		if (null == content)			throw new IllegalArgumentException("content can't be null");
		
		final Pair<String, String> split_location = splitLocation(location);
		
		final ContentStore store = mMimeMapping.get(content.getMimeType());
		if (null == store)
		{
			throw new UnsupportedMimeTypeException(content.getMimeType());
		}
		
		// ensure that all version number increases are handled in a serial fashion
		// relying on database locks is error prone and does offer any advantages
		synchronized (sVersionMonitor)
		{
			// get repository id
			final int repository_id = executeGetFirstInt(mGetContentRepositoryId, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString("repository", split_location.getFirst());
					}
				});

			// verify the existance of the repository
			if (-1 == repository_id)
			{
				throw new UnknownContentRepositoryException(split_location.getFirst());
			}

			// get version
			final int version = executeGetFirstInt(mGetVersion, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("repositoryId", repository_id)
							.setString("path", split_location.getSecond());
					}
				});
			
			// store the content
			final int[] ids_array = new int[1];
			if (executeUpdate(mStoreContentInfo, new DbPreparedStatementHandler() {
					public DbPreparedStatement getPreparedStatement(Query query, DbConnection connection)
					{
						return connection.getPreparedStatement(query, Statement.RETURN_GENERATED_KEYS);
					}

					public int performUpdate(DbPreparedStatement statement)
					{
						statement
							.setString("path", split_location.getSecond())
							.setString("mimeType", content.getMimeType().toString())
							.setBoolean("fragment", content.isFragment())
							.setDate("created", new java.sql.Date(System.currentTimeMillis()))
							.setInt("repositoryId", repository_id)
							.setInt("version", version);
						if (content.hasName())
						{
							statement
								.setString("name", content.getName());
						}
						else
						{
							statement
								.setNull("name", Types.VARCHAR);
						}

						int query_result = statement.executeUpdate();
						ids_array[0] = statement.getFirstGeneratedIntKey();
						return query_result;
					}
				}) > 0)
			{
				// store the attributes if there are some
				if (content.hasAttributes())
				{
					for (Map.Entry<String, String> attribute : content.getAttributes().entrySet())
					{
						final String name = attribute.getKey();
						final String value = attribute.getValue();

						executeUpdate(mStoreContentAttribute, new DbPreparedStatementHandler() {
								public void setParameters(DbPreparedStatement statement)
								{
									statement
										.setInt("contentId", ids_array[0])
										.setString("name", name)
										.setString(getValueColumnName(), value);
								}
							});
					}
				}

				// put the actual content data in the content store
				if (!store.storeContentData(ids_array[0], content, transformer))
				{
					return false;
				}

				// store the content data properties if there are some
				if (content.hasProperties())
				{
					for (Map.Entry<String, String> property : content.getProperties().entrySet())
					{
						final String name = property.getKey();
						final String value = property.getValue();

						executeUpdate(mStoreContentProperty, new DbPreparedStatementHandler() {
								public void setParameters(DbPreparedStatement statement)
								{
									statement
										.setInt("contentId", ids_array[0])
										.setString("name", name)
										.setString(getValueColumnName(), value);
								}
							});
					}
				}

				return true;
			}

			return false;
		}
	}
}
