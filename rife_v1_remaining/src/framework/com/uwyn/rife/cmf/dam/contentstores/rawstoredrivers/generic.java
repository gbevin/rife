/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentstores.rawstoredrivers;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.contentstores.DatabaseRawStore;
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
import java.sql.Blob;

public class generic extends DatabaseRawStore
{
	protected CreateTable		mCreateTableContentInfo = null;
	protected CreateTable		mCreateTableContentChunk = null;
	protected DropTable			mDropTableContentInfo = null;
	protected DropTable			mDropTableContentChunk = null;
	protected Insert			mStoreContentInfo = null;
	protected Delete			mDeleteContentInfo = null;
	protected Select			mRetrieveSize = null;
	protected Select			mHasContentData = null;
	protected Insert			mStoreContentChunk = null;
	protected Delete			mDeleteContentChunk = null;
	protected Select			mRetrieveContentChunks = null;

	public generic(Datasource datasource)
	{
		super(datasource);

		mCreateTableContentInfo = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreRawInfo())
			.column("contentId", int.class, CreateTable.NOTNULL)
			.column(getContentSizeColumnName(), int.class, CreateTable.NOTNULL)
			.primaryKey(("PK_"+RifeConfig.Cmf.getTableContentStoreRawInfo()).toUpperCase(), "contentId")
			.foreignKey(("FK_"+RifeConfig.Cmf.getTableContentStoreRawInfo()).toUpperCase(), RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");

		mCreateTableContentChunk = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreRawChunk())
			.column("contentId", int.class, CreateTable.NOTNULL)
			.column("ordinal", int.class, CreateTable.NOTNULL)
			.column("chunk", Blob.class)
			.primaryKey(("PK_"+RifeConfig.Cmf.getTableContentStoreRawChunk()).toUpperCase(), new String[] {"contentId", "ordinal"})
			.foreignKey(("FK_"+RifeConfig.Cmf.getTableContentStoreRawChunk()).toUpperCase(), RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");

		mDropTableContentInfo = new DropTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreRawInfo());

		mDropTableContentChunk = new DropTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentStoreRawChunk());

		mStoreContentInfo = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentStoreRawInfo())
			.fieldParameter("contentId")
			.fieldParameter(getContentSizeColumnName());

		mDeleteContentInfo = new Delete(getDatasource())
			.from(RifeConfig.Cmf.getTableContentStoreRawInfo())
			.whereParameter("contentId", "=");

		mRetrieveSize = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentStoreRawInfo())
			.field(getContentSizeColumnName())
			.whereParameter("contentId", "=");

		mHasContentData = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentStoreRawInfo())
			.field("contentId")
			.whereParameter("contentId", "=")
			.whereAnd(getContentSizeColumnName(), "!=", 0);

		mStoreContentChunk = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentStoreRawChunk())
			.fieldParameter("contentId")
			.fieldParameter("ordinal")
			.fieldParameter("chunk");

		mDeleteContentChunk = new Delete(getDatasource())
			.from(RifeConfig.Cmf.getTableContentStoreRawChunk())
			.whereParameter("contentId", "=");

		mRetrieveContentChunks = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentStoreRawChunk())
			.field("chunk")
			.whereParameter("contentId", "=")
			.orderBy("ordinal");
	}
	
	public boolean install()
	throws ContentManagerException
	{
		return _install(mCreateTableContentInfo, mCreateTableContentChunk);
	}
	
	public boolean remove()
	throws ContentManagerException
	{
		return _remove(mDropTableContentInfo, mDropTableContentChunk);
	}
	
	public boolean storeContentData(int id, Content content, ContentTransformer transformer)
	throws ContentManagerException
	{
		return _storeContentData(mStoreContentInfo, mStoreContentChunk, id, content, transformer);
	}
	
	public boolean deleteContentData(int id)
	throws ContentManagerException
	{
		return _deleteContentData(mDeleteContentInfo, mDeleteContentChunk, id);
	}
	
	public <ResultType> ResultType useContentData(int id, ContentDataUser user)
	throws ContentManagerException
	{
		return (ResultType)_useContentData(mRetrieveContentChunks, id, user);
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
		_serveContentData(mRetrieveContentChunks, element, id);
	}
}
