/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RawContentStream.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores;

import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbResultSet;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.Select;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class RawContentStream extends InputStream
{
	protected DbPreparedStatement	mStatement = null;
	protected DbResultSet			mResultSet = null;
	protected byte[]				mBuffer = null;
	protected int					mIndex = 0;
	protected boolean				mHasRow = false;
	
	protected RawContentStream(DbPreparedStatement statement)
	{
		mStatement = statement;
		mResultSet = mStatement.getResultSet();
		mHasRow = true;
		
		assert mStatement != null;
		assert mResultSet != null;
	}
	
	public int read()
	throws IOException
	{
		if (null == mResultSet)
		{
			throw new IOException("Trying to read from a closed raw content stream.");
		}
		
		int result = -1;
		try
		{
			if (null == mBuffer)
			{
				if (!mHasRow)
				{
					return -1;
				}
				
				mBuffer = mResultSet.getBytes("chunk");
				mIndex = 0;
			}
			
			result = mBuffer[mIndex++];
			
			if (mIndex >= mBuffer.length)
			{
				mBuffer = null;
				mHasRow = mResultSet.next();
			}
		}
		catch (SQLException e)
		{
			IOException e2 = new IOException("Unexpected error while reading the next bytes.");
			e2.initCause(e);
			throw e2;
		}
		
		return result;
	}

    public void close()
	throws IOException
	{
		if (null == mStatement)
		{
			return;
		}
		
		try
		{
			mStatement.close();
		}
		catch (DatabaseException e)
		{
			IOException e2 = new IOException("Unable to close prepared statement.");
			e2.initCause(e);
			throw e2;
		}
		finally
		{
			mStatement = null;
			mResultSet = null;
			mBuffer = null;
		}
	}
	
	protected static DbPreparedStatement prepareStatement(DatabaseRawStore store, Select retrieveContentChunks, int id)
	{
		DbPreparedStatement statement = store.getStreamPreparedStatement(retrieveContentChunks, store.getConnection());

		statement
			.setInt("contentId", id);
		statement.executeQuery();
		DbResultSet resultset = statement.getResultSet();
		try
		{
			if (!resultset.next())
			{
				statement.close();
				return null;
			}
		}
		catch (SQLException e)
		{
			statement.close();
			return null;
		}
		
		return statement;
	}
	
	public static RawContentStream getInstance(DatabaseRawStore store, Select retrieveContentChunks, int id)
	{
		DbPreparedStatement statement = prepareStatement(store,  retrieveContentChunks, id);
		if (null == statement)
		{
			return null;
		}
		
		return new RawContentStream(statement);
	}
}

