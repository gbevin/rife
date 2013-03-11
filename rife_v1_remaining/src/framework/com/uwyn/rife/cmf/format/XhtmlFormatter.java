/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XhtmlFormatter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.format.exceptions.FormatException;
import com.uwyn.rife.cmf.format.exceptions.InvalidContentDataTypeException;
import com.uwyn.rife.cmf.format.exceptions.UnreadableDataFormatException;
import com.uwyn.rife.cmf.loader.XhtmlContentLoader;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.tools.StringUtils;
import java.util.HashSet;
import java.util.Set;

/**
 * Formats raw <code>Content</code> data as valid Xhtml.
 * <p>No content attributes are supported:
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 * @see Formatter
 */
public class XhtmlFormatter implements Formatter<String, String>
{
	public String format(Content content, ContentTransformer<String> transformer)
	throws FormatException
	{
		if (!(content.getData() instanceof String))
		{
			throw new InvalidContentDataTypeException(this, content.getMimeType(), String.class, content.getData().getClass());
		}
		
		String data = null;
		
		// check if the content contains a cached value of the loaded data
		if (content.hasCachedLoadedData())
		{
			data = (String)content.getCachedLoadedData();
		}
		
		if (null == data)
		{
			// get an image
			Set<String> errors = new HashSet<String>();
			data = new XhtmlContentLoader().load(content.getData(), content.isFragment(), errors);
			if (null == data)
			{
				throw new UnreadableDataFormatException(content.getMimeType(), errors);
			}
		}
		
		// ensure that as much as possible entities are encoded
		data =  StringUtils.encodeHtmlDefensive(data);
		
		// transform the content, if needed
		if (transformer != null)
		{
			data = transformer.transform(data, content.getAttributes());
		}
		
		return data;
	}
}

