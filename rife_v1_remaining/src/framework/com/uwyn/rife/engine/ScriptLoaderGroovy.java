/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ScriptLoaderGroovy.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.ElementImplementationInstantiationException;
import com.uwyn.rife.engine.exceptions.ElementImplementationNotFoundException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

class ScriptLoaderGroovy
{
	private ResourceFinder		mResourceFinder = null;
	private Map<String, Long>	mScriptsModification = new HashMap<String, Long>();
	private GroovyClassLoader	mGroovyClassloader = null;
	
	ScriptLoaderGroovy(ResourceFinder resourceFinder)
	{
		mResourceFinder = resourceFinder;
	}
		
	ElementAware getInstance(final ElementInfo elementInfo)
	throws EngineException
	{
		ClassLoader parent = getClass().getClassLoader();
		try
		{
			if (null == mGroovyClassloader)
			{
				mGroovyClassloader = new GroovyClassLoader(parent);
			}
			
			URL resource = mResourceFinder.getResource(elementInfo.getImplementation());
			long current_modification = -1;
			if (resource != null)
			{
				current_modification = mResourceFinder.getModificationTime(elementInfo.getImplementation());
			}
			else
			{
				resource = mResourceFinder.getResource(EngineClassLoader.DEFAULT_IMPLEMENTATIONS_PATH+elementInfo.getImplementation());
				current_modification = mResourceFinder.getModificationTime(EngineClassLoader.DEFAULT_IMPLEMENTATIONS_PATH+elementInfo.getImplementation());
			}
			if (null == resource)
			{
				throw new ElementImplementationNotFoundException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), null);
			}
			
			Long previous_modification = mScriptsModification.get(elementInfo.getImplementation());
			
			if (previous_modification != null &&
				previous_modification.longValue() != current_modification)
			{
				mGroovyClassloader = new GroovyClassLoader(parent);
			}
			
			Class groovyClass = mResourceFinder.useStream(resource, new InputStreamUser() {
					public Class useInputStream(final InputStream stream)
					throws InnerClassException
					{
						try
						{
							GroovyCodeSource gcs = (GroovyCodeSource) AccessController.doPrivileged(new PrivilegedAction() {
									public Object run() {
										return new GroovyCodeSource(stream, elementInfo.getImplementation(), "/groovy/script");
									}
								});
							gcs.setCachable(true);
							return mGroovyClassloader.parseClass(gcs);
						}
						catch (Throwable e)
						{
							throw new ElementImplementationInstantiationException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), e);
						}
					}
				});
			
			if (null == previous_modification ||
				previous_modification.longValue() != current_modification)
			{
				mScriptsModification.put(elementInfo.getImplementation(), current_modification);
			}
			
			return (ElementAware)groovyClass.newInstance();
		}
		catch (Throwable e)
		{
			throw new ElementImplementationInstantiationException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), e);
		}
	}
}
