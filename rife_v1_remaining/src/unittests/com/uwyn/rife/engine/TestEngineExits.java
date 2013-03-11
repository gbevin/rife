/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineExits.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.*;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.SwallowingLogFormatter;

import java.util.HashMap;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class TestEngineExits extends TestCaseServerside
{
	public TestEngineExits(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testContextIndependence()
	throws Exception
	{
		WebConversation	conversation = new WebConversation();
		WebRequest		request = null;
		WebResponse		response = null;

		setupSite("site/exits.xml");
		request = new GetMethodWebRequest("http://localhost:8181/exits/simple/source");
		response = conversation.getResponse(request);

		assertEquals("destination", response.getText());

		setupSite("/PREFIX", "site/exits.xml");
		request = new GetMethodWebRequest("http://localhost:8181/PREFIX/exits/simple/source");
		response = conversation.getResponse(request);

		assertEquals("destination", response.getText());
	}

	public void testExitSimple()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/exits/simple/source");
		WebResponse response = conversation.getResponse(request);
		assertEquals("destination", response.getText());
	}

	public void testExitSelective()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/selective/source");
		request.setParameter("switch", "1");
		response = conversation.getResponse(request);
		assertEquals("destination1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/selective/source");
		request.setParameter("switch", "2");
		response = conversation.getResponse(request);
		assertEquals("destination2", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/selective/source");
		request.setParameter("switch", "3");
		response = conversation.getResponse(request);
		assertEquals("destination3", response.getText());
	}

	public void testExistsDirectlink()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/exits/directlink/source");
		WebResponse response = conversation.getResponse(request);
		WebLink	direct_link = response.getLinkWith("direct link");
		direct_link.click();
		response = conversation.getCurrentPage();
		assertEquals("this isgreat", response.getText());
	}

	public void testExitFlowlinkSpecific()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	direct_link = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/flowlinkspecific/source");
		request.setParameter("exitselector", "exit1");
		response = conversation.getResponse(request);
		assertEquals("output1 value"+
					 "output3 value", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/flowlinkspecific/source");
		request.setParameter("exitselector", "exit2");
		response = conversation.getResponse(request);
		assertEquals("output2 value"+
					 "output3 value", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/flowlinkspecific/source");
		request.setParameter("exitselector", "exit1");
		request.setParameter("type", "directlink");
		response = conversation.getResponse(request);
		direct_link = response.getLinkWith("link");
		direct_link.click();
		response = conversation.getCurrentPage();
		assertEquals("output1 value"+
					 "output3 value", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/flowlinkspecific/source");
		request.setParameter("exitselector", "exit2");
		request.setParameter("type", "directlink");
		response = conversation.getResponse(request);
		direct_link = response.getLinkWith("link");
		direct_link.click();
		response = conversation.getCurrentPage();
		assertEquals("output2 value"+
					 "output3 value", response.getText());
	}

	public void testExitAutolink()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	direct_link = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/autolink/source");
		response = conversation.getResponse(request);
		assertEquals("output1 value,output2 value,null\n" +
			"stringvalue1,stringvalue2,stringvalue3,MONDAY,20070313081228000+0100\n"+
			"null,null,null,null,null\n", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/autolink/source");
		request.setParameter("type", "directlink");
		response = conversation.getResponse(request);
		direct_link = response.getLinkWith("link");
		direct_link.click();
		response = conversation.getCurrentPage();
		assertEquals("output1 value,output2 value,null\n"+
					 "stringvalue1,stringvalue2,stringvalue3,MONDAY,20070313081228000+0100\n"+
					 "null,null,null,null,null\n", response.getText());
	}

	public void testExitAutolinkAnnotations()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;
		WebLink	direct_link = null;

		request = new GetMethodWebRequest("http://localhost:8181/autolinkannotationsource");
		response = conversation.getResponse(request);
		assertEquals("output1 value,output2 value,null\n" +
					 "stringvalue1,stringvalue2,stringvalue3,TUESDAY,20070313082332000+0100\n"+
					 "null,null,null,null,null\n", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/autolinkannotationsource");
		request.setParameter("type", "directlink");
		response = conversation.getResponse(request);
		direct_link = response.getLinkWith("link");
		direct_link.click();
		response = conversation.getCurrentPage();
		assertEquals("output1 value,output2 value,null\n"+
					 "stringvalue1,stringvalue2,stringvalue3,TUESDAY,20070313082332000+0100\n"+
					 "null,null,null,null,null\n", response.getText());
	}

	public void testExitsGeneratedUrl()
	throws Exception
	{
		setupSite("site/exits.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedurl/source");
		response = conversation.getResponse(request);
		WebLink	exit1_link = response.getLinkWith("exit1");
		response = exit1_link.click();
		assertEquals("the first,the second", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedurl/source");
		response = conversation.getResponse(request);
		WebLink	exit2_link = response.getLinkWith("exit2");
		response = exit2_link.click();
		assertEquals("the second,the third", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedurl/source?switch=overridden");
		response = conversation.getResponse(request);
		WebLink	exit1_link_overridden = response.getLinkWith("exit1");
		response = exit1_link_overridden.click();
		assertEquals("the overridden first,the second", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedurl/source?switch=overridden");
		response = conversation.getResponse(request);
		WebLink	exit2_link_overridden = response.getLinkWith("exit2");
		response = exit2_link_overridden.click();
		assertEquals("the second,the overridden third", response.getText());
	}

	public void testExitsGeneratedUrlOverflow()
	throws Exception
	{
		setupSite("site/exits.xml");

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
			WebConversation	conversation = new WebConversation();
			WebRequest request = null;
			WebResponse response = null;

			request = new GetMethodWebRequest("http://localhost:8181/exits/generatedurloverflow/source");
			response = conversation.getResponse(request);
			WebLink	exit1_link = response.getLinkWith("exit1");
			assertTrue(exit1_link.getURLString().startsWith("/exits/generatedurloverflow/destination;jsessionid="));
			assertTrue(exit1_link.getURLString().indexOf("?stateid=") != -1);
			response = exit1_link.click();
			assertEquals("the first,"+StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", 76)+"012345678", response.getText());

			assertEquals(2, formatter.getRecords().size());
			String msg = "The exit 'exit1' of element '.GENERATEDURLOVERFLOW_SOURCE' generated an URL whose length of 2049 exceeds the maximum length of 2048 bytes, using session state store instead. The generated URL was '/exits/generatedurloverflow/destination?input1=the+first&input2="+StringUtils.repeat("abcdefghijklmnopqrstuvwxyz", 76)+"012345678"+"'.";
			assertEquals(msg, formatter.getRecords().get(0).getMessage());
			assertEquals(msg, formatter.getRecords().get(1).getMessage());
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

	public void testExitsGeneratedForm()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedform/source");
		response = conversation.getResponse(request);
		assertEquals("<!--V 'EXIT:FORM:exit3'/-->", response.getFormWithName("test3").getAction());
		WebForm	exit1_form = response.getFormWithName("test1");
		response = exit1_form.submit();
		assertEquals("another first,another second", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedform/source?setoutputs=false");
		response = conversation.getResponse(request);
		WebForm	exit1_form_nooutputs = response.getFormWithName("test1");
		response = exit1_form_nooutputs.submit();
		assertEquals("null,null", response.getText());
		
		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedform/source");
		response = conversation.getResponse(request);
		WebForm	exit2_form = response.getFormWithName("test2");
		response = exit2_form.submit();
		assertEquals("another second,another third", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedform/source");
		response = conversation.getResponse(request);
		WebForm	exit1_form2 = response.getFormWithName("test4");
		response = exit1_form2.submit();
		assertEquals("another first,another second", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedform/source?setoutputs=false");
		response = conversation.getResponse(request);
		WebForm	exit1_form2_nooutputs = response.getFormWithName("test4");
		response = exit1_form2_nooutputs.submit();
		assertEquals("null,null", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/generatedform/source");
		response = conversation.getResponse(request);
		WebForm	exit2_form2 = response.getFormWithName("test5");
		response = exit2_form2.submit();
		assertEquals("another second,another third", response.getText());
	}

	public void testExitSnapback()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/snapback/simple");
		request.setParameter("activate_exit", "1");
		response = conversation.getResponse(request);

		assertEquals("this is the output of simple snapback passthrough"+
			"the content of simple snapback source", response.getText());
	}

	public void testExitSnapbackDatalinks()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/snapback/datalinks");
		request.setParameter("submission", "activate_exit");
		request.setParameter("param1", "param1value");
		response = conversation.getResponse(request);

		assertEquals("this is the output of datalinks snapback passthrough"+
			"param1value"+
			"the content of datalinks snapback source"+
			"passthrough value 1"+
			"passthrough value 2", response.getText());
	}

	public void testExitBeanNormal()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/bean/normal");
		request.setParameter("selector", "first");
		response = conversation.getResponse(request);

		assertEquals("stringvalue1,stringvalue2,stringvalue3,MONDAY,20070313083217000+0100"+
					 "MONDAY,20070313083217000+0100,null,null,null", response.getText());
	}

	public void testExitBeanDifferent()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/bean/different");
		request.setParameter("selector", "second");
		response = conversation.getResponse(request);

		assertEquals("null,null,null,TUESDAY,20070421083217000+0200"+
					 "TUESDAY,20070421083217000+0200,null,null,null", response.getText());
	}

	public void testExitBeanSnapback()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/bean/snapback");
		request.setParameter("selector", "exit");
		response = conversation.getResponse(request);

		assertEquals("stringvalue1,stringvalue2,stringvalue3,SUNDAY,20070313082712000+0100", response.getText());
	}

	public void testExitBeanPrefixSame()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/bean/prefix/same");
		response = conversation.getResponse(request);

		assertEquals("stringvalue1,stringvalue2,stringvalue3,WEDNESDAY,20070313081324000+0100"+
			"null,null,null,null,null", response.getText());
	}

	public void testExitBeanPrefixDifferent()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/bean/prefix/different");
		response = conversation.getResponse(request);

		assertEquals("null,null,null,null,null"+
			"null,null,null,null,null", response.getText());
	}

	public void testExitRedirect()
	throws Exception
	{
		setupSite("site/exits.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/exits/redirect/source");
		request.setParameter("switch", "intern");
		response = conversation.getResponse(request);
		assertEquals("this is"+
					 "great"+
					 "/exits/redirect/source"+
					 "switch=intern", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/exits/redirect/source");
		request.setParameter("switch", "redirect");
		response = conversation.getResponse(request);
		assertEquals("this is"+
					 "great"+
					 "/exits/redirect/destination"+
					 "input1=this+is&input2=great", response.getText());
	}
}

