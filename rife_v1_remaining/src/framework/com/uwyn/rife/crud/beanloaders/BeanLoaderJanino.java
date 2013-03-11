/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanLoaderJanino.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.beanloaders;

import com.uwyn.rife.crud.BeanClassType;
import com.uwyn.rife.crud.exceptions.BeanImplementationInstantiationException;
import com.uwyn.rife.crud.exceptions.BeanImplementationNotFoundException;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.tools.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.SimpleCompiler;

public class BeanLoaderJanino
{
	private ResourceFinder			mResourceFinder = null;
	private HashMap<String, Long>	mScriptsModification = new HashMap<String, Long>();
	private HashMap<String, Class>	mScriptsCache = new HashMap<String, Class>();
	
	private class JaninoClassCompiler
	{
		private Class mClass = null;
		
		public JaninoClassCompiler(String className, Scanner scanner, ClassLoader optionalParentClassLoader)
		throws CompileException, Parser.ParseException, Scanner.ScanException, IOException
		{			
			SimpleCompiler compiler = new SimpleCompiler();
			compiler.setParentClassLoader(optionalParentClassLoader);
			compiler.cook(scanner);
			try
			{
				mClass = compiler.getClassLoader().loadClass(className);
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		public Class evaluate()
		{
			return mClass;
		}
	}
	
	public BeanLoaderJanino(ResourceFinder resourceFinder)
	{
		mResourceFinder = resourceFinder;
	}
	
	public synchronized Class getClassInstance(final String implementation)
	throws ClassNotFoundException
	{
		try
		{
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
			
			Class bean_class = null;
			if (mScriptsCache.containsKey(implementation) &&
				(null == previous_modification ||
				 previous_modification == current_modification))
			{
				bean_class = mScriptsCache.get(implementation);
			}
			else
			{
				try
				{
					URL	script_resource = mResourceFinder.getResource(implementation);
					if (null == script_resource)
					{
						throw new BeanImplementationNotFoundException(implementation, null);
					}
					
					// get the classname
					String classname = StringUtils.replace(implementation.substring(0, implementation.length()-BeanClassType.SCRIPT_EXT_JANINO.length()), "/", ".");
					
					// create the input stream
					URLConnection connection = script_resource.openConnection();
					connection.setUseCaches(false);
					InputStream script_stream = connection.getInputStream();
					
					bean_class = new JaninoClassCompiler(classname, new Scanner(implementation, script_stream), getClass().getClassLoader()).evaluate();
				}
				catch (Throwable e)
				{
					throw new BeanImplementationInstantiationException(implementation, e);
				}
				
				if (null == previous_modification ||
					previous_modification != current_modification)
				{
					mScriptsModification.put(implementation, current_modification);
					mScriptsCache.put(implementation, bean_class);
				}
			}
			
			return bean_class;
		}
		catch (Throwable e)
		{
			throw new BeanImplementationInstantiationException(implementation, e);
		}
	}
}
