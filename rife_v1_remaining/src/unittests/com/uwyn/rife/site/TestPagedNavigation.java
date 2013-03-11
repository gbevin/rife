/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPagedNavigation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.meterware.httpunit.*;
import com.uwyn.rife.TestCaseServerside;

public class TestPagedNavigation extends TestCaseServerside
{
	public TestPagedNavigation(int siteType, String name)
	{
		super(siteType, name);
	}

	public void testDefaults()
	throws Exception
	{
		setupSite("site/pagednavigation.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/defaults");
		WebResponse response = null;
		WebLink link = null;

		response = conversation.getResponse(request);

		// check the 'next' link
		String reponse0 = response.getText();
		assertEquals(response.getText(), "0 : Pages: 8 ( first prev <a href=\"/defaults?offset=10\">next</a> <a href=\"/defaults?offset=70\">last</a> |  1  <a href=\"/defaults?offset=10\">2</a>  <a href=\"/defaults?offset=20\">3</a>  <a href=\"/defaults?offset=30\">4</a>  ...  )");
		link = response.getLinkWith("next");
		response = link.click();
		String reponse10 = response.getText();
		assertEquals(response.getText(), "10 : Pages: 8 ( <a href=\"/defaults?offset=0\">first</a> <a href=\"/defaults?offset=0\">prev</a> <a href=\"/defaults?offset=20\">next</a> <a href=\"/defaults?offset=70\">last</a> |  <a href=\"/defaults?offset=0\">1</a>  2  <a href=\"/defaults?offset=20\">3</a>  <a href=\"/defaults?offset=30\">4</a>  <a href=\"/defaults?offset=40\">5</a>  ...  )");
		link = response.getLinkWith("next");
		response = link.click();
		String reponse20 = response.getText();
		assertEquals(response.getText(), "20 : Pages: 8 ( <a href=\"/defaults?offset=0\">first</a> <a href=\"/defaults?offset=10\">prev</a> <a href=\"/defaults?offset=30\">next</a> <a href=\"/defaults?offset=70\">last</a> |  <a href=\"/defaults?offset=0\">1</a>  <a href=\"/defaults?offset=10\">2</a>  3  <a href=\"/defaults?offset=30\">4</a>  <a href=\"/defaults?offset=40\">5</a>  <a href=\"/defaults?offset=50\">6</a>  ...  )");
		link = response.getLinkWith("next");
		response = link.click();
		String reponse30 = response.getText();
		assertEquals(response.getText(), "30 : Pages: 8 ( <a href=\"/defaults?offset=0\">first</a> <a href=\"/defaults?offset=20\">prev</a> <a href=\"/defaults?offset=40\">next</a> <a href=\"/defaults?offset=70\">last</a> |  <a href=\"/defaults?offset=0\">1</a>  <a href=\"/defaults?offset=10\">2</a>  <a href=\"/defaults?offset=20\">3</a>  4  <a href=\"/defaults?offset=40\">5</a>  <a href=\"/defaults?offset=50\">6</a>  <a href=\"/defaults?offset=60\">7</a>  ...  )");
		link = response.getLinkWith("next");
		response = link.click();
		String reponse40 = response.getText();
		assertEquals(response.getText(), "40 : Pages: 8 ( <a href=\"/defaults?offset=0\">first</a> <a href=\"/defaults?offset=30\">prev</a> <a href=\"/defaults?offset=50\">next</a> <a href=\"/defaults?offset=70\">last</a> |  ...  <a href=\"/defaults?offset=10\">2</a>  <a href=\"/defaults?offset=20\">3</a>  <a href=\"/defaults?offset=30\">4</a>  5  <a href=\"/defaults?offset=50\">6</a>  <a href=\"/defaults?offset=60\">7</a>  <a href=\"/defaults?offset=70\">8</a>  )");
		link = response.getLinkWith("next");
		response = link.click();
		String reponse50 = response.getText();
		assertEquals(response.getText(), "50 : Pages: 8 ( <a href=\"/defaults?offset=0\">first</a> <a href=\"/defaults?offset=40\">prev</a> <a href=\"/defaults?offset=60\">next</a> <a href=\"/defaults?offset=70\">last</a> |  ...  <a href=\"/defaults?offset=20\">3</a>  <a href=\"/defaults?offset=30\">4</a>  <a href=\"/defaults?offset=40\">5</a>  6  <a href=\"/defaults?offset=60\">7</a>  <a href=\"/defaults?offset=70\">8</a>  )");
		link = response.getLinkWith("next");
		response = link.click();
		String reponse60 = response.getText();
		assertEquals(response.getText(), "60 : Pages: 8 ( <a href=\"/defaults?offset=0\">first</a> <a href=\"/defaults?offset=50\">prev</a> <a href=\"/defaults?offset=70\">next</a> <a href=\"/defaults?offset=70\">last</a> |  ...  <a href=\"/defaults?offset=30\">4</a>  <a href=\"/defaults?offset=40\">5</a>  <a href=\"/defaults?offset=50\">6</a>  7  <a href=\"/defaults?offset=70\">8</a>  )");
		link = response.getLinkWith("next");
		response = link.click();
		String reponse70 = response.getText();
		assertEquals(response.getText(), "70 : Pages: 8 ( <a href=\"/defaults?offset=0\">first</a> <a href=\"/defaults?offset=60\">prev</a> next last |  ...  <a href=\"/defaults?offset=40\">5</a>  <a href=\"/defaults?offset=50\">6</a>  <a href=\"/defaults?offset=60\">7</a>  8  )");
		link = response.getLinkWith("next");
		assertNull(link);

		// check the 'previous' link
		link = response.getLinkWith("prev");
		response = link.click();
		assertEquals(response.getText(), reponse60);
		link = response.getLinkWith("prev");
		response = link.click();
		assertEquals(response.getText(), reponse50);
		link = response.getLinkWith("prev");
		response = link.click();
		assertEquals(response.getText(), reponse40);
		link = response.getLinkWith("prev");
		response = link.click();
		assertEquals(response.getText(), reponse30);
		link = response.getLinkWith("prev");
		response = link.click();
		assertEquals(response.getText(), reponse20);
		link = response.getLinkWith("prev");
		response = link.click();
		assertEquals(response.getText(), reponse10);
		link = response.getLinkWith("prev");
		response = link.click();
		assertEquals(response.getText(), reponse0);
		link = response.getLinkWith("prev");
		assertNull(link);

		// check the 'last' and 'first' links
		assertEquals(response.getText(), reponse0);
		link = response.getLinkWith("last");
		assertEquals(link.click().getText(), reponse70);
		link = response.getLinkWith("first");
		assertNull(link);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse10);
		link = response.getLinkWith("last");
		assertEquals(link.click().getText(), reponse70);
		link = response.getLinkWith("first");
		assertEquals(link.click().getText(), reponse0);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse20);
		link = response.getLinkWith("last");
		assertEquals(link.click().getText(), reponse70);
		link = response.getLinkWith("first");
		assertEquals(link.click().getText(), reponse0);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse30);
		link = response.getLinkWith("last");
		assertEquals(link.click().getText(), reponse70);
		link = response.getLinkWith("first");
		assertEquals(link.click().getText(), reponse0);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse40);
		link = response.getLinkWith("last");
		assertEquals(link.click().getText(), reponse70);
		link = response.getLinkWith("first");
		assertEquals(link.click().getText(), reponse0);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse50);
		link = response.getLinkWith("last");
		assertEquals(link.click().getText(), reponse70);
		link = response.getLinkWith("first");
		assertEquals(link.click().getText(), reponse0);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse60);
		link = response.getLinkWith("last");
		assertEquals(link.click().getText(), reponse70);
		link = response.getLinkWith("first");
		assertEquals(link.click().getText(), reponse0);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse70);
		link = response.getLinkWith("last");
		assertNull(link);
		link = response.getLinkWith("first");
		assertEquals(link.click().getText(), reponse0);

		// check the absolute links
		response = link.click();
		assertEquals(response.getText(), reponse0);
		link = response.getLinkWith("1");
		assertNull(link);
		link = response.getLinkWith("2");
		assertEquals(link.click().getText(), reponse10);
		link = response.getLinkWith("3");
		assertEquals(link.click().getText(), reponse20);
		link = response.getLinkWith("4");
		assertEquals(link.click().getText(), reponse30);
		link = response.getLinkWith("5");
		assertNull(link);
		link = response.getLinkWith("6");
		assertNull(link);
		link = response.getLinkWith("7");
		assertNull(link);
		link = response.getLinkWith("8");
		assertNull(link);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse10);
		link = response.getLinkWith("1");
		assertEquals(link.click().getText(), reponse0);
		link = response.getLinkWith("2");
		assertNull(link);
		link = response.getLinkWith("3");
		assertEquals(link.click().getText(), reponse20);
		link = response.getLinkWith("4");
		assertEquals(link.click().getText(), reponse30);
		link = response.getLinkWith("5");
		assertEquals(link.click().getText(), reponse40);
		link = response.getLinkWith("6");
		assertNull(link);
		link = response.getLinkWith("7");
		assertNull(link);
		link = response.getLinkWith("8");
		assertNull(link);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse20);
		link = response.getLinkWith("1");
		assertEquals(link.click().getText(), reponse0);
		link = response.getLinkWith("2");
		assertEquals(link.click().getText(), reponse10);
		link = response.getLinkWith("3");
		assertNull(link);
		link = response.getLinkWith("4");
		assertEquals(link.click().getText(), reponse30);
		link = response.getLinkWith("5");
		assertEquals(link.click().getText(), reponse40);
		link = response.getLinkWith("6");
		assertEquals(link.click().getText(), reponse50);
		link = response.getLinkWith("7");
		assertNull(link);
		link = response.getLinkWith("8");
		assertNull(link);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse30);
		link = response.getLinkWith("1");
		assertEquals(link.click().getText(), reponse0);
		link = response.getLinkWith("2");
		assertEquals(link.click().getText(), reponse10);
		link = response.getLinkWith("3");
		assertEquals(link.click().getText(), reponse20);
		link = response.getLinkWith("4");
		assertNull(link);
		link = response.getLinkWith("5");
		assertEquals(link.click().getText(), reponse40);
		link = response.getLinkWith("6");
		assertEquals(link.click().getText(), reponse50);
		link = response.getLinkWith("7");
		assertEquals(link.click().getText(), reponse60);
		link = response.getLinkWith("8");
		assertNull(link);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse40);
		link = response.getLinkWith("1");
		assertNull(link);
		link = response.getLinkWith("2");
		assertEquals(link.click().getText(), reponse10);
		link = response.getLinkWith("3");
		assertEquals(link.click().getText(), reponse20);
		link = response.getLinkWith("4");
		assertEquals(link.click().getText(), reponse30);
		link = response.getLinkWith("5");
		assertNull(link);
		link = response.getLinkWith("6");
		assertEquals(link.click().getText(), reponse50);
		link = response.getLinkWith("7");
		assertEquals(link.click().getText(), reponse60);
		link = response.getLinkWith("8");
		assertEquals(link.click().getText(), reponse70);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse50);
		link = response.getLinkWith("1");
		assertNull(link);
		link = response.getLinkWith("2");
		assertNull(link);
		link = response.getLinkWith("3");
		assertEquals(link.click().getText(), reponse20);
		link = response.getLinkWith("4");
		assertEquals(link.click().getText(), reponse30);
		link = response.getLinkWith("5");
		assertEquals(link.click().getText(), reponse40);
		link = response.getLinkWith("6");
		assertNull(link);
		link = response.getLinkWith("7");
		assertEquals(link.click().getText(), reponse60);
		link = response.getLinkWith("8");
		assertEquals(link.click().getText(), reponse70);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse60);
		link = response.getLinkWith("1");
		assertNull(link);
		link = response.getLinkWith("2");
		assertNull(link);
		link = response.getLinkWith("3");
		assertNull(link);
		link = response.getLinkWith("4");
		assertEquals(link.click().getText(), reponse30);
		link = response.getLinkWith("5");
		assertEquals(link.click().getText(), reponse40);
		link = response.getLinkWith("6");
		assertEquals(link.click().getText(), reponse50);
		link = response.getLinkWith("7");
		assertNull(link);
		link = response.getLinkWith("8");
		assertEquals(link.click().getText(), reponse70);
		response = response.getLinkWith("next").click();
		assertEquals(response.getText(), reponse70);
		link = response.getLinkWith("1");
		assertNull(link);
		link = response.getLinkWith("2");
		assertNull(link);
		link = response.getLinkWith("3");
		assertNull(link);
		link = response.getLinkWith("4");
		assertNull(link);
		link = response.getLinkWith("5");
		assertEquals(link.click().getText(), reponse40);
		link = response.getLinkWith("6");
		assertEquals(link.click().getText(), reponse50);
		link = response.getLinkWith("7");
		assertEquals(link.click().getText(), reponse60);
		link = response.getLinkWith("8");
		assertNull(link);
	}

	public void testCustom()
	throws Exception
	{
		setupSite("site/pagednavigation.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/custom");
		WebResponse response = null;
		WebLink link = null;

		response = conversation.getResponse(request);

		// check the 'next' link
		assertEquals(response.getText(), "0 : Pages: 4 ( first prev <a href=\"/custom?myoff=3\">next</a> <a href=\"/custom?myoff=9\">last</a> |  1  <a href=\"/custom?myoff=3\">2</a>  <a href=\"/custom?myoff=6\">3</a>  <a href=\"/custom?myoff=9\">4</a>  )");
		link = response.getLinkWith("next");
		response = link.click();
		assertEquals(response.getText(), "3 : Pages: 4 ( <a href=\"/custom?myoff=0\">first</a> <a href=\"/custom?myoff=0\">prev</a> <a href=\"/custom?myoff=6\">next</a> <a href=\"/custom?myoff=9\">last</a> |  <a href=\"/custom?myoff=0\">1</a>  2  <a href=\"/custom?myoff=6\">3</a>  <a href=\"/custom?myoff=9\">4</a>  )");
		link = response.getLinkWith("next");
		response = link.click();
		assertEquals(response.getText(), "6 : Pages: 4 ( <a href=\"/custom?myoff=0\">first</a> <a href=\"/custom?myoff=3\">prev</a> <a href=\"/custom?myoff=9\">next</a> <a href=\"/custom?myoff=9\">last</a> |  <a href=\"/custom?myoff=0\">1</a>  <a href=\"/custom?myoff=3\">2</a>  3  <a href=\"/custom?myoff=9\">4</a>  )");
		link = response.getLinkWith("next");
		response = link.click();
		assertEquals(response.getText(), "9 : Pages: 4 ( <a href=\"/custom?myoff=0\">first</a> <a href=\"/custom?myoff=6\">prev</a> next last |  <a href=\"/custom?myoff=0\">1</a>  <a href=\"/custom?myoff=3\">2</a>  <a href=\"/custom?myoff=6\">3</a>  4  )");
	}

	public void testNegativeOffset()
	throws Exception
	{
		setupSite("site/pagednavigation.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/custom?myoff=-10");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals(response.getText(), "-10 : Pages: 4 ( first prev <a href=\"/custom?myoff=3\">next</a> <a href=\"/custom?myoff=9\">last</a> |  1  <a href=\"/custom?myoff=3\">2</a>  <a href=\"/custom?myoff=6\">3</a>  <a href=\"/custom?myoff=9\">4</a>  )");
	}

	public void testOffsetEqualToCount()
	throws Exception
	{
		setupSite("site/pagednavigation.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/defaults?offset=80");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals(response.getText(), "80 : Pages: 8 ( <a href=\"/defaults?offset=0\">first</a> <a href=\"/defaults?offset=60\">prev</a> next last |  ...  <a href=\"/defaults?offset=40\">5</a>  <a href=\"/defaults?offset=50\">6</a>  <a href=\"/defaults?offset=60\">7</a>  8  )");
	}

	public void testOffsetLargerThanCount()
	throws Exception
	{
		setupSite("site/pagednavigation.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/defaults?offset=800");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals(response.getText(), "800 : Pages: 8 ( <a href=\"/defaults?offset=0\">first</a> <a href=\"/defaults?offset=60\">prev</a> next last |  ...  <a href=\"/defaults?offset=40\">5</a>  <a href=\"/defaults?offset=50\">6</a>  <a href=\"/defaults?offset=60\">7</a>  8  )");
	}

	public void testNegativeCount()
	throws Exception
	{
		setupSite("site/pagednavigation.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/negativecount");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals(response.getText(), "0 : Pages: 0 ( first prev next last |  )");
	}

	public void testNoRangeCount()
	throws Exception
	{
		setupSite("site/pagednavigation.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/norangecount");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals(response.getText(), "0 : Pages: ( first prev <a href=\"/norangecount?offset=10\">next</a> <a href=\"/norangecount?offset=70\">last</a> |  1  <a href=\"/norangecount?offset=10\">2</a>  <a href=\"/norangecount?offset=20\">3</a>  <a href=\"/norangecount?offset=30\">4</a>  ...  )");
	}
	
	public void testPathinfo()
	throws Exception
	{
		setupSite("site/pagednavigation.xml");

		WebConversation	conversation = new WebConversation();
		WebRequest request = new GetMethodWebRequest("http://localhost:8181/pathinfo");
		WebResponse response = null;

		response = conversation.getResponse(request);
		assertEquals(response.getText(), "0 : Pages: 8 ( first prev <a href=\"/pathinfo/test/pathinfo?offset=10\">next</a> <a href=\"/pathinfo/test/pathinfo?offset=70\">last</a> |  1  <a href=\"/pathinfo/test/pathinfo?offset=10\">2</a>  <a href=\"/pathinfo/test/pathinfo?offset=20\">3</a>  <a href=\"/pathinfo/test/pathinfo?offset=30\">4</a>  ...  )");
	}
}
