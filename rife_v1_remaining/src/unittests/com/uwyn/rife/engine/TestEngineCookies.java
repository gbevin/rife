/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEngineCookies.java 3933 2008-04-25 20:41:45Z gbevin $
 */
package com.uwyn.rife.engine;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.uwyn.rife.TestCaseServerside;
import com.uwyn.rife.engine.exceptions.IncookieUnknownException;
import com.uwyn.rife.tools.HttpUtils;
import javax.servlet.http.Cookie;

public class TestEngineCookies extends TestCaseServerside
{
	public TestEngineCookies(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testCookiesValid()
	throws Exception
	{
		setupSite("site/cookies.xml");

		HttpUtils.Page page = null;

		// initial page that accepts and overrides cookies
		page = new HttpUtils.Request("http://localhost:8181/cookies/valid/source")
			.cookie("cookie1", "this is the first cookie")
			.cookie("cookie2", "this is the second cookie")
			.cookie("cookie3", "this is the third cookie")
			.retrieve();

		// check if the correct cookies were returned
		assertTrue(page.checkReceivedCookies(new Cookie[] {
				new Cookie("cookie3", "\"this is the first cookie\""),
				new Cookie("cookie4", "\"this is the second cookie\"")
			}));

		// new page with cookie context
		page = new HttpUtils.Request("http://localhost:8181/cookies/valid/destination")
			.cookie("cookie1", "this is the first cookie")
			.cookie("cookie2", "this is the second cookie")
			.cookie("cookie3", "this is the first cookie")
			.cookie("cookie4", "this is the second cookie")
			.retrieve();

		assertEquals("this is the second cookie,this is the first cookie,this is the second cookie", page.getContent());
	 }
	
	public void testCookiesBijection()
	throws Exception
	{
		setupSite("site/cookies.xml");
		
		HttpUtils.Page page = null;
		
		// initial page that accepts and overrides cookies
		page = new HttpUtils.Request("http://localhost:8181/cookies/valid/source/bijection")
			.cookie("cookie1", "this is the first cookie")
			.cookie("cookie2", "this is the second cookie")
			.cookie("cookie3", "this is the third cookie")
			.retrieve();
		
		// check if the correct cookies were returned
		assertTrue(page.checkReceivedCookies(new Cookie[] {
												 new Cookie("cookie3", "\"this is the first cookie\""),
												 new Cookie("cookie4", "\"this is the second cookie\"")
											 }));
		
		// new page with cookie context
		page = new HttpUtils.Request("http://localhost:8181/cookies/valid/destination")
			.cookie("cookie1", "this is the first cookie")
			.cookie("cookie2", "this is the second cookie")
			.cookie("cookie3", "this is the first cookie")
			.cookie("cookie4", "this is the second cookie")
			.retrieve();
		
		assertEquals("this is the second cookie,this is the first cookie,this is the second cookie", page.getContent());
	}
	
	public void testCookiesInvalid()
	throws Exception
	{
		setupSite("site/cookies.xml");

		HttpUtils.Page page = null;

		try
		{
			page = new HttpUtils.Request("http://localhost:8181/cookies/invalid/source")
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
			page = new HttpUtils.Request("http://localhost:8181/cookies/invalid/destination")
				.cookie("cookie1", "this is the first cookie")
				.cookie("cookie2", "this is the second cookie")
				.cookie("cookie3", "this is the third cookie")
				.retrieve();
			fail();
		}
		catch (Exception e)
		{
			assertTrue(getLogSink().getInternalException() instanceof IncookieUnknownException);

			IncookieUnknownException	e2 = (IncookieUnknownException)getLogSink().getInternalException();
			assertEquals("cookie2", e2.getIncookieName());
		}
	}

	public void testCookiesDefault()
	throws Exception
	{
		setupSite("site/cookies.xml");
		
		HttpUtils.Page page = HttpUtils.retrievePage("http://localhost:8181/cookies/defaults");
		assertEquals("cookie1 : the first cookie"+
			"cookie3 : the element config value", page.getContent());

		// check if the correct cookies were returned
		assertTrue(page.checkReceivedCookies(new Cookie[] {
				new Cookie("cookie4", "\"the element config value\""),
				new Cookie("cookie5", "\"the fifth cookie\"")
			}));
	 }

	public void testIncookiesInjection()
	throws Exception
	{
		setupSite("site/cookies.xml");
		HttpUtils.Page page = new HttpUtils.Request("http://localhost:8181/incookies/injection")
			.cookie("firstname", "Geert")
			.cookie("lastname", "Bevin")
			.cookie("globalcookie1", "globalcookievalue1")
			.retrieve();

		assertEquals("Welcome Geert  Bevin\nglobalcookievalue1 globalcookievalue2 ", page.getContent());
	}

	public void testIncookiesGenerated()
	throws Exception
	{
		setupSite("site/cookies.xml");
		HttpUtils.Page page = new HttpUtils.Request("http://localhost:8181/incookies/generated")
			.cookie("firstname", "Geert")
			.cookie("lastname", "Bevin")
			.cookie("globalcookie1", "globalcookievalue1")
			.retrieve();

		assertEquals("Welcome Geert <!--V 'INCOOKIE:middlename'/--> Bevin\nglobalcookievalue1 globalcookievalue2 <!--V 'INCOOKIE:globalcookie3'/--> <!--V 'INCOOKIE:unknown'/-->\n", page.getContent());
	}
	
	public void testOutcookiesGenerated()
	throws Exception
	{
		setupSite("site/cookies.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outcookies/generated");
		WebResponse response = conversation.getResponse(request);
		assertEquals("Welcome Geert Bevin\n<!--V 'OUTCOOKIE:globalcookie1'/--> <!--V 'OUTCOOKIE:globalcookie2'/--> <!--V 'OUTCOOKIE:globalcookie3'/--> <!--V 'OUTCOOKIE:unknown'/-->\n", response.getText());
	}
	
	public void testOutcookiesOutjection()
	throws Exception
	{
		setupSite("site/cookies.xml");
		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/outcookies/outjection");
		WebResponse response = conversation.getResponse(request);
		assertEquals("Welcome John Darryl\n<!--V 'OUTCOOKIE:globalcookie1'/--> <!--V 'OUTCOOKIE:globalcookie2'/--> <!--V 'OUTCOOKIE:globalcookie3'/--> <!--V 'OUTCOOKIE:unknown'/-->\n", response.getText());
	}
}

