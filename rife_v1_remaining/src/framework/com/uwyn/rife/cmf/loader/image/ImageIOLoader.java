/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ImageIOLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader.image;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.loader.ImageContentLoaderBackend;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.ImageWaiter;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 * This is an image loader back-end that uses ImageIO to load image files.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class ImageIOLoader extends ImageContentLoaderBackend
{
	public Image loadFromBytes(byte[] data, Set<String> errors)
	throws ContentManagerException
	{
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		Image image = null;

		try
		{
			// create an awt image and wait 'till it's fully loaded
			image = ImageIO.read(is);
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
	
	public boolean isBackendPresent()
	{
		return true;
	}
}
