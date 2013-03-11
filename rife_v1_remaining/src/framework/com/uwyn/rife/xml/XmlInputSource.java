/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XmlInputSource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.xml;

import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.xml.exceptions.CantFindResourceException;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.xml.sax.InputSource;

public class XmlInputSource extends InputSource
{
	private URL		mResource = null;
	
	public XmlInputSource(String xmlPath, ResourceFinder resourceFinder)
	throws XmlErrorException
	{
		super();
		
		if (null == xmlPath)		throw new IllegalArgumentException("xmlPath can't be null.");
		if (xmlPath.length() == 0)	throw new IllegalArgumentException("xmlPath can't be empty.");
		if (null == resourceFinder)	throw new IllegalArgumentException("resourceFinder can't be null.");
		
		URL resource = resourceFinder.getResource(xmlPath);
		
		if (null == resource)
		{
			throw new CantFindResourceException(xmlPath, null);
		}

		setResource(resource);
	}
	
	public XmlInputSource(URL resource)
	throws XmlErrorException
	{
		super();
		
		if (null == resource)	throw new IllegalArgumentException("resource can't be null.");
		
		setResource(resource);
	}
	
	public void setResource(URL resource)
	{
		mResource = resource;

		setSystemId(mResource.toExternalForm());
		
		// catch orion's classloader resource protocol
		try
		{
			if (resource.getProtocol().equals("classloader"))
			{
				// force byte stream that is used so that Orion doesn't set a wrong
				// one
				URLConnection connection = resource.openConnection();
				connection.setUseCaches(false);
				setByteStream(connection.getInputStream());
			}
			else
			{
				String sax_driver = System.getProperty("org.xml.sax.driver");
				
				if (sax_driver != null &&
					sax_driver.equals("com.caucho.xml.Xml"))
				{
					// force byte stream that is used so that Resin doesn't set a wrong
					// one
					URLConnection connection = resource.openConnection();
					connection.setUseCaches(false);
					setByteStream(connection.getInputStream());
				}
			}
		}
		// if an exception occurs, clear to byte stream and let the sax driver
		// try to find it
		catch (IOException e)
		{
			setByteStream(null);
		}
	}
	
	public String toString()
	{
		return mResource.toExternalForm();
	}
}


