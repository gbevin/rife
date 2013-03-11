/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineMocks.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.ReservedParameters;
import com.uwyn.rife.engine.SiteBuilder;
import com.uwyn.rife.engine.exceptions.MultipartFileTooBigException;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.Base64;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.SwallowingLogFormatter;
import junit.framework.TestCase;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

// These are just a collection of different kind of engine tests that
// are already done in the engine testsuite within the embedded servlet
// container. By using the same site-structures, we can check that the
// out-of-container tests yield the same results.
public class TestEngineMocks extends TestCase
{
	public TestEngineMocks(String name)
	{
		super(name);
	}
	
	public void testSimpleHtml()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance())
			.enterElement("element/engine/simple_html.xml")
			.setUrl("/simple/html")
			.leaveElement();
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response = conversation.doRequest("http://localhost/simple/html");
		assertEquals(200, response.getStatus());
		
		assertEquals("text/html; charset=UTF-8", response.getContentType());
		assertEquals("Just some text 127.0.0.1:localhost:.simple_html:", response.getText());
		
		assertEquals(".simple_html", response.getLastElementId());
	}
	
	public void testWrongServerRootUrl()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("test", ResourceFinderClasspath.getInstance())
			.enterElement("element/engine/simple_html.xml")
			.setUrl("/simple/html")
			.leaveElement();
		MockConversation conversation = new MockConversation(builder.getSite());
		
		assertNull(conversation.doRequest("http://10.0.0.1/simple/html"));
	}
	
	public void testContinuationsConditional()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/continuations.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		MockForm form;
		ParsedHtml parsed;
		
		MockResponse response = conversation.doRequest("/conditional");
		assertEquals(200, response.getStatus());
		parsed = response.getParsedHtml();
		
		assertEquals("printing", parsed.getTitle());
		form = parsed.getFormWithName("pause");
		assertNotNull(form);
		form.setParameter("answer", "1");
		response = form.submit();
		assertEquals(".conditional", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		assertEquals("pauzing", parsed.getTitle());
		form = parsed.getFormWithName("pause");
		assertNotNull(form);
		form.setParameter("answer", "1"); // will not be checked
		response = form.submit();
		assertEquals(".conditional", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		assertEquals("pauzingprinting", parsed.getTitle());
		form = parsed.getFormWithName("pause");
		assertNotNull(form);
		form.setParameter("answer", "0");
		response = form.submit();
		assertEquals(".conditional", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		assertEquals("printing", parsed.getTitle());
		form = parsed.getFormWithName("pause");
		assertNotNull(form);
		response = conversation.doRequest("/conditional", new MockRequest());
		assertEquals(".conditional", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		assertEquals("printing", parsed.getTitle());
		form = parsed.getFormWithName("pause");
		assertNotNull(form);
		form.setParameter("stop", "1");
		response = form.submit();
		assertEquals(".conditional", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		assertEquals("stopping", parsed.getTitle());
	}
	
	public void testCookies()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/cookies.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite())
			.cookie("cookie1", "this is the first cookie")
			.cookie("cookie2", "this is the second cookie")
			.cookie("cookie3", "this is the third cookie");
		
		MockResponse response = conversation.doRequest("/cookies/valid/source");
		
		// check if the correct cookies were returned
		assertEquals(conversation.getCookie("cookie3").getValue(), "this is the first cookie");
		assertEquals(conversation.getCookie("cookie4").getValue(), "this is the second cookie");
		
		// new page with cookie context
		conversation.cookie("cookie4", "this is the fourth cookie");
		response = conversation.doRequest("/cookies/valid/destination");
		assertEquals(".COOKIES_VALID_DESTINATION", response.getLastElementId());
		assertEquals("this is the second cookie,this is the first cookie,this is the fourth cookie", response.getText());
	}
	
	public void testEmbeddingCookies()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/embedding.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response = conversation.doRequest("/cookies");
		assertEquals("value 1," +
					 "value 2," +
					 "value 3," +
					 "value 4," +
					 "<!--V 'OUTCOOKIE:cookie5'/-->," +
					 "<form action=\"/cookies\"><input name=\"submission\" type=\"hidden\" value=\"submission\" /><input name=\"submissioncontext\" type=\"hidden\" value=\"" + Base64.encodeToString(".COOKIES::engine_embedding_cookies:.COOKIES_EMBEDDED^.COOKIES_EMBEDDED".getBytes("UTF-8"), false) + "\" /><input type=\"submit\" /></form>\n", response.getText());
		
		ParsedHtml parsed = response.getParsedHtml();
		MockForm form = parsed.getForms().get(0);
		response = form.submit();
		
		assertEquals(".COOKIES", response.getLastElementId());
		assertEquals("embedded value 1," +
					 "embedded value 2," +
					 "value 3," +
					 "embedded value 4," +
					 "<!--V 'OUTCOOKIE:cookie5'/-->,submitted\n", response.getText());
	}
	
	public void testEmbeddingPriorities()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/embedding.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response = conversation.doRequest("/priorities");
		assertEquals("These elements are embedded:\n"+
					 ".PRIORITIES_EMBEDDED_LATE\n"+
					 ".PRIORITIES_EMBEDDED_NORMAL\n"+
					 ".PRIORITIES_EMBEDDED_EARLY\n"+
					 "done.\n"+
					 ".PRIORITIES_EMBEDDED_EARLY\n"+
					 ".PRIORITIES_EMBEDDED_NORMAL\n"+
					 ".PRIORITIES\n"+
					 ".PRIORITIES_EMBEDDED_LATE\n", response.getText());
		
		List<MockResponse> embedded_reponses = response.getEmbeddedResponses();
		assertEquals(3, embedded_reponses.size());
		assertEquals(".PRIORITIES_EMBEDDED_EARLY", embedded_reponses.get(0).getLastElementId());
		assertEquals(0, embedded_reponses.get(0).getEmbeddedResponses().size());
		assertEquals(".PRIORITIES_EMBEDDED_NORMAL", embedded_reponses.get(1).getLastElementId());
		assertEquals(0, embedded_reponses.get(1).getEmbeddedResponses().size());
		assertEquals(".PRIORITIES_EMBEDDED_LATE", embedded_reponses.get(2).getLastElementId());
		assertEquals(0, embedded_reponses.get(2).getEmbeddedResponses().size());
		
		assertEquals(".PRIORITIES_EMBEDDED_EARLY", response.getEmbeddedResponse("-PRIORITIES_EMBEDDED_EARLY").getLastElementId());
		assertEquals(".PRIORITIES_EMBEDDED_NORMAL", response.getEmbeddedResponse("PRIORITIES_EMBEDDED_NORMAL").getLastElementId());
		assertEquals(".PRIORITIES_EMBEDDED_LATE", response.getEmbeddedResponse("+PRIORITIES_EMBEDDED_LATE").getLastElementId());
	}
	
	public void testEmbeddingSuccessiveGlobalcookie()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/embedding.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response = conversation.doRequest("/successive_globalcookie");
		assertEquals(".SUCCESSIVE_GLOBALCOOKIE", response.getLastElementId());
		assertEquals("embedded value 1,embedded value 2,<!--V 'OUTCOOKIE:cookie3'/-->,<!--V 'OUTCOOKIE:cookie4'/-->,<!--V 'OUTCOOKIE:cookie5'/-->\n" +
					 "first: embedded\n" +
					 "second: embedded value 1embedded value 2no cookie3\n", response.getText());
	}
	
	public void testXPath()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/exits.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response = null;
		
		response = conversation.doRequest("/exits/generatedurl/source");

		NodeList links = response.xpathNodeSet("//a");
		assertNotNull(links);
		assertEquals(4, links.getLength());
		assertEquals("exit1", links.item(0).getTextContent());
		assertEquals("exit2", links.item(1).getTextContent());
		assertEquals("/exits/generatedurl/destination1?input2=the+second&input1=the+first", links.item(0).getAttributes().getNamedItem("href").getTextContent());
		assertEquals("/exits/generatedurl/destination2?input1=the+second&input2=the+third", links.item(1).getAttributes().getNamedItem("href").getTextContent());
		
		Node body = response.xpathNode("/html/body");
		NodeList body_children = body.getChildNodes();
		assertEquals(11, body_children.getLength());
		assertEquals(Node.TEXT_NODE, body_children.item(0).getNodeType());
		assertEquals("", body_children.item(0).getTextContent().trim());
		assertEquals(Node.ELEMENT_NODE, body_children.item(1).getNodeType());
		assertEquals("h1", body_children.item(1).getNodeName());
		assertEquals(Node.TEXT_NODE, body_children.item(2).getNodeType());
		assertEquals("", body_children.item(2).getTextContent().trim());
		assertEquals(Node.ELEMENT_NODE, body_children.item(3).getNodeType());
		assertEquals("a", body_children.item(3).getNodeName());
		assertEquals(Node.TEXT_NODE, body_children.item(4).getNodeType());
		assertEquals("", body_children.item(4).getTextContent().trim());
		assertEquals(Node.ELEMENT_NODE, body_children.item(5).getNodeType());
		assertEquals("a", body_children.item(5).getNodeName());
		assertEquals(Node.TEXT_NODE, body_children.item(6).getNodeType());
		assertEquals("", body_children.item(6).getTextContent().trim());
		
		assertEquals("This is a template where the exit urls are generated.", response.xpathString("//h1/text()"));

		assertEquals(4, response.xpathNumber("count(//a)").intValue());

		assertTrue(response.xpathBoolean("contains(//a[1]/text(), 'exit1')"));
		assertFalse(response.xpathBoolean("contains(//a[2]/text(), 'exit1')"));
		assertEquals(true, response.xpathBoolean("contains(a[1]/text(), 'exit1')", body).booleanValue());
		
		assertEquals(true, response.xpathBoolean("contains(a[1]/text(), 'exit1')", body).booleanValue());
		assertEquals(false, response.xpathBoolean("contains(a[2]/text(), 'exit1')", body).booleanValue());
		assertEquals("/exits/generatedurl/destination1?input2=the+second&input1=the+first", response.xpathNode("a[1]", body).getAttributes().getNamedItem("href").getTextContent());
		assertEquals(4, response.xpathNodeSet("a", body).getLength());
		assertEquals(4, response.xpathNumber("count(a)", body).intValue());
		assertEquals("This is a template where the exit urls are generated.", response.xpathString("h1", body));
	}
	
	public void testExitsGeneratedUrl()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/exits.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		ParsedHtml parsed;
		
		MockResponse response = null;
		
		response = conversation.doRequest("/exits/generatedurl/source");
		parsed = response.getParsedHtml();
		MockLink exit1_link = parsed.getLinkWithText("exit1");
		response = exit1_link.click();
		assertEquals(".GENERATEDURL_DESTINATION1", response.getLastElementId());
		assertEquals("the first,the second", response.getText());
		
		response = conversation.doRequest("/exits/generatedurl/source");
		parsed = response.getParsedHtml();
		MockLink exit2_link = parsed.getLinkWithText("exit2");
		response = exit2_link.click();
		assertEquals(".GENERATEDURL_DESTINATION2", response.getLastElementId());
		assertEquals("the second,the third", response.getText());
		
		response = conversation.doRequest("/exits/generatedurl/source?switch=overridden");
		parsed = response.getParsedHtml();
		MockLink exit1_link_overridden = parsed.getLinkWithText("exit1");
		response = exit1_link_overridden.click();
		assertEquals(".GENERATEDURL_DESTINATION1", response.getLastElementId());
		assertEquals("the overridden first,the second", response.getText());
		
		response = conversation.doRequest("/exits/generatedurl/source?switch=overridden");
		parsed = response.getParsedHtml();
		MockLink exit2_link_overridden = parsed.getLinkWithText("exit2");
		response = exit2_link_overridden.click();
		assertEquals(".GENERATEDURL_DESTINATION2", response.getLastElementId());
		assertEquals("the second,the overridden third", response.getText());
	}
	
	public void testExitSelective()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/exits.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response1 = conversation.doRequest("/exits/selective/source", new MockRequest().parameter("switch", "1"));
		assertEquals(".SELECTIVE_DESTINATION1", response1.getLastElementId());
		assertEquals("destination1", response1.getText());
		
		MockResponse response2 = conversation.doRequest("/exits/selective/source", new MockRequest().parameter("switch", "2"));
		assertEquals(".SELECTIVE_DESTINATION2", response2.getLastElementId());
		assertEquals("destination2", response2.getText());
		
		MockResponse response3 = conversation.doRequest("/exits/selective/source", new MockRequest().parameter("switch", "3"));
		assertEquals(".SELECTIVE_DESTINATION3", response3.getLastElementId());
		assertEquals("destination3", response3.getText());
	}
	
	public void testExitsGeneratedUrlOverflow()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/exits.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		// setup swallowing log formatters
		HashMap<Handler, Formatter>	formatters = new HashMap<Handler, Formatter>();
		SwallowingLogFormatter		formatter = new SwallowingLogFormatter();
		Logger logger = Logger.getLogger("");
		for (Handler handler : logger.getHandlers())
		{
			formatters.put(handler, handler.getFormatter());
			handler.setFormatter(formatter);
		}
		
		try
		{
			MockResponse response = conversation.doRequest("/exits/generatedurloverflow/source");
			assertEquals(".GENERATEDURLOVERFLOW_SOURCE", response.getLastElementId());

			ParsedHtml	parsed = response.getParsedHtml();
			MockLink	exit1_link = parsed.getLinkWithText("exit1");
			assertTrue(exit1_link.getHref().startsWith("/exits/generatedurloverflow/destination;jsessionid="));
			assertTrue(exit1_link.getHref().indexOf("?stateid=") != -1);
			response = exit1_link.click();
			assertEquals(".GENERATEDURLOVERFLOW_DESTINATION", response.getLastElementId());
			assertEquals("the first,"+StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", 76)+"012345678", response.getText());
			
			assertEquals(2, formatter.getRecords().size());
			assertEquals("The exit 'exit1' of element '.GENERATEDURLOVERFLOW_SOURCE' generated an URL whose length of 2049 exceeds the maximum length of 2048 bytes, using session state store instead. The generated URL was '/exits/generatedurloverflow/destination?input1=the+first&input2="+StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", 76)+"012345678"+"'.", formatter.getRecords().get(0).getMessage());
		}
		finally
		{
			// restore the previous formatters
			for (Handler handler : logger.getHandlers())
			{
				handler.setFormatter(formatters.get(handler));
			}
		}
	}
	
	public void testExpressionInputsGroovy()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/expressionelement.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response;
		
		response = conversation.doRequest("/inputs_groovy?input1=value1");
		assertEquals(".INPUTS_GROOVY", response.getLastElementId());
		assertEquals("this is value 1\n\n\n", response.getText());
		
		response = conversation.doRequest("/inputs_groovy?input1=value2");
		assertEquals(".INPUTS_GROOVY", response.getLastElementId());
		assertEquals("this is value 2\n\n\n", response.getText());
		
		response = conversation.doRequest("/inputs_groovy?input1=value3");
		assertEquals(".INPUTS_GROOVY", response.getLastElementId());
		assertEquals("<!--V 'GROOVY:value'/-->\n\n\n", response.getText());
	}
	
	public void testInheritanceExits()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/inheritance.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockForm		form;
		ParsedHtml		parsed;
		
		response = conversation.doRequest("/inheritance/exits",
									 new MockRequest()
									 .parameter("childinput1", "will this")
									 .parameter("childinput2", "arrive too"));
		assertEquals(".EXITS_PARENT2", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent2");
		response = form
			.parameter("activationparent2", "don't go to child")
			.submit();
		assertEquals(".EXITS_PARENT2", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent2");
		response = form
			.parameter("activationparent2", "go to child")
			.submit();
		assertEquals(".EXITS_PARENT1", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent1");
		response = form
			.parameter("activationparent1", "don't go to exit")
			.submit();
		assertEquals(".EXITS_PARENT1", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent1");
		response = form
			.parameter("activationparent1", "go to exit")
			.submit();
		assertEquals(".EXITS_PARENT1_EXIT1", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent1exit1");
		response = form
			.parameter("activationparent1exit1", "don't go to exit")
			.submit();
		assertEquals(".EXITS_PARENT1_EXIT1", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent1exit1");
		response = form
			.parameter("activationparent1exit1", "go to exit")
			.submit();
		assertEquals(".EXITS_PARENT1_EXIT2", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent1exit2");
		response = form
			.parameter("activationparent1exit2", "don't go to child")
			.submit();
		assertEquals(".EXITS_PARENT1_EXIT2", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent1exit2");
		response = form
			.parameter("activationparent1exit2", "go to child")
			.submit();
		assertEquals(".EXITS_PARENT2", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent2");
		response = form
			.parameter("activationparent2", "don't go to child")
			.submit();
		assertEquals(".EXITS_PARENT2", response.getLastElementId());
		parsed = response.getParsedHtml();
		
		form = parsed.getFormWithName("formparent2");
		response = form
			.parameter("activationparent2", "go to child")
			.submit();
		assertEquals(".EXITS_CHILD", response.getLastElementId());
		
		assertEquals("will this,arrive too", response.getText());
	}
	
	public void testInputsNamedBeanPrefix()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/inputs.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response;
		response = conversation.doRequest("/inputs/named_bean/prefix",
									 new MockRequest()
									 .parameter("prefix_string", "the string")
									 .parameter("prefix_boolean", "y")
									 .parameter("prefix_string", "the string")
									 .parameter("prefix_stringbuffer", "the stringbuffer")
									 .parameter("prefix_int", "23154")
									 .parameter("prefix_integer", "893749")
									 .parameter("prefix_char", "u")
									 .parameter("prefix_character", "R")
									 .parameter("prefix_boolean", "y")
									 .parameter("prefix_booleanObject", "no")
									 .parameter("prefix_byte", "120")
									 .parameter("prefix_byteObject", "21")
									 .parameter("prefix_double", "34878.34")
									 .parameter("prefix_doubleObject", "25435.98")
									 .parameter("prefix_float", "3434.76")
									 .parameter("prefix_floatObject", "6534.8")
									 .parameter("prefix_long", "34347897")
									 .parameter("prefix_longObject", "2335454")
									 .parameter("prefix_short", "32")
									 .parameter("prefix_shortObject", "12"));
		assertEquals(".NAMED_BEAN_PREFIX", response.getLastElementId());
		assertEquals("the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());
		
		response = conversation.doRequest("/inputs/named_bean/prefix",
									 new MockRequest()
									 .parameter("prefix_string", "the string")
									 .parameter("prefix_boolean", "y")
									 .parameter("prefix_string", "the string")
									 .parameter("prefix_stringbuffer", "the stringbuffer")
									 .parameter("prefix_int", "23fd33")
									 .parameter("prefix_char", "u")
									 .parameter("prefix_character", "R")
									 .parameter("prefix_boolean", "y")
									 .parameter("prefix_booleanObject", "no")
									 .parameter("prefix_byte", "120")
									 .parameter("prefix_byteObject", "21")
									 .parameter("prefix_double", "zef.34")
									 .parameter("prefix_doubleObject", "25435.98")
									 .parameter("prefix_float", "3434.76")
									 .parameter("prefix_floatObject", "6534.8")
									 .parameter("prefix_long", "34347897")
									 .parameter("prefix_longObject", "233f5454")
									 .parameter("prefix_short", "32")
									 .parameter("prefix_shortObject", ""));
		assertEquals(".NAMED_BEAN_PREFIX", response.getLastElementId());
		assertEquals("NOTNUMERIC : int\nNOTNUMERIC : double\nNOTNUMERIC : longObject\nthe string,the stringbuffer,0,null,u,null,true,false,0,21,0.0,25435.98,3434.76,6534.8,34347897,null,32,null", response.getText());
	}
	
	public void testInputsGenerated()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/inputs.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse response;
		response = conversation.doRequest("/inputs/generated",
									 new MockRequest()
									 .parameter("wantsupdates", "on")
									 .parameter("colors", new String[] {"orange", "blue", "green"})
									 .parameter("firstname", "Geert")
									 .parameter("lastname", "Bevin"));
		assertEquals(".GENERATED", response.getLastElementId());
		assertEquals("Geert, Bevin\n" +
					 "<input type=\"checkbox\" name=\"wantsupdates\" checked=\"checked\"> I want updates<br />\n" +
					 "<input type=\"checkbox\" name=\"colors\" value=\"orange\" checked=\"checked\">orange<br />\n" +
					 "<input type=\"checkbox\" name=\"colors\" value=\"blue\" checked=\"checked\">blue<br />\n" +
					 "<input type=\"checkbox\" name=\"colors\" value=\"red\">red<br />\n" +
					 "<input type=\"radio\" name=\"firstname\" checked=\"checked\"> Geert\n" +
					 "<input type=\"radio\" name=\"firstname\"> Nathalie\n" +
					 "<select name=\"lastname\">\n" +
					 "\t<option value=\"Bevin\" selected=\"selected\">Bevin</option>\n" +
					 "\t<option value=\"Mafessoni\">Mafessoni</option>\n" +
					 "</select>\n", response.getText());
		
		response = conversation.doRequest("/inputs/generated");
		assertEquals(".GENERATED", response.getLastElementId());
		assertEquals("<!--V 'INPUT:firstname'/-->, <!--V 'INPUT:lastname'/-->\n" +
					 "<input type=\"checkbox\" name=\"wantsupdates\"> I want updates<br />\n" +
					 "<input type=\"checkbox\" name=\"colors\" value=\"orange\">orange<br />\n" +
					 "<input type=\"checkbox\" name=\"colors\" value=\"blue\">blue<br />\n" +
					 "<input type=\"checkbox\" name=\"colors\" value=\"red\">red<br />\n" +
					 "<input type=\"radio\" name=\"firstname\"> Geert\n" +
					 "<input type=\"radio\" name=\"firstname\"> Nathalie\n" +
					 "<select name=\"lastname\">\n" +
					 "\t<option value=\"Bevin\">Bevin</option>\n" +
					 "\t<option value=\"Mafessoni\">Mafessoni</option>\n" +
					 "</select>\n", response.getText());
	}
	
	public void testFileUploadSingleParam()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/submissions.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockForm		form;
		ParsedHtml		parsed;
		
		response = conversation.doRequest("/fileupload/simple");
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		
		String	upload_content = "abcdefghijklmnopqrstuvwxyz";
		File	upload = File.createTempFile("rifetest", ".tmp");
		upload.deleteOnExit();
		FileUtils.writeString(upload_content, upload);
		
		form.setParameter("purpose", "it will serve you well");
		MockFileUpload file_upload = new MockFileUpload(upload);
		form.setFile("doc1", file_upload);
		
		response = form.submit();
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		assertEquals(upload_content+";no file 2;it will serve you well", response.getText());
		
		upload.delete();
	}
	
	public void testFileUploadMultipleParams()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/submissions.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockForm		form;
		ParsedHtml		parsed;
		
		response = conversation.doRequest("/fileupload/simple");
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		
		String	upload1_content = "abcdefghijklmnopqrstuvwxyz";
		File	upload1 = File.createTempFile("rifetest", ".tmp");
		String	upload2_content = "oiuezroizehfkjsdgfhgizeugfizuhfksjdhfiuzhfiuzehfizeuhfziuh";
		File	upload2 = File.createTempFile("rifetest", ".tmp");
		upload1.deleteOnExit();
		FileUtils.writeString(upload1_content, upload1);
		upload2.deleteOnExit();
		FileUtils.writeString(upload2_content, upload2);
		
		form.setParameter("purpose", "it will serve you well");
		MockFileUpload file_upload1 = new MockFileUpload(upload1);
		MockFileUpload file_upload2 = new MockFileUpload(upload2);
		form.setFile("doc1", file_upload1);
		form.setFile("doc2", file_upload2);
		
		response = form.submit();
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		assertEquals(upload1_content+";"+upload2_content+";it will serve you well", response.getText());
		
		upload1.delete();
		upload2.delete();
	}
	
	public void testFileUploadSingleParamMultipleFiles()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/submissions.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockForm		form;
		ParsedHtml		parsed;
		
		response = conversation.doRequest("/fileupload/simple");
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		
		String	upload1_content = "abcdefghijklmnopqrstuvwxyz";
		File	upload1 = File.createTempFile("rifetest", ".tmp");
		String	upload2_content = "oiuezroizehfkjsdgfhgizeugfizuhfksjdhfiuzhfiuzehfizeuhfziuh";
		File	upload2 = File.createTempFile("rifetest", ".tmp");
		upload1.deleteOnExit();
		FileUtils.writeString(upload1_content, upload1);
		upload2.deleteOnExit();
		FileUtils.writeString(upload2_content, upload2);
		
		form.setParameter("purpose", "it will serve you well");
		MockFileUpload file_upload1 = new MockFileUpload(upload1);
		MockFileUpload file_upload2 = new MockFileUpload(upload2);
		form.setFiles("doc1", new MockFileUpload[] {file_upload1, file_upload2});
		
		response = form.submit();
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		assertEquals(upload1_content+","+upload2_content+";no file 2;it will serve you well", response.getText());
		
		upload1.delete();
		upload2.delete();
	}
	
	public void testFileUploadSizeLimit()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/submissions.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockForm		form;
		ParsedHtml		parsed;
		
		String			upload_content_part = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567"; // 128 bytes
		StringBuffer	upload_content = new StringBuffer();
		File	upload = File.createTempFile("rifetest", ".tmp");
		upload.deleteOnExit();
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 8; j++) // 1KB
			{
				upload_content.append(upload_content_part);
			}
		}
		FileUtils.writeString(upload_content.toString(), upload);
		MockFileUpload file_upload = null;
		
		// exactly the same size as the limit
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_LIMIT, ""+upload_content.length());
		response = conversation.doRequest("/fileupload/simple");
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		
		form.setParameter("purpose", "it will serve you well");
		file_upload = new MockFileUpload(upload);
		form.setFile("doc1", file_upload);
		
		response = form.submit();
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		assertEquals(upload_content.toString()+";no file 2;it will serve you well", response.getText());
		
		// exceeding the size by 1
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_LIMIT, ""+(upload_content.length()-1));
		response = conversation.doRequest("/fileupload/simple");
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		
		// throw no exception when size is exceeded, but don't provide the uploaded file
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_EXCEPTION, false);
		form.setParameter("purpose", "it will serve you well");
		file_upload = new MockFileUpload(upload);
		form.setFile("doc1", file_upload);
		response = form.submit();
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		assertEquals("file 1 size exceeded;no file 2;it will serve you well", response.getText());
		
		// throw exception when size is exceeded
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_EXCEPTION, true);
		form.setParameter("purpose", "it will serve you well");
		file_upload = new MockFileUpload(upload);
		form.setFile("doc1", file_upload);
		
		try
		{
			response = form.submit();
			fail();
		}
		catch (MultipartFileTooBigException e)
		{
			assertEquals("doc1", e.getFileName());
			assertEquals(upload_content.length()-1, e.getSizeLimit());
		}
		
		// disable size limit
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_CHECK, false);
		response = conversation.doRequest("/fileupload/simple");
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		parsed = response.getParsedHtml();
		form = parsed.getForms().get(0);
		
		form.setParameter("purpose", "it will serve you well");
		file_upload = new MockFileUpload(upload);
		form.setFile("doc1", file_upload);
		
		response = form.submit();
		assertEquals(".FILEUPLOAD_SIMPLE", response.getLastElementId());
		
		upload.delete();
		
		Config.getRepInstance().setParameter(RifeConfig.Engine.PARAM_FILEUPLOAD_SIZE_CHECK, "true");
	}
	
	private boolean isSessionResponse(MockResponse response)
	{
		return 1 == response.getNewCookieNames().size() &&
			"JSESSIONID".equals(response.getNewCookieNames().get(0));
	}
	
	private boolean isSessionLink(MockLink link)
	{
		return link.getHref().indexOf(ReservedParameters.STATEID) != -1;
	}
	
	private boolean isSessionForm(MockForm form)
	{
		return form.hasParameter(ReservedParameters.STATEID);
	}
	
	public void testSessionSubmissionInputsPreserved()
	throws Exception
	{
		SiteBuilder builder = new SiteBuilder("site/statesession.xml", ResourceFinderClasspath.getInstance());
		MockConversation conversation = new MockConversation(builder.getSite());
		
		MockResponse	response;
		MockLink		submission_link;
		MockForm		submission_form;
		ParsedHtml		parsed;
		
		response = conversation.doRequest("/submissions/inputspreserved",
									 new MockRequest()
									 .parameter("input1", "submission1input1value")
									 .parameter("input4", "submission1input4value"));
		assertEquals(".INPUTSPRESERVED", response.getLastElementId());
		parsed = response.getParsedHtml();
		assertTrue(isSessionResponse(response));
		submission_link = parsed.getLinkWithText("submission1");
		assertTrue(isSessionLink(submission_link));
		response = submission_link.click();
		assertEquals(".INPUTSPRESERVED", response.getLastElementId());
		
		assertEquals("thevalue"+
					 "submission1input1value"+
					 "null"+
					 "input3default", response.getText());
		
		response = conversation.doRequest("/submissions/inputspreserved",
									 new MockRequest()
									 .parameter("input1", "submission2input1value")
									 .parameter("input4", "submission2input4value"));
		assertEquals(".INPUTSPRESERVED", response.getLastElementId());
		parsed = response.getParsedHtml();
		submission_form = parsed.getForms().get(0);
		assertTrue(isSessionForm(submission_form));
		submission_form.setParameter("login", "gbevin2");
		response = submission_form.submit();
		assertEquals(".INPUTSPRESERVED", response.getLastElementId());
		
		submission_form = parsed.getForms().get(0);
		assertTrue(isSessionForm(submission_form));
		submission_form.setParameter("password", "oncemoremypassword");
		response = submission_form.submit();
		assertEquals(".INPUTSPRESERVED", response.getLastElementId());
		
		assertEquals("gbevin2,oncemoremypassword"+
					 "submission2input1value"+
					 "null"+
					 "input3default", response.getText());
		
		response = conversation.doRequest("/submissions/inputspreserved",
									 new MockRequest()
									 .parameter("input1", "submission3input1value")
									 .parameter("input4", "submission3input4value"));
		parsed = response.getParsedHtml();
		submission_form = parsed.getForms().get(1);
		assertTrue(isSessionForm(submission_form));
		response = submission_form.submit();
		assertEquals(".INPUTSPRESERVED", response.getLastElementId());

		submission_form = parsed.getForms().get(1);
		assertTrue(isSessionForm(submission_form));
		submission_form.setParameter("login", "me");
		response = submission_form.submit();
		assertEquals(".INPUTSPRESERVED", response.getLastElementId());
		
		assertEquals("me,it is"+
					 "submission3input1value"+
					 "null"+
					 "input3default", response.getText());
		
		response = conversation.doRequest("/submissions/inputspreserved",
									 new MockRequest()
									 .parameter("input1", "submission4input1value")
									 .parameter("input4", "submission4input4value"));
		assertEquals(".INPUTSPRESERVED", response.getLastElementId());
		parsed = response.getParsedHtml();
		submission_link = parsed.getLinkWithText("submission4");
		assertTrue(isSessionLink(submission_link));
		response = submission_link.click();
		assertEquals(".INPUTSPRESERVED", response.getLastElementId());
		
		assertEquals("submission4"+
					 "submission4input1value"+
					 "null"+
					 "input3default", response.getText());
	}
}
