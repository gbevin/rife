/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineClassLoaderClasspath.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.tools.ClasspathUtils;
import java.io.File;

/**
 * Helper class to avoid Double Check Locking
 * and still have a thread-safe singleton pattern
 */
public class EngineClassLoaderClasspath
{
	public static final String	CLASSPATH;
	
	static
	{
		StringBuilder classpath = new StringBuilder(ClasspathUtils.getClassPath(EngineClassLoaderClasspath.class));
		
		// add the rife.webapp.path paths
		if (EngineClassLoaderRifeWebappPath.RIFE_WEBAPP_PATH != null)
		{
			for (String path : EngineClassLoaderRifeWebappPath.RIFE_WEBAPP_PATH)
			{
				classpath.append(File.pathSeparator);
				classpath.append(path);
			}
		}
		
		CLASSPATH = classpath.toString();
	}
}
