/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ImageroReaderLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader.image;

import com.imagero.reader.ImageReader;
import com.imagero.reader.ReaderFactory;
import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import com.uwyn.rife.cmf.loader.ImageContentLoaderBackend;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.ImageWaiter;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

/**
 * This is an image loader back-end that uses ImageroReader to load image
 * files, if its classes are present in the classpath.
 * <p>More information about ImageroReader can be obtained from <a
 * href="http://reader.imagero.com">http://reader.imagero.com</a>.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class ImageroReaderLoader extends ImageContentLoaderBackend
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
			return null != Class.forName("com.imagero.reader.ReaderFactory");
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

			PrintStream default_out = System.out;
			PrintStream default_err = System.err;
			try
			{
				System.setOut(new PrintStream(new ByteArrayOutputStream())); // remove output to standard output
				System.setErr(new PrintStream(new ByteArrayOutputStream())); // remove output to standard error

				ImageReader imagero = ReaderFactory.createReader(in);
				if (imagero.getImageCount() > 0)
				{
					ImageProducer producer = imagero.getProducer(0);

					// create an awt image from it and wait 'till it's fully loaded
					image = Toolkit.getDefaultToolkit().createImage(producer);
					ImageWaiter.wait(image);
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
			finally
			{
				System.setOut(default_out); // restore default system standard output
				System.setErr(default_err); // restore default system standard error output
			}

			return image;
		}
	}
}
