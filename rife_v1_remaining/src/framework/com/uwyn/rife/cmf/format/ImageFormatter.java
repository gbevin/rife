/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ImageFormatter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.format;

import com.uwyn.rife.cmf.Content;
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.cmf.format.exceptions.FormatException;
import com.uwyn.rife.cmf.format.exceptions.InvalidContentDataTypeException;
import com.uwyn.rife.cmf.format.exceptions.UnexpectedConversionErrorException;
import com.uwyn.rife.cmf.format.exceptions.UnreadableDataFormatException;
import com.uwyn.rife.cmf.format.exceptions.UnsupportedTargetMimeTypeException;
import com.uwyn.rife.cmf.loader.ImageContentLoader;
import com.uwyn.rife.cmf.transform.ContentTransformer;
import com.uwyn.rife.tools.ImageWaiter;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * Formats raw <code>Content</code> data as an image.
 * 
 * <p>The following content attributes are supported:
 * <table>
 * <tr>
 * <td valign="top"><code>width</code></td>
 * <td>Changes the width of the image. If no height is provided, the image
 * will be proportionally scaled.</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>height</code></td>
 * <td>Changes the height of the image. If no width is provided, the image
 * will be proportionally scaled.</td>
 * </tr>
 * <tr>
 * <td valign="top"><code>longest-edge-length</code></td>
 * <td>Changes the longest edge of the image.  Aspect ratio is preserved.  The &quot;width&quot; or
 * &quot;height&quot; attributes take precendence if set, and this attribute will be ignored.
 * </td>
 * </tr>
 * </table>
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 * @see Formatter
 */
public class ImageFormatter implements Formatter<byte[], Image>
{
	public static final String CONTENT_ATTRIBUTE_WIDTH = "width";
	public static final String CONTENT_ATTRIBUTE_HEIGHT = "height";
	public static final String CONTENT_ATTRIBUTE_LONGEST_EDGE_LENGTH = "longestEdgeLength";
	
	public static final String CMF_PROPERTY_WIDTH = "cmf:width";
	public static final String CMF_PROPERTY_HEIGHT = "cmf:height";

	public byte[] format(Content content, ContentTransformer<Image> transformer)
	throws FormatException
	{
		if (!(content.getData() instanceof byte[]))
		{
			throw new InvalidContentDataTypeException(this, content.getMimeType(), byte[].class, content.getData().getClass());
		}
		
		Image data = null;
		
		// check if the content contains a cached value of the loaded data
		if (content.hasCachedLoadedData())
		{
			data = (Image)content.getCachedLoadedData();
		}
		
		if (null == data)
		{
			// get an image
			Set<String> errors = new HashSet<String>();
			data = new ImageContentLoader().load(content.getData(), false, errors);
			if (null == data)
			{
				throw new UnreadableDataFormatException(content.getMimeType(), errors);
			}
		}

		// perform additional conversions according to the provided attributes
		if (content.hasAttributes())
		{
			int width = -1;
			int height = -1;
			if (content.hasAttribute(CONTENT_ATTRIBUTE_WIDTH) ||
				content.hasAttribute(CONTENT_ATTRIBUTE_HEIGHT))
			{
				String width_value = content.getAttribute(CONTENT_ATTRIBUTE_WIDTH);
				String height_value = content.getAttribute(CONTENT_ATTRIBUTE_HEIGHT);
				// retrieve the width and the height values
				if (width_value != null)
				{
					try
					{
						width = Integer.parseInt(width_value);
					}
					catch (NumberFormatException e)
					{
						throw new FormatException(e);
					}
				}
				if (height_value != null)
				{
					try
					{
						height = Integer.parseInt(height_value);
					}
					catch (NumberFormatException e)
					{
						throw new FormatException(e);
					}
				}
			}
			else if (content.hasAttribute(CONTENT_ATTRIBUTE_LONGEST_EDGE_LENGTH))
			{
				// retrieve the attributes
				String lel_value = content.getAttribute(CONTENT_ATTRIBUTE_LONGEST_EDGE_LENGTH);
				int lel = -1;
				if (lel_value != null)
				{
					try
					{
						lel = Integer.parseInt(lel_value);
					}
					catch (NumberFormatException e)
					{
						throw new FormatException(e);
					}
				}

				if (lel >= 0)
				{
					int orig_width = data.getWidth(null);
					int orig_height = data.getHeight(null);

					// If the width is the longer side, set it to the lEL.
					if (orig_width >= orig_height)
					{
						if (lel >= 0)
						{
							width = lel;
						}
					}
					else
					{
						if (lel >= 0)
						{
							height = lel;
						}
					}
				}
			}

			if (width >= 0 || height >= 0)
			{
				int orig_width = data.getWidth(null);
				int orig_height = data.getHeight(null);
				
				// ensure that the aspect is preserved at all times
				if (width >= 0 && height >= 0)
				{
					double width_ratio = ((double)orig_width)/width;
					double height_ratio = ((double)orig_height)/height;
					if (width_ratio > height_ratio)
					{
						height = -1;
					}
					else if (width_ratio < height_ratio)
					{
						width = -1;
					}
				}
				
				// only do a rescale when the dimensions are actually different
				if ((width >= 0 && width != orig_width) ||
						(height >= 0 && height != orig_height))
				{
					data = data.getScaledInstance(width, height, Image.SCALE_SMOOTH);
					if (data.getWidth(null) < 0 ||
							data.getHeight(null) < 0)
					{
						ImageWaiter.wait(data);
					}
				}
			}
		}
		
		// transform the content, if needed
		if (transformer != null)
		{
			data = transformer.transform(data, content.getAttributes());
		}
		
		// draw it on a new buffer
		BufferedImage   buffer  = null;
		if (content.getMimeType() == MimeType.IMAGE_JPEG)
		{
			buffer = new BufferedImage(data.getWidth(null), data.getHeight(null), BufferedImage.TYPE_INT_RGB);
		}
		else
		{
			buffer = new BufferedImage(data.getWidth(null), data.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		}
		Graphics2D g2 = buffer.createGraphics();
		g2.setComposite(AlphaComposite.SrcOver);
		g2.drawImage(data, 0, 0, null);
		g2.dispose();
		
		// set the content data properties
		content
			.property(CMF_PROPERTY_WIDTH, String.valueOf(buffer.getWidth()))
			.property(CMF_PROPERTY_HEIGHT, String.valueOf(buffer.getHeight()));
		
		// write it out as the correct mimetype
		ByteArrayOutputStream   bytes_out = new ByteArrayOutputStream();
		BufferedOutputStream    buffered_out = new BufferedOutputStream(bytes_out);
		
		try
		{
			
			// retrieve a supported writer
			Iterator<ImageWriter>   writers = ImageIO.getImageWritersByMIMEType(content.getMimeType().toString());
			ImageWriter             writer = null;
			if (writers.hasNext())
			{
				writer = writers.next();
			}
			if (null == writer)
			{
				throw new UnsupportedTargetMimeTypeException(content.getMimeType());
			}
			ImageOutputStream   image_out = ImageIO.createImageOutputStream(buffered_out);
			writer.setOutput(image_out);
			writer.write(buffer);
			writer.dispose();
			bytes_out.flush();
			bytes_out.close();
		}
		catch (IOException e)
		{
			throw new UnexpectedConversionErrorException(e);
		}
		
		return bytes_out.toByteArray();
	}
}

