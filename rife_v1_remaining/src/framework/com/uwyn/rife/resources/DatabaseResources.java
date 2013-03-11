/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseResources.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.*;
import com.uwyn.rife.resources.exceptions.*;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.logging.Logger;

/**
 * This class offers <code>ResourceFinder</code> and <code>ResourceWriter</code>
 * capabilities for resources that are stored in a database. The relevant database
 * is specified through a <code>Datasource/code> instance at construction.
 * <p>
 * While the table can be configured through the <code>TABLE_RESOURCES</code>
 * configuration setting, the structure of the table is fixed. It can be
 * installed with the <code>install()</code> method and removed with the
 * <code>remove()</code> method. The latter will implicitely erase all the
 * resources that have been stored in the database table.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.resources.ResourceFinder
 * @since 1.0
 */
public abstract class DatabaseResources extends DbQueryManager implements ResourceFinder,  ResourceWriter
{
	protected final static String	PROTOCOL = "file";
	
	protected final static String	COLUMN_NAME = "name";
	protected final static String	COLUMN_CONTENT = "content";
	protected final static String	COLUMN_MODIFIED = "modified";
		
	/**
	 * Creates a new instance according to the provided datasource.
	 *
	 * @param datasource the <code>Datasource</code> instance that defines the
	 * database that will be used as resources storage.
	 *
	 * @since 1.0
	 */
	protected DatabaseResources(Datasource datasource)
	{
		super(datasource);
	}
	
	/**
	 * Installs the database structure that's needed to store and retrieve
	 * resources in and from a database.
	 *
	 * @exception ResourceFinderErrorException when an error occurred during the
	 * installation
	 */
	public abstract boolean install() throws ResourceWriterErrorException;
	
	/**
	 * Removes the database structure that's needed to store and retrieve
	 * resources in and from a database.
	 *
	 * @exception ResourceFinderErrorException when an error occurred during the
	 * removal
	 */
	public abstract boolean remove() throws ResourceWriterErrorException;
	
	protected boolean _install(CreateTable createTable)
	throws ResourceWriterErrorException
	{
		try
		{
			executeUpdate(createTable);
		}
		catch (DatabaseException e)
		{
			throw new ResourceStructureInstallationException(e);
		}
		
		return true;
	}

	protected boolean _remove(DropTable dropTable)
	throws ResourceWriterErrorException
	{
		try
		{
			executeUpdate(dropTable);
		}
		catch (DatabaseException e)
		{
			throw new ResourceStructureRemovalException(e);
		}
		
		return true;
	}

	protected void _addResource(Insert addResource, final String name, final String content)
	throws ResourceWriterErrorException
	{
		assert addResource != null;
		
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		if (null == content)	throw new IllegalArgumentException("content can't be null.");
		
		try
		{
			executeUpdate(addResource, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString(COLUMN_NAME, name)
							.setString(COLUMN_CONTENT, content)
							.setTimestamp(COLUMN_MODIFIED, new java.sql.Timestamp(System.currentTimeMillis()));
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new ResourceAdditionErrorException(name, content, e);
		}
	}
	
	protected boolean _updateResource(Update updateResource, final String name, final String content)
	throws ResourceWriterErrorException
	{
		assert updateResource != null;
		
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		if (null == content)	throw new IllegalArgumentException("content can't be null.");
		
		boolean result = false;
		
		try
		{
			if (0 != executeUpdate(updateResource, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString(COLUMN_CONTENT, content)
							.setTimestamp(COLUMN_MODIFIED, new java.sql.Timestamp(System.currentTimeMillis()))
							.setString(COLUMN_NAME, name);
					}
				}))
			{
				result = true;
			}
		}
		catch (DatabaseException e)
		{
			throw new ResourceUpdateErrorException(name, content, e);
		}

		return result;
	}
	
	protected boolean _removeResource(Delete removeResource, final String name)
	throws ResourceWriterErrorException
	{
		assert removeResource != null;
		
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		
		boolean result = false;
		
		try
		{
			if (0 != executeUpdate(removeResource, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString(COLUMN_NAME, name);
					}
				}))
			{
				result = true;
			}
		}
		catch (DatabaseException e)
		{
			throw new ResourceRemovalErrorException(name, e);
		}

		return result;
	}

	protected URL _getResource(Select hasResource, final String name)
	{
		assert hasResource != null;
		
		if (null == name)
		{
			return null;
		}
		
		URL resource = null;
		
		try
		{
			if (executeHasResultRows(hasResource, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString(COLUMN_NAME, name);
					}
				}))
			{
				resource = new URL(PROTOCOL, "", name);
			}
		}
		catch (MalformedURLException e)
		{
			return null;
		}
		catch (DatabaseException e)
		{
			Logger.getLogger("com.uwyn.rife.resources").severe("Error while retrieving the resource with name '"+name+"' :\n"+ExceptionUtils.getExceptionStackTrace(e));
			return null;
		}

		return resource;
	}
	
	protected <ResultType> ResultType _useStream(Select getResourceContent, final URL resource, InputStreamUser user)
	throws ResourceFinderErrorException, InnerClassException
	{
		assert getResourceContent != null;
		
		if (null == resource ||
			null == user)
		{
			return null;
		}
		
		if (!PROTOCOL.equals(resource.getProtocol()))
		{
			return null;
		}
		
		try
		{
			return (ResultType)executeUseFirstBinaryStream(getResourceContent, user, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString(COLUMN_NAME, URLDecoder.decode(resource.getFile()));
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new CantOpenResourceStreamException(resource, e);
		}
	}

	protected String _getContent(Select getResourceContent, final URL resource, String encoding)
	throws ResourceFinderErrorException
	{
		assert getResourceContent != null;
		
		if (null == resource)
		{
			return null;
		}

		if (!PROTOCOL.equals(resource.getProtocol()))
		{
			return null;
		}

		String result = null;
		try
		{
			 result = executeGetFirstString(getResourceContent, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString(COLUMN_NAME, URLDecoder.decode(resource.getFile()));
					}
				});
		}
		catch (DatabaseException e)
		{
			throw new CantRetrieveResourceContentException(resource, encoding, e);
		}

		return result;
	}
	
	protected long _getModificationTime(Select getResourceModified, final URL resource)
	{
		assert getResourceModified != null;
		
		if (null == resource)
		{
			return -1;
		}

		if (!PROTOCOL.equals(resource.getProtocol()))
		{
			return -1;
		}
		
		try
		{
			long result = -1;
			
			Timestamp timestamp = executeGetFirstTimestamp(getResourceModified, new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setString(COLUMN_NAME, URLDecoder.decode(resource.getFile()));
					}
				});
			if (null == timestamp)
			{
				return -1;
			}
			result = timestamp.getTime();

			return result;
		}
		catch (DatabaseException e)
		{
			return -1;
		}
	}
	
	public <ResultType> ResultType useStream(String name, InputStreamUser user)
	throws ResourceFinderErrorException, InnerClassException
	{
		if (null == name ||
			null == user)
		{
			return null;
		}

		URL resource = getResource(name);
		if (null == resource)
		{
			return null;
		}
		
		return (ResultType)useStream(resource, user);
	}
	
	public String getContent(String name)
	throws ResourceFinderErrorException
	{
		return getContent(name, null);
	}
	
	public String getContent(String name, String encoding)
	throws ResourceFinderErrorException
	{
		if (null == name)
		{
			return null;
		}
		
		URL resource = getResource(name);
		if (null == resource)
		{
			return null;
		}
		
		return getContent(resource, encoding);
	}
	
	public String getContent(URL resource)
	throws ResourceFinderErrorException
	{
		return getContent(resource, null);
	}
	
	public long getModificationTime(String name)
	throws ResourceFinderErrorException
	{
		if (null == name)
		{
			return -1;
		}
		
		URL resource = getResource(name);
		if (null == resource)
		{
			return -1;
		}
		
		return getModificationTime(resource);
	}
}
