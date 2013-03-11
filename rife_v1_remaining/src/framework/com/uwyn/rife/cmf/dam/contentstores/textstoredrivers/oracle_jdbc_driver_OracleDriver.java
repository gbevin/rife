/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: oracle_jdbc_driver_OracleDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.textstoredrivers;

import com.uwyn.rife.database.*;

import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.StoreContentDataErrorException;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.UseContentDataErrorException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;
import oracle.sql.CLOB;

public class oracle_jdbc_driver_OracleDriver extends generic
{
	private Insert	mStoreContentEmptyClob = null;
	
	public oracle_jdbc_driver_OracleDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableContent = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreText())
			.column("contentId",  int.class, CreateTable.NOTNULL)
			.column(getContentSizeColumnName(), int.class, CreateTable.NOTNULL)
			.column("content",  Clob.class)
			.primaryKey(("PK_"+RifeConfig.Cmf.getTableContentStoreText()).toUpperCase(), "contentId")
			.foreignKey(("FK_"+RifeConfig.Cmf.getTableContentStoreText()).toUpperCase(), RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");

		mStoreContentEmptyClob = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentStoreText())
			.fieldParameter("contentId")
			.fieldParameter(getContentSizeColumnName())
			.fieldCustom("content", "empty_clob()");
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
						
						CharArrayWriter writer = new CharArrayWriter();
						CLOB clob = (CLOB)resultset.getClob("content");
						if (null == clob)
						{
							return null;
						}
						
						Reader reader = clob.getCharacterStream();
						try
						{
							try
							{
								char[] buffer = new char[clob.getBufferSize()];
								int size = 0;
								while ((size = reader.read(buffer)) != -1)
								{
									writer.write(buffer, 0, size);
								}
							
								writer.flush();
							}
							finally
							{
								reader.close();
							}
						}
						catch (IOException e)
						{
							throw new UseContentDataErrorException(id, e);
						}
							
						return writer.toString();
					}
				}));
		}
		catch (DatabaseException e)
		{
			throw new UseContentDataErrorException(id, e);
		}
	}
	
	protected boolean storeContent(Insert storeContent, final int id, final String data)
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
								.setNull("content", Types.CLOB)
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
							int result = executeUpdate(mStoreContentEmptyClob, new DbPreparedStatementHandler() {
									public void setParameters(DbPreparedStatement statement)
									{
										statement
											.setInt("contentId", id)
											.setInt(getContentSizeColumnName(), data.length());
									}
								});
						
							executeQuery(new Select(getDatasource())
											.from(mStoreContentEmptyClob.getInto())
											.field("content")
											.where("contentId = "+id+" FOR UPDATE"), new DbResultSetHandler() {
									public Object concludeResults(DbResultSet resultset) throws SQLException
									{
										if (!resultset.next())
										{
											return null;
										}
										
										CLOB clob = (CLOB)resultset.getClob(1);
										char[] buffer = new char[clob.getBufferSize()];
										StringReader reader = new StringReader(data);
										Writer writer = clob.getCharacterOutputStream();
										try
										{
											try
											{
												int size = 0;
												while ((size = reader.read(buffer)) != -1)
												{
													writer.write(buffer, 0, size);
												}
											}
											finally
											{
												writer.close();
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
