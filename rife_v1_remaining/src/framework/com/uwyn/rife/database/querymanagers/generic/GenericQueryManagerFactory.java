/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GenericQueryManagerFactory.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManagerCache;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.exceptions.UnsupportedJdbcDriverException;
import com.uwyn.rife.database.querymanagers.generic.exceptions.MissingDefaultConstructorException;
import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.tools.ClassUtils;
import com.uwyn.rife.tools.StringUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class GenericQueryManagerFactory
{
	private final static String	GENERIC_DRIVER = "generic";
	
	private static DbQueryManagerCache	mCache = new DbQueryManagerCache();
	private static String				mPackageName = GenericQueryManagerFactory.class.getPackage().getName()+".databasedrivers.";
	
	public static <BeanType> GenericQueryManager<BeanType> getInstance(Datasource datasource, Class<BeanType> beanClass)
	throws DatabaseException
	{
		String short_name = ClassUtils.shortenClassName(beanClass);
		
		return getInstance(datasource, beanClass, short_name);
	}
	
	public static <BeanType> GenericQueryManager<BeanType> getInstance(Datasource datasource, Class<BeanType> beanClass, String tableName)
	throws DatabaseException
	{
		AbstractGenericQueryManager<BeanType>	query_manager = null;
		
		String driver = datasource.getAliasedDriver();
		
		try
		{
			beanClass.getConstructor(new Class[] {});
		}
		catch (NoSuchMethodException e)
		{
			throw new MissingDefaultConstructorException(beanClass, e);
		}
		
		// get the identifier column
		String primary_key = null;
		
		boolean has_identifier = false;
		Constrained constrained_bean = ConstrainedUtils.getConstrainedInstance(beanClass);
		if (constrained_bean != null)
		{
			for (ConstrainedProperty property : (Collection<ConstrainedProperty>)constrained_bean.getConstrainedProperties())
			{
				if (property.isIdentifier())
				{
					primary_key = property.getPropertyName();
					has_identifier = true;
					break;
				}
			}
		}
		
		if (null == primary_key)
		{
			primary_key = "id";
		}
		
		// check if the query manager wasn't cached before
		String cache_name = "GENERIC."+beanClass.getName()+"."+primary_key;
		
		query_manager = (AbstractGenericQueryManager<BeanType>)mCache.get(datasource, cache_name);
		
		if (query_manager != null)
		{
			return query_manager;
		}
		
		// construct the specialized driver class name
		StringBuilder specialized_name = new StringBuilder(mPackageName);
		specialized_name.append(StringUtils.encodeClassname(driver));
		
		try
		{
			try
			{
				Class<AbstractGenericQueryManager<BeanType>>		specialized_class = (Class<AbstractGenericQueryManager<BeanType>>)Class.forName(specialized_name.toString());
				Constructor<AbstractGenericQueryManager<BeanType>>	specialized_constructor = specialized_class.getConstructor(new Class[] {Datasource.class, String.class, String.class, Class.class, boolean.class});
	
				query_manager = specialized_constructor.newInstance(new Object[] {datasource, tableName, primary_key, beanClass, Boolean.valueOf(has_identifier)});
			}
			catch (ClassNotFoundException e)
			{
				// could not find a specialized class, try to get a generic driver
				try
				{
					// construct the generic driver class name
					StringBuilder generic_name = new StringBuilder(mPackageName);
					generic_name.append(GENERIC_DRIVER);
					
					Class<AbstractGenericQueryManager<BeanType>>		generic_class = (Class<AbstractGenericQueryManager<BeanType>>)Class.forName(generic_name.toString());
					Constructor<AbstractGenericQueryManager<BeanType>>	generic_constructor = generic_class.getConstructor(new Class[] {Datasource.class, String.class, String.class, Class.class, boolean.class});

					query_manager = generic_constructor.newInstance(new Object[] {datasource, tableName, primary_key, beanClass, Boolean.valueOf(has_identifier)});
				}
				catch (ClassNotFoundException e2)
				{
					throw new UnsupportedJdbcDriverException(driver, e);
				}
			}
		}
		catch (InstantiationException e)
		{
			throw new UnsupportedJdbcDriverException(driver, e);
		}
		catch (IllegalAccessException e)
		{
			throw new UnsupportedJdbcDriverException(driver, e);
		}
		catch (NoSuchMethodException e)
		{
			throw new UnsupportedJdbcDriverException(driver, e);
		}
		catch (SecurityException e)
		{
			throw new UnsupportedJdbcDriverException(driver, e);
		}
		catch (InvocationTargetException e)
		{
			if (e.getTargetException() != null)
			{
				throw new RuntimeException(e.getTargetException());
			}
			else
			{
				throw new UnsupportedJdbcDriverException(driver, e);
			}
		}
		
		return query_manager;
	}
}
