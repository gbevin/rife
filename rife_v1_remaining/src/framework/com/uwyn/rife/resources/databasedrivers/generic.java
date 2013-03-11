/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.resources.databasedrivers;

import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.resources.DatabaseResources;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.resources.exceptions.ResourceWriterErrorException;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import java.net.URL;

public class generic extends DatabaseResources
{
	protected CreateTable	mCreateTable = null;
	protected DropTable		mDropTable = null;
	protected Insert		mAddResource = null;
	protected Update		mUpdateResource = null;
	protected Delete		mRemoveResource = null;
	protected Select		mHasResource = null;
	protected Select		mGetResourceContent = null;
	protected Select		mGetResourceModified = null;

	public generic(Datasource datasource)
	{
		super(datasource);
		
		String table = RifeConfig.Resources.getTableResources();
		
		mCreateTable = new CreateTable(getDatasource())
			.table(table)
			.column(COLUMN_NAME, String.class, 255, CreateTable.NOTNULL)
			.column(COLUMN_CONTENT, String.class)
			.column(COLUMN_MODIFIED, java.sql.Timestamp.class)
			.primaryKey(COLUMN_NAME);
		
		mDropTable = new DropTable(getDatasource())
			.table(table);
		
		mAddResource = new Insert(getDatasource())
			.into(table)
			.fieldParameter(COLUMN_NAME)
			.fieldParameter(COLUMN_CONTENT)
			.fieldParameter(COLUMN_MODIFIED);
		
		mUpdateResource = new Update(getDatasource())
			.table(table)
			.fieldParameter(COLUMN_CONTENT)
			.fieldParameter(COLUMN_MODIFIED)
			.whereParameter(COLUMN_NAME, "=");
		
		mRemoveResource = new Delete(getDatasource())
			.from(table)
			.whereParameter(COLUMN_NAME, "=");
		
		mHasResource = new Select(getDatasource())
			.from(table)
			.field(COLUMN_NAME)
			.whereParameter(COLUMN_NAME, "=");

		mGetResourceContent = new Select(getDatasource())
			.from(table)
			.field(COLUMN_CONTENT)
			.whereParameter(COLUMN_NAME, "=");

		mGetResourceModified = new Select(getDatasource())
			.from(table)
			.field(COLUMN_MODIFIED)
			.whereParameter(COLUMN_NAME, "=");
	}
	
	public boolean install()
	throws ResourceWriterErrorException
	{
		return _install(mCreateTable);
	}
	
	public boolean remove()
	throws ResourceWriterErrorException
	{
		return _remove(mDropTable);
	}
	
	public void addResource(String name, String content)
	throws ResourceWriterErrorException
	{
		_addResource(mAddResource, name, content);
	}
	
	public boolean updateResource(String name, String content)
	throws ResourceWriterErrorException
	{
		return _updateResource(mUpdateResource, name, content);
	}
	
	public boolean removeResource(String name)
	throws ResourceWriterErrorException
	{
		return _removeResource(mRemoveResource, name);
	}
	
	public URL getResource(String name)
	{
		return _getResource(mHasResource, name);
	}
	
	public <ResultType> ResultType useStream(URL resource, InputStreamUser user)
	throws ResourceFinderErrorException, InnerClassException
	{
		return (ResultType)_useStream(mGetResourceContent, resource, user);
	}
	
	public String getContent(URL resource, String encoding)
	throws ResourceFinderErrorException
	{
		return _getContent(mGetResourceContent, resource, encoding);
	}
	
	public long getModificationTime(URL resource)
	throws ResourceFinderErrorException
	{
		return _getModificationTime(mGetResourceModified, resource);
	}
}
