/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseContent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers;

import com.uwyn.rife.database.*;
import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentRepository;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.ContentManager;
import com.uwyn.rife.cmf.dam.ContentStore;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.InstallContentErrorException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.RemoveContentErrorException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.UnknownContentRepositoryException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.UnsupportedMimeTypeException;
import com.uwyn.rife.cmf.dam.contentstores.DatabaseImageStoreFactory;
import com.uwyn.rife.cmf.dam.contentstores.DatabaseRawStoreFactory;
import com.uwyn.rife.cmf.dam.contentstores.DatabaseTextStoreFactory;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.datastructures.Pair;
import com.uwyn.rife.engine.ElementSupport;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public abstract class DatabaseContent extends DbQueryManager implements ContentManager
{
	protected ArrayList<ContentStore>			mStores = null;
	protected HashMap<MimeType, ContentStore>	mMimeMapping = null;
	
	public DatabaseContent(Datasource datasource)
	{
		super(datasource);

		mStores = new ArrayList<ContentStore>();
		mStores.add(DatabaseTextStoreFactory.getInstance(getDatasource()));
		mStores.add(DatabaseImageStoreFactory.getInstance(getDatasource()));
		mStores.add(DatabaseRawStoreFactory.getInstance(getDatasource()));
		
		mMimeMapping = new HashMap<MimeType, ContentStore>();
		for (ContentStore store : mStores)
		{
			for (MimeType mime_type : store.getSupportedMimeTypes())
			{
				mMimeMapping.put(mime_type, store);
			}
		}
	}
	
	public abstract DatabaseContentInfo getContentInfo(String location) throws ContentManagerException;

	protected boolean _install(CreateSequence createSequenceContentRepository, CreateSequence createSequenceContentInfo,
							   CreateTable createTableContentRepository, CreateTable createTableContentInfo, CreateTable createTableContentAttribute, CreateTable createTableContentProperty)
	throws ContentManagerException
	{
		assert createSequenceContentRepository != null;
		assert createSequenceContentInfo != null;
		assert createTableContentRepository != null;
		assert createTableContentInfo != null;
		assert createTableContentAttribute != null;
		assert createTableContentProperty != null;
		
		try
		{
			executeUpdate(createSequenceContentRepository);
			executeUpdate(createSequenceContentInfo);
			executeUpdate(createTableContentRepository);
			executeUpdate(createTableContentInfo);
			executeUpdate(createTableContentAttribute);
			executeUpdate(createTableContentProperty);
			
			createRepository(ContentRepository.DEFAULT);

			for (ContentStore store : mStores)
			{
				store.install();
			}
		}
		catch (DatabaseException e)
		{
			throw new InstallContentErrorException(e);
		}
		
		return true;
	}
	
	protected boolean _remove(DropSequence dropSequenceContentRepository, DropSequence dropSequenceContentInfo,
							  DropTable dropTableContentRepository, DropTable dropTableContentInfo, DropTable dropTableContentAttribute, DropTable dropTableContentProperty)
	throws ContentManagerException
	{
		assert dropSequenceContentRepository != null;
		assert dropSequenceContentInfo != null;
		assert dropTableContentRepository != null;
		assert dropTableContentInfo != null;
		assert dropTableContentAttribute != null;
		assert dropTableContentProperty != null;
		
		try
		{
			for (ContentStore store : mStores)
			{
				store.remove();
			}

			executeUpdate(dropTableContentProperty);
			executeUpdate(dropTableContentAttribute);
			executeUpdate(dropTableContentInfo);
			executeUpdate(dropTableContentRepository);
			executeUpdate(dropSequenceContentInfo);
			executeUpdate(dropSequenceContentRepository);
		}
		catch (DatabaseException e)
		{
			throw new RemoveContentErrorException(e);
		}
		
		return true;
	}
	
	protected String getValueColumnName()
	{
		return "value";
	}
	
	protected boolean _createRepository(final SequenceValue getContentRepositoryId, final Insert storeContentRepository, final String name)
	throws ContentManagerException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty");
		
		assert getContentRepositoryId != null;
		assert storeContentRepository != null;

		Boolean result = null;

		try
		{
			result = inTransaction(new DbTransactionUser() {
					public Boolean useTransaction() throws InnerClassException
					{
						// get new repository id
						final int id = executeGetFirstInt(getContentRepositoryId);
						
						// store the content
						return executeUpdate(storeContentRepository, new DbPreparedStatementHandler() {
								public void setParameters(DbPreparedStatement statement)
								{
									statement
										.setInt("repositoryId", id)
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
	
	protected boolean _containsRepository(final Select containsContentRepository, final String name)
	throws ContentManagerException
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null");
		
		assert containsContentRepository != null;
		
		final String repository;
		if (0 == name.length())
		{
			repository = ContentRepository.DEFAULT;
		}
		else
		{
			repository = name;
		}
		
		return executeGetFirstInt(containsContentRepository, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					statement
						.setString("name", repository);
				}
			}) > 0;
	}
	
	protected Pair<String, String> splitLocation(String location)
	{
		if (null == location)			throw new IllegalArgumentException("location can't be null");
		if (0 == location.length())		throw new IllegalArgumentException("location can't be empty");
		
		int colon_index = location.indexOf(":");

		String repository = null;
		String path = null;
		if (colon_index != -1)
		{
			repository = location.substring(0, colon_index);
			path = location.substring(colon_index+1);
		}
		else
		{
			path = location;
		}
		
		if (null == repository ||
			0 == repository.length())
		{
			repository = ContentRepository.DEFAULT;
		}
		
		if (0 == path.length())		throw new IllegalArgumentException("path can't be empty");
		if (!path.startsWith("/"))	throw new IllegalArgumentException("path needs to be absolute");
		
		return new Pair<String, String>(repository, path);
	}

	protected boolean _storeContent(final SequenceValue getContentId, final Select getContentRepositoryId, final Insert storeContentInfo, final Insert storeContentAttribute, final Insert storeContentProperty, String location, final Content content, final ContentTransformer transformer)
	throws ContentManagerException
	{
		if (null == content)	throw new IllegalArgumentException("content can't be null");
		
		final Pair<String, String> split_location = splitLocation(location);
		
		assert getContentId != null;
		assert getContentRepositoryId != null;
		assert storeContentInfo != null;
		assert storeContentAttribute != null;
		assert storeContentProperty != null;

		final ContentStore store = mMimeMapping.get(content.getMimeType());
		if (null == store)
		{
			throw new UnsupportedMimeTypeException(content.getMimeType());
		}
		
		Boolean result = null;

		try
		{
			result = inTransaction(new DbTransactionUser() {
					public Boolean useTransaction() throws InnerClassException
					{
						// get new content id
						final int id = executeGetFirstInt(getContentId);
						
						// get repository id
						final int repository_id = executeGetFirstInt(getContentRepositoryId, new DbPreparedStatementHandler() {
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
			
						// store the content
						if (executeUpdate(storeContentInfo, new DbPreparedStatementHandler() {
								public void setParameters(DbPreparedStatement statement)
								{
									statement
										.setInt("contentId", id)
										.setInt("repositoryId", repository_id)
										.setString("path", split_location.getSecond())
										.setString("mimeType", content.getMimeType().toString())
										.setBoolean("fragment", content.isFragment());
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
										
									executeUpdate(storeContentAttribute, new DbPreparedStatementHandler() {
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

									executeUpdate(storeContentProperty, new DbPreparedStatementHandler() {
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

	protected boolean _deleteContent(final Select getContentInfo, final Delete deleteContentInfo, final Delete deleteContentAttributes, final Delete deleteContentProperties, String location)
	throws ContentManagerException
	{
		final Pair<String, String> split_location = splitLocation(location);
		
		assert getContentInfo != null;
		assert deleteContentInfo != null;
		assert deleteContentAttributes != null;
		assert deleteContentProperties != null;
		
		Boolean result = null;

		try
		{
			result = inTransaction(new DbTransactionUser() {
					public Boolean useTransaction() throws InnerClassException
					{
						return executeFetchAll(getContentInfo, new DbRowProcessor() {
							public boolean processRow(ResultSet resultSet)
							throws SQLException
							{
								final int contentid = resultSet.getInt("contentId");
											
								MimeType mimetype = MimeType.getMimeType(resultSet.getString("mimeType"));
								
								ContentStore store = mMimeMapping.get(mimetype);
								if (null == store)
								{
									throw new UnsupportedMimeTypeException(mimetype);
								}
								
								if (!store.deleteContentData(contentid))
								{
									rollback();
								}
									
								executeUpdate(deleteContentAttributes, new DbPreparedStatementHandler() {
										public void setParameters(DbPreparedStatement statement)
										{
											statement
												.setInt("contentId", contentid);
										}
									});
									
								executeUpdate(deleteContentProperties, new DbPreparedStatementHandler() {
										public void setParameters(DbPreparedStatement statement)
										{
											statement
												.setInt("contentId", contentid);
										}
									});
								
								if (0 == executeUpdate(deleteContentInfo, new DbPreparedStatementHandler() {
										public void setParameters(DbPreparedStatement statement)
										{
											statement
												.setInt("contentId", contentid);
										}
									}))
								{
									rollback();
								}
								
								return true;
							}
						}, new DbPreparedStatementHandler() {
							public void setParameters(DbPreparedStatement statement)
							{
								statement
									.setString("repository", split_location.getFirst())
									.setString("path", split_location.getSecond());
							}
						});
					}
				});
		}
		catch (InnerClassException e)
		{
			throw (ContentManagerException)e.getCause();
		}
		
		return result != null && result.booleanValue();
	}
	
	private Pair<String, String> splitPath(String path)
	{
		assert path != null;
		
		int slash_index = path.lastIndexOf('/');
		String path_part = path.substring(0, slash_index);
		String name_part = path.substring(slash_index+1);
		
		return new Pair<String, String>(path_part, name_part);
	}
	
	protected <ResultType> ResultType _useContentData(Select retrieveContent, String location, ContentDataUser user)
	throws ContentManagerException
	{
		if (null == user)	throw new IllegalArgumentException("user can't be null");
		
		final Pair<String, String> split_location = splitLocation(location);
		
		assert retrieveContent != null;
		
		DatabaseContentInfo content_info = executeFetchFirstBean(retrieveContent, DatabaseContentInfo.class, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					Pair<String, String> path_parts = splitPath(split_location.getSecond());
					statement
						.setString("repository", split_location.getFirst())
						.setString("path", split_location.getSecond())
						.setString("pathpart", path_parts.getFirst())
						.setString("namepart", path_parts.getSecond());
				}
			});
		
		if (null == content_info)
		{
			return null;
		}

		MimeType mime_type = MimeType.getMimeType(content_info.getMimeType());
		ContentStore store = mMimeMapping.get(mime_type);
		if (null == store)
		{
			throw new UnsupportedMimeTypeException(mime_type);
		}

		return (ResultType)store.useContentData(content_info.getContentId(), user);
	}

	protected boolean _hasContentData(Select retrieveContent, String location)
	throws ContentManagerException
	{
		final Pair<String, String> split_location = splitLocation(location);
		
		assert retrieveContent != null;
		
		DatabaseContentInfo content_info = executeFetchFirstBean(retrieveContent, DatabaseContentInfo.class, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					Pair<String, String> path_parts = splitPath(split_location.getSecond());
					statement
						.setString("repository", split_location.getFirst())
						.setString("path", split_location.getSecond())
						.setString("pathpart", path_parts.getFirst())
						.setString("namepart", path_parts.getSecond());
				}
			});

		if (null == content_info)
		{
			return false;
		}

		MimeType mime_type = MimeType.getMimeType(content_info.getMimeType());
		ContentStore store = mMimeMapping.get(mime_type);
		if (null == store)
		{
			throw new UnsupportedMimeTypeException(mime_type);
		}

		return store.hasContentData(content_info.getContentId());
	}
	
	protected DatabaseContentInfo _getContentInfo(Select getContentInfo, Select getContentAttributes, Select getContentProperties, String location)
	throws ContentManagerException
	{
		final Pair<String, String> split_location = splitLocation(location);
		
		assert getContentInfo != null;
		assert getContentAttributes != null;
		assert getContentProperties != null;
		
		final DatabaseContentInfo content_info = executeFetchFirstBean(getContentInfo, DatabaseContentInfo.class, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement)
				{
					Pair<String, String> path_parts = splitPath(split_location.getSecond());
					statement
						.setString("repository", split_location.getFirst())
						.setString("path", split_location.getSecond())
						.setString("pathpart", path_parts.getFirst())
						.setString("namepart", path_parts.getSecond());
				}
			});
		
		if (content_info != null)
		{
			// get the content attributes
			ContentAttributesProcessor processor_attributes = new ContentAttributesProcessor();
			executeFetchAll(getContentAttributes, processor_attributes, new DbPreparedStatementHandler() {
						public void setParameters(DbPreparedStatement statement)
						{
							statement
								.setInt("contentId", content_info.getContentId());
						}
					}
				);
			content_info.setAttributes(processor_attributes.getAttributes());
			
			// get the content data properties
			ContentPropertiesProcessor processor_properties = new ContentPropertiesProcessor();
			executeFetchAll(getContentProperties, processor_properties, new DbPreparedStatementHandler() {
						public void setParameters(DbPreparedStatement statement)
						{
							statement
								.setInt("contentId", content_info.getContentId());
						}
					}
				);
			content_info.setProperties(processor_properties.getProperties());
			
			// retrieve the content store
			MimeType mime_type = MimeType.getMimeType(content_info.getMimeType());
			ContentStore store = mMimeMapping.get(mime_type);
			if (null == store)
			{
				throw new UnsupportedMimeTypeException(mime_type);
			}
			
			// retrieve the content size
			content_info.setSize(store.getSize(content_info.getContentId()));
		}
		
		return content_info;
	}

	protected void _serveContentData(ElementSupport element, final String location)
	throws ContentManagerException
    {
		if (null == element)	throw new IllegalArgumentException("element can't be null.");
		
		try
		{
			splitLocation(location);
		}
		catch (IllegalArgumentException e)
		{
			element.defer();
			return;
		}
		
		
		DatabaseContentInfo content_info = null;
		try
		{
			content_info = getContentInfo(location);
		}
		catch (IllegalArgumentException e)
		{
			element.defer();
			return;
		}
		if (null == content_info)
		{
			element.defer();
			return;
		}
		
		// retrieve the content store
		MimeType mime_type = MimeType.getMimeType(content_info.getMimeType());
		ContentStore store = mMimeMapping.get(mime_type);
		if (null == store)
		{
			throw new UnsupportedMimeTypeException(mime_type);
		}
		
		// set cache headers
		long		if_modified_since = element.getDateHeader("If-Modified-Since");
		Timestamp	last_modified = content_info.getCreated();
		long		last_modified_timestamp = (last_modified.getTime() /1000)*1000;
		if (if_modified_since > 0 &&
			if_modified_since >= last_modified_timestamp)
		{
			element.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		
		// set general headers
		element.setContentType(store.getContentType(content_info));
		if (content_info.hasName())
		{
			element.addHeader("Content-Disposition", "inline; filename="+content_info.getName());
		}
		element.addHeader("Cache-Control", "must-revalidate");
		element.addDateHeader("Expires", System.currentTimeMillis()+60*60*1000);
		element.addDateHeader("Last-Modified", last_modified_timestamp);

		store.serveContentData(element, content_info.getContentId());
    }

	protected String _getContentForHtml(String location, ElementSupport element, String serveContentExitName)
	throws ContentManagerException
    {
		DatabaseContentInfo content_info = null;
		try
		{
			content_info = getContentInfo(location);
		}
		catch (IllegalArgumentException e)
		{
			return "";
		}
		if (null == content_info)
		{
			return "";
		}
		
		// retrieve the content store
		MimeType mime_type = MimeType.getMimeType(content_info.getMimeType());
		ContentStore store = mMimeMapping.get(mime_type);
		if (null == store)
		{
			throw new UnsupportedMimeTypeException(mime_type);
		}
		
		return store.getContentForHtml(content_info.getContentId(), content_info, element, serveContentExitName);
    }
	
	private class ContentAttributesProcessor extends DbRowProcessor
	{
		private Map<String, String> mAttributes = null;
		
		public boolean processRow(ResultSet result)
		throws SQLException
		{
			if (null == mAttributes)
			{
				mAttributes = new HashMap<String, String>();
			}
					
			mAttributes.put(result.getString("name"), result.getString(getValueColumnName()));
			return true;
		}
		
		public Map<String, String> getAttributes()
		{
			return mAttributes;
		}
	}
	
	private class ContentPropertiesProcessor extends DbRowProcessor
	{
		private Map<String, String> mProperties = null;
		
		public boolean processRow(ResultSet result)
		throws SQLException
		{
			if (null == mProperties)
			{
				mProperties = new HashMap<String, String>();
			}
					
			mProperties.put(result.getString("name"), result.getString(getValueColumnName()));
			return true;
		}
		
		public Map<String, String> getProperties()
		{
			return mProperties;
		}
	}
}
