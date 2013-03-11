/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam.contentmanagers.databasedrivers;

import com.uwyn.rife.database.queries.*;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.ContentRepository;
import com.uwyn.rife.cmf.dam.ContentDataUser;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContent;
import com.uwyn.rife.cmf.dam.contentmanagers.DatabaseContentInfo;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.InstallContentErrorException;
import com.uwyn.rife.cmf.dam.contentmanagers.exceptions.RemoveContentErrorException;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.engine.ElementSupport;

public class generic extends DatabaseContent
{
	protected CreateSequence	mCreateSequenceContentRepository = null;
	protected CreateSequence	mCreateSequenceContentInfo = null;
	protected CreateTable		mCreateTableContentRepository = null;
	protected CreateTable		mCreateTableContentInfo = null;
	protected CreateTable		mCreateTableContentAttribute = null;
	protected CreateTable		mCreateTableContentProperty = null;
	protected String			mCreateContentInfoPathIndex = null;
	protected String			mCreateContentInfoPathNameIndex = null;
	protected DropSequence		mDropSequenceContentRepository = null;
	protected DropSequence		mDropSequenceContentInfo = null;
	protected DropTable			mDropTableContentRepository = null;
	protected DropTable			mDropTableContentInfo = null;
	protected DropTable			mDropTableContentAttribute = null;
	protected DropTable			mDropTableContentProperties = null;
	protected String			mDropContentInfoPathIndex = null;
	protected String			mDropContentInfoPathNameIndex = null;
	protected SequenceValue		mGetNewContentRepositoryId = null;
	protected SequenceValue		mGetNewContentId = null;
	protected Select			mGetContentRepositoryId = null;
	protected Select			mGetContentInfo = null;
	protected Select			mGetVersion = null;
	protected Insert			mStoreContentRepository = null;
	protected Select			mContainsContentRepository = null;
	protected Insert			mStoreContentInfo = null;
	protected Insert			mStoreContentAttribute = null;
	protected Insert			mStoreContentProperty = null;
	protected Delete			mDeleteContentInfo = null;
	protected Delete			mDeleteContentAttributes = null;
	protected Delete			mDeleteContentProperties = null;
	protected Select			mGetLatestContentInfo = null;
	protected Select			mGetContentAttributes = null;
	protected Select			mGetContentProperties = null;

	public generic(Datasource datasource)
	{
		super(datasource);

		mCreateSequenceContentRepository = new CreateSequence(getDatasource())
			.name(RifeConfig.Cmf.getSequenceContentRepository());

		mCreateSequenceContentInfo = new CreateSequence(getDatasource())
			.name(RifeConfig.Cmf.getSequenceContentInfo());

		mCreateTableContentRepository = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentRepository())
			.columns(ContentRepository.class)
			.column("repositoryId", int.class)
			.primaryKey("PK_"+RifeConfig.Cmf.getTableContentRepository(), "repositoryId");

		mCreateTableContentInfo = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentInfo())
			.columns(DatabaseContentInfo.class)
			.column("repositoryId", int.class, CreateTable.NOTNULL)
			.defaultFunction("created", "CURRENT_TIMESTAMP")
			.unique(("UQ_"+RifeConfig.Cmf.getTableContentInfo()).toUpperCase(), new String[] {"repositoryId", "path", "version"})
			.foreignKey("FK_"+RifeConfig.Cmf.getTableContentInfo()+"_REPOSITORYID", RifeConfig.Cmf.getTableContentRepository(), "repositoryId", "repositoryId");

		mCreateTableContentAttribute = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentAttribute())
			.column("contentId", int.class, CreateTable.NOTNULL)
			.column("name", String.class, 255, CreateTable.NOTNULL)
			.column(getValueColumnName(), String.class, 255, CreateTable.NOTNULL)
			.foreignKey(("FK_"+RifeConfig.Cmf.getTableContentAttribute()).toUpperCase(), RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");

		mCreateTableContentProperty = new CreateTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentProperty())
			.column("contentId", int.class, CreateTable.NOTNULL)
			.column("name", String.class, 255, CreateTable.NOTNULL)
			.column(getValueColumnName(), String.class, 255, CreateTable.NOTNULL)
			.foreignKey(("FK_"+RifeConfig.Cmf.getTableContentProperty()).toUpperCase(), RifeConfig.Cmf.getTableContentInfo(), "contentId", "contentId");
	
		mCreateContentInfoPathIndex = "CREATE INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_path ON "+RifeConfig.Cmf.getTableContentInfo()+" (path)";
		mCreateContentInfoPathNameIndex = "CREATE INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_pathname ON "+RifeConfig.Cmf.getTableContentInfo()+" (path, name)";

		mDropSequenceContentRepository = new DropSequence(getDatasource())
			.name(RifeConfig.Cmf.getSequenceContentRepository());

		mDropSequenceContentInfo = new DropSequence(getDatasource())
			.name(RifeConfig.Cmf.getSequenceContentInfo());

		mDropTableContentRepository = new DropTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentRepository());

		mDropTableContentInfo = new DropTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentInfo());

		mDropTableContentAttribute = new DropTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentAttribute());

		mDropTableContentProperties = new DropTable(getDatasource())
			.table(RifeConfig.Cmf.getTableContentProperty());

		mDropContentInfoPathIndex = "DROP INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_path";

		mDropContentInfoPathNameIndex = "DROP INDEX "+RifeConfig.Cmf.getTableContentInfo()+"_pathname";

		mGetNewContentRepositoryId = new SequenceValue(getDatasource())
			.name(RifeConfig.Cmf.getSequenceContentRepository())
			.next();
		
		mGetNewContentId = new SequenceValue(getDatasource())
			.name(RifeConfig.Cmf.getSequenceContentInfo())
			.next();
		
		mGetContentRepositoryId = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentRepository())
			.field("repositoryId")
			.whereParameter("name", "repository", "=");

		mGetVersion = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentInfo())
			.field("COALESCE(MAX(version)+1, 0)")
			.whereParameter("repositoryId", "=")
			.whereParameterAnd("path", "=");
			
		mGetContentInfo = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentInfo())
			.join(RifeConfig.Cmf.getTableContentRepository())
			.field(RifeConfig.Cmf.getTableContentInfo()+".*")
			.where(RifeConfig.Cmf.getTableContentInfo()+".repositoryId = "+RifeConfig.Cmf.getTableContentRepository()+".repositoryId")
			.whereParameter("path", "=")
			.whereParameterAnd(RifeConfig.Cmf.getTableContentRepository()+".name", "repository", "=")
			.orderBy("version", Select.DESC);
			
		mStoreContentRepository = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentRepository())
			.fieldsParameters(ContentRepository.class)
			.fieldParameter("repositoryId");
		
		mContainsContentRepository = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentRepository())
			.field("count(1)")
			.whereParameter("name", "=");
		
		mStoreContentInfo = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentInfo())
			.fieldsParameters(DatabaseContentInfo.class)
			.fieldParameter("repositoryId")
			.field("version", mGetVersion);

		mStoreContentAttribute = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentAttribute())
			.fieldParameter("contentId")
			.fieldParameter("name")
			.fieldParameter(getValueColumnName());

		mStoreContentProperty = new Insert(getDatasource())
			.into(RifeConfig.Cmf.getTableContentProperty())
			.fieldParameter("contentId")
			.fieldParameter("name")
			.fieldParameter(getValueColumnName());
		
		mDeleteContentInfo = new Delete(getDatasource())
			.from(RifeConfig.Cmf.getTableContentInfo())
			.whereParameter("contentId", "=");
		
		mDeleteContentAttributes = new Delete(getDatasource())
			.from(RifeConfig.Cmf.getTableContentAttribute())
			.whereParameter("contentId", "=");
		
		mDeleteContentProperties = new Delete(getDatasource())
			.from(RifeConfig.Cmf.getTableContentProperty())
			.whereParameter("contentId", "=");

		mGetLatestContentInfo = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentInfo())
			.join(RifeConfig.Cmf.getTableContentRepository())
			.field(RifeConfig.Cmf.getTableContentInfo()+".*")
			.where(RifeConfig.Cmf.getTableContentInfo()+".repositoryId = "+RifeConfig.Cmf.getTableContentRepository()+".repositoryId")
			.whereParameterAnd(RifeConfig.Cmf.getTableContentRepository()+".name", "repository", "=")
			.startWhereAnd()
				.whereParameter("path", "=")
				.startWhereOr()
					.whereParameter("path", "pathpart", "=")
					.whereParameterAnd(RifeConfig.Cmf.getTableContentInfo()+".name", "namepart", "=")
				.end()
			.end()
			.orderBy("version", Select.DESC)
			.limit(1);
		
		mGetContentAttributes = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentAttribute())
			.field("contentId")
			.field("name")
			.field(getValueColumnName())
			.whereParameter("contentId", "=");
		
		mGetContentProperties = new Select(getDatasource())
			.from(RifeConfig.Cmf.getTableContentProperty())
			.field("contentId")
			.field("name")
			.field(getValueColumnName())
			.whereParameter("contentId", "=");
	}
	
	public boolean install()
	throws ContentManagerException
	{
		boolean result = _install(mCreateSequenceContentRepository, mCreateSequenceContentInfo,
								  mCreateTableContentRepository, mCreateTableContentInfo, mCreateTableContentAttribute, mCreateTableContentProperty);
		try
		{
			executeUpdate(mCreateContentInfoPathIndex);
			executeUpdate(mCreateContentInfoPathNameIndex);
		}
		catch (DatabaseException e)
		{
			throw new InstallContentErrorException(e);
		}
		return result;
	}
	
	public boolean remove()
	throws ContentManagerException
	{
		try
		{
			executeUpdate(mDropContentInfoPathNameIndex);
			executeUpdate(mDropContentInfoPathIndex);
		}
		catch (DatabaseException e)
		{
			throw new RemoveContentErrorException(e);
		}
		return _remove(mDropSequenceContentRepository, mDropSequenceContentInfo,
					   mDropTableContentRepository, mDropTableContentInfo, mDropTableContentAttribute, mDropTableContentProperties);
	}
	
	public boolean createRepository(String name)
	throws ContentManagerException
	{
		return _createRepository(mGetNewContentRepositoryId, mStoreContentRepository, name);
	}
	
	public boolean containsRepository(String name)
	throws ContentManagerException
	{
		return _containsRepository(mContainsContentRepository, name);
	}
	
	public boolean storeContent(String location, Content content, ContentTransformer transformer)
	throws ContentManagerException
	{
		return _storeContent(mGetNewContentId, mGetContentRepositoryId, mStoreContentInfo, mStoreContentAttribute, mStoreContentProperty, location, content, transformer);
	}
	
	public boolean deleteContent(String location)
	throws ContentManagerException
	{
		return _deleteContent(mGetContentInfo, mDeleteContentInfo, mDeleteContentAttributes, mDeleteContentProperties, location);
	}

	public <ResultType> ResultType useContentData(String location, ContentDataUser user)
	throws ContentManagerException
	{
		return (ResultType)_useContentData(mGetLatestContentInfo, location, user);
	}
	
	public boolean hasContentData(String location)
	throws ContentManagerException
	{
		return _hasContentData(mGetLatestContentInfo, location);
	}
	
	public void serveContentData(ElementSupport element, String location)
	throws ContentManagerException
	{
		_serveContentData(element, location);
	}

	public DatabaseContentInfo getContentInfo(String location)
	throws ContentManagerException
	{
		return _getContentInfo(mGetLatestContentInfo, mGetContentAttributes, mGetContentProperties, location);
	}
	
	public String getContentForHtml(String location, ElementSupport element, String serveContentExitName)
	throws ContentManagerException
	{
		return _getContentForHtml(location, element, serveContentExitName);
	}
}
