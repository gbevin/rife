/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JaiLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader.image;

import com.sun.media.jai.codec.SeekableStream;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.loader.ImageContentLoaderBackend;
import com.uwyn.rife.tools.ExceptionUtils;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

/**
 * This is an image loader back-end that uses Java Advanced Imaging to load
 * image files, if its classes are present in the classpath.
 * <p>More information about Java Advanced Imaging can be obtained from <a
 * href="http://java.sun.com/products/java-media/jai">http://java.sun.com/products/java-media/jai</a>.
 * <p>Plug-ins for additional formats can be obtained from <a
 * href="http://java.sun.com/products/java-media/jai/downloads/download-iio.html">http://java.sun.com/products/java-media/jai/downloads/download-iio.html</a>.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class JaiLoader extends ImageContentLoaderBackend
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
			return null != Class.forName("javax.media.jai.JAI");
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
			BufferedImage image = null;
	
			SeekableStream in_stream = SeekableStream.wrapInputStream(in, false);
			PlanarImage jai = JAI.create("stream", in_stream);
			PrintStream default_err = System.err;
			try
			{
				System.setErr(new PrintStream(new ByteArrayOutputStream())); // remove output to standard error
				
				image = jai.getAsBufferedImage();
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
				System.setErr(default_err); // restore default system standard error output
			}

			return image;
		}
	}
}
