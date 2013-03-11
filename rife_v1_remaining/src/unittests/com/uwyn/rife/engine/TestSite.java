/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSite.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.DuplicateElementIdException;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.UrlExistsException;
import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

public class TestSite extends TestCase
{
	public TestSite(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		Site site = null;
		
		site = new Site();
		
		assertNotNull(site);
	}
	
	public void testNoInitialUrls()
	{
		Site site = new Site();
		
		assertEquals(site.getUrls().size(), 0);
	}
	
	public void testAddElement()
	throws EngineException
	{
		Site		site = new Site();
		ElementInfo	element_info1 = null;
		ElementInfo	element_info2 = null;
		try
		{
			element_info1 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT1", element_info1, "/element1");
			assertEquals(site.getUrls().size(), 1);
			element_info2 = new ElementInfo("element/test2.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT2", element_info2, "/element2");
			assertEquals(site.getUrls().size(), 2);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(site.containsUrl("/element1"));
		assertTrue(site.containsUrl("/element2"));
		assertSame(site.resolveUrl("/element1", null), element_info1);
		assertSame(site.resolveUrl("/element2", null), element_info2);
		assertNull(site.resolveUrl("/element1", "/pathinfo"));
		assertNull(site.resolveUrl("/element2", "/pathinfo"));
		assertSame(site.resolveId(".ELEMENT1"), element_info1);
		assertSame(site.resolveId(".ELEMENT2"), element_info2);
		assertEquals(element_info1.getUrl(), "/element1");
		assertEquals(element_info2.getUrl(), "/element2");
	}
	
	public void testTrailingSlash()
	throws EngineException
	{
		Site		site = new Site();
		ElementInfo	element_info1 = null;
		ElementInfo	element_info2 = null;
		ElementInfo	element_info3a = null;
		ElementInfo	element_info3b = null;
		ElementInfo	element_info4 = null;
		ElementInfo	element_info5 = null;
		try
		{
			element_info1 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT1", element_info1, "/element1");
			element_info2 = new ElementInfo("element/test2.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT2", element_info2, "/element2/");
			element_info3a = new ElementInfo("element/test3a.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT3a", element_info3a, "/element3");
			element_info3b = new ElementInfo("element/test3b.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT3b", element_info3b, "/element3/");
			element_info4 = new ElementInfo("element/test4.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT4", element_info4, "/element4.html");
			element_info5 = new ElementInfo("element/test5.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT5", element_info5, "/element5.html/itis");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		assertTrue(site.containsUrl("/element1"));
		assertFalse(site.containsUrl("/element2"));
		assertTrue(site.containsUrl("/element3"));
		assertTrue(site.containsUrl("/element1/"));
		assertTrue(site.containsUrl("/element2/"));
		assertTrue(site.containsUrl("/element3/"));
		assertTrue(site.containsUrl("/element4.html"));
		assertFalse(site.containsUrl("/element4.html/"));
		assertTrue(site.containsUrl("/element5.html/itis"));
		assertTrue(site.containsUrl("/element5.html/itis/"));
		assertSame(site.resolveUrl("/element1", null), element_info1);
		assertSame(site.resolveUrl("/element1/", null), element_info1);
		assertNull(site.resolveUrl("/element2", null));
		assertSame(site.resolveUrl("/element2/", null), element_info2);
		assertSame(site.resolveUrl("/element3", null), element_info3a);
		assertSame(site.resolveUrl("/element3/", null), element_info3b);
		assertSame(site.resolveUrl("/element4.html", null), element_info4);
		assertNull(site.resolveUrl("/element4.html/", null));
		assertSame(site.resolveUrl("/element5.html/itis", null), element_info5);
		assertSame(site.resolveUrl("/element5.html/itis/", null), element_info5);
		assertEquals(element_info1.getUrl(), "/element1");
		assertEquals(element_info2.getUrl(), "/element2/");
		assertEquals(element_info3a.getUrl(), "/element3");
		assertEquals(element_info3b.getUrl(), "/element3/");
		assertEquals(element_info4.getUrl(), "/element4.html");
		assertEquals(element_info5.getUrl(), "/element5.html/itis");
	}
	
	public void testAddDuplicateUrl()
	throws EngineException
	{
		Site site = new Site();
		
		ElementInfo	element_info1 = null;
		ElementInfo	element_info2 = null;
		try
		{
			element_info1 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			element_info2 = new ElementInfo("element/test2.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT1", element_info1, "/element1");
			site.addElementInfo(".ELEMENT2", element_info2, "/element1");
			fail();
		}
		catch (UrlExistsException e)
		{
			assertEquals(e.getUrl(), "/element1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testAddDuplicateUrlPathinfo()
	throws EngineException
	{
		Site site = new Site();
		
		ElementInfo	element_info1 = null;
		ElementInfo	element_info2 = null;
		element_info1 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
		element_info1.setPathInfoUsed(true);
		element_info2 = new ElementInfo("element/test2.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
		element_info2.setPathInfoUsed(true);
		site.addElementInfo(".ELEMENT1", element_info1, "/element1");
		site.addElementInfo(".ELEMENT2", element_info2, "/element1");
		assertSame(element_info1, site.resolveUrl("/element1", null));
		assertSame(element_info1, site.resolveUrl("/element1", "/pathinfo"));
	}
	
	public void testAddDuplicateId()
	throws EngineException
	{
		Site site = new Site();
		
		ElementInfo	element_info1 = null;
		ElementInfo	element_info2 = null;
		try
		{
			element_info1 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			element_info2 = new ElementInfo("element/test2.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT1", element_info1, "/element1");
			site.addElementInfo(".ELEMENT1", element_info2, "/element2");
			fail();
		}
		catch (DuplicateElementIdException e)
		{
			assertEquals(e.getId(), ".ELEMENT1");
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}
	}
	
	public void testMapElementToSeveralUrls()
	throws EngineException
	{
		Site		site = new Site();
		ElementInfo	element_info = null;
		try
		{
			element_info = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
			site.addElementInfo(".ELEMENT1", element_info, "/element");
			assertEquals(site.getUrls().size(), 1);
		}
		catch (EngineException e)
		{
			assertTrue(ExceptionUtils.getExceptionStackTrace(e), false);
		}

		try
		{
			site.addElementInfo(".ELEMENT2", element_info, "/element2");
			fail();
		}
		catch (EngineException e)
		{
			assertEquals(site.getUrls().size(), 1);
		}
	}
	
	public void testResolveElementIds()
	throws EngineException
	{
		Site		site = new Site();
		ElementInfo	element_info1 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo	element_info2 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo	element_info3 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo	element_info4 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo	element_info5 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
		ElementInfo	element_info6 = new ElementInfo("element/test1.xml", "text/html", TestElement1.class.getName(), ElementType.JAVA_CLASS);
		site.addElementInfo(".ELEMENT1", element_info1, "/element1");
		site.addElementInfo(".ELEMENT2", element_info2, "/element2");
		site.addElementInfo(".ELEMENTS.ELEMENT", element_info3, "/element3");
		site.addElementInfo(".ELEMENTS.ANOTHER", element_info4, "/element4");
		site.addElementInfo(".MORE_ELEMENTS.ELEMENT", element_info5, "/element5");
		site.addElementInfo(".MORE_ELEMENTS.ANOTHER", element_info6, "/element6");
		
		try
		{
			site.resolveId("ELEMENT1");
			fail();
		}
		catch (IllegalArgumentException e)
		{
			assertTrue(true);
		}
		
		assertSame(element_info1, site.resolveId(".ELEMENT1"));
		assertSame(element_info2, site.resolveId(".ELEMENT2"));
		assertSame(element_info3, site.resolveId(".ELEMENTS.ELEMENT"));
		assertSame(element_info4, site.resolveId(".ELEMENTS.ANOTHER"));
		assertSame(element_info5, site.resolveId(".MORE_ELEMENTS.ELEMENT"));
		assertSame(element_info6, site.resolveId(".MORE_ELEMENTS.ANOTHER"));

		assertSame(element_info1, site.resolveId("ELEMENT1", element_info1));
		assertSame(element_info1, site.resolveId("ELEMENT1", element_info2));
		assertNull(site.resolveId("ELEMENT1", element_info3));
		assertNull(site.resolveId("ELEMENT1", element_info4));
		assertNull(site.resolveId("ELEMENT1", element_info5));
		assertNull(site.resolveId("ELEMENT1", element_info6));
		
		assertSame(element_info2, site.resolveId("ELEMENT2", element_info1));
		assertSame(element_info2, site.resolveId("ELEMENT2", element_info2));
		assertNull(site.resolveId("ELEMENT2", element_info3));
		assertNull(site.resolveId("ELEMENT2", element_info4));
		assertNull(site.resolveId("ELEMENT2", element_info5));
		assertNull(site.resolveId("ELEMENT2", element_info6));
		
		assertSame(element_info3, site.resolveId("ELEMENTS.ELEMENT", element_info1));
		assertSame(element_info3, site.resolveId("ELEMENTS.ELEMENT", element_info2));
		assertNull(site.resolveId("ELEMENTS.ELEMENT", element_info3));
		assertNull(site.resolveId("ELEMENTS.ELEMENT", element_info4));
		assertNull(site.resolveId("ELEMENTS.ELEMENT", element_info5));
		assertNull(site.resolveId("ELEMENTS.ELEMENT", element_info6));

		assertSame(element_info4, site.resolveId("ELEMENTS.ANOTHER", element_info1));
		assertSame(element_info4, site.resolveId("ELEMENTS.ANOTHER", element_info2));
		assertNull(site.resolveId("ELEMENTS.ANOTHER", element_info3));
		assertNull(site.resolveId("ELEMENTS.ANOTHER", element_info4));
		assertNull(site.resolveId("ELEMENTS.ANOTHER", element_info5));
		assertNull(site.resolveId("ELEMENTS.ANOTHER", element_info6));
		
		assertSame(element_info5, site.resolveId("MORE_ELEMENTS.ELEMENT", element_info1));
		assertSame(element_info5, site.resolveId("MORE_ELEMENTS.ELEMENT", element_info2));
		assertNull(site.resolveId("MORE_ELEMENTS.ELEMENT", element_info3));
		assertNull(site.resolveId("MORE_ELEMENTS.ELEMENT", element_info4));
		assertNull(site.resolveId("MORE_ELEMENTS.ELEMENT", element_info5));
		assertNull(site.resolveId("MORE_ELEMENTS.ELEMENT", element_info6));

		assertSame(element_info6, site.resolveId("MORE_ELEMENTS.ANOTHER", element_info1));
		assertSame(element_info6, site.resolveId("MORE_ELEMENTS.ANOTHER", element_info2));
		assertNull(site.resolveId("MORE_ELEMENTS.ANOTHER", element_info3));
		assertNull(site.resolveId("MORE_ELEMENTS.ANOTHER", element_info4));
		assertNull(site.resolveId("MORE_ELEMENTS.ANOTHER", element_info5));
		assertNull(site.resolveId("MORE_ELEMENTS.ANOTHER", element_info6));
		
		assertNull(site.resolveId("ELEMENT", element_info1));
		assertNull(site.resolveId("ELEMENT", element_info2));
		assertSame(element_info3, site.resolveId("ELEMENT", element_info3));
		assertSame(element_info3, site.resolveId("ELEMENT", element_info4));
		assertSame(element_info5, site.resolveId("ELEMENT", element_info5));
		assertSame(element_info5, site.resolveId("ELEMENT", element_info6));
		
		assertNull(site.resolveId("ANOTHER", element_info1));
		assertNull(site.resolveId("ANOTHER", element_info2));
		assertSame(element_info4, site.resolveId("ANOTHER", element_info3));
		assertSame(element_info4, site.resolveId("ANOTHER", element_info4));
		assertSame(element_info6, site.resolveId("ANOTHER", element_info5));
		assertSame(element_info6, site.resolveId("ANOTHER", element_info6));
		
		assertSame(element_info2, site.resolveId(".ELEMENT1^ELEMENT2"));
		assertSame(element_info1, site.resolveId(".ELEMENT2^ELEMENT1"));
		assertSame(element_info4, site.resolveId(".ELEMENTS.ELEMENT^ANOTHER"));
		assertSame(element_info3, site.resolveId(".ELEMENTS.ANOTHER^ELEMENT"));
		assertSame(element_info6, site.resolveId(".MORE_ELEMENTS.ELEMENT^ANOTHER"));
		assertSame(element_info5, site.resolveId(".MORE_ELEMENTS.ANOTHER^ELEMENT"));
		assertSame(element_info5, site.resolveId(".ELEMENTS.ELEMENT^^MORE_ELEMENTS.ELEMENT"));
		assertSame(element_info6, site.resolveId(".ELEMENTS.ANOTHER^^MORE_ELEMENTS.ANOTHER"));
		assertSame(element_info3, site.resolveId(".MORE_ELEMENTS.ELEMENT^^ELEMENTS.ELEMENT"));
		assertSame(element_info4, site.resolveId(".MORE_ELEMENTS.ANOTHER^^ELEMENTS.ANOTHER"));
		
		assertSame(element_info1, site.resolveId("^ELEMENT1", element_info1));
		assertSame(element_info1, site.resolveId("^ELEMENT1", element_info2));
		assertSame(element_info1, site.resolveId("^ELEMENT1", element_info3));
		assertSame(element_info1, site.resolveId("^ELEMENT1", element_info4));
		assertSame(element_info1, site.resolveId("^ELEMENT1", element_info5));
		assertSame(element_info1, site.resolveId("^ELEMENT1", element_info6));
		
		assertSame(element_info2, site.resolveId("^ELEMENT2", element_info1));
		assertSame(element_info2, site.resolveId("^ELEMENT2", element_info2));
		assertSame(element_info2, site.resolveId("^ELEMENT2", element_info3));
		assertSame(element_info2, site.resolveId("^ELEMENT2", element_info4));
		assertSame(element_info2, site.resolveId("^ELEMENT2", element_info5));
		assertSame(element_info2, site.resolveId("^ELEMENT2", element_info6));
	}
}

class TestElement1 extends Element
{
	public void processElement()
	throws EngineException
	{
		print("the content");
	}
}
