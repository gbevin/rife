/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JMagickLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader.image;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.loader.ImageContentLoaderBackend;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.ImageWaiter;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.Set;
import magick.ImageInfo;
import magick.MagickImage;
import magick.MagickProducer;

/**
 * This is an image loader back-end that uses JMagick to load image files, if its
 * classes are present in the classpath.
 * <p>More information about JMagick can be obtained from <a
 * href="http://www.yeo.id.au/jmagick">http://www.yeo.id.au/jmagick</a>.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class JMagickLoader extends ImageContentLoaderBackend
{
	public Image loadFromBytes(byte[] data, Set<String> errors)
	throws ContentManagerException
	{
		return new LoaderDelegate().load(data, errors);
	}
	
	public boolean isBackendPresent()
	{
		try
		{
			return null != Class.forName("magick.ImageInfo");
		}
		catch (Throwable e)
		{
			return false;
		}
	}
	
	private static class LoaderDelegate
	{
		public Image load(byte[] data, Set<String> errors)
		throws ContentManagerException
		{
			Image image = null;
			File tmp_file = null;
			try
			{

				tmp_file = File.createTempFile("cmfjmagick", "", new File(RifeConfig.Global.getTempPath()));
				FileUtils.writeBytes(data, tmp_file);

				ImageInfo image_info = new ImageInfo(tmp_file.getAbsolutePath());
				MagickImage magick = new MagickImage();
				magick.readImage(image_info);
				
				// if it's an unsupported JMagick image, return null
				if (0 == magick.getMagick().length())
				{
					return null;
				}
				
				// create an awt image from it and wait 'till it's fully loaded
				MagickProducer producer = new MagickProducer(magick);
				image = Toolkit.getDefaultToolkit().createImage(producer);
				ImageWaiter.wait(image);
			}
			catch (Throwable e)
			{
				if (errors != null)
				{
					errors.add(ExceptionUtils.getExceptionStackTrace(e));
				}
				
				image = null;
			}
			finally
			{
				if (tmp_file != null)
				{
					tmp_file.delete();
				}
			}
			
			return image;
		}
	}
}
