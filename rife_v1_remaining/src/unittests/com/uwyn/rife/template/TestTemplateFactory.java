/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestTemplateFactory.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.resources.DatabaseResources;
import com.uwyn.rife.resources.DatabaseResourcesFactory;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.exceptions.ResourceWriterErrorException;
import com.uwyn.rife.template.exceptions.AmbiguousTemplateNameException;
import com.uwyn.rife.template.exceptions.ResourceBundleNotFoundException;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.Localization;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class TestTemplateFactory extends TemplateTestCase
{
	public TestTemplateFactory(String name)
	{
		super(name);
	}

	public void testUniqueFactory()
	{
		assertSame(TemplateFactory.ENGINEHTML, TemplateFactory.ENGINEHTML);
		assertSame(TemplateFactory.ENGINEXHTML, TemplateFactory.ENGINEXHTML);
		assertSame(TemplateFactory.ENGINEXML, TemplateFactory.ENGINEXML);
		assertSame(TemplateFactory.ENGINETXT, TemplateFactory.ENGINETXT);
		assertSame(TemplateFactory.HTML, TemplateFactory.HTML);
		assertSame(TemplateFactory.XHTML, TemplateFactory.XHTML);
		assertSame(TemplateFactory.XML, TemplateFactory.XML);
		assertSame(TemplateFactory.JAVA, TemplateFactory.JAVA);
		assertSame(TemplateFactory.SQL, TemplateFactory.SQL);
		assertSame(TemplateFactory.TXT, TemplateFactory.TXT);
	}

	public void testUniqueParser()
	{
		assertSame(TemplateFactory.ENGINEHTML.getParser(), TemplateFactory.ENGINEHTML.getParser());
		assertSame(TemplateFactory.ENGINEXHTML.getParser(), TemplateFactory.ENGINEXHTML.getParser());
		assertSame(TemplateFactory.ENGINEXML.getParser(), TemplateFactory.ENGINEXML.getParser());
		assertSame(TemplateFactory.ENGINETXT.getParser(), TemplateFactory.ENGINETXT.getParser());
		assertSame(TemplateFactory.HTML.getParser(), TemplateFactory.HTML.getParser());
		assertSame(TemplateFactory.XHTML.getParser(), TemplateFactory.XHTML.getParser());
		assertSame(TemplateFactory.XML.getParser(), TemplateFactory.XML.getParser());
		assertSame(TemplateFactory.JAVA.getParser(), TemplateFactory.JAVA.getParser());
		assertSame(TemplateFactory.SQL.getParser(), TemplateFactory.SQL.getParser());
		assertSame(TemplateFactory.TXT.getParser(), TemplateFactory.TXT.getParser());
	}

	public void testDefaultContentType()
	{
		assertEquals(TemplateFactory.ENGINEHTML.get("testhtml_in").getDefaultContentType(), "text/html");
		assertEquals(TemplateFactory.ENGINEXHTML.get("testxhtml_in").getDefaultContentType(), "text/html");
		assertEquals(TemplateFactory.ENGINEXML.get("testxml_in").getDefaultContentType(), "application/xml");
		assertEquals(TemplateFactory.ENGINETXT.get("testtext_in").getDefaultContentType(), "text/plain");
		assertEquals(TemplateFactory.HTML.get("testhtml_in").getDefaultContentType(), "text/html");
		assertEquals(TemplateFactory.XHTML.get("testxhtml_in").getDefaultContentType(), "text/html");
		assertEquals(TemplateFactory.XML.get("testxml_in").getDefaultContentType(), "application/xml");
		assertEquals(TemplateFactory.JAVA.get("TestJavaIn").getDefaultContentType(), "text/x-java-source");
		assertEquals(TemplateFactory.SQL.get("testsql_in").getDefaultContentType(), "text/plain");
		assertEquals(TemplateFactory.TXT.get("testtext_in").getDefaultContentType(), "text/plain");
	}

	public void testEngineHtmlTemplate()
	{
		Template template = TemplateFactory.ENGINEHTML.get("testhtml_in");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testhtml_out", TemplateFactory.ENGINEHTML.getParser()));
	}

	public void testEngineXhtmlTemplate()
	{
		Template template = TemplateFactory.ENGINEXHTML.get("testxhtml_in");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testxhtml_out", TemplateFactory.ENGINEXHTML.getParser()));
	}

	public void testEngineXmlTemplate()
	{
		Template template = TemplateFactory.ENGINEXML.get("testxml_in");
		template.setValue("name", "name1");
		template.setValue("value", "value1");
		template.appendBlock("params", "param");
		template.setValue("name", "name2");
		template.setValue("value", "value2");
		template.appendBlock("params", "param");
		template.setValue("name", "name3");
		template.setValue("value", "value3");
		template.appendBlock("params", "param");
		assertEquals(template.getContent(), getTemplateContent("testxml_out", TemplateFactory.ENGINEXML.getParser()));
	}

	public void testEngineTextTemplate()
	{
		Template template = TemplateFactory.ENGINETXT.get("testtext_in");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testtext_out", TemplateFactory.ENGINETXT.getParser()));
	}

	public void testHtmlTemplate()
	{
		Template template = TemplateFactory.HTML.get("testhtml_in");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testhtml_out", TemplateFactory.HTML.getParser()));
	}

	public void testXhtmlTemplate()
	{
		Template template = TemplateFactory.XHTML.get("testxhtml_in");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testxhtml_out", TemplateFactory.XHTML.getParser()));
	}

	public void testXmlTemplate()
	{
		Template template = TemplateFactory.XML.get("testxml_in");
		template.setValue("name", "name1");
		template.setValue("value", "value1");
		template.appendBlock("params", "param");
		template.setValue("name", "name2");
		template.setValue("value", "value2");
		template.appendBlock("params", "param");
		template.setValue("name", "name3");
		template.setValue("value", "value3");
		template.appendBlock("params", "param");
		assertEquals(template.getContent(), getTemplateContent("testxml_out", TemplateFactory.XML.getParser()));
	}

	public void testJavaTemplate()
	{
		Template template = TemplateFactory.JAVA.get("TestJavaIn");
		template.setValue("classname", "TestJavaOut");
		template.setValue("name", "name1");
		template.setValue("value", "value1");
		template.appendBlock("members", "member");
		template.setValue("name", "name2");
		template.setValue("value", "value2");
		template.appendBlock("members", "member");
		template.setValue("name", "name3");
		template.setValue("value", "value3");
		template.appendBlock("members", "member");
		assertEquals(template.getContent(), getTemplateContent("TestJavaOut", TemplateFactory.JAVA.getParser()));
	}

	public void testSqlTemplate()
	{
		Template template = TemplateFactory.SQL.get("testsql_in");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testsql_out", TemplateFactory.SQL.getParser()));
	}

	public void testTextTemplate()
	{
		Template template = TemplateFactory.TXT.get("testtext_in");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testtext_out", TemplateFactory.TXT.getParser()));
	}

	public void testEngineHtmlTemplatePath()
	{
		Template template = TemplateFactory.ENGINEHTML.get("testhtml_in.html");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testhtml_out", TemplateFactory.ENGINEHTML.getParser()));
	}

	public void testEngineXhtmlTemplatePath()
	{
		Template template = TemplateFactory.ENGINEXHTML.get("testxhtml_in.xhtml");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testxhtml_out", TemplateFactory.ENGINEXHTML.getParser()));
	}

	public void testEngineXmlTemplatePath()
	{
		Template template = TemplateFactory.ENGINEXML.get("testxml_in.xml");
		template.setValue("name", "name1");
		template.setValue("value", "value1");
		template.appendBlock("params", "param");
		template.setValue("name", "name2");
		template.setValue("value", "value2");
		template.appendBlock("params", "param");
		template.setValue("name", "name3");
		template.setValue("value", "value3");
		template.appendBlock("params", "param");
		assertEquals(template.getContent(), getTemplateContent("testxml_out", TemplateFactory.ENGINEXML.getParser()));
	}

	public void testEngineTextTemplatePath()
	{
		Template template = TemplateFactory.ENGINETXT.get("testtext_in.txt");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testtext_out", TemplateFactory.ENGINETXT.getParser()));
	}

	public void testHtmlTemplatePath()
	{
		Template template = TemplateFactory.HTML.get("testhtml_in.html");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testhtml_out", TemplateFactory.HTML.getParser()));
	}

	public void testXhtmlTemplatePath()
	{
		Template template = TemplateFactory.XHTML.get("testxhtml_in.xhtml");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testxhtml_out", TemplateFactory.XHTML.getParser()));
	}

	public void testXmlTemplatePath()
	{
		Template template = TemplateFactory.XML.get("testxml_in.xml");
		template.setValue("name", "name1");
		template.setValue("value", "value1");
		template.appendBlock("params", "param");
		template.setValue("name", "name2");
		template.setValue("value", "value2");
		template.appendBlock("params", "param");
		template.setValue("name", "name3");
		template.setValue("value", "value3");
		template.appendBlock("params", "param");
		assertEquals(template.getContent(), getTemplateContent("testxml_out", TemplateFactory.XML.getParser()));
	}

	public void testJavaTemplatePath()
	{
		Template template = TemplateFactory.JAVA.get("TestJavaIn.java");
		template.setValue("classname", "TestJavaOut");
		template.setValue("name", "name1");
		template.setValue("value", "value1");
		template.appendBlock("members", "member");
		template.setValue("name", "name2");
		template.setValue("value", "value2");
		template.appendBlock("members", "member");
		template.setValue("name", "name3");
		template.setValue("value", "value3");
		template.appendBlock("members", "member");
		assertEquals(template.getContent(), getTemplateContent("TestJavaOut", TemplateFactory.JAVA.getParser()));
	}

	public void testSqlTemplatePath()
	{
		Template template = TemplateFactory.SQL.get("testsql_in.sql");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testsql_out", TemplateFactory.SQL.getParser()));
	}

	public void testTextTemplatePath()
	{
		Template template = TemplateFactory.TXT.get("testtext_in.txt");
		template.setValue("first", "first1");
		template.setValue("second", "second1");
		template.appendBlock("lines", "line");
		template.setValue("first", "first2");
		template.setValue("second", "second2");
		template.appendBlock("lines", "line");
		template.setValue("first", "first3");
		template.setValue("second", "second3");
		template.appendBlock("lines", "line");
		assertEquals(template.getContent(), getTemplateContent("testtext_out", TemplateFactory.TXT.getParser()));
	}

	public void testEngineHtmlTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.ENGINEHTML.get("html.html");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "html.html");
		}
	}

	public void testEngineXHtmlTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.ENGINEXHTML.get("xhtml.xhtml");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "xhtml.xhtml");
		}
	}

	public void testEngineXmlTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.ENGINEXML.get("xml.xml");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "xml.xml");
		}
	}

	public void testEngineTxtTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.ENGINETXT.get("txt.txt");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "txt.txt");
		}
	}

	public void testHtmlTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.HTML.get("html.html");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "html.html");
		}
	}

	public void testXHtmlTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.XHTML.get("xhtml.xhtml");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "xhtml.xhtml");
		}
	}

	public void testXmlTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.XML.get("xml.xml");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "xml.xml");
		}
	}

	public void testJavaTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.JAVA.get("java.java");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "java.java");
		}
	}

	public void testSqlTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.SQL.get("sql.sql");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "sql.sql");
		}
	}

	public void testTxtTemplateAmbiguous()
	{
		try
		{
			TemplateFactory.TXT.get("txt.txt");
			fail("exception not thrown");
		}
		catch (AmbiguousTemplateNameException e)
		{
			assertEquals(e.getName(), "txt.txt");
		}
	}

	public void testXsltTransformation()
	{
		TemplateTransformerXslt transformer = new TemplateTransformerXslt();
		transformer.addFilter("testxslt_stylesheet2.xsl");
		transformer.addFilter("testxslt_stylesheet3.xsl");
		Template template = TemplateFactory.XML.get("testxslt_in", transformer);
		template.appendBlock("out", "out");
		template.appendBlock("out", "out");
		String version_string = System.getProperty("java.version");
		int first_point = version_string.indexOf(".");
		int second_point = version_string.indexOf(".", first_point+1);
		double version;
		if (second_point != -1)
		{
			version = Double.parseDouble(version_string.substring(0, second_point));
		}
		else
		{
			version = Double.parseDouble(version_string);
		}
		// line handling is different in 1.4 and 1.5
		if (version < 1.5)
		{
			assertEquals(template.getContent(), getTemplateContent("testxslt_out", TemplateFactory.XML.getParser()));
		}
		else
		{
			assertEquals(template.getContent(), getTemplateContent("testxslt_out_5_0", TemplateFactory.XML.getParser()));
		}
	}

	public void testTemplateInitializer()
	{
		TemplateFactory factory = null;
		Template template = null;

		factory = new TemplateFactory(ResourceFinderClasspath.getInstance(), "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, "text/html", ".html", null, null, null, null, null);
		template = factory.get("testhtml_in", null, null);
		assertFalse(template.getContent().equals(getTemplateContent("testhtml_out", TemplateFactory.HTML.getParser())));

		factory = new TemplateFactory(ResourceFinderClasspath.getInstance(), "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, "text/html", ".html", null, null, null, null, new TemplateInitializer() {
				public void initialize(Template template)
				{
					template.setValue("first", "first1");
					template.setValue("second", "second1");
					template.appendBlock("lines", "line");
					template.setValue("first", "first2");
					template.setValue("second", "second2");
					template.appendBlock("lines", "line");
					template.setValue("first", "first3");
					template.setValue("second", "second3");
					template.appendBlock("lines", "line");
				}
			});
		template = factory.get("testhtml_in", null, null);
		assertEquals(template.getContent(), getTemplateContent("testhtml_out", TemplateFactory.HTML.getParser()));
		template.setValue("first", "otherfirst1");
		template.setValue("second", "othersecond1");
		template.appendBlock("lines", "line");
		assertFalse(template.getContent().equals(getTemplateContent("testhtml_out", TemplateFactory.HTML.getParser())));
		template.clear();
		assertEquals(template.getContent(), getTemplateContent("testhtml_out", TemplateFactory.HTML.getParser()));
	}
	
	public void testFilteredTagsRenderHtml()
	throws Exception
	{
		Template t = TemplateFactory.HTML.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:1'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:1'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:2'.\n", t.getContent());
	}
	
	public void testFilteredTagsRenderXhtml()
	throws Exception
	{
		Template t = TemplateFactory.XHTML.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:3'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:3'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:4'.\n", t.getContent());
	}
	
	public void testFilteredTagsRenderXml()
	throws Exception
	{
		Template t = TemplateFactory.XML.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:5'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:5'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:6'.\n", t.getContent());
	}
	
	public void testFilteredTagsRenderTxt()
	throws Exception
	{
		Template t = TemplateFactory.TXT.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:7'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:7'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:8'.\n", t.getContent());
	}
	
	public void testFilteredTagsRenderSql()
	throws Exception
	{
		Template t = TemplateFactory.SQL.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:9'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:9'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:10'.\n", t.getContent());
	}
	
	public void testFilteredTagsRenderJava()
	throws Exception
	{
		Template t = TemplateFactory.JAVA.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:11'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:11'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:12'.\n", t.getContent());
	}
	
	public void testFilteredTagsRenderEnginehtml()
	throws Exception
	{
		Template t = TemplateFactory.ENGINEHTML.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:13'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:13'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:14'.\n", t.getContent());
	}
	
	public void testFilteredTagsRenderEnginexhtml()
	throws Exception
	{
		Template t = TemplateFactory.ENGINEXHTML.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:15'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:15'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:16'.\n", t.getContent());
	}
	
	public void testFilteredTagsRenderEnginexml()
	throws Exception
	{
		Template t = TemplateFactory.ENGINEXML.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:17'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:17'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:18'.\n", t.getContent());
	}
	
	public void testFilteredTagsRenderEnginetxt()
	throws Exception
	{
		Template t = TemplateFactory.ENGINETXT.get("filtered_tags_render");
		assertEquals("This is the render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:19'.\n"+
			"This is another render value 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPLnull:19'.\n"+
			"This is the render value with a differentiator 'RENDER:COM.UWYN.RIFE.TEMPLATE.RENDERERIMPL:DIFFERENT:different:20'.\n", t.getContent());
	}
	
	public void testFilteredTagsConfigHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_config");
		assertEquals("This is the config value 'the config value'.\nThis is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_html.getContent());
	}

	public void testFilteredTagsConfigXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_config");
		assertEquals("This is the config value 'the config value'.\nThis is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_xhtml.getContent());
	}

	public void testFilteredTagsConfigXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_config");
		assertEquals("This is the config value 'the config value'.\nThis is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_xml.getContent());
	}

	public void testFilteredTagsConfigTxt()
	throws Exception
	{
		Template template_text = TemplateFactory.TXT.get("filtered_tags_config");
		assertEquals("This is the config value 'the config value'.\nThis is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_text.getContent());
	}

	public void testFilteredTagsConfigSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_config");
		assertEquals("This is the config value 'the config value'.\nThis is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_sql.getContent());
	}

	public void testFilteredTagsConfigJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_config");
		assertEquals("// This is the config value 'the config value'.\n// This is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_java.getContent());
	}

	public void testFilteredTagsConfigEnginehtml()
	throws Exception
	{
		Template template_enginehtml = TemplateFactory.ENGINEHTML.get("filtered_tags_config");
		assertEquals("This is the config value 'the config value'.\nThis is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_enginehtml.getContent());
	}

	public void testFilteredTagsConfigEnginexhtml()
	throws Exception
	{
		Template template_enginexhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_config");
		assertEquals("This is the config value 'the config value'.\nThis is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_enginexhtml.getContent());
	}

	public void testFilteredTagsConfigEnginexml()
	throws Exception
	{
		Template template_enginexml = TemplateFactory.ENGINEXML.get("filtered_tags_config");
		assertEquals("This is the config value 'the config value'.\nThis is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_enginexml.getContent());
	}

	public void testFilteredTagsConfigEnginetxt()
	throws Exception
	{
		Template template_enginetext = TemplateFactory.ENGINETXT.get("filtered_tags_config");
		assertEquals("This is the config value 'the config value'.\nThis is an unknown config value '[!V 'CONFIG:TEMPLATE_CONFIG_VALUE_UNKNOWN'/]'.\n", template_enginetext.getContent());
	}

	public void testFilteredTagsL10nEnginehtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());
	}

	public void testFilteredTagsL10nEnginehtmlResourceBundleNotFound()
	throws Exception
	{
		try
		{
			TemplateFactory.ENGINEHTML.get("filtered_tags_l10n_bundlenotfound");
			fail();
		}
		catch (ResourceBundleNotFoundException e)
		{
			assertEquals(e.getTemplateName(), "filtered_tags_l10n_bundlenotfound");
			assertEquals(e.getValueTag(), "L10N:loc/bundlenotpresent-l10n:THE_KEY");
			assertEquals(e.getBundleName(), "loc/bundlenotpresent-l10n");
		}
	}

	public void testFilteredTagsL10nEnginehtmlDefaultResourceBundles()
	throws Exception
	{
		Template template_html;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.ENGINEHTML, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\nThis is a key with a bundle 'De Nederlandse tekst'.\n", template_html.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\nThis is a key with a bundle 'Le texte Francais'.\n", template_html.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.ENGINEHTML, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nEnginehtmlSeveralResourcebundles()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'list key'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_html.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());
	}

	public void testFilteredTagsL10nEnginexhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.clear();
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.clear();
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());
	}

	public void testFilteredTagsL10nEnginexhtmlDefaultResourceBundles()
	throws Exception
	{
		Template template_xhtml;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.ENGINEXHTML, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xhtml.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xhtml.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xhtml.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.ENGINEXHTML, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nEnginexhtmlSeveralResourcebundles()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'list key'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.clear();
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_xhtml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.clear();
		template_xhtml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());
	}

	public void testFilteredTagsL10nEnginexml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEXML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_html.getContent());

		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_html.getContent());
	}

	public void testFilteredTagsL10nEnginexmlDefaultResourceBundles()
	throws Exception
	{
		Template template_xml;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.ENGINEXML, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xml.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xml.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xml.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.ENGINEXML, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nEnginexmlSeveralResourcebundles()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xml.getContent());

		template_xml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_xml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'list key'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xml.getContent());

		template_xml.clear();
		template_xml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_xml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xml.getContent());

		template_xml.clear();
		template_xml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_xml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xml.getContent());
	}

	public void testFilteredTagsL10Enginetxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.clear();
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.clear();
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());
	}

	public void testFilteredTagsL10nEnginetxtDefaultResourceBundles()
	throws Exception
	{
		Template template_txt;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.ENGINETXT, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_txt.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_txt.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_txt.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.ENGINETXT, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nEnginetxtSeveralResourcebundles()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'list key'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.clear();
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_txt.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.clear();
		template_txt.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());
	}

	public void testFilteredTagsL10nHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());
	}

	public void testFilteredTagsL10nHtmlDefaultResourceBundles()
	throws Exception
	{
		Template template_html;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.HTML, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_html = TemplateFactory.HTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_html = TemplateFactory.HTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\nThis is a key with a bundle 'De Nederlandse tekst'.\n", template_html.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_html = TemplateFactory.HTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\nThis is a key with a bundle 'Le texte Francais'.\n", template_html.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.HTML, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nHtmlSeveralResourcebundles()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'list key'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_html.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\nThis is a key with a bundle 'The English text'.\n", template_html.getContent());
	}

	public void testFilteredTagsL10nXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.clear();
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.clear();
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());
	}

	public void testFilteredTagsL10nXhtmlDefaultResourceBundles()
	throws Exception
	{
		Template template_xhtml;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.XHTML, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_xhtml = TemplateFactory.XHTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xhtml.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_xhtml = TemplateFactory.XHTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xhtml.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_xhtml = TemplateFactory.XHTML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xhtml.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.XHTML, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nXhtmlSeveralResourcebundles()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'list key'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.clear();
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_xhtml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());

		template_xhtml.clear();
		template_xhtml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_xhtml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xhtml.getContent());
	}

	public void testFilteredTagsL10nXml()
	throws Exception
	{
		Template template_html = TemplateFactory.XML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_html.getContent());

		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_html.getContent());

		template_html.clear();
		template_html.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_html.getContent());
	}

	public void testFilteredTagsL10nXmlDefaultResourceBundles()
	throws Exception
	{
		Template template_xml;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.XML, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_xml = TemplateFactory.XML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xml.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_xml = TemplateFactory.XML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xml.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_xml = TemplateFactory.XML.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_xml.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.XML, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nXmlSeveralResourcebundles()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xml.getContent());

		template_xml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_xml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'list key'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xml.getContent());

		template_xml.clear();
		template_xml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_xml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xml.getContent());

		template_xml.clear();
		template_xml.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_xml.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_xml.getContent());
	}

	public void testFilteredTagsL10nTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.clear();
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.clear();
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());
	}

	public void testFilteredTagsL10nTxtDefaultResourceBundles()
	throws Exception
	{
		Template template_txt;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.TXT, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_txt = TemplateFactory.TXT.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_txt.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_txt = TemplateFactory.TXT.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_txt.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_txt = TemplateFactory.TXT.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_txt.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.TXT, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nTxtSeveralResourcebundles()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'list key'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.clear();
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_txt.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());

		template_txt.clear();
		template_txt.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_txt.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_txt.getContent());
	}

	public void testFilteredTagsL10nSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_sql.getContent());

		template_sql.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_sql.getContent());

		template_sql.clear();
		template_sql.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_sql.getContent());

		template_sql.clear();
		template_sql.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_sql.getContent());
	}

	public void testFilteredTagsL10nSqlDefaultResourceBundles()
	throws Exception
	{
		Template template_sql;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.SQL, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_sql = TemplateFactory.SQL.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_sql.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_sql = TemplateFactory.SQL.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'De Nederlandse tekst'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_sql.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_sql = TemplateFactory.SQL.get("filtered_tags_l10n");
			assertEquals("This is the localized key 'Le texte Francais'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key 'list key class'.\n", template_sql.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.SQL, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nSqlSeveralResourcebundles()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_l10n");
		assertEquals("This is the localized key 'default value'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_sql.getContent());

		template_sql.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_sql.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'list key'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_sql.getContent());

		template_sql.clear();
		template_sql.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_sql.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_sql.getContent());

		template_sql.clear();
		template_sql.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_sql.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("This is the localized key 'The English text'.\nThis is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\nThis is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_sql.getContent());
	}

	public void testFilteredTagsL10nJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_l10n");
		assertEquals("// This is the localized key 'default value'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_java.getContent());

		template_java.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("// This is the localized key 'The English text'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_java.getContent());

		template_java.clear();
		template_java.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "nl"));
		assertEquals("// This is the localized key 'De Nederlandse tekst'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_java.getContent());

		template_java.clear();
		template_java.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "fr"));
		assertEquals("// This is the localized key 'Le texte Francais'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_java.getContent());
	}

	public void testFilteredTagsL10nJavaDefaultResourceBundles()
	throws Exception
	{
		Template template_java;

		ArrayList<String> bundles = new ArrayList<String>();
		bundles.add("localization/filtered-tags-l10n");
		bundles.add("com.uwyn.rife.template.TestResourceBundleClass");
		RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.JAVA, bundles);

		try
		{
			RifeConfig.Tools.setDefaultLanguage("en");
			template_java = TemplateFactory.JAVA.get("filtered_tags_l10n");
			assertEquals("// This is the localized key 'The English text'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key 'list key class'.\n", template_java.getContent());

			RifeConfig.Tools.setDefaultLanguage("nl");
			template_java = TemplateFactory.JAVA.get("filtered_tags_l10n");
			assertEquals("// This is the localized key 'De Nederlandse tekst'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key 'list key class'.\n", template_java.getContent());

			RifeConfig.Tools.setDefaultLanguage("fr");
			template_java = TemplateFactory.JAVA.get("filtered_tags_l10n");
			assertEquals("// This is the localized key 'Le texte Francais'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key 'list key class'.\n", template_java.getContent());
		}
		finally
		{
			RifeConfig.Template.setDefaultResourcebundles(TemplateFactory.JAVA, null);
			RifeConfig.Tools.setDefaultLanguage("en");
		}
	}

	public void testFilteredTagsL10nJavaSeveralResourcebundles()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_l10n");
		assertEquals("// This is the localized key 'default value'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_java.getContent());

		template_java.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		template_java.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("// This is the localized key 'list key'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_java.getContent());

		template_java.clear();
		template_java.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		template_java.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_KEY", "list key"}
				};
			}});
		assertEquals("// This is the localized key 'The English text'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_java.getContent());

		template_java.clear();
		template_java.addResourceBundle(new ListResourceBundle() {
			public Object[][] getContents()
			{
				return new Object[][] {
					{"THE_OTHER_KEY", "list key"}
				};
			}});
		template_java.addResourceBundle(Localization.getResourceBundle("localization/filtered-tags-l10n", "en"));
		assertEquals("// This is the localized key 'The English text'.\n// This is an unknown key '[!V 'L10N:UNKNOWN_KEY'/]'.\n// This is a class key '[!V 'L10N:THE_CLASS_KEY'/]'.\n", template_java.getContent());
	}

	public void testFilteredTagsLangEngineHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_lang");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_html.getContent());

		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_html.getContent());

		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_html.getContent());

		RifeConfig.Tools.setDefaultLanguage(null);

		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_lang");
		template_html.setLanguage("en");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_html.getContent());

		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_lang");
		template_html.setLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_html.getContent());

		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_lang");
		template_html.setLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsLangEngineXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_lang");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_xhtml.getContent());

		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_xhtml.getContent());

		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_xhtml.getContent());

		RifeConfig.Tools.setDefaultLanguage(null);

		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_lang");
		template_xhtml.setLanguage("en");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_xhtml.getContent());

		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_lang");
		template_xhtml.setLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_xhtml.getContent());

		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_lang");
		template_xhtml.setLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsLangEngineXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_lang");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_xml.getContent());

		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_xml.getContent());

		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_xml.getContent());

		RifeConfig.Tools.setDefaultLanguage(null);

		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_lang");
		template_xml.setLanguage("en");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_xml.getContent());

		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_lang");
		template_xml.setLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_xml.getContent());

		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_lang");
		template_xml.setLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsLangEngineTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_lang");
		assertEquals("This expression is Dutch '<!V 'LANG:value1'/>'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_txt.getContent());

		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_txt.getContent());

		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("fr");
		assertEquals("This expression is Dutch '<!V 'LANG:value1'/>'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_txt.getContent());

		RifeConfig.Tools.setDefaultLanguage(null);

		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_lang");
		template_txt.setLanguage("en");
		assertEquals("This expression is Dutch '<!V 'LANG:value1'/>'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_txt.getContent());

		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_lang");
		template_txt.setLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_txt.getContent());

		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_lang");
		template_txt.setLanguage("fr");
		assertEquals("This expression is Dutch '<!V 'LANG:value1'/>'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsLangHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_lang");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_html.getContent());

		template_html = TemplateFactory.HTML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_html.getContent());

		template_html = TemplateFactory.HTML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_html.getContent());

		RifeConfig.Tools.setDefaultLanguage(null);

		template_html = TemplateFactory.HTML.get("filtered_tags_lang");
		template_html.setLanguage("en");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_html.getContent());

		template_html = TemplateFactory.HTML.get("filtered_tags_lang");
		template_html.setLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_html.getContent());

		template_html = TemplateFactory.HTML.get("filtered_tags_lang");
		template_html.setLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsLangXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_lang");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_xhtml.getContent());

		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_xhtml.getContent());

		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_xhtml.getContent());

		RifeConfig.Tools.setDefaultLanguage(null);

		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_lang");
		template_xhtml.setLanguage("en");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_xhtml.getContent());

		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_lang");
		template_xhtml.setLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_xhtml.getContent());

		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_lang");
		template_xhtml.setLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsLangXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_lang");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_xml.getContent());

		template_xml = TemplateFactory.XML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_xml.getContent());

		template_xml = TemplateFactory.XML.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_xml.getContent());

		RifeConfig.Tools.setDefaultLanguage(null);

		template_xml = TemplateFactory.XML.get("filtered_tags_lang");
		template_xml.setLanguage("en");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_xml.getContent());

		template_xml = TemplateFactory.XML.get("filtered_tags_lang");
		template_xml.setLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_xml.getContent());

		template_xml = TemplateFactory.XML.get("filtered_tags_lang");
		template_xml.setLanguage("fr");
		assertEquals("This expression is Dutch '<!--V 'LANG:value1'/-->'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsLangSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_lang");
		assertEquals("This expression is Dutch '/*V 'LANG:value1'-*/'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_sql.getContent());

		template_sql = TemplateFactory.SQL.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_sql.getContent());

		template_sql = TemplateFactory.SQL.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("fr");
		assertEquals("This expression is Dutch '/*V 'LANG:value1'-*/'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_sql.getContent());

		RifeConfig.Tools.setDefaultLanguage(null);

		template_sql = TemplateFactory.SQL.get("filtered_tags_lang");
		template_sql.setLanguage("en");
		assertEquals("This expression is Dutch '/*V 'LANG:value1'-*/'.\nThis expression is French or English 'yes yes'.\n\n\n\n", template_sql.getContent());

		template_sql = TemplateFactory.SQL.get("filtered_tags_lang");
		template_sql.setLanguage("nl");
		assertEquals("This expression is Dutch 'ja ja'.\nThis expression is French or English '[!V 'LANG:value2'/]'.\n\n\n\n", template_sql.getContent());

		template_sql = TemplateFactory.SQL.get("filtered_tags_lang");
		template_sql.setLanguage("fr");
		assertEquals("This expression is Dutch '/*V 'LANG:value1'-*/'.\nThis expression is French or English 'oui oui'.\n\n\n\n", template_sql.getContent());
	}

	public void testFilteredTagsLangJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_lang");
		assertEquals("//This expression is Dutch '/*V 'LANG:value1'-*/'.\n//This expression is French or English 'yes yes'.\n//\n//\n//\n", template_java.getContent());

		template_java = TemplateFactory.JAVA.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("nl");
		assertEquals("//This expression is Dutch 'ja ja'.\n//This expression is French or English '[!V 'LANG:value2'/]'.\n//\n//\n//\n", template_java.getContent());

		template_java = TemplateFactory.JAVA.get("filtered_tags_lang");
		RifeConfig.Tools.setDefaultLanguage("fr");
		assertEquals("//This expression is Dutch '/*V 'LANG:value1'-*/'.\n//This expression is French or English 'oui oui'.\n//\n//\n//\n", template_java.getContent());

		RifeConfig.Tools.setDefaultLanguage(null);

		template_java = TemplateFactory.JAVA.get("filtered_tags_lang");
		template_java.setLanguage("en");
		assertEquals("//This expression is Dutch '/*V 'LANG:value1'-*/'.\n//This expression is French or English 'yes yes'.\n//\n//\n//\n", template_java.getContent());

		template_java = TemplateFactory.JAVA.get("filtered_tags_lang");
		template_java.setLanguage("nl");
		assertEquals("//This expression is Dutch 'ja ja'.\n//This expression is French or English '[!V 'LANG:value2'/]'.\n//\n//\n//\n", template_java.getContent());

		template_java = TemplateFactory.JAVA.get("filtered_tags_lang");
		template_java.setLanguage("fr");
		assertEquals("//This expression is Dutch '/*V 'LANG:value1'-*/'.\n//This expression is French or English 'oui oui'.\n//\n//\n//\n", template_java.getContent());
	}

	public void testFilteredTagsOgnlConfigEngineHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_ognl_config");
		template_html.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_ognl_config");
		template_html.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsOgnlConfigEngineXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_ognl_config");
		template_xhtml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_ognl_config");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsOgnlConfigEngineXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_ognl_config");
		template_xml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_ognl_config");
		template_xml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsOgnlConfigEngineTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_ognl_config");
		template_txt.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_ognl_config");
		template_txt.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsOgnlConfigHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_ognl_config");
		template_html.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.HTML.get("filtered_tags_ognl_config");
		template_html.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsOgnlConfigXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_ognl_config");
		template_xhtml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_ognl_config");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsOgnlConfigXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_ognl_config");
		template_xml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.XML.get("filtered_tags_ognl_config");
		template_xml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsOgnlConfigTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_ognl_config");
		template_txt.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.TXT.get("filtered_tags_ognl_config");
		template_txt.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsOgnlConfigSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_ognl_config");
		template_sql.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_sql.getContent());
		template_sql.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_sql = TemplateFactory.SQL.get("filtered_tags_ognl_config");
		template_sql.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n\n\n\n", template_sql.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());
	}

	public void testFilteredTagsOgnlConfigJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_ognl_config");
		template_java.setExpressionVar("thevalue", "the wrong value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\n//This config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		template_java.setExpressionVar("thevalue", "the value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\n//This config value expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_java = TemplateFactory.JAVA.get("filtered_tags_ognl_config");
		template_java.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\n//This config value expression is dynamic '[!V 'OGNL:CONFIG:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		map.put("thevalue", "the value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'OGNL:CONFIG:value2'/]'.\n//This config value expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());
	}

	public void testFilteredTagsOgnlEngineHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_ognl");
		template_html.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_ognl");
		template_html.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsOgnlEngineXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_ognl");
		template_xhtml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_ognl");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsOgnlEngineXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_ognl");
		template_xml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_ognl");
		template_xml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsOgnlEngineTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_ognl");
		template_txt.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_ognl");
		template_txt.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsOgnlHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_ognl");
		template_html.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.HTML.get("filtered_tags_ognl");
		template_html.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsOgnlXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_ognl");
		template_xhtml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_ognl");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsOgnlXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_ognl");
		template_xml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.XML.get("filtered_tags_ognl");
		template_xml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsOgnlTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_ognl");
		template_txt.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.TXT.get("filtered_tags_ognl");
		template_txt.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsOgnlSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_ognl");
		template_sql.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_sql.getContent());
		template_sql.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_sql = TemplateFactory.SQL.get("filtered_tags_ognl");
		template_sql.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic '[!V 'OGNL:value3'/]'.\n\n\n\n", template_sql.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'OGNL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());
	}

	public void testFilteredTagsOgnlJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_ognl");
		template_java.setExpressionVar("thevalue", false);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'OGNL:value2'/]'.\n//This expression is dynamic '[!V 'OGNL:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		template_java.setExpressionVar("thevalue", true);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'OGNL:value2'/]'.\n//This expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_java = TemplateFactory.JAVA.get("filtered_tags_ognl");
		template_java.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'OGNL:value2'/]'.\n//This expression is dynamic '[!V 'OGNL:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		map.put("thevalue", true);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'OGNL:value2'/]'.\n//This expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());
	}

	public void testFilteredTagsMvelConfigEngineHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_mvel_config");
		template_html.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_mvel_config");
		template_html.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsMvelConfigEngineXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_mvel_config");
		template_xhtml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_mvel_config");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsMvelConfigEngineXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_mvel_config");
		template_xml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_mvel_config");
		template_xml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsMvelConfigEngineTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_mvel_config");
		template_txt.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_mvel_config");
		template_txt.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsMvelConfigHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_mvel_config");
		template_html.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.HTML.get("filtered_tags_mvel_config");
		template_html.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsMvelConfigXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_mvel_config");
		template_xhtml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_mvel_config");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsMvelConfigXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_mvel_config");
		template_xml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.XML.get("filtered_tags_mvel_config");
		template_xml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsMvelConfigTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_mvel_config");
		template_txt.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.TXT.get("filtered_tags_mvel_config");
		template_txt.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsMvelConfigSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_mvel_config");
		template_sql.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_sql.getContent());
		template_sql.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_sql = TemplateFactory.SQL.get("filtered_tags_mvel_config");
		template_sql.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n\n\n\n", template_sql.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());
	}

	public void testFilteredTagsMvelConfigJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_mvel_config");
		template_java.setExpressionVar("thevalue", "the wrong value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\n//This config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		template_java.setExpressionVar("thevalue", "the value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\n//This config value expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_java = TemplateFactory.JAVA.get("filtered_tags_mvel_config");
		template_java.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\n//This config value expression is dynamic '[!V 'MVEL:CONFIG:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		map.put("thevalue", "the value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'MVEL:CONFIG:value2'/]'.\n//This config value expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());
	}

	public void testFilteredTagsMvelEngineHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_mvel");
		template_html.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_mvel");
		template_html.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsMvelEngineXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_mvel");
		template_xhtml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_mvel");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsMvelEngineXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_mvel");
		template_xml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_mvel");
		template_xml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsMvelEngineTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_mvel");
		template_txt.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_mvel");
		template_txt.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsMvelHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_mvel");
		template_html.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.HTML.get("filtered_tags_mvel");
		template_html.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsMvelXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_mvel");
		template_xhtml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_mvel");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsMvelXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_mvel");
		template_xml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.XML.get("filtered_tags_mvel");
		template_xml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsMvelTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_mvel");
		template_txt.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.TXT.get("filtered_tags_mvel");
		template_txt.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsMvelSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_mvel");
		template_sql.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_sql.getContent());
		template_sql.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_sql = TemplateFactory.SQL.get("filtered_tags_mvel");
		template_sql.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic '[!V 'MVEL:value3'/]'.\n\n\n\n", template_sql.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'MVEL:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());
	}

	public void testFilteredTagsMvelJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_mvel");
		template_java.setExpressionVar("thevalue", false);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'MVEL:value2'/]'.\n//This expression is dynamic '[!V 'MVEL:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		template_java.setExpressionVar("thevalue", true);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'MVEL:value2'/]'.\n//This expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_java = TemplateFactory.JAVA.get("filtered_tags_mvel");
		template_java.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'MVEL:value2'/]'.\n//This expression is dynamic '[!V 'MVEL:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		map.put("thevalue", true);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'MVEL:value2'/]'.\n//This expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());
	}

	public void testFilteredTagsGroovyConfigEngineHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_groovy_config");
		template_html.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '<r:v name=\"GROOVY:CONFIG:value2\"/>'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '<r:v name=\"GROOVY:CONFIG:value2\"/>'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_groovy_config");
		template_html.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '<r:v name=\"GROOVY:CONFIG:value2\"/>'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '<r:v name=\"GROOVY:CONFIG:value2\"/>'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsGroovyConfigEngineXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_groovy_config");
		template_xhtml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_groovy_config");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsGroovyConfigEngineXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_groovy_config");
		template_xml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_groovy_config");
		template_xml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsGroovyConfigEngineTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_groovy_config");
		template_txt.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_groovy_config");
		template_txt.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsGroovyConfigHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_groovy_config");
		template_html.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '<r:v name=\"GROOVY:CONFIG:value2\"/>'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '<r:v name=\"GROOVY:CONFIG:value2\"/>'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.HTML.get("filtered_tags_groovy_config");
		template_html.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '<r:v name=\"GROOVY:CONFIG:value2\"/>'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '<r:v name=\"GROOVY:CONFIG:value2\"/>'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsGroovyConfigXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_groovy_config");
		template_xhtml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_groovy_config");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsGroovyConfigXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_groovy_config");
		template_xml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.XML.get("filtered_tags_groovy_config");
		template_xml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsGroovyConfigTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_groovy_config");
		template_txt.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.TXT.get("filtered_tags_groovy_config");
		template_txt.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsGroovyConfigSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_groovy_config");
		template_sql.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_sql.getContent());
		template_sql.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_sql = TemplateFactory.SQL.get("filtered_tags_groovy_config");
		template_sql.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n\n\n\n", template_sql.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());
	}

	public void testFilteredTagsGroovyConfigJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_groovy_config");
		template_java.setExpressionVar("thevalue", "the wrong value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\n//This config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		template_java.setExpressionVar("thevalue", "the value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\n//This config value expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_java = TemplateFactory.JAVA.get("filtered_tags_groovy_config");
		template_java.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\n//This config value expression is dynamic '[!V 'GROOVY:CONFIG:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		map.put("thevalue", "the value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'GROOVY:CONFIG:value2'/]'.\n//This config value expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());
	}

	public void testFilteredTagsGroovyEngineHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_groovy");
		template_html.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '<r:v name=\"GROOVY:value3\"/>'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_groovy");
		template_html.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '<r:v name=\"GROOVY:value3\"/>'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsGroovyEngineXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_groovy");
		template_xhtml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_groovy");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsGroovyEngineXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_groovy");
		template_xml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_groovy");
		template_xml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsGroovyEngineTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_groovy");
		template_txt.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_groovy");
		template_txt.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsGroovyHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_groovy");
		template_html.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '<r:v name=\"GROOVY:value3\"/>'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.HTML.get("filtered_tags_groovy");
		template_html.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '<r:v name=\"GROOVY:value3\"/>'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsGroovyXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_groovy");
		template_xhtml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_groovy");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsGroovyXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_groovy");
		template_xml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.XML.get("filtered_tags_groovy");
		template_xml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsGroovyTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_groovy");
		template_txt.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.TXT.get("filtered_tags_groovy");
		template_txt.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsGroovySql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_groovy");
		template_sql.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_sql.getContent());
		template_sql.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_sql = TemplateFactory.SQL.get("filtered_tags_groovy");
		template_sql.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic '[!V 'GROOVY:value3'/]'.\n\n\n\n", template_sql.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'GROOVY:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());
	}

	public void testFilteredTagsGroovyJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_groovy");
		template_java.setExpressionVar("thevalue", false);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'GROOVY:value2'/]'.\n//This expression is dynamic '[!V 'GROOVY:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		template_java.setExpressionVar("thevalue", true);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'GROOVY:value2'/]'.\n//This expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_java = TemplateFactory.JAVA.get("filtered_tags_groovy");
		template_java.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'GROOVY:value2'/]'.\n//This expression is dynamic '[!V 'GROOVY:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		map.put("thevalue", true);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'GROOVY:value2'/]'.\n//This expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());
	}

	public void testFilteredTagsJaninoConfigEngineHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_janino_config");
		template_html.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_janino_config");
		template_html.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsJaninoConfigEngineXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_janino_config");
		template_xhtml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '${v JANINO:CONFIG:value3/}'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_janino_config");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '${v JANINO:CONFIG:value3/}'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsJaninoConfigEngineXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_janino_config");
		template_xml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_janino_config");
		template_xml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsJaninoConfigEngineTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_janino_config");
		template_txt.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_janino_config");
		template_txt.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsJaninoConfigHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_janino_config");
		template_html.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.HTML.get("filtered_tags_janino_config");
		template_html.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsJaninoConfigXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_janino_config");
		template_xhtml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '${v JANINO:CONFIG:value3/}'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_janino_config");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '${v JANINO:CONFIG:value3/}'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsJaninoConfigXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_janino_config");
		template_xml.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.XML.get("filtered_tags_janino_config");
		template_xml.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsJaninoConfigTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_janino_config");
		template_txt.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.TXT.get("filtered_tags_janino_config");
		template_txt.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsJaninoConfigSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_janino_config");
		template_sql.setExpressionVar("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_sql.getContent());
		template_sql.setExpressionVar("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_sql = TemplateFactory.SQL.get("filtered_tags_janino_config");
		template_sql.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n\n\n\n", template_sql.getContent());
		map.put("thevalue", "the value");
		assertEquals("This config value expression is true 'true value'.\nThis config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\nThis config value expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());
	}

	public void testFilteredTagsJaninoConfigJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_janino_config");
		template_java.setExpressionVar("thevalue", "the wrong value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\n//This config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		template_java.setExpressionVar("thevalue", "the value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\n//This config value expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_java = TemplateFactory.JAVA.get("filtered_tags_janino_config");
		template_java.setExpressionVars(map);
		map.put("thevalue", "the wrong value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\n//This config value expression is dynamic '[!V 'JANINO:CONFIG:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		map.put("thevalue", "the value");
		assertEquals("//This config value expression is true 'true value'.\n//This config value expression is false '[!V 'JANINO:CONFIG:value2'/]'.\n//This config value expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());
	}

	public void testFilteredTagsJaninoEngineHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_janino");
		template_html.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.ENGINEHTML.get("filtered_tags_janino");
		template_html.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsJaninoEngineXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_janino");
		template_xhtml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.ENGINEXHTML.get("filtered_tags_janino");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsJaninoEngineXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_janino");
		template_xml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.ENGINEXML.get("filtered_tags_janino");
		template_xml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsJaninoEngineTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_janino");
		template_txt.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.ENGINETXT.get("filtered_tags_janino");
		template_txt.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsJaninoHtml()
	throws Exception
	{
		Template template_html = TemplateFactory.HTML.get("filtered_tags_janino");
		template_html.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_html.getContent());
		template_html.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_html = TemplateFactory.HTML.get("filtered_tags_janino");
		template_html.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_html.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_html.getContent());
	}

	public void testFilteredTagsJaninoXhtml()
	throws Exception
	{
		Template template_xhtml = TemplateFactory.XHTML.get("filtered_tags_janino");
		template_xhtml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		template_xhtml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xhtml = TemplateFactory.XHTML.get("filtered_tags_janino");
		template_xhtml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_xhtml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xhtml.getContent());
	}

	public void testFilteredTagsJaninoXml()
	throws Exception
	{
		Template template_xml = TemplateFactory.XML.get("filtered_tags_janino");
		template_xml.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_xml.getContent());
		template_xml.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_xml = TemplateFactory.XML.get("filtered_tags_janino");
		template_xml.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_xml.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_xml.getContent());
	}

	public void testFilteredTagsJaninoTxt()
	throws Exception
	{
		Template template_txt = TemplateFactory.TXT.get("filtered_tags_janino");
		template_txt.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_txt.getContent());
		template_txt.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_txt = TemplateFactory.TXT.get("filtered_tags_janino");
		template_txt.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_txt.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_txt.getContent());
	}

	public void testFilteredTagsJaninoSql()
	throws Exception
	{
		Template template_sql = TemplateFactory.SQL.get("filtered_tags_janino");
		template_sql.setExpressionVar("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_sql.getContent());
		template_sql.setExpressionVar("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_sql = TemplateFactory.SQL.get("filtered_tags_janino");
		template_sql.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic '[!V 'JANINO:value3'/]'.\n\n\n\n", template_sql.getContent());
		map.put("thevalue", true);
		assertEquals("This expression is true 'true value'.\nThis expression is false '[!V 'JANINO:value2'/]'.\nThis expression is dynamic 'dynamic value'.\n\n\n\n", template_sql.getContent());
	}

	public void testFilteredTagsJaninoJava()
	throws Exception
	{
		Template template_java = TemplateFactory.JAVA.get("filtered_tags_janino");
		template_java.setExpressionVar("thevalue", false);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'JANINO:value2'/]'.\n//This expression is dynamic '[!V 'JANINO:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		template_java.setExpressionVar("thevalue", true);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'JANINO:value2'/]'.\n//This expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());

		Map<String, Object> map = new HashMap<String, Object>();
		template_java = TemplateFactory.JAVA.get("filtered_tags_janino");
		template_java.setExpressionVars(map);
		map.put("thevalue", false);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'JANINO:value2'/]'.\n//This expression is dynamic '[!V 'JANINO:value3'/]'.\n//\n//\n//\n", template_java.getContent());
		map.put("thevalue", true);
		assertEquals("//This expression is true 'true value'.\n//This expression is false '[!V 'JANINO:value2'/]'.\n//This expression is dynamic 'dynamic value'.\n//\n//\n//\n", template_java.getContent());
	}

	public void testEncoding()
	{
		Template template_iso8859_1 = null;
		Template template_utf_8 = null;
		try
		{
			template_iso8859_1 = TemplateFactory.TXT.get("encoding_latin1_iso88591", "ISO8859-1");
			assertNotNull(template_iso8859_1);
			template_utf_8 = TemplateFactory.TXT.get("encoding_latin1_utf8", "UTF-8");
			assertNotNull(template_utf_8);
			assertEquals(template_iso8859_1.getContent(), template_utf_8.getContent());
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testCaching()
	{
		Template template1 = null;
		Template template2 = null;
		try
		{
			template1 = TemplateFactory.HTML.get("defaultvalues_in");
			assertNotNull(template1);
			template2 = TemplateFactory.HTML.get("defaultvalues_in");
			assertNotNull(template2);
			assertTrue(template1 != template2);
			assertSame(template1.getClass(), template2.getClass());
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testPackageFullName()
	{
		Template template = TemplateFactory.HTML.get("test_package.noblocks_in");
		assertNotNull(template);
		assertEquals("test_package.noblocks_in", template.getFullName());
	}
	
	public void testTemplatesInPackageCaching()
	{
		Template template1 = null;
		Template template2 = null;
		try
		{
			template1 = TemplateFactory.HTML.get("test_package.noblocks_in");
			assertNotNull(template1);
			template2 = TemplateFactory.HTML.get("test_package.noblocks_in");
			assertNotNull(template2);
			assertTrue(template1 != template2);
			assertSame(template1.getClass(), template2.getClass());
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testTemplatesInPackagePath()
	{
		Template template1 = null;
		Template template2 = null;
		try
		{
			template1 = TemplateFactory.HTML.get("test_package/noblocks_in.html");
			assertNotNull(template1);
			template2 = TemplateFactory.HTML.get("test_package/noblocks_in.html");
			assertNotNull(template2);
			assertTrue(template1 != template2);
			assertSame(template1.getClass(), template2.getClass());
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testFilteredBlocks()
	{
		String	filter1 = "^FILTER1:(\\w+):CONST:(\\w+)$";
		String	filter2 = "^FILTER2:(\\w+)$";
		String	filter3 = "^CONST-FILTER3:(\\w+)$";
		String 	filter4 = "(\\w+)";

		TemplateFactory	factory = null;
		Template		template = null;

		try
		{
			factory = new TemplateFactory(ResourceFinderClasspath.getInstance(), "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, "text/html", ".html", new String[] {filter1, filter2, filter3, filter4}, null, null, null, null);

			template = factory.get("blocks_filtered_in");

			assertTrue(template.hasFilteredBlocks(filter1));
			assertTrue(template.hasFilteredBlocks(filter2));
			assertTrue(template.hasFilteredBlocks(filter3));
			assertFalse(template.hasFilteredBlocks(filter4));

			List<String[]>	filtered_blocks = null;

			filtered_blocks = template.getFilteredBlocks(filter1);
			assertEquals(3, filtered_blocks.size());

			boolean filter1_got_block1 = false;
			boolean filter1_got_block2 = false;
			boolean filter1_got_block3 = false;
			for (String[] block_groups : filtered_blocks)
			{
				assertEquals(3, block_groups.length);
				if (block_groups[0].equals("FILTER1:BLOCK1a:CONST:BLOCK1b") &&
					block_groups[1].equals("BLOCK1a") &&
					block_groups[2].equals("BLOCK1b"))
				{
					filter1_got_block1 = true;
				}
				else if (block_groups[0].equals("FILTER1:BLOCK2a:CONST:BLOCK2b") &&
					block_groups[1].equals("BLOCK2a") &&
					block_groups[2].equals("BLOCK2b"))
				{
					filter1_got_block2 = true;
				}
				else if (block_groups[0].equals("FILTER1:BLOCK3a:CONST:BLOCK3b") &&
					block_groups[1].equals("BLOCK3a") &&
					block_groups[2].equals("BLOCK3b"))
				{
					filter1_got_block3 = true;
				}
			}
			assertTrue(filter1_got_block1 && filter1_got_block2 && filter1_got_block3);

			filtered_blocks = template.getFilteredBlocks(filter2);
			assertEquals(2, filtered_blocks.size());

			boolean filter2_got_block1 = false;
			boolean filter2_got_block2 = false;
			for (String[] block_groups : filtered_blocks)
			{
				assertEquals(2, block_groups.length);
				if (block_groups[0].equals("FILTER2:BLOCK1") &&
					block_groups[1].equals("BLOCK1"))
				{
					filter2_got_block1 = true;
				}
				else if (block_groups[0].equals("FILTER2:BLOCK2") &&
					block_groups[1].equals("BLOCK2"))
				{
					filter2_got_block2 = true;
				}
			}
			assertTrue(filter2_got_block1 && filter2_got_block2);

			filtered_blocks = template.getFilteredBlocks(filter3);
			assertEquals(2, filtered_blocks.size());

			boolean filter3_got_block1 = false;
			boolean filter3_got_block2 = false;
			for (String[] block_groups : filtered_blocks)
			{
				assertEquals(2, block_groups.length);
				if (block_groups[0].equals("CONST-FILTER3:BLOCK1") &&
					block_groups[1].equals("BLOCK1"))
				{
					filter3_got_block1 = true;
				}
				else if (block_groups[0].equals("CONST-FILTER3:BLOCK2") &&
					block_groups[1].equals("BLOCK2"))
				{
					filter3_got_block2 = true;
				}
			}
			assertTrue(filter3_got_block1 && filter3_got_block2);

			factory = new TemplateFactory(ResourceFinderClasspath.getInstance(), "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, "text/html", ".html", new String[] {filter4, filter1, filter2, filter3}, null, null, null, null);

			template = factory.get("blocks_filtered_in");

			assertFalse(template.hasFilteredBlocks(filter1));
			assertFalse(template.hasFilteredBlocks(filter2));
			assertFalse(template.hasFilteredBlocks(filter3));
			assertTrue(template.hasFilteredBlocks(filter4));

			filtered_blocks = template.getFilteredBlocks(filter4);
			assertEquals(7, filtered_blocks.size());

			boolean filter4_got_block1 = false;
			boolean filter4_got_block2 = false;
			boolean filter4_got_block3 = false;
			boolean filter4_got_block4 = false;
			boolean filter4_got_block5 = false;
			boolean filter4_got_block6 = false;
			boolean filter4_got_block7 = false;
			for (String[] block_groups : filtered_blocks)
			{
				if (block_groups[0].equals("FILTER1:BLOCK1a:CONST:BLOCK1b") &&
					block_groups[1].equals("FILTER1") &&
					block_groups[2].equals("BLOCK1a") &&
					block_groups[3].equals("CONST") &&
					block_groups[4].equals("BLOCK1b"))
				{
					assertEquals(5, block_groups.length);
					filter4_got_block1 = true;
					continue;
				}
				if (block_groups[0].equals("FILTER1:BLOCK2a:CONST:BLOCK2b") &&
					block_groups[1].equals("FILTER1") &&
					block_groups[2].equals("BLOCK2a") &&
					block_groups[3].equals("CONST") &&
					block_groups[4].equals("BLOCK2b"))
				{
					assertEquals(5, block_groups.length);
					filter4_got_block2 = true;
					continue;
				}
				if (block_groups[0].equals("FILTER1:BLOCK3a:CONST:BLOCK3b") &&
					block_groups[1].equals("FILTER1") &&
					block_groups[2].equals("BLOCK3a") &&
					block_groups[3].equals("CONST") &&
					block_groups[4].equals("BLOCK3b"))
				{
					assertEquals(5, block_groups.length);
					filter4_got_block3 = true;
					continue;
				}
				if (block_groups[0].equals("FILTER2:BLOCK1") &&
					block_groups[1].equals("FILTER2") &&
					block_groups[2].equals("BLOCK1"))
				{
					assertEquals(3, block_groups.length);
					filter4_got_block4 = true;
					continue;
				}
				if (block_groups[0].equals("FILTER2:BLOCK2") &&
					block_groups[1].equals("FILTER2") &&
					block_groups[2].equals("BLOCK2"))
				{
					assertEquals(3, block_groups.length);
					filter4_got_block5 = true;
					continue;
				}
				if (block_groups[0].equals("CONST-FILTER3:BLOCK1") &&
					block_groups[1].equals("CONST") &&
					block_groups[2].equals("FILTER3") &&
					block_groups[3].equals("BLOCK1"))
				{
					assertEquals(4, block_groups.length);
					filter4_got_block6 = true;
					continue;
				}
				if (block_groups[0].equals("CONST-FILTER3:BLOCK2") &&
					block_groups[1].equals("CONST") &&
					block_groups[2].equals("FILTER3") &&
					block_groups[3].equals("BLOCK2"))
				{
					assertEquals(4, block_groups.length);
					filter4_got_block7 = true;
					continue;
				}
			}
			assertTrue(filter4_got_block1 && filter4_got_block2 && filter4_got_block3 &&
				filter4_got_block4 && filter4_got_block5 && filter4_got_block6 &&
				filter4_got_block7);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testFilteredValues()
	{
		String	filter1 = "^FILTER1:(\\w+):CONST:(\\w+)$";
		String	filter2 = "^FILTER2:(\\w+)$";
		String	filter3 = "^CONST-FILTER3:(\\w+)$";
		String 	filter4 = "(\\w+)";

		TemplateFactory	factory = null;
		Template		template = null;

		try
		{
			factory = new TemplateFactory(ResourceFinderClasspath.getInstance(), "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, "text/html", ".html", null, new String[] {filter1, filter2, filter3, filter4}, null, null, null);

			template = factory.get("values_filtered_in");

			assertTrue(template.hasFilteredValues(filter1));
			assertTrue(template.hasFilteredValues(filter2));
			assertTrue(template.hasFilteredValues(filter3));
			assertFalse(template.hasFilteredValues(filter4));

			List<String[]>	filtered_values = null;

			filtered_values = template.getFilteredValues(filter1);
			assertEquals(3, filtered_values.size());

			boolean filter1_got_value1 = false;
			boolean filter1_got_value2 = false;
			boolean filter1_got_value3 = false;
			for (String[] value_groups : filtered_values)
			{
				assertEquals(3, value_groups.length);
				if (value_groups[0].equals("FILTER1:VALUE1a:CONST:VALUE1b") &&
					value_groups[1].equals("VALUE1a") &&
					value_groups[2].equals("VALUE1b"))
				{
					filter1_got_value1 = true;
				}
				else if (value_groups[0].equals("FILTER1:VALUE2a:CONST:VALUE2b") &&
					value_groups[1].equals("VALUE2a") &&
					value_groups[2].equals("VALUE2b"))
				{
					filter1_got_value2 = true;
				}
				else if (value_groups[0].equals("FILTER1:VALUE3a:CONST:VALUE3b") &&
					value_groups[1].equals("VALUE3a") &&
					value_groups[2].equals("VALUE3b"))
				{
					filter1_got_value3 = true;
				}
			}
			assertTrue(filter1_got_value1 && filter1_got_value2 && filter1_got_value3);

			filtered_values = template.getFilteredValues(filter2);
			assertEquals(2, filtered_values.size());

			boolean filter2_got_value1 = false;
			boolean filter2_got_value2 = false;
			for (String[] value_groups : filtered_values)
			{
				assertEquals(2, value_groups.length);
				if (value_groups[0].equals("FILTER2:VALUE1") &&
					value_groups[1].equals("VALUE1"))
				{
					filter2_got_value1 = true;
				}
				else if (value_groups[0].equals("FILTER2:VALUE2") &&
					value_groups[1].equals("VALUE2"))
				{
					filter2_got_value2 = true;
				}
			}
			assertTrue(filter2_got_value1 && filter2_got_value2);

			filtered_values = template.getFilteredValues(filter3);
			assertEquals(2, filtered_values.size());

			boolean filter3_got_value1 = false;
			boolean filter3_got_value2 = false;
			for (String[] value_groups : filtered_values)
			{
				assertEquals(2, value_groups.length);
				if (value_groups[0].equals("CONST-FILTER3:VALUE1") &&
					value_groups[1].equals("VALUE1"))
				{
					filter3_got_value1 = true;
				}
				else if (value_groups[0].equals("CONST-FILTER3:VALUE2") &&
					value_groups[1].equals("VALUE2"))
				{
					filter3_got_value2 = true;
				}
			}
			assertTrue(filter3_got_value1 && filter3_got_value2);

			factory = new TemplateFactory(ResourceFinderClasspath.getInstance(), "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, "text/html", ".html", null, new String[] {filter4, filter1, filter2, filter3}, null, null, null);

			template = factory.get("values_filtered_in");

			assertFalse(template.hasFilteredValues(filter1));
			assertFalse(template.hasFilteredValues(filter2));
			assertFalse(template.hasFilteredValues(filter3));
			assertTrue(template.hasFilteredValues(filter4));

			filtered_values = template.getFilteredValues(filter4);
			assertEquals(7, filtered_values.size());

			boolean filter4_got_value1 = false;
			boolean filter4_got_value2 = false;
			boolean filter4_got_value3 = false;
			boolean filter4_got_value4 = false;
			boolean filter4_got_value5 = false;
			boolean filter4_got_value6 = false;
			boolean filter4_got_value7 = false;
			for (String[] value_groups : filtered_values)
			{
				if (value_groups[0].equals("FILTER1:VALUE1a:CONST:VALUE1b") &&
					value_groups[1].equals("FILTER1") &&
					value_groups[2].equals("VALUE1a") &&
					value_groups[3].equals("CONST") &&
					value_groups[4].equals("VALUE1b"))
				{
					assertEquals(5, value_groups.length);
					filter4_got_value1 = true;
					continue;
				}
				if (value_groups[0].equals("FILTER1:VALUE2a:CONST:VALUE2b") &&
					value_groups[1].equals("FILTER1") &&
					value_groups[2].equals("VALUE2a") &&
					value_groups[3].equals("CONST") &&
					value_groups[4].equals("VALUE2b"))
				{
					assertEquals(5, value_groups.length);
					filter4_got_value2 = true;
					continue;
				}
				if (value_groups[0].equals("FILTER1:VALUE3a:CONST:VALUE3b") &&
					value_groups[1].equals("FILTER1") &&
					value_groups[2].equals("VALUE3a") &&
					value_groups[3].equals("CONST") &&
					value_groups[4].equals("VALUE3b"))
				{
					assertEquals(5, value_groups.length);
					filter4_got_value3 = true;
					continue;
				}
				if (value_groups[0].equals("FILTER2:VALUE1") &&
					value_groups[1].equals("FILTER2") &&
					value_groups[2].equals("VALUE1"))
				{
					assertEquals(3, value_groups.length);
					filter4_got_value4 = true;
					continue;
				}
				if (value_groups[0].equals("FILTER2:VALUE2") &&
					value_groups[1].equals("FILTER2") &&
					value_groups[2].equals("VALUE2"))
				{
					assertEquals(3, value_groups.length);
					filter4_got_value5 = true;
					continue;
				}
				if (value_groups[0].equals("CONST-FILTER3:VALUE1") &&
					value_groups[1].equals("CONST") &&
					value_groups[2].equals("FILTER3") &&
					value_groups[3].equals("VALUE1"))
				{
					assertEquals(4, value_groups.length);
					filter4_got_value6 = true;
					continue;
				}
				if (value_groups[0].equals("CONST-FILTER3:VALUE2") &&
					value_groups[1].equals("CONST") &&
					value_groups[2].equals("FILTER3") &&
					value_groups[3].equals("VALUE2"))
				{
					assertEquals(4, value_groups.length);
					filter4_got_value7 = true;
					continue;
				}
			}
			assertTrue(filter4_got_value1 && filter4_got_value2 && filter4_got_value3 &&
				filter4_got_value4 && filter4_got_value5 && filter4_got_value6 &&
				filter4_got_value7);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testReloadBasic()
	{
		// setup the temporary directory
		String template_dir = RifeConfig.Global.getTempPath();
		File template_dir_file = new File(template_dir);
		template_dir_file.mkdirs();

		// setup the first template file
		URL template1_resource = TemplateFactory.HTML.getParser().resolve("defaultvalues_in");
		String template1_name = "reload_basic";
		File template1_file = new File(template_dir + File.separator + template1_name + TemplateFactory.HTML.getParser().getExtension());
		template1_file.delete();
		try
		{
			FileUtils.copy(template1_resource.openStream(), template1_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		// obtain the original template
		Template template1 = null;
		try
		{
			template1 = TemplateFactory.HTML.get(template1_name);
			assertNotNull(template1);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// wait a second
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// overwrite the template file with new content
		URL template2_resource = TemplateFactory.HTML.getParser().resolve("noblocks_in");
		try
		{
			FileUtils.copy(template2_resource.openStream(), template1_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		// obtain the modified template
		Template template2 = null;
		try
		{
			template2 = TemplateFactory.HTML.get(template1_name);
			assertNotNull(template2);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// verify if the template was correctly reloaded
		assertTrue(template1 != template2);
		assertTrue(template1.getClass() != template2.getClass());
		assertTrue(template1.getModificationTime() < template2.getModificationTime());
		assertFalse(template1.getContent().equals(template2.getContent()));

		// clean up the copied files
		template1_file.delete();
	}

	public void testReloadIncludes()
	{
		// setup the temporary directory
		String template_dir = RifeConfig.Global.getTempPath();
		File template_dir_file = new File(template_dir);
		template_dir_file.mkdirs();

		// setup the first template file with its included file
		URL template1_resource = TemplateFactory.HTML.getParser().resolve("includes_reload_master_in");
		String template1_name = "includes_reload_master";
		File template1_file = new File(template_dir + File.separator + template1_name + TemplateFactory.HTML.getParser().getExtension());
		template1_file.delete();
		URL template1_included_resource = TemplateFactory.HTML.getParser().resolve("defaultvalues_in");
		String template1_included_name = "includes_reload_included_in";
		File template1_included_file = new File(template_dir + File.separator + template1_included_name + TemplateFactory.HTML.getParser().getExtension());
		template1_included_file.delete();
		try
		{
			FileUtils.copy(template1_resource.openStream(), template1_file);
			FileUtils.copy(template1_included_resource.openStream(), template1_included_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		// obtain the original template
		Template template1 = null;
		try
		{
			template1 = TemplateFactory.HTML.get(template1_name);
			assertNotNull(template1);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// wait a second
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// modify the contents of the included file
		URL template1_included_resource2 = TemplateFactory.HTML.getParser().resolve("noblocks_in");
		try
		{
			FileUtils.copy(template1_included_resource2.openStream(), template1_included_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		// obtain the modified template
		Template template2 = null;
		try
		{
			template2 = TemplateFactory.HTML.get(template1_name);
			assertNotNull(template2);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// check if the template was correctly updated
		assertTrue(template1 != template2);
		assertTrue(template1.getClass() != template2.getClass());
		assertTrue(false == template1.getContent().equals(template2.getContent()));

		// clean up the copied files and the created dir
		template1_file.delete();
		template1_included_file.delete();
	}

	public void testReloadMultiLevelIncludes()
	{
		// setup the temporary directory
		String template_dir = RifeConfig.Global.getTempPath();
		File template_dir_file = new File(template_dir);
		template_dir_file.mkdirs();

		// setup the first template file with its included file
		URL template1_resource = TemplateFactory.HTML.getParser().resolve("includes_reload_multi_master_in");
		String template1_name = "includes_reload_multi_master";
		File template1_file = new File(template_dir + File.separator + template1_name + TemplateFactory.HTML.getParser().getExtension());
		template1_file.delete();

		URL template1_included_resource = TemplateFactory.HTML.getParser().resolve("defaultvalues_in");
		String template1_included_name = "includes_reload_multi_included2_in";
		File template1_included_file = new File(template_dir + File.separator + template1_included_name + TemplateFactory.HTML.getParser().getExtension());
		template1_included_file.delete();
		try
		{
			FileUtils.copy(template1_resource.openStream(), template1_file);
			FileUtils.copy(template1_included_resource.openStream(), template1_included_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		// obtain the original template
		Template template1 = null;
		try
		{
			template1 = TemplateFactory.HTML.get(template1_name);
			assertNotNull(template1);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// wait a second
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// modify the contents of the included file
		URL template1_included_resource2 = TemplateFactory.HTML.getParser().resolve("noblocks_in");
		try
		{
			FileUtils.copy(template1_included_resource2.openStream(), template1_included_file);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		// obtain the modified template
		Template template2 = null;
		try
		{
			template2 = TemplateFactory.HTML.get(template1_name);
			assertNotNull(template2);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// check if the template was correctly updated
		assertTrue(template1 != template2);
		assertTrue(template1.getClass() != template2.getClass());
		assertTrue(false == template1.getContent().equals(template2.getContent()));

		// clean up the copied files and the created dir
		template1_file.delete();
		template1_included_file.delete();
	}

	public void testReloadTransformationDependencies()
	{
		// setup the temporary directory
		String template_dir = RifeConfig.Global.getTempPath();
		File template_dir_file = new File(template_dir);
		template_dir_file.mkdirs();

		// setup the first template file with its dependencies
		URL template1_resource = TemplateFactory.XML.getParser().resolve("transformation_reload_master_in");
		String template1_name = "transformation_reload_master";
		File template1_file = new File(template_dir + File.separator + template1_name + TemplateFactory.XML.getParser().getExtension());
		template1_file.delete();

		URL template1_dependency_resource1 = ResourceFinderClasspath.getInstance().getResource("testxslt_stylesheet1.xsl");
		String template1_dependency_name1 = "transformation_reload_dependency1.xsl";
		File template1_dependency_file1 = new File(template_dir + File.separator + template1_dependency_name1);
		template1_dependency_file1.delete();

		URL template1_dependency_resource2 = ResourceFinderClasspath.getInstance().getResource("testxslt_stylesheet2.xsl");
		String template1_dependency_name2 = "transformation_reload_dependency2.xsl";
		File template1_dependency_file2 = new File(template_dir + File.separator + template1_dependency_name2);
		template1_dependency_file2.delete();

		try
		{
			FileUtils.copy(template1_resource.openStream(), template1_file);
			FileUtils.copy(template1_dependency_resource1.openStream(), template1_dependency_file1);
			FileUtils.copy(template1_dependency_resource2.openStream(), template1_dependency_file2);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}

		// setup the transformer
		TemplateTransformerXslt transformer = new TemplateTransformerXslt();
		transformer.addFilter(template1_dependency_name2);

		// obtain the original template
		Template template1 = null;
		try
		{
			template1 = TemplateFactory.XML.get(template1_name, transformer);
			assertNotNull(template1);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// wait a second
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// modify the contents of the second dependency file
		URL template1_dependency_resource3 = ResourceFinderClasspath.getInstance().getResource("testxslt_stylesheet3.xsl");
		try
		{
			FileUtils.copy(template1_dependency_resource3.openStream(), template1_dependency_file2);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}

		// obtain the modified template
		Template template2 = null;
		try
		{
			template2 = TemplateFactory.XML.get(template1_name, transformer);
			assertNotNull(template2);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// check if the template was correctly updated
		assertTrue(template1 != template2);
		assertTrue(template1.getClass() != template2.getClass());
		assertTrue(false == template1.getContent().equals(template2.getContent()));

		// wait a second
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// modify the contents of the second dependency file
		URL template1_dependency_resource4 = ResourceFinderClasspath.getInstance().getResource("testxslt_stylesheet2.xsl");
		try
		{
			FileUtils.copy(template1_dependency_resource4.openStream(), template1_dependency_file1);
		}
		catch (FileUtilsErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}
		catch (IOException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			return;
		}

		// obtain the modified template
		Template template3 = null;
		try
		{
			template3 = TemplateFactory.XML.get(template1_name, transformer);
			assertNotNull(template3);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		// check if the template was correctly updated
		assertTrue(template1 != template3);
		assertTrue(template2 != template3);
		assertTrue(template1.getClass() != template3.getClass());
		assertTrue(template2.getClass() != template3.getClass());
		assertTrue(false == template1.getContent().equals(template3.getContent()));
		assertTrue(false == template2.getContent().equals(template3.getContent()));

		// clean up the copied files and the created dir
		template1_file.delete();
		template1_dependency_file1.delete();
		template1_dependency_file2.delete();
	}

	public void testReloadTransformerState()
	{
		TemplateTransformerXslt transformer1 = new TemplateTransformerXslt();
		transformer1.addFilter("testxslt_stylesheet2.xsl");
		transformer1.addFilter("testxslt_stylesheet3.xsl");

		Template template1 = TemplateFactory.XML.get("testxslt_in", transformer1);

		TemplateTransformerXslt transformer2 = new TemplateTransformerXslt();
		transformer2.addFilter("testxslt_stylesheet2.xsl");
		transformer2.addFilter("testxslt_stylesheet3.xsl");

		Template template2 = TemplateFactory.XML.get("testxslt_in", transformer2);

		TemplateTransformerXslt transformer3 = new TemplateTransformerXslt();
		transformer3.addFilter("testxslt_stylesheet2.xsl");

		Template template3 = TemplateFactory.XML.get("testxslt_in", transformer3);

		TemplateTransformerXslt transformer4 = new TemplateTransformerXslt();
		transformer4.addFilter("testxslt_stylesheet2.xsl");
		transformer4.setOutputProperty(TemplateTransformerXslt.OUTPUT_USE_URL_ESCAPING, "false");

		Template template4 = TemplateFactory.XML.get("testxslt_in", transformer4);

		assertSame(template1.getClass(), template2.getClass());
		assertNotSame(template1.getClass(), template3.getClass());
		assertNotSame(template1.getClass(), template4.getClass());
		assertNotSame(template2.getClass(), template3.getClass());
		assertNotSame(template2.getClass(), template4.getClass());
		assertNotSame(template3.getClass(), template4.getClass());
	}

	public void testOtherResourceFinder()
	{
		DatabaseResources resources = DatabaseResourcesFactory.getInstance(Datasources.getRepInstance().getDatasource("unittestsderby"));
		try
		{
			resources.install();
			resources.addResource("db_template_name.txt", "[!B 'block1']a block with value [!V 'value1'/][!/B][!V 'value2'/]");

			TemplateFactory factory = new TemplateFactory(resources, "databasetext", TemplateFactory.CONFIGS_TXT, "text/txt", ".txt", new String[] {TemplateFactory.TAG_CONFIG, TemplateFactory.TAG_L10N}, null, BeanHandlerPlainSingleton.INSTANCE, null, null);
			Template template = null;

			template = factory.get("db_template_name");
			assertEquals("[!V 'value2'/]", template.getContent());
			template.setValue("value1", 1);
			template.appendBlock("value2", "block1");
			template.setValue("value1", 2);
			template.appendBlock("value2", "block1");
			template.setValue("value1", 3);
			template.appendBlock("value2", "block1");
			template.setValue("value1", 4);
			template.appendBlock("value2", "block1");
			assertEquals("a block with value 1"+
				"a block with value 2"+
				"a block with value 3"+
				"a block with value 4", template.getContent());

			resources.updateResource("db_template_name.txt", "[!B 'block1']another block with value [!V 'value1'/][!/B][!V 'value3'/]");

			template = factory.get("db_template_name");
			assertEquals("[!V 'value3'/]", template.getContent());
			template.setValue("value1", 1);
			template.appendBlock("value3", "block1");
			template.setValue("value1", 2);
			template.appendBlock("value3", "block1");
			template.setValue("value1", 3);
			template.appendBlock("value3", "block1");
			template.setValue("value1", 4);
			template.appendBlock("value3", "block1");
			assertEquals("another block with value 1"+
				"another block with value 2"+
				"another block with value 3"+
				"another block with value 4", template.getContent());
		}
		catch (ResourceWriterErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			try
			{
				resources.remove();
			}
			catch (ResourceWriterErrorException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}

	public void testOtherResourceFinderCommonFactory()
	{
		DatabaseResources	resources = DatabaseResourcesFactory.getInstance(Datasources.getRepInstance().getDatasource("unittestsderby"));
		ResourceFinder		previous = TemplateFactory.ENGINEHTML.getResourceFinder();
		TemplateFactory.ENGINEHTML.setResourceFinder(resources);
		try
		{
			resources.install();
			resources.addResource("db_template_name.html", "<!--B 'block1'-->a block with value <!--V 'value1'/--><!--/B--><!--V 'value2'/-->");

			Template template = null;

			template = TemplateFactory.ENGINEHTML.get("db_template_name");
			assertEquals("<!--V 'value2'/-->", template.getContent());
			template.setValue("value1", 1);
			template.appendBlock("value2", "block1");
			template.setValue("value1", 2);
			template.appendBlock("value2", "block1");
			template.setValue("value1", 3);
			template.appendBlock("value2", "block1");
			template.setValue("value1", 4);
			template.appendBlock("value2", "block1");
			assertEquals("a block with value 1"+
				"a block with value 2"+
				"a block with value 3"+
				"a block with value 4", template.getContent());

			resources.updateResource("db_template_name.html", "<!--B 'block1'-->another block with value <!--V 'value1'/--><!--/B--><!--V 'value3'/-->");

			template = TemplateFactory.ENGINEHTML.get("db_template_name");
			assertEquals("<!--V 'value3'/-->", template.getContent());
			template.setValue("value1", 1);
			template.appendBlock("value3", "block1");
			template.setValue("value1", 2);
			template.appendBlock("value3", "block1");
			template.setValue("value1", 3);
			template.appendBlock("value3", "block1");
			template.setValue("value1", 4);
			template.appendBlock("value3", "block1");
			assertEquals("another block with value 1"+
				"another block with value 2"+
				"another block with value 3"+
				"another block with value 4", template.getContent());
		}
		catch (ResourceWriterErrorException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
		finally
		{
			TemplateFactory.ENGINEHTML.setResourceFinder(previous);
			try
			{
				resources.remove();
			}
			catch (ResourceWriterErrorException e)
			{
				assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
			}
		}
	}
}
