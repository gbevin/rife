/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestParser.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.template.exceptions.*;

import com.uwyn.rife.tools.ExceptionUtils;
import java.io.File;
import java.util.regex.Pattern;

public class TestParser extends TemplateTestCase
{
	private Parser mParser = null;

	public TestParser(String name)
	{
		super(name);
	}

	public void setUp()
	{
		mParser = TemplateFactory.HTML.getParser();
	}

	public void testClone()
	{
		Parser parser_clone = mParser.clone();
		assertNotNull(parser_clone);
		assertTrue(mParser != parser_clone);
		assertTrue(mParser.equals(parser_clone));
	}
	
	public void testEquals()
	{
		Parser parser1 = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {new Parser.Config("<!--", "-->", "<!--/", "/-->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".html", (Pattern[])null, (Pattern[])null);
		Parser parser2 = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {new Parser.Config("<!--", "-->", "<!--/", "/-->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".html", new Pattern[] {Pattern.compile("pattern1"), Pattern.compile("pattern2"), Pattern.compile("pattern3")}, null);
		Parser parser3 = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {new Parser.Config("<!--", "-->", "<!--/", "/-->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".html", new Pattern[] {Pattern.compile("pattern2"), Pattern.compile("pattern3"), Pattern.compile("pattern1")}, null);
		Parser parser4 = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {new Parser.Config("<!--", "-->", "<!--/", "/-->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".html", null, new Pattern[] {Pattern.compile("pattern1"), Pattern.compile("pattern2"), Pattern.compile("pattern3")});
		Parser parser5 = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {new Parser.Config("<!--", "-->", "<!--/", "/-->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".html", null, new Pattern[] {Pattern.compile("pattern2"), Pattern.compile("pattern3"), Pattern.compile("pattern1")});
		Parser parser6 = new Parser(TemplateFactory.HTML, "htm", new Parser.Config[] {new Parser.Config("<!--", "-->", "<!--/", "/-->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".html", (Pattern[])null, (Pattern[])null);
		Parser parser7 = new Parser(TemplateFactory.HTML, "htm", new Parser.Config[] {new Parser.Config("<!", "-->", "<!/", "/-->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".html", (Pattern[])null, (Pattern[])null);
		Parser parser8 = new Parser(TemplateFactory.HTML, "htm", new Parser.Config[] {new Parser.Config("<!", ">", "<!/", "/>", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".html", (Pattern[])null, (Pattern[])null);
		Parser parser9 = new Parser(TemplateFactory.HTML, "htm", new Parser.Config[] {new Parser.Config("<!", ">", "<!-", "->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".html", (Pattern[])null, (Pattern[])null);
		Parser parser10 = new Parser(TemplateFactory.HTML, "htm", new Parser.Config[] {new Parser.Config("<!", ">", "<!-", "->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "'"), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "'"), "V", "B", "BV", "BA", "I", "C")}, ".htm", (Pattern[])null, (Pattern[])null);
		Parser parser11 = new Parser(TemplateFactory.HTML, "htm", new Parser.Config[] {new Parser.Config("<!", ">", "<!-", "->", new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_BEGIN, "\""), new Parser.OptionalConfigPart(Parser.PartType.STRING_DELIMITER_END, "\""),"V", "B", "BV", "BA", "I", "C")}, ".htm", (Pattern[])null, (Pattern[])null);

		assertNotNull(parser1);
		assertNotNull(parser2);
		assertNotNull(parser3);
		assertNotNull(parser4);
		assertNotNull(parser5);
		assertNotNull(parser6);
		assertNotNull(parser7);
		assertNotNull(parser8);
		assertNotNull(parser9);
		assertNotNull(parser10);
		assertNotNull(parser11);
		
		assertTrue(!mParser.equals(parser1));
		assertTrue(!mParser.equals(parser2));
		assertTrue(!mParser.equals(parser3));
		assertTrue(!mParser.equals(parser4));
		assertTrue(!mParser.equals(parser5));
		assertTrue(!mParser.equals(parser6));
		assertTrue(!mParser.equals(parser7));
		assertTrue(!mParser.equals(parser8));
		assertTrue(!mParser.equals(parser9));
		assertTrue(!mParser.equals(parser10));
		assertTrue(!mParser.equals(parser11));

		assertTrue(parser1.equals(parser1));
		assertTrue(!parser1.equals(parser2));
		assertTrue(!parser1.equals(parser3));
		assertTrue(!parser1.equals(parser4));
		assertTrue(!parser1.equals(parser5));
		assertTrue(!parser1.equals(parser6));
		assertTrue(!parser1.equals(parser7));
		assertTrue(!parser1.equals(parser8));
		assertTrue(!parser1.equals(parser9));
		assertTrue(!parser1.equals(parser10));
		assertTrue(!parser1.equals(parser11));

		assertTrue(parser2.equals(parser2));
		assertTrue(!parser2.equals(parser3));
		assertTrue(!parser2.equals(parser4));
		assertTrue(!parser2.equals(parser5));
		assertTrue(!parser2.equals(parser6));
		assertTrue(!parser2.equals(parser7));
		assertTrue(!parser2.equals(parser8));
		assertTrue(!parser2.equals(parser9));
		assertTrue(!parser2.equals(parser10));
		assertTrue(!parser2.equals(parser11));

		assertTrue(parser3.equals(parser3));
		assertTrue(!parser3.equals(parser4));
		assertTrue(!parser3.equals(parser5));
		assertTrue(!parser3.equals(parser6));
		assertTrue(!parser3.equals(parser7));
		assertTrue(!parser3.equals(parser8));
		assertTrue(!parser3.equals(parser9));
		assertTrue(!parser3.equals(parser10));
		assertTrue(!parser3.equals(parser11));

		assertTrue(parser4.equals(parser4));
		assertTrue(!parser4.equals(parser5));
		assertTrue(!parser4.equals(parser6));
		assertTrue(!parser4.equals(parser7));
		assertTrue(!parser4.equals(parser8));
		assertTrue(!parser4.equals(parser9));
		assertTrue(!parser4.equals(parser10));
		assertTrue(!parser4.equals(parser11));

		assertTrue(parser5.equals(parser5));
		assertTrue(!parser5.equals(parser6));
		assertTrue(!parser5.equals(parser7));
		assertTrue(!parser5.equals(parser8));
		assertTrue(!parser5.equals(parser9));
		assertTrue(!parser5.equals(parser10));
		assertTrue(!parser5.equals(parser11));

		assertTrue(parser6.equals(parser6));
		assertTrue(!parser6.equals(parser7));
		assertTrue(!parser6.equals(parser8));
		assertTrue(!parser6.equals(parser9));
		assertTrue(!parser6.equals(parser10));
		assertTrue(!parser6.equals(parser11));

		assertTrue(parser7.equals(parser7));
		assertTrue(!parser7.equals(parser8));
		assertTrue(!parser7.equals(parser9));
		assertTrue(!parser7.equals(parser10));
		assertTrue(!parser7.equals(parser11));

		assertTrue(parser8.equals(parser8));
		assertTrue(!parser8.equals(parser9));
		assertTrue(!parser8.equals(parser10));
		assertTrue(!parser8.equals(parser11));

		assertTrue(parser9.equals(parser9));
		assertTrue(!parser9.equals(parser10));
		assertTrue(!parser9.equals(parser11));

		assertTrue(parser10.equals(parser10));
		assertTrue(!parser10.equals(parser11));
		
		assertTrue(parser11.equals(parser11));
	}

	public void testTemplatePackage()
	{
		try
		{
			Parsed template_parsed = mParser.parse("test_package.noblocks_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 1);
			assertNotNull(template_parsed.getContent());
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("test_package" + File.separator + "noblocks_out_content", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseDefaultValues()
	{
		try
		{
			Parsed template_parsed = mParser.parse("defaultvalues_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 2);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertEquals(template_parsed.getContent().countParts(), 6);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getDefaultValue("VALUE1"), getTemplateContent("defaultvalues_out_default1", mParser));
			assertEquals(template_parsed.getDefaultValue("VALUE2"), getTemplateContent("defaultvalues_out_default2", mParser));
			assertEquals(template_parsed.getDefaultValue("VALUE3"), getTemplateContent("defaultvalues_out_default3", mParser));
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("defaultvalues_out_content_0", mParser));
			assertEquals(template_parsed.getContent().getPart(1).getData(), getTemplateContent("defaultvalues_out_content_1", mParser));
			assertEquals(template_parsed.getContent().getPart(2).getData(), getTemplateContent("defaultvalues_out_content_2", mParser));
			assertEquals(template_parsed.getContent().getPart(3).getData(), getTemplateContent("defaultvalues_out_content_3", mParser));
			assertEquals(template_parsed.getContent().getPart(4).getData(), getTemplateContent("defaultvalues_out_content_4", mParser));
			assertEquals(template_parsed.getContent().getPart(5).getData(), getTemplateContent("defaultvalues_out_content_5", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("defaultvalues_out_block1", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testParseComments()
	{
		try
		{
			Parsed template_parsed = mParser.parse("comments_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 2);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertEquals(template_parsed.getContent().countParts(), 3);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getDefaultValue("VALUE1"), getTemplateContent("comments_out_default1", mParser));
			assertNull(template_parsed.getDefaultValue("VALUE2"));
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("comments_out_content_0", mParser));
			assertEquals(template_parsed.getContent().getPart(1).getData(), getTemplateContent("comments_out_content_1", mParser));
			assertEquals(template_parsed.getContent().getPart(2).getData(), getTemplateContent("comments_out_content_2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("comments_out_block1", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testParseCommentsSuccessiveEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("comments_successive_escaped_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 1);
			assertNotNull(template_parsed.getContent());
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("comments_successive_escaped_out", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testParseNoBlocks()
	{
		try
		{
			Parsed template_parsed = mParser.parse("noblocks_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 1);
			assertNotNull(template_parsed.getContent());
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("noblocks_out_content", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlocksSuccessive()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blocks_successive_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 4);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertNotNull(template_parsed.getBlock("BLOCK3"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK3").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blocks_successive_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("blocks_successive_out_block1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("blocks_successive_out_block2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK3").getPart(0).getData(), getTemplateContent("blocks_successive_out_block3", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlocksSuccessiveEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blocks_successive_escaped_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 2);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blocks_successive_escaped_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("blocks_successive_escaped_out_block2", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlocksSpaced()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blocks_spaced_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 4);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertNotNull(template_parsed.getBlock("BLOCK3"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK3").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blocks_spaced_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("blocks_spaced_out_block1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("blocks_spaced_out_block2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK3").getPart(0).getData(), getTemplateContent("blocks_spaced_out_block3", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlocksSpacedEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blocks_spaced_escaped_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 2);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blocks_spaced_escaped_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("blocks_spaced_escaped_out_block1", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlocksExtremities()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blocks_extremities_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 4);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertNotNull(template_parsed.getBlock("BLOCK3"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK3").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blocks_extremities_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("blocks_extremities_out_block1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("blocks_extremities_out_block2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK3").getPart(0).getData(), getTemplateContent("blocks_extremities_out_block3", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlocksExtremitiesEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blocks_extremities_escaped_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 2);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blocks_extremities_escaped_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("blocks_extremities_escaped_out_block2", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlockvalues()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blockvalues_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 4);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertNotNull(template_parsed.getBlock("BLOCK3"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK3").countParts(), 1);
			assertTrue(template_parsed.hasBlockvalue("BLOCK1"));
			assertTrue(template_parsed.hasBlockvalue("BLOCK2"));
			assertTrue(template_parsed.hasBlockvalue("BLOCK3"));
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blockvalues_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("blockvalues_out_block1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("blockvalues_out_block2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK3").getPart(0).getData(), getTemplateContent("blockvalues_out_block3", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlockvaluesEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blockvalues_escaped_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 3);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK3"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK3").countParts(), 1);
			assertTrue(template_parsed.hasBlockvalue("BLOCK1"));
			assertTrue(template_parsed.hasBlockvalue("BLOCK3"));
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blockvalues_escaped_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("blockvalues_escaped_out_block1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK3").getPart(0).getData(), getTemplateContent("blockvalues_escaped_out_block3", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testParseBlockappends()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blockappends_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 2);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK"));
			assertEquals(template_parsed.getContent().countParts(), 3);
			assertEquals(template_parsed.getBlock("BLOCK").countParts(), 5);
			assertTrue(template_parsed.hasBlockvalue("BLOCK"));
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blockappends_out_content_0", mParser));
			assertEquals(template_parsed.getContent().getPart(1).getType(), ParsedBlockPart.VALUE);
			assertEquals(template_parsed.getContent().getPart(1).getData(), "BLOCK");
			assertEquals(template_parsed.getContent().getPart(2).getData(), getTemplateContent("blockappends_out_content_2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK").getPart(0).getData(), getTemplateContent("blockappends_out_block_0", mParser));
			assertEquals(template_parsed.getBlock("BLOCK").getPart(1).getType(), ParsedBlockPart.VALUE);
			assertEquals(template_parsed.getBlock("BLOCK").getPart(1).getData(), "value2");
			assertEquals(template_parsed.getBlock("BLOCK").getPart(2).getData(), getTemplateContent("blockappends_out_block_2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK").getPart(3).getType(), ParsedBlockPart.VALUE);
			assertEquals(template_parsed.getBlock("BLOCK").getPart(3).getData(), "value3");
			assertEquals(template_parsed.getDefaultValue("value3"), getTemplateContent("blockappends_out_block_3-defaultvalue", mParser));
			assertEquals(template_parsed.getBlock("BLOCK").getPart(4).getData(), getTemplateContent("blockappends_out_block_4", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testParseBlockappendsEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blockappends_escaped_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 3);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 1);
			assertTrue(template_parsed.hasBlockvalue("BLOCK1"));
			assertTrue(template_parsed.hasBlockvalue("BLOCK2"));
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blockappends_escaped_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("blockappends_escaped_out_block1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("blockappends_escaped_out_block2", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testParseBlocksNested()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blocks_nested_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 4);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertNotNull(template_parsed.getBlock("BLOCK3"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK3").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blocks_nested_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("blocks_nested_out_block1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("blocks_nested_out_block2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK3").getPart(0).getData(), getTemplateContent("blocks_nested_out_block3", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlocksNestedEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("blocks_nested_escaped_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 3);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK3"));
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 1);
			assertEquals(template_parsed.getBlock("BLOCK3").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blocks_nested_escaped_out_content", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("blocks_nested_escaped_out_block1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK3").getPart(0).getData(), getTemplateContent("blocks_nested_escaped_out_block3", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseBlocksNameHashcodeConflicts()
	{
		try
		{
			assertTrue("DMn0".hashCode() == "Cln0".hashCode());
			assertTrue("DMn0".hashCode() == "DNNO".hashCode());
			assertTrue("FMmO".hashCode() == "EmMn".hashCode());
			assertTrue("DMn0".hashCode() != "FMmO".hashCode());
			assertTrue("DMn0".hashCode() != "HNMn".hashCode());
			assertTrue("FMmO".hashCode() != "HNMn".hashCode());
			Parsed template_parsed = mParser.parse("blocks_stringconflicts_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 7);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("DMn0"));
			assertNotNull(template_parsed.getBlock("Cln0"));
			assertNotNull(template_parsed.getBlock("DNNO"));
			assertNotNull(template_parsed.getBlock("FMmO"));
			assertNotNull(template_parsed.getBlock("EmMn"));
			assertNotNull(template_parsed.getBlock("HNMn"));
			assertEquals(template_parsed.getContent().countParts(), 3);
			assertEquals(template_parsed.getBlock("DMn0").countParts(), 1);
			assertEquals(template_parsed.getBlock("Cln0").countParts(), 1);
			assertEquals(template_parsed.getBlock("DNNO").countParts(), 1);
			assertEquals(template_parsed.getBlock("FMmO").countParts(), 1);
			assertEquals(template_parsed.getBlock("EmMn").countParts(), 1);
			assertEquals(template_parsed.getBlock("HNMn").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("blocks_stringconflicts_out_content", mParser));
			assertEquals(template_parsed.getBlock("DMn0").getPart(0).getData(), getTemplateContent("blocks_stringconflicts_out_block1", mParser));
			assertEquals(template_parsed.getBlock("Cln0").getPart(0).getData(), getTemplateContent("blocks_stringconflicts_out_block2", mParser));
			assertEquals(template_parsed.getBlock("DNNO").getPart(0).getData(), getTemplateContent("blocks_stringconflicts_out_block3", mParser));
			assertEquals(template_parsed.getBlock("FMmO").getPart(0).getData(), getTemplateContent("blocks_stringconflicts_out_block4", mParser));
			assertEquals(template_parsed.getBlock("EmMn").getPart(0).getData(), getTemplateContent("blocks_stringconflicts_out_block5", mParser));
			assertEquals(template_parsed.getBlock("HNMn").getPart(0).getData(), getTemplateContent("blocks_stringconflicts_out_block6", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseValuesLong()
	{
		try
		{
			Parsed template_parsed = mParser.parse("values_long_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 4);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertNotNull(template_parsed.getBlock("BLOCK3"));
			assertEquals(template_parsed.getContent().countParts(), 5);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 5);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 3);
			assertEquals(template_parsed.getBlock("BLOCK3").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("values_long_out_content_0", mParser));
			assertEquals(template_parsed.getContent().getPart(1).getData(), getTemplateContent("values_long_out_content_1", mParser));
			assertEquals(template_parsed.getContent().getPart(2).getData(), getTemplateContent("values_long_out_content_2", mParser));
			assertEquals(template_parsed.getContent().getPart(3).getData(), getTemplateContent("values_long_out_content_3", mParser));
			assertEquals(template_parsed.getContent().getPart(4).getData(), getTemplateContent("values_long_out_content_4", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("values_long_out_block1_0", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(1).getData(), getTemplateContent("values_long_out_block1_1", mParser));
			assertEquals(template_parsed.getDefaultValue(template_parsed.getBlock("BLOCK1").getPart(1).getData()), getTemplateContent("values_long_out_block1_1-defaultvalue", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(2).getData(), getTemplateContent("values_long_out_block1_2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(3).getData(), getTemplateContent("values_long_out_block1_3", mParser));
			assertEquals(template_parsed.getDefaultValue(template_parsed.getBlock("BLOCK1").getPart(3).getData()), getTemplateContent("values_long_out_block1_3-defaultvalue", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(4).getData(), getTemplateContent("values_long_out_block1_4", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("values_long_out_block2_0", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(1).getData(), getTemplateContent("values_long_out_block2_1", mParser));
			assertEquals(template_parsed.getDefaultValue(template_parsed.getBlock("BLOCK2").getPart(1).getData()), getTemplateContent("values_long_out_block2_1-defaultvalue", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(2).getData(), getTemplateContent("values_long_out_block2_2", mParser));
			assertEquals(template_parsed.getDefaultValue(template_parsed.getBlock("BLOCK2").getPart(2).getData()), getTemplateContent("values_long_out_block2_2-defaultvalue", mParser));
			assertEquals(template_parsed.getBlock("BLOCK3").getPart(0).getData(), getTemplateContent("values_long_out_block3_0", mParser));
			assertEquals(template_parsed.getDefaultValue(template_parsed.getBlock("BLOCK3").getPart(0).getData()), getTemplateContent("values_long_out_block3_0-defaultvalue", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseValuesLongEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("values_long_escaped_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 2);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertEquals(template_parsed.getContent().countParts(), 3);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 6);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("values_long_escaped_out_content_0", mParser));
			assertEquals(template_parsed.getContent().getPart(1).getData(), getTemplateContent("values_long_escaped_out_content_1", mParser));
			assertEquals(template_parsed.getContent().getPart(2).getData(), getTemplateContent("values_long_escaped_out_content_2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("values_long_escaped_out_block1_0", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(1).getData(), getTemplateContent("values_long_escaped_out_block1_1", mParser));
			assertEquals(template_parsed.getDefaultValue(template_parsed.getBlock("BLOCK1").getPart(1).getData()), getTemplateContent("values_long_escaped_out_block1_1-defaultvalue", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(2).getData(), getTemplateContent("values_long_escaped_out_block1_2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(3).getData(), getTemplateContent("values_long_escaped_out_block1_3", mParser));
			assertEquals(template_parsed.getDefaultValue(template_parsed.getBlock("BLOCK1").getPart(3).getData()), getTemplateContent("values_long_escaped_out_block1_3-defaultvalue", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(4).getData(), getTemplateContent("values_long_escaped_out_block1_4", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(5).getData(), getTemplateContent("values_long_escaped_out_block1_5", mParser));
			assertEquals(template_parsed.getDefaultValue(template_parsed.getBlock("BLOCK1").getPart(5).getData()), getTemplateContent("values_long_escaped_out_block1_5-defaultvalue", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseValuesShort()
	{
		try
		{
			Parsed template_parsed = mParser.parse("values_short_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 4);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertNotNull(template_parsed.getBlock("BLOCK2"));
			assertNotNull(template_parsed.getBlock("BLOCK3"));
			assertEquals(template_parsed.getContent().countParts(), 5);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 5);
			assertEquals(template_parsed.getBlock("BLOCK2").countParts(), 3);
			assertEquals(template_parsed.getBlock("BLOCK3").countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("values_short_out_content_0", mParser));
			assertEquals(template_parsed.getContent().getPart(1).getData(), getTemplateContent("values_short_out_content_1", mParser));
			assertEquals(template_parsed.getContent().getPart(2).getData(), getTemplateContent("values_short_out_content_2", mParser));
			assertEquals(template_parsed.getContent().getPart(3).getData(), getTemplateContent("values_short_out_content_3", mParser));
			assertEquals(template_parsed.getContent().getPart(4).getData(), getTemplateContent("values_short_out_content_4", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("values_short_out_block1_0", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(1).getData(), getTemplateContent("values_short_out_block1_1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(2).getData(), getTemplateContent("values_short_out_block1_2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(3).getData(), getTemplateContent("values_short_out_block1_3", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(4).getData(), getTemplateContent("values_short_out_block1_4", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(0).getData(), getTemplateContent("values_short_out_block2_0", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(1).getData(), getTemplateContent("values_short_out_block2_1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK2").getPart(2).getData(), getTemplateContent("values_short_out_block2_2", mParser));
			assertEquals(template_parsed.getBlock("BLOCK3").getPart(0).getData(), getTemplateContent("values_short_out_block3_0", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseValuesShortEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("values_short_escaped_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 2);
			assertNotNull(template_parsed.getContent());
			assertNotNull(template_parsed.getBlock("BLOCK1"));
			assertEquals(template_parsed.getContent().countParts(), 5);
			assertEquals(template_parsed.getBlock("BLOCK1").countParts(), 3);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("values_short_escaped_out_content_0", mParser));
			assertEquals(template_parsed.getContent().getPart(1).getData(), getTemplateContent("values_short_escaped_out_content_1", mParser));
			assertEquals(template_parsed.getContent().getPart(2).getData(), getTemplateContent("values_short_escaped_out_content_2", mParser));
			assertEquals(template_parsed.getContent().getPart(3).getData(), getTemplateContent("values_short_escaped_out_content_3", mParser));
			assertEquals(template_parsed.getContent().getPart(4).getData(), getTemplateContent("values_short_escaped_out_content_4", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(0).getData(), getTemplateContent("values_short_escaped_out_block1_0", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(1).getData(), getTemplateContent("values_short_escaped_out_block1_1", mParser));
			assertEquals(template_parsed.getBlock("BLOCK1").getPart(2).getData(), getTemplateContent("values_short_escaped_out_block1_2", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseIncludes()
	{
		try
		{
			Parsed template_parsed = mParser.parse("includes_master_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 1);
			assertNotNull(template_parsed.getContent());
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("includes_out_content", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testParseIncludesUnquoted()
	{
		try
		{
			Parsed template_parsed = mParser.parse("includes_unquoted_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 1);
			assertNotNull(template_parsed.getContent());
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("includes_unquoted_out_content", mParser));
		}
		catch (TemplateException e) 
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testParseIncludesOtherType()
	{
		try
		{
			Parsed template_parsed = mParser.parse("includes_othertype_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 2);
			assertNotNull(template_parsed.getContent());
			assertEquals(template_parsed.getContent().countParts(), 5);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("includes_othertype_out_content_0", mParser));
			assertEquals(template_parsed.getContent().getPart(1).getData(), getTemplateContent("includes_othertype_out_content_1", mParser));
			assertEquals(template_parsed.getContent().getPart(2).getData(), getTemplateContent("includes_othertype_out_content_2", mParser));
			assertEquals(template_parsed.getContent().getPart(3).getData(), getTemplateContent("includes_othertype_out_content_3", mParser));
			assertEquals(template_parsed.getContent().getPart(4).getData(), getTemplateContent("includes_othertype_out_content_4", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseIncludesEscaped()
	{
		try
		{
			Parsed template_parsed = mParser.parse("includes_escaped_master_in", null, null);
			assertEquals(template_parsed.getBlocks().size(), 1);
			assertNotNull(template_parsed.getContent());
			assertEquals(template_parsed.getContent().countParts(), 1);
			assertEquals(template_parsed.getContent().getPart(0).getData(), getTemplateContent("includes_escaped_out_content", mParser));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testEncodingLatin()
	{
		try
		{
			Parsed template_ascii = TemplateFactory.TXT.getParser().parse("encoding_latin_ascii", "US-ASCII", null);
			Parsed template_utf_16le = TemplateFactory.TXT.getParser().parse("encoding_latin_utf16le", "UTF-16LE", null);
			Parsed template_ascii_wrong = TemplateFactory.TXT.getParser().parse("encoding_latin_ascii", "UTF-16LE", null);
			Parsed template_utf_16le_wrong = TemplateFactory.TXT.getParser().parse("encoding_latin_utf16le", "US-ASCII", null);
			
			assertTrue(template_ascii.getContent().getPart(0).getData().equals(template_utf_16le.getContent().getPart(0).getData()));
			assertTrue(!template_utf_16le.getContent().getPart(0).getData().equals(template_utf_16le_wrong.getContent().getPart(0).getData()));
			assertTrue(!template_ascii.getContent().getPart(0).getData().equals(template_ascii_wrong.getContent().getPart(0).getData()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testEncodingLatin1()
	{
		try
		{
			Parsed template_iso8859_1 = TemplateFactory.TXT.getParser().parse("encoding_latin1_iso88591", "ISO8859-1", null);
			Parsed template_utf_8 = TemplateFactory.TXT.getParser().parse("encoding_latin1_utf8", "UTF-8", null);
			Parsed template_iso8859_1_wrong = TemplateFactory.TXT.getParser().parse("encoding_latin1_iso88591", "UTF-8", null);
			Parsed template_utf_8_wrong = TemplateFactory.TXT.getParser().parse("encoding_latin1_utf8", "ISO8859-1", null);
			
			assertTrue(template_iso8859_1.getContent().getPart(0).getData().equals(template_utf_8.getContent().getPart(0).getData()));
			assertTrue(!template_iso8859_1.getContent().getPart(0).getData().equals(template_iso8859_1_wrong.getContent().getPart(0).getData()));
			assertTrue(!template_utf_8.getContent().getPart(0).getData().equals(template_utf_8_wrong.getContent().getPart(0).getData()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testEncodingNonLatin()
	{
		try
		{
			Parsed template_utf_8 = TemplateFactory.TXT.getParser().parse("encoding_nonlatin_utf8", "UTF-8", null);
			Parsed template_utf_8_wrong = TemplateFactory.TXT.getParser().parse("encoding_nonlatin_utf8", "ISO8859-1", null);
			Parsed template_utf_16be = TemplateFactory.TXT.getParser().parse("encoding_nonlatin_utf16be", "UTF-16BE", null);
			Parsed template_utf_16be_wrong = TemplateFactory.TXT.getParser().parse("encoding_nonlatin_utf16be", "UTF-16LE", null);
			
			assertTrue(template_utf_8.getContent().getPart(0).getData().equals(template_utf_16be.getContent().getPart(0).getData()));
			assertTrue(!template_utf_8.getContent().getPart(0).getData().equals(template_utf_8_wrong.getContent().getPart(0).getData()));
			assertTrue(!template_utf_16be.getContent().getPart(0).getData().equals(template_utf_16be_wrong.getContent().getPart(0).getData()));
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testParseFilteredBlocks()
	{
		try
		{
			Parser			parser = null;
			Parsed			template_parsed = null;
			FilteredTagsMap	filtered_blocks_map = null;
			
			String 			filter1 = "^FILTER1:(\\w+):CONST:(\\w+)$";
			String 			filter2 = "^FILTER2:(\\w+)$";
			String 			filter3 = "^CONST-FILTER3:(\\w+)$";
			String 			filter4 = "(\\w+)";
			FilteredTags	filtered_blocks = null;
			
			parser = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, ".html", (Pattern[])null, (Pattern[])null);
			template_parsed = parser.parse("blocks_filtered_in", null, null);
			filtered_blocks_map = template_parsed.getFilteredBlocksMap();
			assertNull(filtered_blocks_map);
			
			parser = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, ".html", new Pattern[] {Pattern.compile(filter1), Pattern.compile(filter2), Pattern.compile(filter3), Pattern.compile(filter4)}, null);
			template_parsed = parser.parse("blocks_filtered_in", null, null);
			filtered_blocks_map = template_parsed.getFilteredBlocksMap();
			assertNotNull(filtered_blocks_map);
			
			assertTrue(filtered_blocks_map.containsFilter(filter1));
			assertTrue(filtered_blocks_map.containsFilter(filter2));
			assertTrue(filtered_blocks_map.containsFilter(filter3));
			assertFalse(filtered_blocks_map.containsFilter(filter4));
			
			filtered_blocks = filtered_blocks_map.getFilteredTag(filter1);
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
			
			filtered_blocks = filtered_blocks_map.getFilteredTag(filter2);
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
			
			filtered_blocks = filtered_blocks_map.getFilteredTag(filter3);
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
			
			parser = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, ".html", new Pattern[] {Pattern.compile(filter4), Pattern.compile(filter1), Pattern.compile(filter2), Pattern.compile(filter3)}, null);
			template_parsed = parser.parse("blocks_filtered_in", null, null);
			filtered_blocks_map = template_parsed.getFilteredBlocksMap();
			assertNotNull(filtered_blocks_map);
			
			assertFalse(filtered_blocks_map.containsFilter(filter1));
			assertFalse(filtered_blocks_map.containsFilter(filter2));
			assertFalse(filtered_blocks_map.containsFilter(filter3));
			assertTrue(filtered_blocks_map.containsFilter(filter4));
						
			filtered_blocks = filtered_blocks_map.getFilteredTag(filter4);
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

	public void testParseFilteredValues()
	{
		try
		{
			Parser			parser = null;
			Parsed			template_parsed = null;
			FilteredTagsMap	filtered_values_map = null;
			
			String 			filter1 = "^FILTER1:(\\w+):CONST:(\\w+)$";
			String 			filter2 = "^FILTER2:(\\w+)$";
			String 			filter3 = "^CONST-FILTER3:(\\w+)$";
			String 			filter4 = "(\\w+)";
			FilteredTags	filtered_values = null;
			
			parser = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, ".html", (Pattern[])null, (Pattern[])null);
			template_parsed = parser.parse("values_filtered_in", null, null);
			filtered_values_map = template_parsed.getFilteredValuesMap();
			assertNull(filtered_values_map);
			
			parser = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, ".html", null, new Pattern[] {Pattern.compile(filter1), Pattern.compile(filter2), Pattern.compile(filter3), Pattern.compile(filter4)});
			template_parsed = parser.parse("values_filtered_in", null, null);
			filtered_values_map = template_parsed.getFilteredValuesMap();
			assertNotNull(filtered_values_map);
			
			assertTrue(filtered_values_map.containsFilter(filter1));
			assertTrue(filtered_values_map.containsFilter(filter2));
			assertTrue(filtered_values_map.containsFilter(filter3));
			assertFalse(filtered_values_map.containsFilter(filter4));
			
			filtered_values = filtered_values_map.getFilteredTag(filter1);
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
			
			filtered_values = filtered_values_map.getFilteredTag(filter2);
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
			
			filtered_values = filtered_values_map.getFilteredTag(filter3);
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
			
			parser = new Parser(TemplateFactory.HTML, "html", new Parser.Config[] {TemplateFactory.CONFIG_INVISIBLE_XML}, ".html", null, new Pattern[] {Pattern.compile(filter4), Pattern.compile(filter1), Pattern.compile(filter2), Pattern.compile(filter3)});
			template_parsed = parser.parse("values_filtered_in", null, null);
			filtered_values_map = template_parsed.getFilteredValuesMap();
			assertNotNull(filtered_values_map);
			
			assertFalse(filtered_values_map.containsFilter(filter1));
			assertFalse(filtered_values_map.containsFilter(filter2));
			assertFalse(filtered_values_map.containsFilter(filter3));
			assertTrue(filtered_values_map.containsFilter(filter4));
			
			filtered_values = filtered_values_map.getFilteredTag(filter4);
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
	
	public void testErrorTerminatingUnopenedValue()
	{
		try
		{
			mParser.parse("error_terminating_unopened_value", null, null);
			fail();
		}
		catch (TerminatingUnopenedTagException e)
		{
			assertEquals("<!--V 'avalue'/--><!--/V-->", e.getErrorLocation().getLineContent());
			assertEquals(1, e.getErrorLocation().getLine());
			assertEquals(19, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_terminating_unopened_value");
			assertEquals(e.getTagType(), "V");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorTerminatingUnopenedBlock()
	{
		try
		{
			mParser.parse("error_terminating_unopened_block", null, null);
			fail();
		}
		catch (TerminatingUnopenedTagException e)
		{
			assertEquals("<!--/B-->", e.getErrorLocation().getLineContent());
			assertEquals(1, e.getErrorLocation().getLine());
			assertEquals(1, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_terminating_unopened_block");
			assertEquals(e.getTagType(), "B");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testErrorTerminatingUnopenedBlockvalue()
	{
		try
		{
			mParser.parse("error_terminating_unopened_blockvalue", null, null);
			fail();
		}
		catch (TerminatingUnopenedTagException e)
		{
			assertEquals("<!--/BV-->", e.getErrorLocation().getLineContent());
			assertEquals(1, e.getErrorLocation().getLine());
			assertEquals(1, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_terminating_unopened_blockvalue");
			assertEquals(e.getTagType(), "BV");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorTerminatingUnopenedComment()
	{
		try
		{
			mParser.parse("error_terminating_unopened_comment", null, null);
			fail();
		}
		catch (TerminatingUnopenedTagException e)
		{
			assertEquals("<!--/C-->", e.getErrorLocation().getLineContent());
			assertEquals(1, e.getErrorLocation().getLine());
			assertEquals(1, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_terminating_unopened_comment");
			assertEquals(e.getTagType(), "C");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorIncludeNotFound()
	{
		try
		{
			mParser.parse("error_include_not_found", null, null);
			fail();
		}
		catch (IncludeNotFoundException e)
		{
			assertEquals("		<!--I 'error_missing_include'/-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(3, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_include_not_found");
			assertEquals(e.getIncluded(), "error_missing_include");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testErrorCircularIncludes()
	{
		try
		{
			mParser.parse("error_circular_includes_master", null, null);
			fail();
		}
		catch (CircularIncludesException e)
		{
			assertEquals("<!--I 'error_circular_includes_master'/-->", e.getErrorLocation().getLineContent());
			assertEquals(1, e.getErrorLocation().getLine());
			assertEquals(1, e.getErrorLocation().getColumn());
			assertTrue(e.getPreviousIncludes().contains(mParser.getPackage() + "error_circular_includes_master"));
			assertTrue(e.getPreviousIncludes().contains(mParser.getPackage() + "error_circular_includes_included"));
			assertEquals(e.getIncluded(), "error_circular_includes_master");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorIncludeBadlyTerminated()
	{
		try
		{
			mParser.parse("error_include_badly_terminated", null, null);
			fail();
		}
		catch (TagBadlyTerminatedException e)
		{
			assertEquals("		<!--I 'error_badly_terminated_include' erzer /-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(48, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_include_badly_terminated");
			assertEquals(e.getTagType(), "I");
			assertEquals(e.getTagId(), "error_badly_terminated_include");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorValueNameAttributeNotEnded()
	{
		try
		{
			mParser.parse("error_value_name_attribute_not_ended", null, null);
			fail();
		}
		catch (AttributeNotEndedException e)
		{
			assertEquals("		<!--V 'VALUE1/-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(10, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_value_name_attribute_not_ended");
			assertEquals(e.getTagType(), "V");
			assertEquals(e.getAttributeName(), "name");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorValueNameAttributeNotDelimitedInBegin()
	{
		try
		{
			mParser.parse("error_value_name_attribute_not_delimited_in_begin", null, null);
			fail();
		}
		catch (AttributeWronglyEndedException e)
		{
			assertEquals("		<!--V VALUE1'/-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(15, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_value_name_attribute_not_delimited_in_begin");
			assertEquals(e.getTagType(), "V");
			assertEquals(e.getAttributeName(), "name");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorValueTagNotTerminated()
	{
		try
		{
			mParser.parse("error_value_tag_not_terminated", null, null);
			fail();
		}
		catch (TagNotTerminatedException e)
		{
			assertEquals("		<!--V 'VALUE1'-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(3, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_value_tag_not_terminated");
			assertEquals(e.getTagType(), "V");
			assertEquals(e.getTagId(), "VALUE1");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorValueShortBeginTagBadlyTerminated()
	{
		try
		{
			mParser.parse("error_valueshort_begin_badly_terminated", null, null);
			fail();
		}
		catch (BeginTagBadlyTerminatedException e)
		{
			assertEquals("		<!--V 'VALUE1'   eff  /-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(17, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_valueshort_begin_badly_terminated");
			assertEquals(e.getTagType(), "V");
			assertEquals(e.getTagId(), "VALUE1");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorValueLongBeginTagBadlyTerminated()
	{
		try
		{
			mParser.parse("error_valuelong_begin_badly_terminated", null, null);
			fail();
		}
		catch (BeginTagBadlyTerminatedException e)
		{
			assertEquals("		<!--V 'VALUE1'   eff  --><!--/V-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(17, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_valuelong_begin_badly_terminated");
			assertEquals(e.getTagType(), "V");
			assertEquals(e.getTagId(), "VALUE1");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorUnsupportedNestedValueTag()
	{
		try
		{
			mParser.parse("error_unsupported_nested_value_tag", null, null);
			fail();
		}
		catch (UnsupportedNestedTagException e)
		{
			assertEquals("		<!--V 'VALUE2'/-->", e.getErrorLocation().getLineContent());
			assertEquals(9, e.getErrorLocation().getLine());
			assertEquals(3, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_unsupported_nested_value_tag");
			assertEquals(e.getTagType(), "V");
			assertEquals(e.getTagId(), "VALUE1");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testErrorValueBeginTagNotEnded()
	{
		try
		{
			mParser.parse("error_value_begin_tag_not_ended", null, null);
			fail();
		}
		catch (BeginTagNotEndedException e)
		{
			assertEquals("		<!--V 'VALUE1'", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(17, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_value_begin_tag_not_ended");
			assertEquals(e.getTagType(), "V");
			assertEquals(e.getTagId(), "VALUE1");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorCommentMissingTerminationTag()
	{
		try
		{
			mParser.parse("error_comment_missing_termination_tag", null, null);
			fail();
		}
		catch (MissingTerminationTagsException e)
		{
			assertEquals("</html>", e.getErrorLocation().getLineContent());
			assertEquals(10, e.getErrorLocation().getLine());
			assertEquals(8, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_comment_missing_termination_tag");
			assertEquals(e.getTagType(), "C");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorBlockNameAttributeNotEnded()
	{
		try
		{
			mParser.parse("error_block_name_attribute_not_ended", null, null);
			fail();
		}
		catch (AttributeNotEndedException e)
		{
			assertEquals("		<!--B 'BLOCK1--><!--/B-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(10, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_block_name_attribute_not_ended");
			assertEquals(e.getTagType(), "B");
			assertEquals(e.getAttributeName(), "name");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorBlockNameAttributeNotDelimitedInBegin()
	{
		try
		{
			mParser.parse("error_block_name_attribute_not_delimited_in_begin", null, null);
			fail();
		}
		catch (AttributeWronglyEndedException e)
		{
			assertEquals("		<!--B BLOCK1'--><!--/B-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(15, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_block_name_attribute_not_delimited_in_begin");
			assertEquals(e.getTagType(), "B");
			assertEquals(e.getAttributeName(), "name");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorBlockBeginTagNotEnded()
	{
		try
		{
			mParser.parse("error_block_begin_tag_not_ended", null, null);
			fail();
		}
		catch (BeginTagNotEndedException e)
		{
			assertEquals("		<!--B 'BLOCK1'", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(16, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_block_begin_tag_not_ended");
			assertEquals(e.getTagType(), "B");
			assertEquals(e.getTagId(), "BLOCK1");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testErrorBlockMissingTerminationTag()
	{
		try
		{
			mParser.parse("error_block_missing_termination_tag", null, null);
			fail();
		}
		catch (MissingTerminationTagsException e)
		{
			assertEquals("</html>", e.getErrorLocation().getLineContent());
			assertEquals(10, e.getErrorLocation().getLine());
			assertEquals(8, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_block_missing_termination_tag");
			assertEquals(e.getTagType(), "B");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorBlockMismatchedTerminationTag1()
	{
		try
		{
			mParser.parse("error_block_mismatched_termination_tag1", null, null);
			fail();
		}
		catch (MismatchedTerminationTagException e)
		{
			assertEquals("		<!--B 'BLOCK1'--><!--/BV-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(20, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_block_mismatched_termination_tag1");
			assertEquals(e.getTagId(), "BLOCK1");
			assertEquals(e.getExpected(), "B");
			assertEquals(e.getActual(), "BV");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}

	public void testErrorBlockMismatchedTerminationTag2()
	{
		try
		{
			mParser.parse("error_block_mismatched_termination_tag2", null, null);
			fail();
		}
		catch (MismatchedTerminationTagException e)
		{
			assertEquals("		<!--BV 'BLOCK2'--><!--/B-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(21, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_block_mismatched_termination_tag2");
			assertEquals(e.getTagId(), "BLOCK2");
			assertEquals(e.getExpected(), "BV");
			assertEquals(e.getActual(), "B");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorBlockMismatchedTerminationTag3()
	{
		try
		{
			mParser.parse("error_block_mismatched_termination_tag3", null, null);
			fail();
		}
		catch (MismatchedTerminationTagException e)
		{
			assertEquals("		<!--BV 'BLOCK2'--><!--/C-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(21, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_block_mismatched_termination_tag3");
			assertEquals(e.getTagId(), "BLOCK2");
			assertEquals(e.getExpected(), "BV");
			assertEquals(e.getActual(), "C");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorBlockBeginTagBadlyTerminated()
	{
		try
		{
			mParser.parse("error_block_begin_tag_badly_terminated", null, null);
			fail();
		}
		catch (BeginTagBadlyTerminatedException e)
		{
			assertEquals("		<!--B 'BLOCK1' dfsdf -->  <!--/B-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(17, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_block_begin_tag_badly_terminated");
			assertEquals(e.getTagId(), "BLOCK1");
			assertEquals(e.getTagType(), "B");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorBlockValueBeginTagBadlyTerminated()
	{
		try
		{
			mParser.parse("error_blockvalue_begin_tag_badly_terminated", null, null);
			fail();
		}
		catch (BeginTagBadlyTerminatedException e)
		{
			assertEquals("		<!--BV 'BLOCK1' dfsdf -->  <!--/B-->", e.getErrorLocation().getLineContent());
			assertEquals(8, e.getErrorLocation().getLine());
			assertEquals(18, e.getErrorLocation().getColumn());
			assertEquals(e.getTemplateName(), "error_blockvalue_begin_tag_badly_terminated");
			assertEquals(e.getTagId(), "BLOCK1");
			assertEquals(e.getTagType(), "BV");
		}
		catch (TemplateException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testErrorUnsupportedEncoding()
	{
		try
		{
			TemplateFactory.TXT.getParser().parse("encoding_nonlatin_utf8", "THIS_ENCODING_DOESNT_EXIST", null);
			fail();
		}
		catch (GetContentErrorException e)
		{
			assertTrue(e.getCause() instanceof com.uwyn.rife.resources.exceptions.ResourceFinderErrorException);
			assertTrue(e.getCause().getCause() instanceof com.uwyn.rife.tools.exceptions.FileUtilsErrorException);
			assertTrue(e.getCause().getCause().getCause() instanceof java.io.UnsupportedEncodingException);
		}
	}
}
