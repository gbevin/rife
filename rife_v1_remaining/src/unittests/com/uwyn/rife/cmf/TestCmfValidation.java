/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCmfValidation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import com.uwyn.rife.cmf.dam.ContentImage;
import com.uwyn.rife.cmf.validation.SupportedXhtml;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.ValidationRule;
import com.uwyn.rife.site.ValidationRuleNotEmpty;
import com.uwyn.rife.site.ValidationRuleNotNull;
import com.uwyn.rife.tools.FileUtils;
import java.net.URL;
import java.util.Iterator;
import junit.framework.TestCase;

public class TestCmfValidation extends TestCase
{
	public TestCmfValidation(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		CmfValidation validation = new CmfValidation();
		assertEquals(0, validation.getConstrainedProperties().size());
		assertEquals(0, validation.getRules().size());
	}

	public void testMimeTypeConstraint()
	{
		CmfValidation validation = new CmfValidation();
		ConstrainedProperty property = new ConstrainedProperty("property")
			.mimeType(MimeType.APPLICATION_XHTML)
			.notNull(true);

		validation.addConstraint(property);

		assertEquals(1, validation.getConstrainedProperties().size());
		assertEquals(2, validation.getRules().size());
		assertSame(property, validation.getConstrainedProperties().iterator().next());
		Iterator<ValidationRule> it = validation.getRules().iterator();
		assertTrue(it.next() instanceof ValidationRuleNotNull);
		assertTrue(it.next() instanceof SupportedXhtml);
	}

	public void testNoCmfConstraints()
	{
		CmfValidation validation = new CmfValidation();
		ConstrainedProperty property = new ConstrainedProperty("property")
			.notEmpty(true);

		validation.addConstraint(property);

		assertEquals(1, validation.getConstrainedProperties().size());
		assertEquals(1, validation.getRules().size());
		assertSame(property, validation.getConstrainedProperties().iterator().next());
		Iterator<ValidationRule> it = validation.getRules().iterator();
		assertTrue(it.next() instanceof ValidationRuleNotEmpty);
	}

	public void testLoadingErrors()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
		ContentImage content = new ContentImage()
			.name("the content name")
			.image(data_image_gif);
		assertTrue(content.validate());
		assertNull(content.getLoadingErrors("image"));
		
		byte[] data_image_invalid = new byte[] {12, 4, 34, 3, 23}; // random invalid bytes
		content.setImage(data_image_invalid);
		assertFalse(content.validate());
		assertTrue(content.getLoadingErrors("image").size() > 0);
	}
}
