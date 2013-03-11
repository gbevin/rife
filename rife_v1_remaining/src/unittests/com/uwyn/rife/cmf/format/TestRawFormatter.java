/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestRawFormatter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.format.exceptions.InvalidContentDataTypeException;
import com.uwyn.rife.cmf.transform.RawContentTransformer;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import junit.framework.TestCase;

public class TestRawFormatter extends TestCase
{
	public TestRawFormatter(String name)
	{
		super(name);
	}

	public void testFormatBasic()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(data_image_gif));
        RawFormatter formatter = new RawFormatter();
		InputStream result = (InputStream)formatter.format(content, null);

		assertNotNull(result);

		assertTrue(Arrays.equals(data_image_gif, FileUtils.readBytes(result)));
	}

	public void testFormatInvalidDataType()
	throws Exception
	{
		Content content = new Content(MimeType.RAW, new byte[1]);
        RawFormatter formatter = new RawFormatter();
		try
		{
			formatter.format(content, null);
			fail();
		}
		catch (InvalidContentDataTypeException e)
		{
			assertSame(InputStream.class, e.getExpectedType());
			assertSame(formatter, e.getFormatter());
			assertSame(MimeType.RAW, e.getMimeType());
			assertSame(byte[].class, e.getReceivedType());
		}
	}

	public void testFormatTransformer()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.RAW, new ByteArrayInputStream(data_image_gif));
        RawFormatter formatter = new RawFormatter();
		InputStream result = (InputStream)formatter.format(content, new TransparentRawTransformer());

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, FileUtils.readBytes(result)));
	}

	static class TransparentRawTransformer implements RawContentTransformer
	{
		public InputStream transform(InputStream data, Map<String, String> attributes)
		throws ContentManagerException
		{
			try
			{
				URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
				byte[] data_image_png = FileUtils.readBytes(image_resource_png);
				
				return new ByteArrayInputStream(data_image_png);
			}
			catch (FileUtilsErrorException e)
			{
				return null;
			}
		}
	}
}
