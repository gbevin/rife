/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PlainTextFormatter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.format.exceptions.FormatException;
import com.uwyn.rife.cmf.format.exceptions.InvalidContentDataTypeException;
import com.uwyn.rife.cmf.transform.ContentTransformer;

/**
 * Formats plain test <code>Content</code> data.
 * <p>This merely executes the provided transformer on the data.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 * @see Formatter
 */
public class PlainTextFormatter implements Formatter<String, String>
{
	public String format(Content content, ContentTransformer<String> transformer)
	throws FormatException
	{
		if (!(content.getData() instanceof String))
		{
			throw new InvalidContentDataTypeException(this, content.getMimeType(), String.class, content.getData().getClass());
		}
		
		String data = (String)content.getData();
		
		// transform the content, if needed
		if (transformer != null)
		{
			data = transformer.transform(data, content.getAttributes());
		}
		
		return data;
	}
}

