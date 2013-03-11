/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GenericQueryManagerDelegate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbRowProcessor;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.site.Validated;
import java.util.List;

public class GenericQueryManagerDelegate<T> implements GenericQueryManager<T>
{
	private Datasource 				mDatasource = null;
	private GenericQueryManager<T> 	mDelegate = null;
	
	public GenericQueryManagerDelegate(Datasource datasource, Class<T> klass, String table)
	{
		mDatasource = datasource;
		mDelegate = GenericQueryManagerFactory.getInstance(datasource, klass, table);
	}
	
	public GenericQueryManagerDelegate(Datasource datasource, Class<T> klass)
	{
		mDatasource = datasource;
		mDelegate = GenericQueryManagerFactory.getInstance(datasource, klass);
	}
	
	public Datasource getDatasource()
	{
		return mDatasource;
	}
	
	public GenericQueryManager<T> getDelegate()
	{
		return mDelegate;
	}
	
	public Class getBaseClass()
	{
		return mDelegate.getBaseClass();
	}

	public String getIdentifierName()
	throws DatabaseException
	{
		return mDelegate.getIdentifierName();
	}

	public int getIdentifierValue(T bean)
	throws DatabaseException
	{
		return mDelegate.getIdentifierValue(bean);
	}
	
	public void validate(Validated validated)
	{
		mDelegate.validate(validated);
	}
	
	public String getTable()
	{
		return mDelegate.getTable();
	}
	
	public void install()
	throws DatabaseException
	{
		mDelegate.install();
	}
	
	public void install(CreateTable query)
	throws DatabaseException
	{
		mDelegate.install(query);
	}
	
	public void remove()
	throws DatabaseException
	{
		mDelegate.remove();
	}
	
	public int save(T bean)
	throws DatabaseException
	{
		return mDelegate.save(bean);
	}
	
	public int insert(T bean)
	throws DatabaseException
	{
		return mDelegate.insert(bean);
	}
	
	public int update(T bean)
	throws DatabaseException
	{
		return mDelegate.update(bean);
	}
	
	public List<T> restore()
	throws DatabaseException
	{
		return mDelegate.restore();
	}
	
	public T restore(int objectId)
	throws DatabaseException
	{
		return mDelegate.restore(objectId);
	}
	
	public List<T> restore(RestoreQuery query)
	throws DatabaseException
	{
		return mDelegate.restore(query);
	}
	
	public boolean restore(DbRowProcessor rowProcessor)
	throws DatabaseException
	{
		return mDelegate.restore(rowProcessor);
	}
	
	public T restoreFirst(RestoreQuery query)
	throws DatabaseException
	{
		return mDelegate.restoreFirst(query);
	}
	
	public boolean restore(RestoreQuery query, DbRowProcessor rowProcessor)
	throws DatabaseException
	{
		return mDelegate.restore(query, rowProcessor);
	}
	
	public CreateTable getInstallTableQuery()
	throws DatabaseException
	{
		return mDelegate.getInstallTableQuery();
	}
	
	public RestoreQuery getRestoreQuery()
	{
		return mDelegate.getRestoreQuery();
	}
	
	public RestoreQuery getRestoreQuery(int objectId)
	{
		return mDelegate.getRestoreQuery(objectId);
	}
	
	public int count()
	throws DatabaseException
	{
		return mDelegate.count();
	}
	
	public int count(CountQuery query)
	throws DatabaseException
	{
		return mDelegate.count(query);
	}
	
	public CountQuery getCountQuery()
	{
		return mDelegate.getCountQuery();
	}
	
	public boolean delete(int objectId)
	throws DatabaseException
	{
		return mDelegate.delete(objectId);
	}
	
	public boolean delete(DeleteQuery query)
	throws DatabaseException
	{
		return mDelegate.delete(query);
	}
	
	public DeleteQuery getDeleteQuery()
	{
		return mDelegate.getDeleteQuery();
	}
	
	public DeleteQuery getDeleteQuery(int objectId)
	{
		return mDelegate.getDeleteQuery(objectId);
	}
	
	public void addListener(GenericQueryManagerListener listener)
	{
		mDelegate.addListener(listener);
	}
	
	public void removeListeners()
	{
		mDelegate.removeListeners();
	}

	public <OtherBeanType> GenericQueryManager<OtherBeanType> createNewManager(Class<OtherBeanType> type)
	{
		return mDelegate.createNewManager(type);
	}
}

