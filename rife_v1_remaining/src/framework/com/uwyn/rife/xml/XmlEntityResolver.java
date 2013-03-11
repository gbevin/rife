/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XmlEntityResolver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml;

import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.xml.exceptions.CantFindEntityException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class XmlEntityResolver implements EntityResolver
{
	private ResourceFinder			mResourceFinder = null;
	private HashMap<String, String>	mCatalog = null;
	private boolean					mRestrictToCatalog = false;

	public XmlEntityResolver(ResourceFinder resourcefinder)
	{
		mResourceFinder = resourcefinder;
	}
	
	public XmlEntityResolver addToCatalog(String original, String alias)
	{
		if (null == mCatalog)
		{
			mCatalog = new HashMap<String, String>();
		}
		
		mCatalog.put(original, alias);
		
		return this;
	}
	
	public XmlEntityResolver restrictToCatalog(boolean restrict)
	{
		mRestrictToCatalog = restrict;
		
		return this;
	}

	public InputSource resolveEntity(String publicId, String systemId)
	{
		assert systemId != null;
		assert systemId.length() > 0;
		
		if (mCatalog != null)
		{
			String alias = mCatalog.get(systemId);
			if (alias != null)
			{
				systemId = alias;
			}
			else if (mRestrictToCatalog)
			{
				throw new CantFindEntityException(systemId, null);
			}
		}
		else if (mRestrictToCatalog)
		{
			throw new CantFindEntityException(systemId, null);
		}
		
		URL resource = null;
		
		if (systemId.startsWith("http://"))
		{
			try
			{
				resource = new URL(systemId);
				return new XmlInputSource(resource);
			}
			catch (MalformedURLException e)
			{
				resource = null;
			}
		}
		
		// fix around Resin's incompatible classloader resource urls
		resource = mResourceFinder.getResource(systemId);
		
		if (resource != null)
		{
			return new XmlInputSource(resource);
		}

		// support orion's classloader resource url
		if (systemId.startsWith("classloader:/"))
		{
			systemId = systemId.substring("classloader:/".length());
		}
		// support weblogic's classloader resource url
		if (systemId.startsWith("zip:/"))
		{
			systemId = systemId.substring("zip:/".length());
		}
		if (systemId.startsWith("jar:/"))
		{
			systemId = systemId.substring("jar:/".length());
		}
		if (systemId.startsWith("tx:/"))
		{
			systemId = systemId.substring("tx:/".length());
		}
		if (systemId.startsWith("file:/"))
		{
			systemId = systemId.substring("file:/".length());
		}
		if (systemId.startsWith("//"))
		{
			systemId = systemId.substring("//".length());
		}
		int jar_entry_index = systemId.lastIndexOf("!/");
		if (jar_entry_index != -1)
		{
			systemId = systemId.substring(jar_entry_index+"!/".length());
		}
		
		resource = mResourceFinder.getResource(systemId);
		
		if (null == resource)
		{
			throw new CantFindEntityException(systemId, null);
		}

		return new XmlInputSource(resource);
	}
}


