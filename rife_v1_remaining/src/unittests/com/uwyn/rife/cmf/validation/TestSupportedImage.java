/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSupportedImage.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.validation;

import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;
import com.uwyn.rife.tools.FileUtils;
import java.net.URL;
import junit.framework.TestCase;

public class TestSupportedImage extends TestCase
{
	public TestSupportedImage(String name)
	{
		super(name);
	}

	public void testValidateNull()
	throws Exception
	{
		SupportedImage		rule = new SupportedImage("image");
        ImageBean			bean = new ImageBean();
		ConstrainedProperty	property = new ConstrainedProperty("image").mimeType(MimeType.IMAGE_PNG);
		bean.addConstraint(property);
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
		assertNull(property.getCachedLoadedData());
	}

	public void testValidateSupported()
	throws Exception
	{
		SupportedImage		rule = new SupportedImage("image");
        ImageBean			bean = new ImageBean();
		ConstrainedProperty	property = new ConstrainedProperty("image").mimeType(MimeType.IMAGE_PNG);
		bean.addConstraint(property);
		URL image_resource = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] image_bytes = FileUtils.readBytes(image_resource);
		bean.setImage(image_bytes);
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
		assertNotNull(property.getCachedLoadedData());
	}

	public void testValidateUnsupported()
	throws Exception
	{
		SupportedImage		rule = new SupportedImage("image");
        ImageBean			bean = new ImageBean();
		ConstrainedProperty	property = new ConstrainedProperty("image").mimeType(MimeType.IMAGE_PNG);
		bean.addConstraint(property);
		byte[] image_bytes = new byte[] {2, 9, 7, 12, 45}; // just random values
		bean.setImage(image_bytes);
		rule.setBean(bean);
		assertFalse(rule.validate());
		assertTrue(rule.getLoadingErrors().size() > 0);
		assertNull(property.getCachedLoadedData());
	}

	public void testValidateNotConstrained()
	throws Exception
	{
		SupportedImage			rule = new SupportedImage("image");
        ImageBeanNotConstrained	bean = new ImageBeanNotConstrained();
		URL image_resource = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] image_bytes = FileUtils.readBytes(image_resource);
		bean.setImage(image_bytes);
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
	}

	public void testValidateNotCmfProperty()
	throws Exception
	{
		SupportedImage			rule = new SupportedImage("image");
        ImageBeanValidation		bean = new ImageBeanValidation();
		ConstrainedProperty		property = new ConstrainedProperty("image");
		bean.addConstraint(property);
		URL image_resource = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] image_bytes = FileUtils.readBytes(image_resource);
		bean.setImage(image_bytes);
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
	}

	public void testValidateUnknownProperty()
	throws Exception
	{
		SupportedImage	rule = new SupportedImage("image_unknown");
        ImageBean		bean = new ImageBean();
		rule.setBean(bean);
		assertTrue(rule.validate());
		assertNull(rule.getLoadingErrors());
	}

	public void testGetError()
	throws Exception
	{
		SupportedImage rule = new SupportedImage("image");
		assertEquals("image", rule.getError().getSubject());
		assertEquals("INVALID", rule.getError().getIdentifier());
	}

	public static class ImageBean extends Validation
	{
		private byte[] 		mImage = null;

		public ImageBean()
		{
		}

		public byte[] getImage()
		{
			return mImage;
		}

		public void setImage(byte[] image)
		{
			mImage = image;
		}
	}

	public static class ImageBeanNotConstrained
	{
		private byte[] 		mImage = null;

		public ImageBeanNotConstrained()
		{
		}

		public byte[] getImage()
		{
			return mImage;
		}

		public void setImage(byte[] image)
		{
			mImage = image;
		}
	}

	public static class ImageBeanValidation extends Validation
	{
		private byte[] 		mImage = null;

		public ImageBeanValidation()
		{
		}

		public byte[] getImage()
		{
			return mImage;
		}

		public void setImage(byte[] image)
		{
			mImage = image;
		}
	}
}
