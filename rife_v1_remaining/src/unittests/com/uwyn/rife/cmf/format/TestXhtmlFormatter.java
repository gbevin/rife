/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestXhtmlFormatter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.format.exceptions.InvalidContentDataTypeException;
import com.uwyn.rife.cmf.format.exceptions.UnreadableDataFormatException;
import com.uwyn.rife.cmf.loader.XhtmlContentLoader;
import com.uwyn.rife.cmf.transform.TextContentTransformer;
import com.uwyn.rife.tools.StringUtils;
import java.util.Map;
import junit.framework.TestCase;

public class TestXhtmlFormatter extends TestCase
{
	public TestXhtmlFormatter(String name)
	{
		super(name);
	}

	public void testFormatBasic()
	throws Exception
	{
		String data = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"+
					  "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"+
					  "<html><head><title>my title</title></head><body><p>some text \n"+
					  "<i>here</i> and <b>there</b></p></body></html>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data);
        XhtmlFormatter formatter = new XhtmlFormatter();
		String result = formatter.format(content, null);

		assertNotNull(result);

		assertEquals(data, result);
	}

	public void testFormatInvalidDataType()
	throws Exception
	{
		Content content = new Content(MimeType.APPLICATION_XHTML, new Object());
        XhtmlFormatter formatter = new XhtmlFormatter();
		try
		{
			formatter.format(content, null);
			fail();
		}
		catch (InvalidContentDataTypeException e)
		{
			assertSame(String.class, e.getExpectedType());
			assertSame(formatter, e.getFormatter());
			assertSame(MimeType.APPLICATION_XHTML, e.getMimeType());
			assertSame(Object.class, e.getReceivedType());
		}
	}

	public void testFormatCachedLoadedData()
	throws Exception
	{
		String data = "<p>some text <i>here</i> and <b>there</b></p>";
		Content content = new Content(MimeType.IMAGE_PNG, data);
		String xhtml = new XhtmlContentLoader().load(data, true, null);
		content.setCachedLoadedData(xhtml);

        XhtmlFormatter formatter = new XhtmlFormatter();
		String result = formatter.format(content, null);

		assertNotNull(result);

		assertEquals(data, result);
	}

	public void testFormatUnreadableData()
	throws Exception
	{
		Content content = new Content(MimeType.APPLICATION_XHTML, "<p>some text <i>here</b> and <b>there</i></blurp>");
        XhtmlFormatter formatter = new XhtmlFormatter();
		try
		{
			formatter.format(content, null);
			fail();
		}
		catch (UnreadableDataFormatException e)
		{
			assertSame(MimeType.APPLICATION_XHTML, e.getMimeType());
			assertTrue(e.getErrors().size() > 0);
		}
	}

	public void testFormatTransformer()
	throws Exception
	{
		String data = "<p>some text <i>here</i> and <b>there</b></p>";
		Content content = new Content(MimeType.APPLICATION_XHTML, data)
			.fragment(true);
        XhtmlFormatter formatter = new XhtmlFormatter();
		String result = formatter.format(content, new XhtmlTransformer());

		assertNotNull(result);

		String transformed = "<p>some text <i>here</i> and <b>at home</b></p>";
		assertEquals(transformed, result);
	}

	static class XhtmlTransformer implements TextContentTransformer
	{
		public String transform(String data, Map<String, String> attributes)
		throws ContentManagerException
		{
			return StringUtils.replace(data, "there", "at home");
		}
	}
}
