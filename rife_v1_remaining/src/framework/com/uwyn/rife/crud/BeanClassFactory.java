/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanClassFactory.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud;

import com.uwyn.rife.crud.beanloaders.BeanLoaderGroovy;
import com.uwyn.rife.crud.beanloaders.BeanLoaderJanino;
import com.uwyn.rife.crud.exceptions.BeanImplementationInstantiationException;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import java.net.URL;

public class BeanClassFactory
{
	public static final	BeanClassFactory	INSTANCE = new BeanClassFactory();
	
	private static BeanLoaderGroovy	mBeanLoaderGroovy = null;
	private static BeanLoaderJanino	mBeanLoaderJanino = null;
	
	private ResourceFinder	mResourceFinder = ResourceFinderClasspath.getInstance();
	
	private BeanClassFactory()
	{
	}
	
	public Class getClassInstance(final String implementation)
	throws ClassNotFoundException
	{
		if (null == implementation)	throw new IllegalArgumentException("implementation can't be null.");
		
		BeanClassType type = BeanClassType.getBeanType(implementation);
		
		if (BeanClassType.JAVA == type)
		{
			try
			{
				return Class.forName(implementation);
			}
			catch (Throwable e)
			{
				throw new BeanImplementationInstantiationException(implementation, e);
			}
		}
		// handle bean scripting framework scripts
		else if (BeanClassType.SCRIPT == type)
		{
			if (implementation.endsWith(BeanClassType.SCRIPT_EXT_GROOVY))
			{
				if (null == mBeanLoaderGroovy)
				{
					mBeanLoaderGroovy = new BeanLoaderGroovy(mResourceFinder);
				}
				
				return mBeanLoaderGroovy.getClassInstance(implementation);
			}
			else if (implementation.endsWith(BeanClassType.SCRIPT_EXT_JANINO))
			{
				if (null == mBeanLoaderJanino)
				{
					mBeanLoaderJanino = new BeanLoaderJanino(mResourceFinder);
				}
				
				return mBeanLoaderJanino.getClassInstance(implementation);
			}
		}

		throw new BeanImplementationInstantiationException(implementation, null);
	}
	
	public URL getClassResource(final String implementation)
	{
		if (null == implementation) return null;
		
		URL url = null;
		BeanClassType type = BeanClassType.getBeanType(implementation);
		
		if (BeanClassType.JAVA == type)
		{
			String beanclass_filename = implementation.replace('.','/')+".class";
			url = ResourceFinderClasspath.getInstance().getResource(beanclass_filename);
		}
		else if (BeanClassType.SCRIPT == type)
		{
			url = ResourceFinderClasspath.getInstance().getResource(implementation);
		}
		
		return url;
	}
}
