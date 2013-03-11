/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: oracle_jdbc_driver_OracleDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.rawstoredrivers;

import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.UseContentDataErrorException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbResultSet;
import com.uwyn.rife.database.DbResultSetHandler;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import oracle.sql.BLOB;

public class oracle_jdbc_driver_OracleDriver extends generic
{
	private final static int	BUFFER_SIZE = 65535;				// 64kB
	private final static int	MAX_BLOB_SIZE = 1024*1024*1024*2;	// 2GB

	public oracle_jdbc_driver_OracleDriver(Datasource datasource)
	{
		super(datasource);

		mStoreContentChunk = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentStoreRawChunk())
			.fieldParameter("contentId")
			.fieldParameter("ordinal")
			.fieldCustom("chunk", "empty_blob()");
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
			InputStream data = RawContentStreamOracle.getInstance(this, mRetrieveContentChunks, id);
			try
			{
				return (ResultType)user.useContentData(data);
			}
			finally
			{
				if (data != null)
				{
					try
					{
						data.close();
					}
					catch (IOException e)
					{
						throw new UseContentDataErrorException(id, e);
					}
				}
			}
		}
		catch (DatabaseException e)
		{
			throw new UseContentDataErrorException(id, e);
		}
	}
	
	protected int storeChunks(Insert storeContentChunk, final int id, final InputStream data)
	throws IOException
	{
		class Scope {
			int		size = 0;
			int		length = 0;
			int		ordinal = 0;
			byte[]	buffer = null;
			int		blobsize = 0;
		}
		final Scope s = new Scope();
		
		if (data != null)
		{
			s.buffer = new byte[BUFFER_SIZE];
			while (s.length != -1 &&
				   (s.length = data.read(s.buffer)) != -1)
			{
				if (executeUpdate(storeContentChunk, new DbPreparedStatementHandler() {
						public void setParameters(DbPreparedStatement statement)
						{
							statement
								.setInt("contentId", id)
								.setInt("ordinal", s.ordinal);
						}
					}) <= 0)
				{
					return -1;
				}
				
				s.blobsize = 0;

				executeQuery(new Select(getDatasource())
								.from(storeContentChunk.getInto())
								.field("chunk")
								.where("contentId", "=", id)
								.whereAnd("ordinal = "+s.ordinal+" FOR UPDATE"), new DbResultSetHandler() {
						public Object concludeResults(DbResultSet resultset) throws SQLException
						{
							if (!resultset.next())
							{
								return null;
							}
							
							BLOB blob = (BLOB)resultset.getBlob(1);
							OutputStream os = blob.getBinaryOutputStream();
							try
							{
								try
								{
									do
									{
										os.write(s.buffer, 0, s.length);
										s.size += s.length;
										s.blobsize += s.length;
									}
									while (s.blobsize < MAX_BLOB_SIZE &&
										   (s.length = data.read(s.buffer)) != -1);
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

				s.ordinal++;
			}
		}
		
		return s.size;
	}

	protected void serveChunks(DbResultSet resultset, OutputStream os, int size)
	throws SQLException, IOException
	{
		do
		{
			BLOB blob = (BLOB)resultset.getBlob("chunk");
			if (null == blob)
			{
				return;
			}

			InputStream is = blob.getBinaryStream();
			try
			{
				byte[]	buffer = new byte[blob.getBufferSize()];
				BufferedInputStream buffered_image_is = new BufferedInputStream(is, buffer.length);
				int input_size = 0;
				while ((input_size = buffered_image_is.read(buffer)) != -1)
				{
					os.write(buffer, 0, input_size);
				}
			}
			finally
			{
				is.close();
			}
		}
		while (resultset.next());
	}
}
