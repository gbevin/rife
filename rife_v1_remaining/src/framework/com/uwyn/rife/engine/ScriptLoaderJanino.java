/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ScriptLoaderJanino.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.ElementImplementationInstantiationException;
import com.uwyn.rife.engine.exceptions.ElementImplementationNotFoundException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.resources.ResourceFinder;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;

class ScriptLoaderJanino
{
	private ResourceFinder		mResourceFinder = null;
	private Map<String, Long>	mScriptsModification = new HashMap<String, Long>();
	private Map<String, Class>	mScriptsCache = new HashMap<String, Class>();
	
	ScriptLoaderJanino(ResourceFinder resourceFinder)
	{
		mResourceFinder = resourceFinder;
	}
		
	ElementAware getInstance(ElementInfo elementInfo)
	throws EngineException
	{
		try
		{
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
			
			String implementation = elementInfo.getImplementation();
			Long previous_modification = mScriptsModification.get(implementation);
			
			Class element_class = null;
			if (mScriptsCache.containsKey(implementation) &&
				(null == previous_modification ||
				 previous_modification.longValue() == current_modification))
			{
				element_class = mScriptsCache.get(implementation);
			}
			else
			{
				try
				{
					URL script_resource = ElementFactory.getScriptUrl(mResourceFinder, elementInfo);
					URLConnection connection = script_resource.openConnection();
					connection.setUseCaches(false);
					InputStream script_stream = connection.getInputStream();
					
					element_class = new ClassBodyEvaluator(new Scanner(null, script_stream), Element.class, new Class[0], getClass().getClassLoader()).getClazz();
				}
				catch (Throwable e)
				{
					throw new ElementImplementationInstantiationException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), e);
				}
				
				if (null == previous_modification ||
					previous_modification.longValue() != current_modification)
				{
					mScriptsModification.put(implementation, current_modification);
					mScriptsCache.put(implementation, element_class);
				}
			}
			
			return (ElementAware)element_class.newInstance();
		}
		catch (Throwable e)
		{
			throw new ElementImplementationInstantiationException(elementInfo.getDeclarationName(), elementInfo.getImplementation(), e);
		}
	}
}
