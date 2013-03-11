/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestMimeType.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import com.uwyn.rife.cmf.validation.CmfPropertyValidationRule;
import com.uwyn.rife.site.ConstrainedProperty;
import junit.framework.TestCase;

public class TestMimeType extends TestCase
{
	public TestMimeType(String name)
	{
		super(name);
	}

	public void testMimeTypeIdentifiers()
	{
		assertEquals("application/xhtml+xml", MimeType.APPLICATION_XHTML.toString());
		assertEquals("text/plain", MimeType.TEXT_PLAIN.toString());
		assertEquals("image/gif", MimeType.IMAGE_GIF.toString());
		assertEquals("image/jpeg", MimeType.IMAGE_JPEG.toString());
		assertEquals("image/png", MimeType.IMAGE_PNG.toString());
	}

	public void testGetMimeType()
	{
		assertSame(MimeType.getMimeType("application/xhtml+xml"), MimeType.APPLICATION_XHTML);
		assertSame(MimeType.getMimeType("text/plain"), MimeType.TEXT_PLAIN);
		assertSame(MimeType.getMimeType("image/gif"), MimeType.IMAGE_GIF);
		assertSame(MimeType.getMimeType("image/jpeg"), MimeType.IMAGE_JPEG);
		assertSame(MimeType.getMimeType("image/png"), MimeType.IMAGE_PNG);
	}

	public void testGetUnsupportedMimeType()
	{
		assertNull(MimeType.getMimeType("uwynsspecial/type"));
	}

	public void testFormatters()
	{
		assertNotNull(MimeType.APPLICATION_XHTML.getFormatter());
		assertNotNull(MimeType.TEXT_PLAIN.getFormatter());
		assertNotNull(MimeType.IMAGE_GIF.getFormatter());
		assertNotNull(MimeType.IMAGE_JPEG.getFormatter());
		assertNotNull(MimeType.IMAGE_PNG.getFormatter());
	}

	public void testValidationRuleApplicationXhtml()
	{
		CmfPropertyValidationRule rule = MimeType.APPLICATION_XHTML.getValidationRule(new ConstrainedProperty("xhtml"));
		assertNotNull(rule);
		rule.setBean(new TestBean(false));
		assertTrue(rule.validate());
		rule.setBean(new TestBean(true));
		assertFalse(rule.validate());
	}

	public void testValidationRuleTextPlain()
	{
		CmfPropertyValidationRule rule = MimeType.TEXT_PLAIN.getValidationRule(new ConstrainedProperty("textplain"));
		assertNull(rule);
	}

	public void testValidationRuleImageGif()
	{
		CmfPropertyValidationRule rule = MimeType.IMAGE_GIF.getValidationRule(new ConstrainedProperty("gif"));
		assertNotNull(rule);
		rule.setBean(new TestBean(false));
		assertTrue(rule.validate());
		rule.setBean(new TestBean(true));
		assertFalse(rule.validate());
	}

	public void testValidationRuleImageJpeg()
	{
		CmfPropertyValidationRule rule = MimeType.IMAGE_JPEG.getValidationRule(new ConstrainedProperty("jpeg"));
		assertNotNull(rule);
		rule.setBean(new TestBean(false));
		assertTrue(rule.validate());
		rule.setBean(new TestBean(true));
		assertFalse(rule.validate());
	}

	public void testValidationRuleImagePng()
	{
		CmfPropertyValidationRule rule = MimeType.IMAGE_PNG.getValidationRule(new ConstrainedProperty("png"));
		assertNotNull(rule);
		rule.setBean(new TestBean(false));
		assertTrue(rule.validate());
		rule.setBean(new TestBean(true));
		assertFalse(rule.validate());
	}

	public static class TestBean
	{
		private String	mXhtml = null;
		private byte[]	mGif = null;
		private byte[]	mJpeg = null;
		private byte[]	mPng = null;

		public TestBean(boolean invalid)
		{
			if (invalid)
			{
				mXhtml = "invalid<sometag>";
				mGif = "invalid".getBytes();
				mJpeg = "invalid".getBytes();
				mPng = "invalid".getBytes();
			}
		}

		public String getXhtml()
		{
			return mXhtml;
		}

		public void setXhtml(String xhtml)
		{
			mXhtml = xhtml;
		}

		public byte[] getGif()
		{
			return mGif;
		}

		public void setGif(byte[] gif)
		{
			mGif = gif;
		}

		public byte[] getJpeg()
		{
			return mJpeg;
		}

		public void setJpeg(byte[] jpeg)
		{
			mJpeg = jpeg;
		}

		public byte[] getPng()
		{
			return mPng;
		}

		public void setPng(byte[] png)
		{
			mPng = png;
		}
	}
}
