/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JimiLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader.image;

import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.raster.JimiRasterImage;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.loader.ImageContentLoaderBackend;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.ImageWaiter;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.io.ByteArrayInputStream;
import java.util.Set;

/**
 * This is an image loader back-end that uses Jimi to load image files, if its
 * classes are present in the classpath.
 * <p>More information about Jimi can be obtained from <a
 * href="http://java.sun.com/products/jimi">http://java.sun.com/products/jimi</a>.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class JimiLoader extends ImageContentLoaderBackend
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
			return null != Class.forName("com.sun.jimi.core.Jimi");
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}
	
	private static class LoaderDelegate
	{
		public Image load(byte[] data, Set<String> errors)
		throws ContentManagerException
		{
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			Image image = null;
	
			try
			{
				JimiRasterImage raster_image = Jimi.getRasterImage(is, Jimi.SYNCHRONOUS);
				ImageProducer producer = raster_image.getImageProducer();
				
				// create an awt image from it and wait 'till it's fully loaded
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
			
			return image;
		}
	}
}
