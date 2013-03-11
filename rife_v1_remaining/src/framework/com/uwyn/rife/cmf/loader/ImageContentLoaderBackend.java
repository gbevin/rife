/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ImageContentLoaderBackend.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import java.awt.Image;
import java.util.Set;

/**
 * This is an abstract class that should be implemented by all image content
 * loader back-ends.
 * <p>The {@link #load(Object, boolean, Set) load} method simply checks the
 * type of the data and delegates the handling to typed methods that should be
 * implemented by the back-ends.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class ImageContentLoaderBackend implements ContentLoaderBackend<Image>
{
	/**
	 * Loads the data from a byte array.
	 * 
	 * @param data the raw data that has to be loaded
	 * @param errors a set to which possible error messages will be added
	 * @return an instance of the <code>Image</code>; or
	 * <p><code>null</code> if the raw data couldn't be loaded
	 */
	protected abstract Image loadFromBytes(byte[] data, Set<String> errors) throws ContentManagerException;

	public Image load(Object data, boolean fragment, Set<String> errors)
	throws ContentManagerException
	{
		Image image = null;
		
		if (data instanceof byte[])
		{
			image = loadFromBytes((byte[])data, errors);
		}
		else
		{
			return null;
		}
		
		return image;
	}
}
