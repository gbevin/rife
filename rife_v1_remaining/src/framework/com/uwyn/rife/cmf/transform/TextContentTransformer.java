/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TextContentTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.transform;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import java.util.Map;

/**
 * This interface defines the API that has to be implemented by classes that
 * are capable of transforming {@link java.lang.String text} content data
 * after it's initially loaded.
 * <p>The content attributes are provided to the {@link
 * #transform(String data, Map attributes) transform} method and can be used
 * to provide hints for the transformation.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 * @see ContentTransformer
 */
public interface TextContentTransformer extends ContentTransformer<String>
{
	/**
	 * Transforms the {@link java.lang.String text} content data and returns
	 * the transformed data as text.
	 * 
	 * @param data the text that has to be transformed
	 * @param attributes a map of content attributes that can be used to
	 * provide hints or parameters for the transformation
	 * @return the transformed text
	 * @since 1.0
	 */
	public String transform(String data, Map<String, String> attributes) throws ContentManagerException;
}
