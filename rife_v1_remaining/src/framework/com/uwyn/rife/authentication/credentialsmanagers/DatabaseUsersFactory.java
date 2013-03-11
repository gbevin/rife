/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseUsersFactory.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManagerCache;
import com.uwyn.rife.database.DbQueryManagerFactory;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;

/**
 * Factory for {@link DatabaseUsers} manager instances that creates singletons
 * based on the {@code Datasource} and an optional differentiating
 * identifier.
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class DatabaseUsersFactory extends DbQueryManagerFactory implements CredentialsManagerFactory
{
	/** The package name of the datasource-specific implementations */
	public static final String	MANAGER_PACKAGE_NAME = DatabaseUsersFactory.class.getPackage().getName()+".databasedrivers.";
	
	private static DbQueryManagerCache	mCache = new DbQueryManagerCache();
	
	/**
	 * Return an instance of {@code DatabaseUsers} for the provided
	 * {@code Datasource}.
	 *
	 * @param datasource the datasource that will be used to create the manager
	 * @return the requested {@code DatabaseUsers} instance
	 * @since 1.0
	 */
	public static DatabaseUsers getInstance(Datasource datasource)
	{
		return getInstance(datasource, null);
	}

	/**
	 * Return an instance of {@code DatabaseUsers} for the provided
	 * {@code Datasource} and identifier.
	 *
	 * @param datasource the datasource that will be used to create the manager
	 * @param identifier the identifier that will be used to differentiate the
	 * manager when different ones are needed for the same datasource
	 * @return the requested {@code DatabaseUsers} instance
	 * @since 1.0
	 */
	public static DatabaseUsers getInstance(Datasource datasource, String identifier)
	{
		if (null == identifier) identifier = "";
		
		return (DatabaseUsers)getInstance(MANAGER_PACKAGE_NAME, mCache, datasource, identifier);
	}
	
	public CredentialsManager getCredentialsManager(HierarchicalProperties properties)
	throws PropertyValueException
	{
		if (!properties.contains("datasource"))
		{
			throw new MandatoryPropertyMissingException("datasource");
		}
		
		return getInstance(properties.getValueTyped("datasource", Datasource.class),
				properties.getValueString("credentialsmanager_id", ""));
	}
}
