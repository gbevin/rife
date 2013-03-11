/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DbResultSet40.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.RowIndexOutOfBoundsException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;

public class DbResultSet40 extends DbResultSet
{
	DbResultSet40(DbStatement statement, ResultSet resultSet)
	{
		super(statement, resultSet);
	}
	
	public RowId getRowId(int columnIndex) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getRowId(columnIndex);
	}
	
	public RowId getRowId(String columnLabel) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getRowId(columnLabel);
	}
	
	public void updateRowId(int columnIndex, RowId x) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateRowId(columnIndex, x);
	}
	
	public void updateRowId(String columnLabel, RowId x) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateRowId(columnLabel, x);
	}
	
	public int getHoldability() throws SQLException
	{
		return mResultSet.getHoldability();
	}
	
	public boolean isClosed() throws SQLException
	{
		return mResultSet.isClosed();
	}
	
	public void updateNString(int columnIndex, String nString) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNString(columnIndex, nString);
	}
	
	public void updateNString(String columnLabel, String nString) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNString(columnLabel, nString);
	}
	
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNClob(columnIndex, nClob);
	}
	
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNClob(columnLabel, nClob);
	}
	
	public NClob getNClob(int columnIndex) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getNClob(columnIndex);
	}
	
	public NClob getNClob(String columnLabel) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getNClob(columnLabel);
	}
	
	public SQLXML getSQLXML(int columnIndex) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getSQLXML(columnIndex);
	}
	
	public SQLXML getSQLXML(String columnLabel) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getSQLXML(columnLabel);
	}
	
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateSQLXML(columnIndex, xmlObject);
	}
	
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateSQLXML(columnLabel, xmlObject);
	}
	
	public String getNString(int columnIndex) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getNString(columnIndex);
	}
	
	public String getNString(String columnLabel) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getNString(columnLabel);
	}
	
	public Reader getNCharacterStream(int columnIndex) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getNCharacterStream(columnIndex);
	}
	
	public Reader getNCharacterStream(String columnLabel) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		return mResultSet.getNCharacterStream(columnLabel);
	}
	
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNCharacterStream(columnIndex, x);
	}
	
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNCharacterStream(columnIndex, x, length);
	}
	
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNCharacterStream(columnLabel, reader);
	}
	
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNCharacterStream(columnLabel, reader, length);
	}
	
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateAsciiStream(columnIndex, x);
	}
	
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateAsciiStream(columnIndex, x, length);
	}
	
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBinaryStream(columnIndex, x);
	}
	
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBinaryStream(columnIndex, x, length);
	}
	
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateCharacterStream(columnIndex, x);
	}
	
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateCharacterStream(columnIndex, x, length);
	}
	
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateAsciiStream(columnLabel, x);
	}
	
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateAsciiStream(columnLabel, x, length);
	}
	
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBinaryStream(columnLabel, x);
	}
	
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBinaryStream(columnLabel, x, length);
	}
	
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateCharacterStream(columnLabel, reader);
	}
	
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateCharacterStream(columnLabel, reader, length);
	}
	
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBlob(columnIndex, inputStream);
	}
	
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBlob(columnLabel, inputStream);
	}
	
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBlob(columnIndex, inputStream, length);
	}
	
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateBlob(columnLabel, inputStream, length);
	}
	
	public void updateClob(int columnIndex, Reader reader) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateClob(columnIndex, reader);
	}
	
	public void updateClob(String columnLabel, Reader reader) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateClob(columnLabel, reader);
	}
	
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateClob(columnIndex, reader, length);
	}
	
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateClob(columnLabel, reader, length);
	}
	
	public void updateNClob(String columnLabel, Reader reader) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNClob(columnLabel, reader);
	}
	
	public void updateNClob(int columnIndex, Reader reader) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNClob(columnIndex, reader);
	}
	
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNClob(columnIndex, reader, length);
	}
	
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException
	{
		if (mFirstRowSkew)
		{
			throw new RowIndexOutOfBoundsException();
		}
		
		mResultSet.updateNClob(columnLabel, reader, length);
	}
	
	public <T extends Object> T unwrap(Class<T> iface) throws SQLException
	{
		return mResultSet.unwrap(iface);
	}
	
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return mResultSet.isWrapperFor(iface);
	}
}
