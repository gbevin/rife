/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: com_mckoi_JDBCDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.databasedrivers;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.dam.ContentStore;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.UnknownContentRepositoryException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.UnsupportedMimeTypeException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbTransactionUser;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.datastructures.Pair;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.sql.Connection;
import java.sql.Types;
import java.util.Map;

public class com_mckoi_JDBCDriver extends generic
{
	public com_mckoi_JDBCDriver(Datasource datasource)
	{
		super(datasource);

		mDropContentInfoPathIndex = "DROP INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_path ON "+RifeConfig.Cmf.getTableContentInfo();

		mDropContentInfoPathNameIndex = "DROP INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_pathname ON "+RifeConfig.Cmf.getTableContentInfo();

		mStoreContentInfo = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentInfo())
			.fieldsParameters(DatabaseContentInfo.class)
			.fieldParameter("repositoryId")
			.fieldParameter("version");

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
		
		Boolean result = null;

		try
		{
			result = inTransaction(new DbTransactionUser() {
					public int getTransactionIsolation()
					{
						return Connection.TRANSACTION_SERIALIZABLE;
					}
						
					public Boolean useTransaction()
					throws InnerClassException
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
							throwException(new UnknownContentRepositoryException(split_location.getFirst()));
						}
			
						// get new content id
						final int id = executeGetFirstInt(mGetNewContentId);
						
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
						if (executeUpdate(mStoreContentInfo, new DbPreparedStatementHandler() {
								public void setParameters(DbPreparedStatement statement)
								{
									statement
										.setInt("contentId", id)
										.setInt("repositoryId", repository_id)
										.setString("path", split_location.getSecond())
										.setString("mimeType", content.getMimeType().toString())
										.setBoolean("fragment", content.isFragment())
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
													.setInt("contentId", id)
													.setString("name", name)
													.setString(getValueColumnName(), value);
											}
										});
								}
							}

							// put the actual content data in the content store
							try
							{
								if (!store.storeContentData(id, content, transformer))
								{
									rollback();
								}
							}
							catch (ContentManagerException e)
							{
								throwException(e);
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
													.setInt("contentId", id)
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
				});
		}
		catch (InnerClassException e)
		{
			throw (ContentManagerException)e.getCause();
		}
		
		return result != null && result.booleanValue();
	}
}
