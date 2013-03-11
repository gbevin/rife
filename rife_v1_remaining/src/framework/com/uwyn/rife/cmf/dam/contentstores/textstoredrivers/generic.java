/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.textstoredrivers;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.contentstores.DatabaseTextStore;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.Delete;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;
import com.uwyn.rife.engine.ElementSupport;

public class generic extends DatabaseTextStore
{
	protected CreateTable	mCreateTableContent = null;
	protected DropTable		mDropTableContent = null;
	protected Insert		mStoreContentData = null;
	protected Delete		mDeleteContentData = null;
	protected Select		mRetrieveContent = null;
	protected Select		mRetrieveSize = null;
	protected Select		mHasContentData = null;

	public generic(Datasource datasource)
	{
		super(datasource);

		mCreateTableContent = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreText())
			.column("contentId",  int.class, CreateTable.NOTNULL)
			.column(getContentSizeColumnName(), int.class, CreateTable.NOTNULL)
			.column("content", String.class)
			.primaryKey(("PK_"+RifeConfig.Cmf.getTableContentStoreText()).toUpperCase(), "contentId")
			.foreignKey(("FK_"+RifeConfig.Cmf.getTableContentStoreText()).toUpperCase(), RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");

		mDropTableContent = new DropTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreText());
		
		mStoreContentData = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentStoreText())
			.fieldParameter("contentId")
			.fieldParameter(getContentSizeColumnName())
			.fieldParameter("content");
		
		mDeleteContentData = new Delete(getDatasource())
			.from(RifeConfig.Cmf.getTableContentStoreText())
			.whereParameter("contentId", "=");
		
		mRetrieveContent = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentStoreText())
			.field("content")
			.field(getContentSizeColumnName())
			.whereParameter("contentId", "=");
		
		mRetrieveSize = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentStoreText())
			.field(getContentSizeColumnName())
			.whereParameter("contentId", "=");

		mHasContentData = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentStoreText())
			.field("contentId")
			.whereParameter("contentId", "=")
			.whereAnd(getContentSizeColumnName(), "!=", 0);
	}
	
	public boolean install()
	throws ContentManagerException
	{
		return _install(mCreateTableContent);
	}
	
	public boolean remove()
	throws ContentManagerException
	{
		return _remove(mDropTableContent);
	}
	
	public boolean storeContentData(int id, Content content, ContentTransformer transformer)
	throws ContentManagerException
	{
		return _storeContentData(mStoreContentData, id, content, transformer);
	}
	
	public boolean deleteContentData(int id)
	throws ContentManagerException
	{
		return _deleteContentData(mDeleteContentData, id);
	}
	
	public <ResultType> ResultType useContentData(int id, ContentDataUser user)
	throws ContentManagerException
	{
		return (ResultType)_useContentData(mRetrieveContent, id, user);
	}
	
	public int getSize(int id)
	throws ContentManagerException
	{
		return _getSize(mRetrieveSize, id);
	}

	public boolean hasContentData(int id)
	throws ContentManagerException
	{
		return _hasContentData(mHasContentData, id);
	}

	public void serveContentData(ElementSupport element, int id)
	throws ContentManagerException
	{
		_serveContentData(mRetrieveContent, element, id);
	}
}
