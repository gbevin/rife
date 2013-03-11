/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataBytecodeTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site.instrument;

import com.uwyn.rife.asm.ClassReader;
import com.uwyn.rife.asm.ClassVisitor;
import com.uwyn.rife.asm.ClassWriter;
import com.uwyn.rife.site.MetaDataMerged;

/**
 * This utility class provides an entrance method to modify the bytecode of a
 * class so that meta data from a sibling class is merged into the first class.
 * <p>
 * Basically, this automatically creates an instance of the meta data class and
 * stores it as a field of the modified class. All the interfaces of the meta
 * data class are also automatically implemented by the modified class by
 * delegating all the method calls to the added field instance.
 * <p>
 * WARNING: this class is not supposed to be used directly, it is made public
 * since the general RIFE EngineClassLoader has to be able to access it.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public abstract class MetaDataBytecodeTransformer
{
	/**
	 * Performs the actual merging of the meta data class' functionalities into
	 * the bytes of the class that were provided.
	 *
	 * @param origBytes the bytes that have to be modied
	 * @param metaData the meta data classes that will be merged into it
	 * @return the modified bytes
	 * @since 1.6
	 */
	public static byte[] mergeMetaDataIntoBytes(byte[] origBytes, Class metaData)
	{
		// only perform the instrumentation if the MetaDataMerged interface is implemented
		if (!MetaDataMerged.class.isAssignableFrom(metaData))
		{
			return origBytes;
		}
		
		// merge the meta data class into the original bytes
		ClassReader		cr = new ClassReader(origBytes);
		
		MetaDataMethodCollector	method_collector = new MetaDataMethodCollector();
		cr.accept(method_collector, ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
		
		ClassWriter		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassVisitor	meta_data_adapter = new MetaDataClassAdapter(method_collector.getMethods(), metaData, cw);
		cr.accept(meta_data_adapter, ClassReader.SKIP_FRAMES);

		return cw.toByteArray();
	}
}
