/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CrudContentQueryManager.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.dam;

import com.uwyn.rife.cmf.dam.ContentQueryManager;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.ExceptionUtils;
import java.net.URL;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class CrudContentQueryManager<T> extends ContentQueryManager<T>
{
	private Preferences	mPreferences = null;
	
	public CrudContentQueryManager(Datasource datasource, Class<T> klass)
	{
		super(datasource, klass);
		
		mPreferences = Preferences.userNodeForPackage(this.getClass());
	}

	public void install()
	throws DatabaseException
	{
		super.install();
		
		long modification_time = getBaseClassModificationTime();
		if (modification_time!= -1)
		{
			mPreferences.putLong(getBaseClass().getName(), modification_time);
		}
	}
	
	public boolean isStructureOutdated()
	{
		long current = getBaseClassModificationTime();
		if (-1 == current)
		{
			return false;
		}
		
		long previous = mPreferences.getLong(getBaseClass().getName(), Long.MAX_VALUE);
		return previous < current;
	}

	public void remove()
	throws DatabaseException
	{
		super.remove();
		
		mPreferences.remove(getBaseClass().getName());
	}
	
	private long getBaseClassModificationTime()
	{
		long	result = -1;
		
		Class	baseclass = getBaseClass();
		String	baseclass_filename = baseclass.getName().replace('.','/')+".class";
		
		ResourceFinder resource_finder = ResourceFinderClasspath.getInstance();
		URL baseclass_url = resource_finder.getResource(baseclass_filename);
		if (baseclass_url != null &&
			!baseclass_url.getProtocol().equals("jar"))
		{
			try
			{
				result = ResourceFinderClasspath.getInstance().getModificationTime(baseclass_url);
			}
			catch (ResourceFinderErrorException e)
			{
				Logger.getLogger("com.uwyn.rife.crud").warning("An error occurred while trying to obtain the modification time of the file '"+baseclass_filename+"': "+ExceptionUtils.getExceptionStackTrace(e));
			}
		}
		
		return result;
	}
}
