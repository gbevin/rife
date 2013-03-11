/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseRawStore.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores;

import com.uwyn.rife.cmf.dam.contentstores.exceptions.*;
import com.uwyn.rife.database.*;
import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.ContentStore;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.format.Formatter;
import com.uwyn.rife.cmf.format.exceptions.FormatException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.engine.ElementSupport;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

public abstract class DatabaseRawStore extends DbQueryManager implements ContentStore
{
	private ArrayList<MimeType>	mMimeTypes = new ArrayList<MimeType>();

	public DatabaseRawStore(Datasource datasource)
	{
		super(datasource);

		addMimeType(MimeType.RAW);
	}

	protected void addMimeType(MimeType mimeType)
	{
		mMimeTypes.add(mimeType);
	}

	public Collection<MimeType> getSupportedMimeTypes()
	{
		return mMimeTypes;
	}

	public String getContentType(ContentInfo contentInfo)
	{
		MimeType mimeType = MimeType.getMimeType(contentInfo.getMimeType());
		if (!getSupportedMimeTypes().contains(mimeType))
		{
			return null;
		}
		
		Map<String, String> attributes = contentInfo.getAttributes();
		if (attributes != null)
		{
			if (attributes.containsKey("content-type"))
			{
				return attributes.get("content-type");
			}
		}
		if (contentInfo.hasName())
		{
			return RifeConfig.Mime.getMimeType(FileUtils.getExtension(contentInfo.getName()));
		}
		
		return null;
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
		return "";
	}
	
	protected boolean _install(CreateTable createTableContentInfo, CreateTable createTableContentChunk)
	throws ContentManagerException
	{
		assert createTableContentInfo != null;
		assert createTableContentChunk != null;

		try
		{
			executeUpdate(createTableContentInfo);
			executeUpdate(createTableContentChunk);
		}
		catch (DatabaseException e)
		{
			throw new InstallContentStoreErrorException(e);
		}

		return true;
	}

	protected boolean _remove(DropTable dropTableContentInfo, DropTable dropTableContentChunk)
	throws ContentManagerException
	{
		assert dropTableContentInfo != null;

		try
		{
			executeUpdate(dropTableContentChunk);
			executeUpdate(dropTableContentInfo);
		}
		catch (DatabaseException e)
		{
			throw new RemoveContentStoreErrorException(e);
		}

		return true;
	}

	protected boolean _deleteContentData(final Delete deleteContentInfo, final Delete deleteContentChunk, final int id)
	throws ContentManagerException
	{
		if (id < 0)	throw new IllegalArgumentException("id must be positive");
		
		assert deleteContentInfo != null;
		assert deleteContentChunk != null;
		
		Boolean result = null;

		try
		{
			result = inTransaction(new DbTransactionUser() {
					public Boolean useTransaction() throws InnerClassException
					{
						if (0 == executeUpdate(deleteContentChunk, new DbPreparedStatementHandler() {
								public void setParameters(DbPreparedStatement statement)
								{
									statement
										.setInt("contentId", id);
								}
							}))
						{
							return false;
						}

						return 0 != executeUpdate(deleteContentInfo, new DbPreparedStatementHandler()
						{
							public void setParameters(DbPreparedStatement statement)
							{
								statement
										.setInt("contentId", id);
							}
						});

					}
				});
		}
		catch (DatabaseException e)
		{
			throw new DeleteContentDataErrorException(id, e);
		}
		
		return result != null && result.booleanValue();
	}

	protected int _getSize(Select retrieveSize, final int id)
	throws ContentManagerException
	{
		if (id < 0)	throw new IllegalArgumentException("id must be positive");

		assert retrieveSize != null;

		try
		{
			return executeGetFirstInt(retrieveSize, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("contentId", id);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new RetrieveSizeErrorException(id, e);
		}
	}

	protected boolean _hasContentData(Select hasContentData, final int id)
	throws ContentManagerException
	{
		if (id < 0)	throw new IllegalArgumentException("id must be positive");

		assert hasContentData != null;

		try
		{
			return executeHasResultRows(hasContentData, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("contentId", id);
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new HasContentDataErrorException(id, e);
		}
	}

	protected String getContentSizeColumnName()
	{
		return "size";
	}

	protected boolean _storeContentData(final Insert storeContentInfo, final Insert storeContentChunk, final int id, Content content, ContentTransformer transformer)
	throws ContentManagerException
	{
		if (id < 0)											throw new IllegalArgumentException("id must be positive");
		if (content != null &&
			content.getData() != null &&
			!(content.getData() instanceof InputStream) &&
			!(content.getData() instanceof byte[]))	throw new IllegalArgumentException("the content data must be of type InputStream or byte[]");

		assert storeContentInfo != null;
		assert storeContentChunk != null;

		final InputStream typed_data;

		if (null == content ||
			null == content.getData())
		{
		    typed_data = null;
		}
		else
		{
			if (content.getData() instanceof byte[])
			{
				Content cloned_content = content.clone();
				cloned_content.setData(new ByteArrayInputStream((byte[])content.getData()));
				cloned_content.setCachedLoadedData(null);
				content = cloned_content;
			}
			
			Formatter formatter = null;
			if (!Convert.toBoolean(content.getAttribute("unformatted"), false))
			{
				formatter = getFormatter(content.getMimeType(), content.isFragment());
			}

			if (formatter != null)
			{
				try
				{
					typed_data = (InputStream)formatter.format(content, transformer);
				}
				catch (FormatException e)
				{
					throw new StoreContentDataErrorException(id, e);
				}
			}
			else
			{
				typed_data = (InputStream)content.getData();
			}
		}
		
		// store the data
		try
		{
			Boolean success = inTransaction(new DbTransactionUser() {
					public Object useTransaction()
					throws InnerClassException
					{
						try
						{
							final int size = storeChunks(storeContentChunk, id, typed_data);
							if (size < 0)
							{
								rollback();
							}
							
							if (executeUpdate(storeContentInfo, new DbPreparedStatementHandler() {
									public void setParameters(DbPreparedStatement statement)
									{
										statement
											.setInt("contentId", id)
											.setInt(getContentSizeColumnName(), size);
									}
								}) <= 0)
							{
								rollback();
							}
						}
						catch (IOException e)
						{
							throwException(e);
						}

						return true;
					}
				});

			return null != success && success.booleanValue();
		}
		catch (InnerClassException e)
		{
			throw new StoreContentDataErrorException(id, e.getCause());
		}
		catch (DatabaseException e)
		{
			throw new StoreContentDataErrorException(id, e);
		}
	}
	
	protected int storeChunks(Insert storeContentChunk, final int id, InputStream data)
	throws IOException
	{
		class Scope {
			int		size = 0;
			int		length = -1;
			int		ordinal = 0;
			byte[]	buffer = null;
		}
		final Scope s = new Scope();
		
		if (data != null)
		{
			s.buffer = new byte[65535];
			while ((s.length = data.read(s.buffer)) != -1)
			{
				s.size += s.length;

				if (executeUpdate(storeContentChunk, new DbPreparedStatementHandler() {
						public void setParameters(DbPreparedStatement statement)
						{
							statement
								.setInt("contentId", id)
								.setInt("ordinal", s.ordinal)
								.setBinaryStream("chunk", new ByteArrayInputStream(s.buffer), s.length);
						}
					}) <= 0)
				{
					return -1;
				}

				s.ordinal++;
			}
		}
		
		return s.size;
	}
	
	protected int storeChunksNoStream(Insert storeContentChunk, final int id, InputStream data)
	throws IOException
	{
		class Scope {
			int		size = 0;
			int		length = -1;
			int		ordinal = 0;
			byte[]	buffer = null;
			byte[]	buffer_swp = null;
		}
		final Scope s = new Scope();
		
		if (data != null)
		{
			s.buffer = new byte[65535];
			while ((s.length = data.read(s.buffer)) != -1)
			{
				s.size += s.length;
				
				if (s.length < s.buffer.length)
				{
					byte[] new_buffer = new byte[s.length];
					System.arraycopy(s.buffer, 0, new_buffer, 0, s.length);
					s.buffer_swp = s.buffer;
					s.buffer = new_buffer;
				}
				
				if (executeUpdate(storeContentChunk, new DbPreparedStatementHandler() {
							public void setParameters(DbPreparedStatement statement)
							{
								statement
									.setInt("contentId", id)
									.setInt("ordinal", s.ordinal)
									.setBytes("chunk", s.buffer);
							}
						}) <= 0)
				{
					return -1;
				}
				
				if (s.buffer_swp != null)
				{
					s.buffer = s.buffer_swp;
					s.buffer_swp = null;
				}
				
				s.ordinal++;
			}
		}
		
		return s.size;
	}
	
	protected <ResultType> ResultType _useContentData(Select retrieveContentChunks, final int id, ContentDataUser user)
	throws ContentManagerException
	{
		if (id < 0)			throw new IllegalArgumentException("id must be positive");
		if (null == user)	throw new IllegalArgumentException("user can't be null");

		assert retrieveContentChunks != null;
		
		try
		{
			InputStream data = RawContentStream.getInstance(this, retrieveContentChunks, id);
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
	
	protected DbPreparedStatement getStreamPreparedStatement(Query query, DbConnection connection)
	{
		DbPreparedStatement statement = connection.getPreparedStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
		statement.setFetchDirection(ResultSet.FETCH_FORWARD);
		statement.setFetchSize(1);
		return statement;
	}
	
	protected void _serveContentData(final Select retrieveContentChunks, final ElementSupport element, final int id)
	throws ContentManagerException
	{
		if (null == element)	throw new IllegalArgumentException("element can't be null");

		if (id < 0)
		{
			element.defer();
			return;
		}

		assert retrieveContentChunks != null;

		// set the content length header
		final int size = getSize(id);
		if (size < 0)
		{
			element.defer();
			return;
		}
		element.setContentLength(size);
		
		try
		{
			Boolean success = executeQuery(retrieveContentChunks, new DbPreparedStatementHandler() {
					public DbPreparedStatement getPreparedStatement(Query query, DbConnection connection)
					{
						return getStreamPreparedStatement(query, connection);
					}
					
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
							return false;
						}
						
						// output the content
						OutputStream	os = element.getOutputStream();
						try
						{
							serveChunks(resultset, os, size);
							
							os.flush();
						}
						catch (IOException e)
						{
							// don't do anything, the client has probably disconnected
						}
						
						return true;
					}
				});
			
			if (null == success || !success.booleanValue())
			{
				element.defer();
			}
		}
		catch (DatabaseException e)
		{
			Logger.getLogger("com.uwyn.rife.cmf").severe(ExceptionUtils.getExceptionStackTrace(e));
			element.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected void serveChunks(DbResultSet resultset, OutputStream os, int size)
	throws SQLException, IOException
	{
		byte[]	buffer = new byte[512];
		do
		{
			InputStream	is = resultset.getBinaryStream("chunk");
			BufferedInputStream buffered_raw_is = new BufferedInputStream(is, 512);
			int buffer_size = 0;
			try
			{
				while ((buffer_size = buffered_raw_is.read(buffer)) != -1)
				{
					os.write(buffer, 0, buffer_size);
				}
			}
			catch (IOException e)
			{
				// don't do anything, the client has probably disconnected
			}
		}
		while (resultset.next());
	}
}
