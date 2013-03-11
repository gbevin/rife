/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: LazyLoadAccessorsBytecodeTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic.instrument;

import com.uwyn.rife.asm.ClassReader;
import com.uwyn.rife.asm.ClassVisitor;
import com.uwyn.rife.asm.ClassWriter;
import com.uwyn.rife.database.querymanagers.generic.instrument.LazyLoadClassAdapter;
import com.uwyn.rife.database.querymanagers.generic.instrument.LazyLoadMethodCollector;

/**
 * This utility class will modify property accessors of classes that implement
 * the {@code Constrained} interface and add lazy loading for properties with
 * manyToOne constraints.
 * <p>
 * This is done by modifying the bytecode of the getters by checking if the
 * value that is returned is {@code null}, and in that case performing the
 * actual database call to fetch the property value. After that, the property
 * value is stored in a map that is stored in an added class field so that it
 * will not be fetched at subsequent calls of the getter. The setter is
 * modified to remove the values from this map when it is executed, so that
 * users can still provide their own values for the properties. 
 * <p>
 * WARNING: this class is not supposed to be used directly, it is made public
 * since the general RIFE EngineClassLoader has to be able to access it.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public abstract class LazyLoadAccessorsBytecodeTransformer
{
	/**
	 * The name of the synthentic class field that will be added so that the
	 * instance of the {@code GenericQueryManager} that restored the bean can be
	 * used afterwards to lazily load the relations.
	 */
	public final static String	GQM_VAR_NAME = "$Rife$Generic$Query$Manager$";
	/**
	 * The name of the synthentic class field that will be added so that already
	 * restored lazy relations can be cached.
	 */
	public final static String	LAZYLOADED_VAR_NAME = "$Rife$Lazy$Loaded$Properties$";
	
	/**
	 * Performs the actual modification of the bean class's bytecode.
	 * @since 1.6
	 *
	 * @param origBytes the bytes of the bean class that should be modified
	 * @return the modified bytes
	 */
	public static byte[] addLazyLoadToBytes(byte[] origBytes)
	{
		// obtain all the methods that should be instrumented to add lazy loading
		ClassReader cr = new ClassReader(origBytes);
		
		LazyLoadMethodCollector	method_collector = new LazyLoadMethodCollector();
		cr.accept(method_collector, ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
		
		ClassWriter		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassVisitor	meta_data_adapter = new LazyLoadClassAdapter(method_collector.getMethods(), cw);
		cr.accept(meta_data_adapter, ClassReader.SKIP_FRAMES);
		
		return cw.toByteArray();
	}
}
