/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XmlSelectorResolver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.selector;

import com.uwyn.rife.resources.ResourceFinder;
import java.net.URL;

/**
 * Looks up XML configuration file locations based on participant parameters.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class XmlSelectorResolver
{
	/**
	 * Returns an XML file location.
	 * 
	 * @param parameter
	 * 	Parameter of the participant for this configuration file. This is a
	 * 	comma-separated list of path names and XML selector class names;
	 *  they are tried in order until one of them resolves to a resource
	 *  that exists.
	 * @param resourceFinder
	 * 	Object that looks up resources on the classpath.
	 * @param prefix
	 * 	Configuration file prefix; or
	 * <p><code>null</code> if none is needed
	 * @since 1.0
	 */
	public static String resolve(String parameter, ResourceFinder resourceFinder, String prefix)
	{
		if (null == parameter)			throw new IllegalArgumentException("xmlPath can't be null.");
		if (0 == parameter.length())	throw new IllegalArgumentException("xmlPath can't be empty.");
		if (null == resourceFinder)		throw new IllegalArgumentException("resourceFinder can't be null.");
		
		String	result = null;
		
		URL resource = null;
		for (String resourceName : parameter.split(","))
		{
			resource = resourceFinder.getResource(resourceName);

			// check if the xml file could be found
			if (resource != null)
			{
				result = resourceName;
				break;
			}
			else
			{
				// it could not be found, try to see if it was a class name of a XmlSelector
				// that is present in the classpath
				Class	klass = null;
				try
				{
					// try a complete classname
					klass = Class.forName(resourceName);
				}
				catch (ClassNotFoundException e)
				{
					klass = null;
				}
				// try this package's prefix if the class couldn't be found
				if (null == klass)
				{
					try
					{
						klass = Class.forName(XmlSelectorResolver.class.getPackage().getName()+"."+resourceName);
					}
					catch (ClassNotFoundException e)
					{
						klass = null;
					}
				}
				// if the class was found, create an instance
				if (klass != null)
				{
					try
					{
						Object	instance = klass.newInstance();
						if (instance instanceof XmlSelector)
						{
							String path = ((XmlSelector)instance).getXmlPath(prefix);
							resource = resourceFinder.getResource(path);
							if (resource != null)
							{
								result = path;
								break;
							}
						}
					}
					catch (InstantiationException e)
					{
						continue;
					}
					catch (IllegalAccessException e)
					{
						continue;
					}
				}
			}
		}

		return result;
	}
}
