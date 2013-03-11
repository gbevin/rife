/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: oracle_jdbc_driver_OracleDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.imagestoredrivers;

import com.uwyn.rife.database.*;
import java.io.*;

import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.StoreContentDataErrorException;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.UseContentDataErrorException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.sql.SQLException;
import java.sql.Types;
import oracle.sql.BLOB;

public class oracle_jdbc_driver_OracleDriver extends generic
{
	private Insert	mStoreContentDataEmptyBlob = null;
	
	public oracle_jdbc_driver_OracleDriver(Datasource datasource)
	{
		super(datasource);

		mStoreContentDataEmptyBlob = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentStoreImage())
			.fieldParameter("contentId")
			.fieldParameter(getContentSizeColumnName())
			.fieldCustom("content", "empty_blob()");
	}
	
	protected String getContentSizeColumnName()
	{
		return "contentsize";
	}
	
	public <ResultType> ResultType useContentData(final int id, ContentDataUser user)
	throws ContentManagerException
	{
		if (id < 0)			throw new IllegalArgumentException("id must be positive");
		if (null == user)	throw new IllegalArgumentException("user can't be null");

		try
		{
			return (ResultType)user.useContentData(executeQuery(mRetrieveContent, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("contentId", id);
					}
					
					public Object concludeResults(DbResultSet resultset)
					throws SQLException
					{
						if (!resultset.next())
						{
							return null;
						}
						
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						BLOB blob = (BLOB)resultset.getBlob("content");
						if (null == blob)
						{
							return null;
						}

						InputStream is = blob.getBinaryStream();
						try
						{
							byte[]	buffer = new byte[blob.getBufferSize()];
							BufferedInputStream buffered_image_is = new BufferedInputStream(is, buffer.length);
							int size = 0;
							while ((size = buffered_image_is.read(buffer)) != -1)
							{
								os.write(buffer, 0, size);
							}
							
							try
							{
								os.flush();
							}
							catch (IOException e)
							{
								// don't do anything, the client has probably disconnected
							}
						}
						catch (IOException e)
						{
							throw new UseContentDataErrorException(id, e);
						}
						
						return os.toByteArray();
					}
				}));
		}
		catch (DatabaseException e)
		{
			throw new UseContentDataErrorException(id, e);
		}
	}
	
	protected boolean storeTypedData(Insert storeContent, final int id, final byte[] data)
	throws ContentManagerException
	{
		try
		{
			if (null == data)
			{
				return executeUpdate(storeContent, new DbPreparedStatementHandler() {
						public void setParameters(DbPreparedStatement statement)
						{
							statement
								.setInt("contentId", id)
								.setNull("content", Types.BLOB)
								.setInt(getContentSizeColumnName(), 0);
						}
					}) != -1;
			}
			else
			{
				Integer result = inTransaction(new DbTransactionUser() {
						public Object useTransaction()
						throws InnerClassException
						{
							int result = executeUpdate(mStoreContentDataEmptyBlob, new DbPreparedStatementHandler() {
									public void setParameters(DbPreparedStatement statement)
									{
										statement
											.setInt("contentId", id)
											.setInt(getContentSizeColumnName(), data.length);
									}
								});
						
							executeQuery(new Select(getDatasource())
											.from(mStoreContentDataEmptyBlob.getInto())
											.field("content")
											.where("contentId = "+id+" FOR UPDATE"), new DbResultSetHandler() {
									public Object concludeResults(DbResultSet resultset) throws SQLException
									{
										if (!resultset.next())
										{
											return null;
										}
										
										BLOB blob = (BLOB)resultset.getBlob(1);
										byte[] buffer = new byte[blob.getBufferSize()];
										InputStream is = new ByteArrayInputStream(data);
										OutputStream os = blob.getBinaryOutputStream();
										try
										{
											try
											{
												int size = 0;
												while ((size = is.read(buffer)) != -1)
												{
													os.write(buffer, 0, size);
												}
											}
											finally
											{
												os.close();
											}
										}
										catch (IOException e)
										{
											throw new DatabaseException(e);
										}
										
										return null;
									}
								});
							
							return result;
						}
					});
				
				return null != result && result != -1;
			}
		}
		catch (DatabaseException e)
		{
			throw new StoreContentDataErrorException(id, e);
		}
	}
}
