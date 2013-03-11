/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ImageJLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader.image;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.loader.ImageContentLoaderBackend;
import com.uwyn.rife.tools.ExceptionUtils;
import ij.ImagePlus;
import ij.io.Opener;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.util.Set;

/**
 * This is an image loader back-end that uses ImageJ to load TIFF files, if
 * its classes are present in the classpath.
 * <p>More information about ImageJ can be obtained from <a
 * href="http://rsb.info.nih.gov/ij/">http://rsb.info.nih.gov/ij/</a>.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class ImageJLoader extends ImageContentLoaderBackend
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
			return null != Class.forName("ij.io.Opener");
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
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			Image image = null;
	
			try
			{
				ImagePlus imagej = new Opener().openTiff(in, "cmfdata");
				if (imagej != null)
				{
					image = imagej.getImage();
				}
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
