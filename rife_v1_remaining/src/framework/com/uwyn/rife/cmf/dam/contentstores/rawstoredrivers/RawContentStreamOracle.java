/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RawContentStreamOracle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.rawstoredrivers;

import com.uwyn.rife.cmf.dam.contentstores.DatabaseRawStore;
import com.uwyn.rife.cmf.dam.contentstores.RawContentStream;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.queries.Select;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import oracle.sql.BLOB;

public class RawContentStreamOracle extends RawContentStream
{
	private InputStream	mInputStream = null;
	
	protected RawContentStreamOracle(DbPreparedStatement statement)
	{
		super(statement);
	}
	
	public int read()
	throws IOException
	{
        BLOB blob = null;

		int result = -1;
		try
		{
			while (true)
			{
				if (null == mInputStream)
				{
					if (!mHasRow)
					{
						return -1;
					}
					
					blob = (BLOB)mResultSet.getBlob("chunk");
					mInputStream = blob.getBinaryStream();
				}
	
				result = mInputStream.read();
				
				if (-1 == result)
				{
					mInputStream.close();
					mInputStream = null;
					mHasRow = mResultSet.next();
					continue;
				}
				
				break;
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
		if (mInputStream != null)
		{
			mInputStream.close();
			
			mInputStream = null;
		}
	}
	
	public static RawContentStream getInstance(DatabaseRawStore store, Select retrieveContentChunks, int id)
	{
		DbPreparedStatement statement = prepareStatement(store,  retrieveContentChunks, id);
		if (null == statement)
		{
			return null;
		}
		
		return new RawContentStreamOracle(statement);
	}
}
