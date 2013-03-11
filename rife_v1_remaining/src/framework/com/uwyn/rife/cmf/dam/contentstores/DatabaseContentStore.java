/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseContentStore.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores;

import com.uwyn.rife.database.*;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.ContentStore;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.DeleteContentDataErrorException;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.HasContentDataErrorException;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.InstallContentStoreErrorException;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.RemoveContentStoreErrorException;
import com.uwyn.rife.cmf.dam.contentstores.exceptions.RetrieveSizeErrorException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Delete;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.engine.ElementSupport;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

public abstract class DatabaseContentStore extends DbQueryManager implements ContentStore
{
	private ArrayList<MimeType>	mMimeTypes = new ArrayList<MimeType>();
	
	public DatabaseContentStore(Datasource datasource)
	{
		super(datasource);
	}
	
	protected void addMimeType(MimeType mimeType)
	{
		mMimeTypes.add(mimeType);
	}
	
	public Collection<MimeType> getSupportedMimeTypes()
	{
		return mMimeTypes;
	}
	
	protected boolean _install(CreateTable createTableContentStore)
	throws ContentManagerException
	{
		assert createTableContentStore != null;
		
		try
		{
			executeUpdate(createTableContentStore);
		}
		catch (DatabaseException e)
		{
			throw new InstallContentStoreErrorException(e);
		}
		
		return true;
	}
	
	protected boolean _remove(DropTable dropTableContentStore)
	throws ContentManagerException
	{
		assert dropTableContentStore != null;
		
		try
		{
			executeUpdate(dropTableContentStore);
		}
		catch (DatabaseException e)
		{
			throw new RemoveContentStoreErrorException(e);
		}
		
		return true;
	}

	protected boolean _deleteContentData(final Delete deleteContentData, final int id)
	throws ContentManagerException
	{
		if (id < 0)	throw new IllegalArgumentException("id must be positive");
		
		assert deleteContentData != null;
		
		Boolean result = null;

		try
		{
			result = inTransaction(new DbTransactionUser() {
					public Boolean useTransaction() throws InnerClassException
					{
						return 0 != executeUpdate(deleteContentData, new DbPreparedStatementHandler()
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
	
	protected void _serveContentData(Select retrieveContent, final ElementSupport element, final int id)
	throws ContentManagerException
	{
		if (null == element)	throw new IllegalArgumentException("element can't be null");
		
		if (id < 0)
		{
			element.defer();
			return;
		}

		assert retrieveContent != null;
		
		try
		{
			if (!executeFetchFirst(retrieveContent,
				new DbRowProcessor() {
					public boolean processRow(ResultSet resultSet) throws SQLException
					{
						// set the content length header
						element.setContentLength(resultSet.getInt(getContentSizeColumnName()));
						
						// output the content
						OutputStream	os = element.getOutputStream();
						outputContentColumn(resultSet, os);
						
						return true;
					}
				},
				new DbPreparedStatementHandler() {
					public void setParameters(DbPreparedStatement statement)
					{
						statement
							.setInt("contentId", id);
					}
				}))
			{
				element.defer();
				return;
			}
		}
		catch (DatabaseException e)
		{
			Logger.getLogger("com.uwyn.rife.cmf").severe(ExceptionUtils.getExceptionStackTrace(e));
			element.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	protected abstract void outputContentColumn(ResultSet resultSet, OutputStream os) throws SQLException;
}
