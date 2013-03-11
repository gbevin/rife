/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RawFormatter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.format.exceptions.FormatException;
import com.uwyn.rife.cmf.format.exceptions.InvalidContentDataTypeException;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import java.io.InputStream;

/**
 * Formats raw <code>Content</code> data.
 * <p>This merely executes the provided transformer on the data.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 * @see Formatter
 */
public class RawFormatter implements Formatter<InputStream, InputStream>
{
	public InputStream format(Content content, ContentTransformer<InputStream> transformer)
	throws FormatException
	{
		if (!(content.getData() instanceof InputStream))
		{
			throw new InvalidContentDataTypeException(this, content.getMimeType(), InputStream.class, content.getData().getClass());
		}
		
		InputStream data = (InputStream)content.getData();
		
		// transform the content, if needed
		if (transformer != null)
		{
			data = transformer.transform(data, content.getAttributes());
		}
		
		return data;
	}
}

