/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineGlobals.java 3933 2008-04-25 20:41:45Z gbevin $
 */
package com.uwyn.rife.engine;

import javax.servlet.http.Cookie;

import com.meterware.httpunit.*;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.engine.exceptions.GlobalExitOverriddenException;
import com.uwyn.rife.engine.exceptions.IncookieUnknownException;
import com.uwyn.rife.engine.exceptions.OutcookieUnknownException;
import com.uwyn.rife.tools.HttpUtils;

public class TestEngineGlobals extends TestCaseServerside
{
	public TestEngineGlobals(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testGlobalVars()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/source");
		request.setParameter("switch", "1");
		response = conversation.getResponse(request);
		assertEquals("value2a|value2b|value2c,value1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/source");
		request.setParameter("switch", "2");
		response = conversation.getResponse(request);
		assertEquals("value1,value2a|value2b|value2c", response.getText());
	}

	public void testGlobalVarsInjection()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/injection");
		request.setParameter("switch", "1");
		try
		{
			response = conversation.getResponse(request);
		}
		catch (Throwable e) {getLogSink().getInternalException().printStackTrace();}
		assertEquals("value2a|value2b|value2c,value1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/injection");
		request.setParameter("switch", "2");
		response = conversation.getResponse(request);
		assertEquals("value1,value2a|value2b|value2c", response.getText());
	}

	public void testGlobalVarsGroup()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/group/source");
		request.setParameter("switch", "1");
		response = conversation.getResponse(request);
		assertEquals("value4a|value4b|value4c,value3,value2a|value2b|value2c,value1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/group/source");
		request.setParameter("switch", "2");
		response = conversation.getResponse(request);
		assertEquals("value1,value2a|value2b|value2c,value3,value4a|value4b|value4c", response.getText());
	}

	public void testGlobalVarsGroupIsolation()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/group/isolation_source");
		request.setParameter("switch", "1");
		response = conversation.getResponse(request);
		assertEquals("value1|value2|value5", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/group/isolation_source");
		request.setParameter("switch", "2");
		response = conversation.getResponse(request);
		assertEquals("value1|default value 2 local|null", response.getText());
	}

	public void testGlobalDefaultVars()
	throws Exception
	{
		setupSite("site/global_defaults.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/defaults");
		response = conversation.getResponse(request);
		assertEquals("the first value"+
			"2rda-2rdd-2rdc-2rdb", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/defaults");
		request.setParameter("global1", "a new global value");
		response = conversation.getResponse(request);
		assertEquals("a new global value"+
			"2rda-2rdd-2rdc-2rdb", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/defaults");
		request.setParameter("input1", "1");
		response = conversation.getResponse(request);
		assertEquals("the first value"+
			"2rda-2rdd-2rdc-2rdb"+
			"the first value"+
			"2rda-2rdd-2rdc-2rdb"+
			"the element config value", response.getText());
	}

	public void testGlobalExits()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/source");
		request.setParameter("switch", "3");
		response = conversation.getResponse(request);
		assertEquals("global exit : value1,value2a|value2b|value2c", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/source");
		request.setParameter("switch", "4");
		request.setParameter("reflected", "reflected_value");
		request.setParameter("reflected_overridden", "reflected_value");
		request.setParameter("not_reflected", "not_reflected_value");
		response = conversation.getResponse(request);
		assertEquals("reflected"+
			"reflected_value"+
			"reflected_value_overridden"+
			"null", response.getText());
	}

	public void testGlobalExitsConflict()
	throws Exception
	{
		setupSite("site/global_conflict_exit.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;

		request = new GetMethodWebRequest("http://localhost:8181/subsite");
		try
		{
			conversation.getResponse(request);
		}
		catch (Throwable e)
		{
			Throwable e2 = getLogSink().getInternalException();
			assertTrue(e2 instanceof GlobalExitOverriddenException);
			assertEquals(((GlobalExitOverriddenException)e2).getGlobalExitName(), "globalexit1");
		}
	}

	public void testGlobalExitsGroup()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/group/source");
		request.setParameter("switch", "3");
		response = conversation.getResponse(request);
		assertEquals("global exit : value1,value2a|value2b|value2c", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/group/source");
		request.setParameter("switch", "4");
		response = conversation.getResponse(request);
		assertEquals("reflective1", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/group/source");
		request.setParameter("switch", "5");
		response = conversation.getResponse(request);
		assertEquals("global exit : value1,value2a|value2b|value2c,value3,value4a|value4b|value4c", response.getText());

		request = new GetMethodWebRequest("http://localhost:8181/globals/group/source");
		request.setParameter("switch", "6");
		response = conversation.getResponse(request);
		assertEquals("reflective2", response.getText());
	}

	public void testGlobalVarsSubmission()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/submission");
		request.setParameter("globalvar1", "var one");
		request.setParameter("globalvar2", new String [] {"var two a", "var two b"});
		response = conversation.getResponse(request);
		WebForm form = response.getForms()[0];
		response = form.submit();
		assertEquals("value1,value2a|value2b|value2c,one param", response.getText());
	 }

	public void testGlobalVarsInputsOverriding()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globalvars/inputs/source");
		request.setParameter("globalvar1", "global var one");
		request.setParameter("globalvar2", new String [] {"global var two a", "global var two b", "global var two c"});
		request.setParameter("input1", "input var one");
		request.setParameter("input2", "input var two");
		response = conversation.getResponse(request);
		assertEquals("global var one,global var two a|global var two b|global var two c,output input var one:outit,output input var two", response.getText());
	}

	public void testGlobalExitToRootArrival()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/globalexit_to_root_arrival");
		request.setParameter("globalvar1", "global var one");
		request.setParameter("globalvar2", new String [] {"global var two a", "global var two b", "global var two c"});
		request.setParameter("input1", "input var one");
		request.setParameter("input2", "input var two");
		response = conversation.getResponse(request);
		assertEquals("global var one,global var two a|global var two b|global var two c,output input var one:outit,output input var two", response.getText());
	}

	public void testGlobalsBeanNormal()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/globals/bean/normal");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals("the string,the stringbuffer,23154,893749,u,false,true,false,false,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());
	}

	public void testGlobalsBeanPrefix()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/globals/bean/prefix");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals("the string,the stringbuffer,23154,893749,u,false,true,false,false,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());
	}

	public void testNamedGlobalBeanNormal()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/globals/namedbean/normal");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals("the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());
	}

	public void testNamedGlobalBeanPrefix()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/globals/namedbean/prefix");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals("the string,the stringbuffer,23154,893749,u,null,true,false,0,21,34878.34,25435.98,3434.76,6534.8,34347897,2335454,32,12", response.getText());
	}

	public void testGlobalExitSnapback()
	throws Exception
	{
		setupSite("site/globals.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = null;
		WebResponse response = null;

		request = new GetMethodWebRequest("http://localhost:8181/globals/snapback/source");
		request.setParameter("activate_exit", "1");
		response = conversation.getResponse(request);

		assertEquals("this is the output of SnapbackPassthrough"+
			"the content of SnapbackSource", response.getText());
	}

	public void testGlobalCookiesValid()
	throws Exception
	{
		setupSite("site/globals.xml");

		HttpUtils.Page page = null;

		// initial page that accepts and overrides cookies

		page = new HttpUtils.Request("http://localhost:8181/globals/globalcookie/valid/source")
			.cookie("cookie1", "cookie1")
			.cookie("cookie2", "cookie2")
			.cookie("cookie3", "cookie3")
			.retrieve();
		// check if the correct cookies were returned
		assertTrue(page.checkReceivedCookies(new Cookie[] {
				new Cookie("cookie1", "cookie4"),
				new Cookie("cookie2", "cookie5"),
				new Cookie("cookie3", "cookie6"),
			}));

		// new page with cookie context
		page = new HttpUtils.Request("http://localhost:8181/globals/globalcookie/valid/destination")
			.cookie("cookie1", "cookie7")
			.cookie("cookie2", "cookie8")
			.cookie("cookie3", "cookie9")
			.retrieve();
		assertEquals("cookie7,cookie8,cookie9", page.getContent());
	}

	public void testGlobalCookiesInvalid()
	throws Exception
	{
		setupSite("site/globals.xml");

		HttpUtils.Page page = null;

		try
		{
			page = new HttpUtils.Request("http://localhost:8181/globals/globalcookie/invalid/source")
				.cookie("cookie1", "this is the first cookie")
				.cookie("cookie2", "this is the second cookie")
				.cookie("cookie3", "this is the third cookie")
				.retrieve();
			fail();
			assertNotNull(page);
		}
		catch (Exception e)
		{
			assertTrue(getLogSink().getInternalException() instanceof IncookieUnknownException);

			IncookieUnknownException	e2 = (IncookieUnknownException)getLogSink().getInternalException();
			assertEquals("cookie3", e2.getIncookieName());
		}

		try
		{
			page = new HttpUtils.Request("http://localhost:8181/globals/globalcookie/invalid/source2")
				.cookie("cookie1", "this is the first cookie")
				.cookie("cookie2", "this is the second cookie")
				.retrieve();
			fail();
		}
		catch (Exception e)
		{
			assertTrue(getLogSink().getInternalException() instanceof OutcookieUnknownException);

			OutcookieUnknownException	e2 = (OutcookieUnknownException)getLogSink().getInternalException();
			assertEquals("cookie3", e2.getOutcookieName());
		}
	}

	public void testGlobalCookiesDefault()
	throws Exception
	{
		setupSite("site/globals.xml");

		HttpUtils.Page page = HttpUtils.retrievePage("http://localhost:8181/globals/globalcookie/defaults");

		assertEquals("defcookie1 : the first cookie"+
			"defcookie3 : the element config value", page.getContent());

		// check if the correct cookies were returned
		assertTrue(page.checkReceivedCookies(new Cookie[] {
				new Cookie("defcookie4", "\"the element config value\""),
				new Cookie("defcookie5", "\"the fifth cookie\"")
			}));
	}
}

