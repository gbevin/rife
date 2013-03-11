/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DbQueryManagerCache.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManager;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a simple cache for {@link DbQueryManager} objects. {@link
 * DbQueryManager} objects are cached by their related {@link Datasource} and
 * an identifier.
 * 
 * @author JR Boyens (jboyens[remove] at uwyn dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class DbQueryManagerCache
{
	private final Map<Datasource, HashMap<String, DbQueryManager>>	mCache = new HashMap<Datasource, HashMap<String, DbQueryManager>>();

	/**
	 * Default constructor
	 * 
	 * @since 1.0
	 */
	public DbQueryManagerCache()
	{
	}
	
	/**
	 * Retrieve a cached {@link DbQueryManager}
	 * 
	 * @param datasource the {@link Datasource} associated with the
	 * desired {@link DbQueryManager}
	 * @param identifier the identifier associate with the desired {@link
	 * DbQueryManager}
	 * @return the cached {@link DbQueryManager}
	 * @since 1.0
	 */
	public DbQueryManager get(Datasource datasource, String identifier)
	{
		if (null == datasource) throw new IllegalArgumentException("datasource can't be null.");
		if (null == identifier) throw new IllegalArgumentException("identifier can't be null.");
		
		HashMap<String, DbQueryManager> dbquery_managers = null;
		synchronized (mCache)
		{
			dbquery_managers = mCache.get(datasource);
			if (null == dbquery_managers)
			{
				return null;
			}
		}
		
		synchronized (dbquery_managers)
		{
			return dbquery_managers.get(identifier);
		}
	}
	
	/**
	 * Place a {@link DbQueryManager} in the cache
	 * 
	 * @param datasource the {@link Datasource} associated with the {@link
	 * DbQueryManager} to put in the cache
	 * @param identifier the identifier associated with the {@link
	 * DbQueryManager} to put in the cache
	 * @param dbQueryManager the {@link DbQueryManager} to put in the
	 * cache
	 * @since 1.0
	 */
	public void put(Datasource datasource, String identifier, DbQueryManager dbQueryManager)
	{
		if (null == datasource)         throw new IllegalArgumentException("datasource can't be null.");
		if (null == identifier)         throw new IllegalArgumentException("identifier can't be null.");
		if (null == dbQueryManager)     throw new IllegalArgumentException("dbQueryManager can't be null.");
		
		HashMap<String, DbQueryManager> dbquery_managers = null;
		
		synchronized (mCache)
		{
			dbquery_managers = mCache.get(datasource);
	
			if (null == dbquery_managers)
			{
				dbquery_managers = new HashMap<String, DbQueryManager>();
				mCache.put(datasource, dbquery_managers);
			}
		}
		
		synchronized (dbquery_managers)
		{
			dbquery_managers.put(identifier, dbQueryManager);
		}
		
		assert mCache.containsKey(datasource);
		assert mCache.get(datasource).containsKey(identifier);
		assert mCache.get(datasource).get(identifier) == dbQueryManager;
	}
}
