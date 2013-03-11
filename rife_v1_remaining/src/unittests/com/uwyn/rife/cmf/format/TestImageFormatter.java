/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestImageFormatter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import javax.swing.ImageIcon;

import junit.framework.TestCase;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.format.exceptions.FormatException;
import com.uwyn.rife.cmf.format.exceptions.InvalidContentDataTypeException;
import com.uwyn.rife.cmf.format.exceptions.UnreadableDataFormatException;
import com.uwyn.rife.cmf.format.exceptions.UnsupportedTargetMimeTypeException;
import com.uwyn.rife.cmf.loader.ImageContentLoader;
import com.uwyn.rife.cmf.transform.ImageContentTransformer;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.tools.FileUtils;

public class TestImageFormatter extends TestCase
{
	public TestImageFormatter(String name)
	{
		super(name);
	}

	public void testFormatBasic()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	public void testFormatInvalidDataType()
	throws Exception
	{
		Content content = new Content(MimeType.IMAGE_PNG, new Object());
        ImageFormatter formatter = new ImageFormatter();
		try
		{
			formatter.format(content, null);
			fail();
		}
		catch (InvalidContentDataTypeException e)
		{
			assertSame(byte[].class, e.getExpectedType());
			assertSame(formatter, e.getFormatter());
			assertSame(MimeType.IMAGE_PNG, e.getMimeType());
			assertSame(Object.class, e.getReceivedType());
		}
	}

	public void testFormatCachedLoadedData()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		Image image = new ImageContentLoader().load(data_image_gif, false, null);
		content.setCachedLoadedData(image);

        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	public void testFormatUnreadableData()
	throws Exception
	{
		Content content = new Content(MimeType.IMAGE_PNG, new byte[] {34, 9, 12, 5, 92}); // random invalid bytes
        ImageFormatter formatter = new ImageFormatter();
		try
		{
			formatter.format(content, null);
			fail();
		}
		catch (UnreadableDataFormatException e)
		{
			assertSame(MimeType.IMAGE_PNG, e.getMimeType());
			assertTrue(e.getErrors().size() > 0);
		}
	}

	public void testFormatUnsupportedMimetype()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.APPLICATION_XHTML, data_image_gif);
        ImageFormatter formatter = new ImageFormatter();
		try
		{
			formatter.format(content, null);
			fail();
		}
		catch (UnsupportedTargetMimeTypeException e)
		{
			assertSame(MimeType.APPLICATION_XHTML, e.getMimeType());
		}
	}

	public void testFormatAttributeWidth()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("width", 20);
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn_resized-width_20.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	public void testFormatInvalidAttributeWidth()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("width", "notanumber");
        ImageFormatter formatter = new ImageFormatter();
		try
		{
			formatter.format(content, null);
			fail();
		}
		catch (FormatException e)
		{
			assertTrue(e.getCause() instanceof NumberFormatException);
		}
	}

	public void testFormatAttributeLongestEdgeLengthHorizontal()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("longestEdgeLength", 20);
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn_resized-width_20.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	public void testFormatAttributeLongestEdgeLengthVertical()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn-rotated_90_cw.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("longestEdgeLength", 20);
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn-rotated_90_cw_resized-width_20.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	public void testFormatInvalidAttributeLongestEdgeLength()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("longestEdgeLength", "notanumber");
        ImageFormatter formatter = new ImageFormatter();
		try
		{
			formatter.format(content, null);
			fail();
		}
		catch (FormatException e)
		{
			assertTrue(e.getCause() instanceof NumberFormatException);
		}
	}

	public void testFormatNegativeAttributeLongestEdgeLength()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("longestEdgeLength", "-20");
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	public void testFormatAttributeHeight()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("height", 15);
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn_resized-height_15.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	public void testFormatInvalidAttributeHeight()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("height", "notanumber");
        ImageFormatter formatter = new ImageFormatter();
		try
		{
			formatter.format(content, null);
			fail();
		}
		catch (FormatException e)
		{
			assertTrue(e.getCause() instanceof NumberFormatException);
		}
	}

	public void testFormatAttributeWidthHeight()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_JPEG, data_image_gif);
		content
			.attribute("width", 30)
			.attribute("height", 70);
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn_resized-width_30-height_70.jpg");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}
	
	public void testFormatAttributeWidthHeight2()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);
		
		Content content = new Content(MimeType.IMAGE_JPEG, data_image_gif);
		content
			.attribute("width", 300)
			.attribute("height", 70);
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);
		
		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn_resized-width_300-height_70.jpg");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);
		
		assertTrue(Arrays.equals(data_image_png, result));
	}
	
	public void testFormatNegativeAttributeWidthHeight()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("width", -12)
			.attribute("height", -5);
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	public void testFormatUnsupportedAttributes()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
		content
			.attribute("unsupported", "blah");
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, null);

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	public void testFormatTransformer()
	throws Exception
	{
		URL image_resource_gif = ResourceFinderClasspath.getInstance().getResource("uwyn.gif");
		byte[] data_image_gif = FileUtils.readBytes(image_resource_gif);

		Content content = new Content(MimeType.IMAGE_PNG, data_image_gif);
        ImageFormatter formatter = new ImageFormatter();
		byte[] result = formatter.format(content, new TransparentImageTransformer());

		assertNotNull(result);

		URL image_resource_png = ResourceFinderClasspath.getInstance().getResource("uwyn-transparent.png");
		byte[] data_image_png = FileUtils.readBytes(image_resource_png);

		assertTrue(Arrays.equals(data_image_png, result));
	}

	static class TransparentImageTransformer implements ImageContentTransformer
	{
		public Image transform(Image data, Map<String, String> attributes)
		throws ContentManagerException
		{
			// retreive the rife logo to stamp on top of it
			URL		rife_url = ResourceFinderClasspath.getInstance().getResource("rife-logo_small.png");
			Image	rife_image = new ImageIcon(rife_url).getImage();

			// create a new drawing buffer
			int width = data.getWidth(null);
			int height = data.getHeight(null);
			BufferedImage   buffer  = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2 = buffer.createGraphics();

			// make the background white
			g2.setColor(Color.WHITE);
			g2.fillRect(0, 0, width, height);

			// draw a transparent image on it
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2.drawImage(rife_image, 0, 0, null);

			// draw a transparent image on it
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2.drawImage(data, 0, 0, null);

			// clean up
			g2.dispose();

			return buffer;
		}
	}
}
