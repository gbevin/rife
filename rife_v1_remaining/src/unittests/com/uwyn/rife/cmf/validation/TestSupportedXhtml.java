/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSupportedXhtml.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.validation;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;
import junit.framework.TestCase;

public class TestSupportedXhtml extends TestCase
{
	public TestSupportedXhtml(String name)
	{
		super(name);
	}

	public void testValidateNull()
	throws Exception
	{
		SupportedXhtml		rule = new SupportedXhtml("xhtml", true);
        XhtmlBean			bean = new XhtmlBean();
		ConstrainedProperty	property = new ConstrainedProperty("xhtml").mimeType(MimeType.APPLICATION_XHTML);
		bean.addConstraint(property);
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
		assertNull(property.getCachedLoadedData());
	}

	public void testValidateSupportedDocument()
	throws Exception
	{
		SupportedXhtml		rule = new SupportedXhtml("xhtml", false);
        XhtmlBean			bean = new XhtmlBean();
		ConstrainedProperty	property = new ConstrainedProperty("xhtml").mimeType(MimeType.APPLICATION_XHTML);
		bean.addConstraint(property);
		bean.setXhtml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
					  "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
					  "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title></title></head><body>\n" +
					  "<p>body</p>\n" +
					  "</body></html>");
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
		assertNotNull(property.getCachedLoadedData());
	}

	public void testValidateUnsupportedDocument()
	throws Exception
	{
		SupportedXhtml		rule = new SupportedXhtml("xhtml", false);
        XhtmlBean			bean = new XhtmlBean();
		ConstrainedProperty	property = new ConstrainedProperty("xhtml").mimeType(MimeType.APPLICATION_XHTML);
		bean.addConstraint(property);
		bean.setXhtml("<p>some <b>html</b> here</p>");
		rule.setBean(bean);
		assertFalse(rule.validate());
		assertTrue(rule.getLoadingErrors().size() > 0);
		assertNull(property.getCachedLoadedData());
	}

	public void testValidateSupportedFragment()
	throws Exception
	{
		SupportedXhtml		rule = new SupportedXhtml("xhtml", true);
        XhtmlBean			bean = new XhtmlBean();
		ConstrainedProperty	property = new ConstrainedProperty("xhtml").mimeType(MimeType.APPLICATION_XHTML);
		bean.addConstraint(property);
		bean.setXhtml("<p>some <b>html</b> here</p>");
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
		assertNotNull(property.getCachedLoadedData());
	}

	public void testValidateUnsupportedFragment()
	throws Exception
	{
		SupportedXhtml		rule = new SupportedXhtml("xhtml", true);
        XhtmlBean			bean = new XhtmlBean();
		ConstrainedProperty	property = new ConstrainedProperty("xhtml").mimeType(MimeType.APPLICATION_XHTML);
		bean.addConstraint(property);
		bean.setXhtml("<i><b>error</i>");
		rule.setBean(bean);
		assertFalse(rule.validate());
		assertTrue(rule.getLoadingErrors().size() > 0);
		assertNull(property.getCachedLoadedData());
	}

	public void testValidateNotConstrained()
	throws Exception
	{
		SupportedXhtml				rule = new SupportedXhtml("xhtml", true);
        XhtmlBeanNotConstrained		bean = new XhtmlBeanNotConstrained();
		bean.setXhtml("<p>some <b>html</b> here</p>");
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
	}

	public void testValidateNotCmfProperty()
	throws Exception
	{
		SupportedXhtml			rule = new SupportedXhtml("xhtml", true);
        XhtmlBeanValidation		bean = new XhtmlBeanValidation();
		ConstrainedProperty		property = new ConstrainedProperty("xhtml");
		bean.addConstraint(property);
		bean.setXhtml("<p>some <b>html</b> here</p>");
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
	}

	public void testValidateUnknownProperty()
	throws Exception
	{
		SupportedXhtml	rule = new SupportedXhtml("xhtml_unknown", true);
        XhtmlBean		bean = new XhtmlBean();
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
	}

	public void testGetError()
	throws Exception
	{
		SupportedXhtml rule = new SupportedXhtml("xhtml", true);
		assertEquals("xhtml", rule.getError().getSubject());
		assertEquals("INVALID", rule.getError().getIdentifier());
	}

	public static class XhtmlBean extends Validation
	{
		private String 		mXhtml = null;

		public XhtmlBean()
		{
		}

		public String getXhtml()
		{
			return mXhtml;
		}

		public void setXhtml(String xhtml)
		{
			mXhtml = xhtml;
		}
	}

	public static class XhtmlBeanValidation extends Validation
	{
		private String 		mXhtml = null;

		public XhtmlBeanValidation()
		{
		}

		public String getXhtml()
		{
			return mXhtml;
		}

		public void setXhtml(String xhtml)
		{
			mXhtml = xhtml;
		}
	}

	public static class XhtmlBeanNotConstrained
	{
		private String 		mXhtml = null;

		public XhtmlBeanNotConstrained()
		{
		}

		public String getXhtml()
		{
			return mXhtml;
		}

		public void setXhtml(String xhtml)
		{
			mXhtml = xhtml;
		}
	}
}
