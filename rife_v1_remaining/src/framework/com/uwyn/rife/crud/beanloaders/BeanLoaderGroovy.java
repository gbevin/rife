/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanLoaderGroovy.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.beanloaders;

import com.uwyn.rife.crud.exceptions.BeanImplementationInstantiationException;
import com.uwyn.rife.crud.exceptions.BeanImplementationNotFoundException;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import groovy.lang.GroovyClassLoader;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BeanLoaderGroovy
{
	private ResourceFinder		mResourceFinder = null;
	private Map<String, Long>	mScriptsModification = new HashMap<String, Long>();
	private GroovyClassLoader	mGroovyClassloader = null;
	
	public BeanLoaderGroovy(ResourceFinder resourceFinder)
	{
		mResourceFinder = resourceFinder;
	}
	
	public synchronized Class getClassInstance(final String implementation)
	throws ClassNotFoundException
	{
		ClassLoader parent = getClass().getClassLoader();
		try
		{
			if (null == mGroovyClassloader)
			{
				mGroovyClassloader = new GroovyClassLoader(parent);
			}
			
			URL resource = mResourceFinder.getResource(implementation);
			
			long current_modification = -1;
			if (resource != null)
			{
				current_modification = mResourceFinder.getModificationTime(implementation);
			}
			if (null == resource)
			{
				throw new BeanImplementationNotFoundException(implementation, null);
			}
			
			Long previous_modification = mScriptsModification.get(implementation);
			
			if (previous_modification != null &&
				previous_modification != current_modification)
			{
				mGroovyClassloader = new GroovyClassLoader(parent);
			}
			
			Class groovy_class = null;
			try
			{
				groovy_class = mResourceFinder.useStream(resource, new InputStreamUser() {
						public Class useInputStream(InputStream stream)
						throws InnerClassException
						{
							try
							{
								return mGroovyClassloader.parseClass(stream, implementation);
							}
							catch (Throwable e)
							{
								throwException(new BeanImplementationInstantiationException(implementation, e));
							}
							return null;
						}
					});
			}
			catch (InnerClassException e)
			{
				throw (ClassNotFoundException)e.getCause();
			}
			
			if (null == previous_modification ||
				previous_modification != current_modification)
			{
				mScriptsModification.put(implementation, current_modification);
			}
			
			return groovy_class;
		}
		catch (Throwable e)
		{
			throw new BeanImplementationInstantiationException(implementation, e);
		}
	}
}
