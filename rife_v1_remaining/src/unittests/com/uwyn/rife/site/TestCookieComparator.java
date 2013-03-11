/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCookieComparator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import javax.servlet.http.Cookie;
import junit.framework.TestCase;

public class TestCookieComparator extends TestCase
{
	public TestCookieComparator(String name)
	{
		super(name);
	}

	public void testCompare()
	{
		Cookie cookie1 = new Cookie("name1", "value1");
		Cookie cookie2 = new Cookie("name2", "value2");
		Cookie cookie3 = new Cookie("name2", "value1");
		
		CookieComparator comparator = new CookieComparator();
		assertTrue(comparator.compare(cookie1, cookie1) == 0);
		assertTrue(comparator.compare(cookie1, cookie2) < 0);
		assertTrue(comparator.compare(cookie1, cookie3) < 0);
		assertTrue(comparator.compare(cookie2, cookie1) > 0);
		assertTrue(comparator.compare(cookie2, cookie2) == 0);
		assertTrue(comparator.compare(cookie2, cookie3) == 0);
		assertTrue(comparator.compare(cookie3, cookie1) > 0);
		assertTrue(comparator.compare(cookie3, cookie2) == 0);
		assertTrue(comparator.compare(cookie3, cookie3) == 0);
	}
}
