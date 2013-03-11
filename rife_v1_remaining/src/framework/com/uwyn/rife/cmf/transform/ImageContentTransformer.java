/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ImageContentTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.transform;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import java.awt.Image;
import java.util.Map;

/**
 * This interface defines the API that has to be implemented by classes that
 * are capable of transforming {@link java.awt.Image image} content data after
 * it's initially loaded.
 * <p>The content attributes are provided to the {@link
 * #transform(Image data, Map attributes) transform} method and can be used to
 * provide hints for the transformation.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 * @see ContentTransformer
 */
public interface ImageContentTransformer extends ContentTransformer<Image>
{
	/**
	 * Transforms the {@link java.awt.Image image} content data and returns
	 * the transformed data as an <code>Image</code>.
	 * 
	 * @param data the image that has to be transformed
	 * @param attributes a map of content attributes that can be used to
	 * provide hints or parameters for the transformation
	 * @return the transformed image
	 * @since 1.0
	 */
	public Image transform(Image data, Map<String, String> attributes) throws ContentManagerException;
}
