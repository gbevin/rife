/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseImageStore.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.StoreContentDataErrorException;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.UseContentDataErrorException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.format.Formatter;
import com.uwyn.rife.cmf.format.ImageFormatter;
import com.uwyn.rife.cmf.format.exceptions.FormatException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbResultSet;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.engine.ElementSupport;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.StringUtils;

public abstract class DatabaseImageStore extends DatabaseContentStore
{
	public DatabaseImageStore(Datasource datasource)
	{
		super(datasource);
		
		addMimeType(MimeType.IMAGE_GIF);
		addMimeType(MimeType.IMAGE_JPEG);
		addMimeType(MimeType.IMAGE_PNG);
	}
	
	public String getContentType(ContentInfo contentInfo)
	{
		MimeType mimeType = MimeType.getMimeType(contentInfo.getMimeType());
		if (!getSupportedMimeTypes().contains(mimeType))
		{
			return null;
		}
		
		String content_type = mimeType.toString();
		
		Map<String, String> attributes = contentInfo.getAttributes();
		if (attributes != null)
		{
			if (attributes.containsKey("content-type"))
			{
				content_type = attributes.get("content-type");
			}
		}
		
		return content_type;
	}
	
	public Formatter getFormatter(MimeType mimeType, boolean fragment)
	{
		if (!getSupportedMimeTypes().contains(mimeType))
		{
			return null;
		}
		return mimeType.getFormatter();
	}
	
	public String getContentForHtml(int id, ContentInfo info, ElementSupport element, String serveContentExitName)
	throws ContentManagerException
	{
		if (null == element)				throw new IllegalArgumentException("element can't be null.");
		if (null == serveContentExitName)	throw new IllegalArgumentException("serveContentExitName can't be null.");
		
		StringBuilder result = new StringBuilder();
		result.append("<img src=\"");
		result.append(StringUtils.encodeHtml(element.getExitQueryUrl(serveContentExitName, info.getPath()).toString()));
		result.append("\"");
		Map<String, String> properties = info.getProperties();
		if (properties != null)
		{
			String width = properties.get(ImageFormatter.CMF_PROPERTY_WIDTH);
			if (width != null)
			{
				result.append(" width=\"");
				result.append(width);
				result.append("\"");
			}
			String height = properties.get(ImageFormatter.CMF_PROPERTY_HEIGHT);
			if (height != null)
			{
				result.append(" height=\"");
				result.append(height);
				result.append("\"");
			}
		}
		result.append(" alt=\"\" />");
		
		return result.toString();
	}
	
	protected boolean _storeContentData(final Insert storeContent, final int id, Content content, ContentTransformer transformer)
	throws ContentManagerException
	{
		if (id < 0)									throw new IllegalArgumentException("id must be positive");
		if (content != null &&
			content.getData() != null &&
			!(content.getData() instanceof byte[]))	throw new IllegalArgumentException("the content data must be of type byte[]");
		
		assert storeContent != null;
		
		final byte[] typed_data;

		if (null == content ||
			null == content.getData())
		{
		    typed_data = null;
		}
		else
		{
			Formatter formatter = null;
			if (!Convert.toBoolean(content.getAttribute("unformatted"), false))
			{
				formatter = getFormatter(content.getMimeType(), content.isFragment());
			}
			
			if (formatter != null)
			{
				try
				{
					typed_data = (byte[])formatter.format(content, transformer);
				}
				catch (FormatException e)
				{
					throw new StoreContentDataErrorException(id, e);
				}
			}
			else
			{
				typed_data = (byte[])content.getData();
			}
		}
		
		return storeTypedData(storeContent, id, typed_data);
	}
	
	protected boolean storeTypedData(Insert storeContent, final int id, final byte[] data)
	throws ContentManagerException
	{
		try
		{
			int result = executeUpdate(storeContent, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("contentId", id);
						if (null == data)
						{
							statement
							.setNull("content", getNullSqlType())
								.setInt(getContentSizeColumnName(), 0);
						}
						else
						{
							statement
								.setBinaryStream("content", new ByteArrayInputStream(data), data.length)
								.setInt(getContentSizeColumnName(), data.length);
						}
					}
				});
			
			return result != -1;
		}
		catch (DatabaseException e)
		{
			throw new StoreContentDataErrorException(id, e);
		}
	}
	
	protected int getNullSqlType()
	{
		return Types.BLOB;
	}
	
	protected <ResultType> ResultType _useContentData(Select retrieveContent, final int id, ContentDataUser user)
	throws ContentManagerException
	{
		if (id < 0)			throw new IllegalArgumentException("id must be positive");
		if (null == user)	throw new IllegalArgumentException("user can't be null");

		assert retrieveContent != null;
		
		try
		{
			return (ResultType)user.useContentData(executeQuery(retrieveContent, new DbPreparedStatementHandler() {
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
						
						return resultset.getBytes("content");
					}
				}));
		}
		catch (DatabaseException e)
		{
			throw new UseContentDataErrorException(id, e);
		}
	}
	
	protected void outputContentColumn(ResultSet resultSet, OutputStream os)
	throws SQLException
	{
		InputStream	is = resultSet.getBinaryStream("content");
		byte[]		buffer = new byte[512];
		BufferedInputStream buffered_raw_is = new BufferedInputStream(is, 512);
		int size = 0;
		try
		{
			while ((size = buffered_raw_is.read(buffer)) != -1)
			{
				os.write(buffer, 0, size);
			}

			os.flush();
		}
		catch (IOException e)
		{
			// don't do anything, the client has probably disconnected
		}
	}
}
