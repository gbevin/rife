/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: UrlResource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.net.URL;

/**
 * Packages an <code>URL</code> resource together with additional data that
 * can't be stored in the <code>URL</code> instance itself.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 */
public class UrlResource
{
	private URL		mUrl = null;
	private String	mSourceName = null;
	
	/**
	 * Creates a new <code>UrlResource</code> instance.
	 * 
	 * @param url the URL of the resource
	 * @param sourceName the source name that was used to look up the resource
	 * @since 1.5
	 */
	public UrlResource(URL url, String sourceName)
	{
		assert url != null;
		
		mUrl = url;
		mSourceName = sourceName;
	}
	
	/**
	 * Retrieves the URL of the resource.
	 * 
	 * @return the URL of the resource
	 * @since 1.5
	 */
	public URL getUrl()
	{
		return mUrl;
	}
	
	/**
	 * Retrieves the source name that was used to look up the resource.
	 * 
	 * @return the source name that was used to look up the resource
	 * @since 1.5
	 */
	public String getSourceName()
	{
		return mSourceName;
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof UrlResource)
		{
			return mUrl.equals(((UrlResource)other).getUrl());
		}
		
		return false;
	}
	
	public int hashCode()
	{
		return mUrl.hashCode();
	}
}


