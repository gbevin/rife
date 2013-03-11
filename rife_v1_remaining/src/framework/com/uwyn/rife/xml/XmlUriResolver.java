/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XmlUriResolver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml;

import com.uwyn.rife.resources.ResourceFinder;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

public class XmlUriResolver implements URIResolver
{
	private ResourceFinder	mResourceFinder = null;

	public XmlUriResolver(ResourceFinder resourcefinder)
	{
		mResourceFinder = resourcefinder;
	}
	
	public Source resolve(String href, String base)
	throws TransformerException
	{
		URL resource = mResourceFinder.getResource(href);
		if (null == resource)
		{
			return null;
		}
		
		return new SAXSource(new XmlInputSource(resource));
	}
}

