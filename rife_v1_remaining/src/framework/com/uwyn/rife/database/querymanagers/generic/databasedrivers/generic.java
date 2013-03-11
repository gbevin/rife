/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.databasedrivers;

import com.uwyn.rife.database.queries.*;
import com.uwyn.rife.database.querymanagers.generic.*;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbRowProcessor;
import com.uwyn.rife.database.exceptions.DatabaseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class generic<BeanType> extends AbstractGenericQueryManager<BeanType> implements GenericQueryManager<BeanType>
{
	private CreateTable 	mCreateTable = null;
	private CreateSequence 	mCreateSequence = null;
	private DropTable 		mDropTable = null;
	private DropSequence 	mDropSequence = null;

	private Select 			mRestore = null;
	private SequenceValue	mGetNextId = null;
	private Delete 			mDelete = null;
	private Delete 			mDeleteNoId = null;
	private Update 			mSaveUpdate = null;
	private Select 			mRestoreQuery = null;
	private Insert 			mSave = null;
	private Select 			mCount = null;
	
	protected String 		mTableName = null;
	protected String 		mPrimaryKey = null;
	protected boolean 		mHasIdentifier;
	
	public generic(Datasource datasource, String tableName, String primaryKey, Class<BeanType> beanClass, boolean hasIdentifier)
	throws DatabaseException
	{
		super(datasource, beanClass, primaryKey);
		
		mBaseClass = beanClass;
		mTableName = tableName;
		mPrimaryKey = primaryKey;
		mHasIdentifier = hasIdentifier;				
	}
	
	protected CreateTable getInternalCreateTableQuery()
	{
		if (null == mCreateTable)
		{
			final CreateTable query = new CreateTable(getDatasource())
				.table(mTableName)
				.columns(mBaseClass);
			if (!mHasIdentifier)
			{
				query.primaryKey(mPrimaryKey);
			}
			
			addCreateTableManyToOneColumns(query);
			
			mCreateTable = query;
		}
		
		return mCreateTable;
	}

	protected void addCreateTableManyToOneColumns(final CreateTable query)
	{
		final Map<String, CreateTable.Column> columns = query.getColumnMapping();
		GenericQueryManagerRelationalUtils.processManyToOneJoinColumns(this, new ManyToOneJoinColumnProcessor() {
				public boolean processJoinColumn(String columnName, String propertyName, ManyToOneDeclaration declaration)
				{
					if (!columns.containsKey(columnName))
					{
						query
							.column(columnName, int.class, CreateTable.NULL)
							.foreignKey(declaration.getAssociationTable(), columnName, declaration.getAssociationColumn());
					}
					return true;
				}
			});
	}
	
	protected CreateSequence getInternalCreateSequenceQuery()
	{
		if (null == mCreateSequence)
		{
			CreateSequence query = new CreateSequence(getDatasource())
				.name(getSequenceName());
			mCreateSequence = query;
		}
		
		return mCreateSequence;
	}
	
	protected String getSequenceName()
	{
		return "SEQ_"+mTableName;
	}
	
	protected DropTable getInternalDropTableQuery()
	{
		if (null == mDropTable)
		{
			DropTable query = new DropTable(getDatasource())
				.table(mTableName);
			mDropTable = query;
		}
		
		return mDropTable;
	}
	
	protected DropSequence getInternalDropSequenceQuery()
	{
		if (null == mDropSequence)
		{
			DropSequence query = new DropSequence(getDatasource())
				.name(getSequenceName());
			mDropSequence = query;
		}
		
		return mDropSequence;
	}
	
	protected Select getInternalRestoreByIdQuery()
	{
		if (null == mRestore)
		{
			Select query = new Select(getDatasource())
				.from(mTableName)
				.whereParameter(mPrimaryKey, "=");
			mRestore = query;
		}
		
		return mRestore;
	}
	
	protected SequenceValue getInternalGetNextIdQuery()
	{
		if (null == mGetNextId)
		{
			SequenceValue query = new SequenceValue(getDatasource())
				.name(getSequenceName())
				.next();
			mGetNextId = query;
		}
		
		return mGetNextId;
	}
	
	protected Delete getInternalDeleteQuery()
	{
		if (null == mDelete)
		{
			Delete query = new Delete(getDatasource())
				.from(mTableName)
				.whereParameter(mPrimaryKey, "=");
			mDelete = query;
		}
		
		return mDelete;
	}
	
	protected Delete getInternalDeleteNoIdQuery()
	{
		if (null == mDeleteNoId)
		{
			Delete query = new Delete(getDatasource())
				.from(mTableName);
			mDeleteNoId = query;
		}
		
		return mDeleteNoId;
	}
	
	protected Update getInternalSaveUpdateQuery()
	{
		if (null == mSaveUpdate)
		{
			final Update query = new Update(getDatasource())
				.table(mTableName)
				.fieldsParametersExcluded(mBaseClass, new String[] {mPrimaryKey})
				.whereParameter(mPrimaryKey, "=");
			
			addSaveUpdateManyToOneFields(query);
			
			mSaveUpdate = query;
		}
		
		return mSaveUpdate;
	}
	
	protected void addSaveUpdateManyToOneFields(final Update query)
	{
		final Set<String> columns = query.getFields().keySet();
		GenericQueryManagerRelationalUtils.processManyToOneJoinColumns(this, new ManyToOneJoinColumnProcessor() {
				public boolean processJoinColumn(String columnName, String propertyName, ManyToOneDeclaration declaration)
				{
					if (!columns.contains(columnName))
					{
						query.fieldParameter(columnName);
					}
					
					return true;
				}
			});
	}
	
	protected Select getInternalRestoreListQuery()
	{
		if (null == mRestoreQuery)
		{
			Select query = new Select(getDatasource(), getBaseClass())
				.from(mTableName);
			mRestoreQuery = query;
		}
		
		return mRestoreQuery;
	}
	
	protected Insert getInternalSaveQuery()
	{
		if (null == mSave)
		{
			final Insert query = new Insert(getDatasource())
				.into(mTableName)
				.fieldsParameters(getBaseClass());
			if (!query.getFields().containsKey(mPrimaryKey))
			{
				query.fieldParameter(mPrimaryKey);
			}

			addSaveManyToOneFields(query);
			
			mSave = query;
		}
		
		return mSave;
	}
	
	protected void addSaveManyToOneFields(final Insert query)
	{
		final Set<String> columns = query.getFields().keySet();
		GenericQueryManagerRelationalUtils.processManyToOneJoinColumns(this, new ManyToOneJoinColumnProcessor() {
				public boolean processJoinColumn(String columnName, String propertyName, ManyToOneDeclaration declaration)
				{
					if (!columns.contains(columnName))
					{
						query.fieldParameter(columnName);
					}
					
					return true;
				}
			});
	}
	
	protected Select getInternalCountQuery()
	{
		if (null == mCount)
		{
			Select query = new Select(getDatasource())
				.from(mTableName)
				.field("count(*)");
			mCount = query;
		}
		
		return mCount;
	}

	public void install()
	throws DatabaseException
	{
		_install(getInternalCreateSequenceQuery(), getInternalCreateTableQuery());
	}
	
	public void install(CreateTable query)
	throws DatabaseException
	{
		_install(getInternalCreateSequenceQuery(), query);
	}
	
	public int save(BeanType bean)
	throws DatabaseException
	{
		return _save(getInternalGetNextIdQuery(), getInternalSaveQuery(), getInternalSaveUpdateQuery(), bean);
	}
	
	public int insert(BeanType bean)
	throws DatabaseException
	{
		return _insert(getInternalGetNextIdQuery(), getInternalSaveQuery(), bean);
	}
	
	public int update(BeanType bean)
	throws DatabaseException
	{
		return _update(getInternalSaveUpdateQuery(), bean);
	}
	
	public boolean delete(DeleteQuery query)
	throws DatabaseException
	{
		return _delete(query.getDelegate());
	}
	
	public boolean delete(int objectId)
	throws DatabaseException
	{
		return _delete(getInternalDeleteQuery(), objectId);
	}
	
	public int count()
	throws DatabaseException
	{
		return _count(getInternalCountQuery());
	}
	
	public int count(CountQuery query)
	throws DatabaseException
	{
		return _count(query.getDelegate());
	}
	
	public BeanType restore(int objectId)
	throws DatabaseException
	{
		return _restore(getInternalRestoreByIdQuery(), objectId);
	}
	
	public List<BeanType> restore()
	throws DatabaseException
	{
		return _restore(getInternalRestoreListQuery());
	}
	
	public boolean restore(DbRowProcessor rowProcessor)
	throws DatabaseException
	{
		return _restore(getInternalRestoreListQuery(), rowProcessor);
	}
	
	public List<BeanType> restore(RestoreQuery query)
	throws DatabaseException
	{
		return _restore(query.getDelegate());
	}
	
	public boolean restore(RestoreQuery query, DbRowProcessor rowProcessor)
	throws DatabaseException
	{
		return _restore(query.getDelegate(), rowProcessor);
	}
	
	public BeanType restoreFirst(RestoreQuery query)
	throws DatabaseException
	{
		return _restoreFirst(query.getDelegate());
	}
	
	public void remove()
	throws DatabaseException
	{
		_remove(getInternalDropSequenceQuery(), getInternalDropTableQuery());
	}
	
	public CreateTable getInstallTableQuery()
	{
		return getInternalCreateTableQuery().clone();
	}
	
	public RestoreQuery getRestoreQuery()
	{
		return new RestoreQuery(getInternalRestoreListQuery());
	}
	
	public RestoreQuery getRestoreQuery(int objectId)
	{
		return new RestoreQuery(getInternalRestoreListQuery()).where(mPrimaryKey, "=", objectId);
	}
	
	public CountQuery getCountQuery()
	{
		return new CountQuery(getInternalCountQuery());
	}
	
	public DeleteQuery getDeleteQuery()
	{
		return new DeleteQuery(getInternalDeleteNoIdQuery());
	}
	
	public DeleteQuery getDeleteQuery(int objectId)
	{
		return new DeleteQuery(getInternalDeleteNoIdQuery()).where(mPrimaryKey, "=", objectId);
	}
	
	public String getTable()
	{
		return mTableName;
	}
}
