/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.transform;

import com.uwyn.rife.cmf.dam.exceptions.ContentManagerException;
import java.util.Map;

/**
 * This interface defines the API that has to be implemented by classes that
 * are capable of transforming content data after it's initially loaded.
 * <p>The content attributes are provided to the {@link
 * #transform(Object data, Map attributes) transform} method and can be used
 * to provide hints for the transformation.
 * <p>Each transformer is supposed to transform content data of a certain data
 * type and return the transformed content in the same data type. You should
 * be careful that this data type corresponds to the data type that's returned
 * by the {@link com.uwyn.rife.cmf.format.Formatter#format format} method of
 * the {@link com.uwyn.rife.cmf.format.Formatter formatter} that handles the
 * content's mime type.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface ContentTransformer<InternalType>
{
	/**
	 * Transforms the content data and returns the transformed data in the
	 * same data type as the original.
	 * 
	 * @param data the content data that has to be transformed
	 * @param attributes a map of content attributes that can be used to
	 * provide hints or parameters for the transformation
	 * @return the transformed data
	 * @since 1.0
	 */
	public InternalType transform(InternalType data, Map<String, String> attributes) throws ContentManagerException;
}
