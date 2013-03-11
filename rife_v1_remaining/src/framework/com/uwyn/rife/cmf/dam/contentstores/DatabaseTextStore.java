/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseTextStore.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.StoreContentDataErrorException;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.UseContentDataErrorException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.format.Formatter;
import com.uwyn.rife.cmf.format.exceptions.FormatException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.engine.ElementSupport;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.StringUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;
import java.util.logging.Logger;

public abstract class DatabaseTextStore extends DatabaseContentStore
{
	public DatabaseTextStore(Datasource datasource)
	{
		super(datasource);

		addMimeType(MimeType.APPLICATION_XHTML);
		addMimeType(MimeType.TEXT_PLAIN);
		addMimeType(MimeType.TEXT_XML);
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

		return content_type+"; charset=UTF-8";
	}
	
	public Formatter getFormatter(MimeType mimeType, boolean fragment)
	{
		if (!getSupportedMimeTypes().contains(mimeType))
		{
			return null;
		}
		return mimeType.getFormatter();
	}
	
	protected boolean _storeContentData(Insert storeContent, final int id, Content content, ContentTransformer transformer)
	throws ContentManagerException
	{
		if (id < 0)									throw new IllegalArgumentException("id must be positive");
		if (content != null &&
			content.getData() != null &&
			!(content.getData() instanceof String))	throw new IllegalArgumentException("the content data must be of type String");
		
		assert storeContent != null;
		
		final String typed_data;

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
					typed_data = (String)formatter.format(content, transformer);
				}
				catch (FormatException e)
				{
					throw new StoreContentDataErrorException(id, e);
				}
			}
			else
			{
				typed_data = (String)content.getData();
			}
		}
		
		return storeContent(storeContent, id, typed_data);
	}

	protected boolean storeContent(Insert storeContent, final int id, final String data)
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
									.setNull("content", Types.CLOB)
									.setInt(getContentSizeColumnName(), 0);
						}
						else
						{
							byte[] bytes = null;
							try
							{
								bytes = data.getBytes("UTF-8");
							}
							catch (UnsupportedEncodingException e)
							{
								// this is impossible, UTF-8 is always supported
								Logger.getLogger("com.uwyn.rife.cmf").severe(ExceptionUtils.getExceptionStackTrace(e));
							}

							statement
								.setInt(getContentSizeColumnName(), bytes.length)
								.setCharacterStream("content", new StringReader(data), data.length());
						}
					}
				});
			
			return result > 0;
		}
		catch (DatabaseException e)
		{
			throw new StoreContentDataErrorException(id, e);
		}
	}
	
	protected <ResultType> ResultType _useContentData(Select retrieveContent, final int id, ContentDataUser user)
	throws ContentManagerException
	{
		if (id < 0)			throw new IllegalArgumentException("id must be positive");
		if (null == user)	throw new IllegalArgumentException("user can't be null");

		assert retrieveContent != null;
		
		try
		{
			return (ResultType)user.useContentData(executeGetFirstString(retrieveContent, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("contentId", id);
					}
				}));
		}
		catch (DatabaseException e)
		{
			throw new UseContentDataErrorException(id, e);
		}
	}
	
	public String getContentForHtml(int id, final ContentInfo info, ElementSupport element, String serveContentExitName)
	throws ContentManagerException
	{
		return useContentData(id, new ContentDataUser() {
					public String useContentData(Object contentData)
					throws InnerClassException
					{
						if (null == contentData)
						{
							return "";
						}
						
						if (MimeType.APPLICATION_XHTML.equals(info.getMimeType()))
						{
							return contentData.toString();
						}
						else if (MimeType.TEXT_PLAIN.equals(info.getMimeType()))
						{
							return StringUtils.encodeHtml(contentData.toString());
						}
						
						return "";
					}
				});
	}
	
	protected void outputContentColumn(ResultSet resultSet, OutputStream os)
	throws SQLException
	{
		Reader 	text_reader = resultSet.getCharacterStream("content");
		char[]	buffer = new char[512];
		int size = 0;
		try
		{
			while ((size = text_reader.read(buffer)) != -1)
			{
				String string_buffer = new String(buffer, 0, size);
				os.write(string_buffer.getBytes("UTF-8"));
			}
			
			os.flush();
		}
		catch (IOException e)
		{
			// don't do anything, the client has probably disconnected
		}
	}
}
