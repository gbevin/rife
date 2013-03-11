/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataInstrumenter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.instrument;

/**
 * This is a bytecode instrumenter that will modify classes so that they
 * receive the functionalities that are required to support meta-data
 * merging.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6.2
 */
public class MetaDataInstrumenter
{
	public final static String	META_DATA_SUFFIX = "MetaData";

	public static byte[] instrument(final ClassLoader loader, final String classNameDotted, final byte[] classfileBuffer)
	{
		Class metadata_class = null;

		String metadata_classname = MetaDataClassAnnotationDetector.getMetaDataClassName(classfileBuffer);

		if (null == metadata_classname &&
			!classNameDotted.endsWith(META_DATA_SUFFIX))
		{
			metadata_classname = classNameDotted + META_DATA_SUFFIX;
		}

		if (metadata_classname != null)
		{
			try
			{
				metadata_class = loader.loadClass(metadata_classname);
			}
			catch (ClassNotFoundException e)
			{
				metadata_class = null;
			}
		}

		if (metadata_class != null)
		{
			byte[] result = MetaDataBytecodeTransformer.mergeMetaDataIntoBytes(classfileBuffer, metadata_class);
			if (result != null)
			{
				return result;
			}
		}

		return classfileBuffer;
	}
}