/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineClassLoaderRifeWebappPath.java 3957 2008-05-26 07:57:51Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.tools.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to avoid Double Check Locking
 * and still have a thread-safe singleton pattern
 */
public class EngineClassLoaderRifeWebappPath
{
	public static final ArrayList<String>	RIFE_WEBAPP_PATH;
	
	static
	{
		String rife_webapp_path = System.getProperty("rife.webapp.path");
		if (null == rife_webapp_path)
		{
			RIFE_WEBAPP_PATH = null;
		}
		else
		{
			String seperator = File.pathSeparator;
			if (-1 == rife_webapp_path.indexOf(File.pathSeparator))
			{
				seperator = ":";
			}
			List<String> path_elements = StringUtils.split(rife_webapp_path, seperator);
			RIFE_WEBAPP_PATH = new ArrayList<String>();
			
			File path = null;
			for (String path_element : path_elements)
			{
				path = new File(path_element);
				if (path.exists())
				{
					try
					{
						RIFE_WEBAPP_PATH.add(path.getCanonicalPath());
					}
					catch (IOException e)
					{
						// just skip this path id the canonical path can't be
						// constructed
					}
				}
			}
		}
	}
}
