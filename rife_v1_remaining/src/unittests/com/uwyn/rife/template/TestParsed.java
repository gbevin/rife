/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestParsed.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.tools.ExceptionUtils;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;

public class TestParsed extends TestCase
{
	private Parser mParser = null;

	public TestParsed(String name)
	{
		super(name);
	}

	public void setUp()
	{
		mParser = TemplateFactory.HTML.getParser();
	}

	public void testInstantiation()
	{
		Parsed tp = new Parsed(mParser);

		assertNotNull(tp);
		assertNull(tp.getResource());
		assertNull(tp.getClassName());
		assertNull(tp.getFullClassName());
		assertNull(tp.getBlock("test"));
		assertNull(tp.getDefaultValue("test"));
		assertEquals(tp.getBlocks().size(), 0);
		assertEquals(tp.getDefaultValues().size(), 0);
		assertEquals(tp.getDependencies().size(), 0);
		assertNull(tp.getContent());
	}

	public void testResource()
	{
		Parsed tp = new Parsed(mParser);

		assertNull(tp.getResource());
		URL url = null;
		try
		{
			url = new URL("file:/test/");
		}
		catch (MalformedURLException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		tp.setResource(url);
		assertEquals(tp.getResource(), url);
	}

	public void testClassname()
	{
		Parsed tp = new Parsed(mParser);

		assertNull(tp.getClassName());
		assertNull(tp.getFullClassName());
		String classname = "some_template";
		tp.setClassName(classname);
		assertNotNull(tp.getFullClassName());
		assertTrue(!classname.equals(tp.getFullClassName()));
	}

	public void testBlocks()
	{
		Parsed tp = new Parsed(mParser);

		assertEquals(tp.getBlocks().size(), 0);

		ParsedBlockData blockdata1 = new ParsedBlockData();
		ParsedBlockData blockdata2 = new ParsedBlockData();
		tp.setBlock("blockparts1", blockdata1);
		tp.setBlock("blockparts2", blockdata2);

		assertEquals(tp.getBlocks().size(), 2);
		assertSame(tp.getBlock("blockparts1"), blockdata1);
		assertSame(tp.getBlock("blockparts2"), blockdata2);
		assertNull(tp.getBlock("blockparts3"));
		assertNull(tp.getContent());

		ParsedBlockData contentdata = new ParsedBlockData();
		tp.setBlock("", contentdata);
		assertEquals(tp.getBlocks().size(), 3);
		assertSame(tp.getContent(), contentdata);
	}

	public void testDefaultValues()
	{
		Parsed tp = new Parsed(mParser);

		assertEquals(tp.getDefaultValues().size(), 0);

		String defaultvalue1 = new String();
		String defaultvalue2 = new String();
		tp.setDefaultValue("defaultvalue1", defaultvalue1);
		tp.setDefaultValue("defaultvalue2", defaultvalue2);

		assertEquals(tp.getDefaultValues().size(), 2);
		assertSame(tp.getDefaultValue("defaultvalue1"), defaultvalue1);
		assertSame(tp.getDefaultValue("defaultvalue2"), defaultvalue2);
		assertNull(tp.getDefaultValue("defaultvalue3"));
	}

	public void testIncludes()
	{
		Parsed tp = new Parsed(mParser);

		String include1_name = "noblocks_in";
		Parsed include1 = mParser.prepare(include1_name, mParser.resolve(include1_name));
		String include2_name = "defaultvalues_in";
		Parsed include2 = mParser.prepare(include2_name, mParser.resolve(include2_name));
		String include3_name = "blocks_successive_in";
		Parsed include3 = mParser.prepare(include3_name, mParser.resolve(include3_name));

		try
		{
			tp.addDependency(include1);
			assertEquals(tp.getDependencies().size(), 1);
			tp.addDependency(include2);
			assertEquals(tp.getDependencies().size(), 2);
			tp.addDependency(include3);
			assertEquals(tp.getDependencies().size(), 3);
		}
		catch (TemplateException e)
		{
            assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertEquals(include1_name.indexOf(include1.getClassName()), 0);
		assertEquals(include2_name.indexOf(include2.getClassName()), 0);
		assertEquals(include3_name.indexOf(include3.getClassName()), 0);
	}
}
