/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestContentDataUser.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.dam;

import com.uwyn.rife.tools.exceptions.InnerClassException;
import junit.framework.TestCase;

public class TestContentDataUser extends TestCase
{
	public TestContentDataUser(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
	    ContentDataUser user = new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					return null;
				}
			};

		assertNotNull(user);
	}

	public void testClone()
	{
	    ContentDataUser user = new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					return null;
				}
			};

		ContentDataUser cloned = user.clone();

		assertNotNull(cloned);
		assertNotSame(cloned, user);
	}

	public void testData()
	{
	    ContentDataUser user = new ContentDataUser<String, Integer>(123) {
				public String useContentData(Object contentData)
				throws InnerClassException
				{
					return contentData+" some string "+getData();
				}
			};

		assertEquals("the data some string 123", user.useContentData("the data"));
	}

	public void testException()
	{
		ContentDataUser user = new ContentDataUser() {
				public Object useContentData(Object contentData)
				throws InnerClassException
				{
					throwException(new Exception(contentData.toString()));
					return null;
				}
			};

		try
		{
			user.useContentData("some exception");

			fail();
		}
		catch (InnerClassException e)
		{
			assertEquals("some exception", e.getCause().getMessage());
		}
	}
}
