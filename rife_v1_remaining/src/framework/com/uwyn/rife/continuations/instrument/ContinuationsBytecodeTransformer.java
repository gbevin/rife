/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationsBytecodeTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.asm.ClassReader;
import com.uwyn.rife.asm.ClassVisitor;
import com.uwyn.rife.asm.ClassWriter;
import com.uwyn.rife.continuations.ContinuationConfigInstrument;
import com.uwyn.rife.continuations.instrument.ContinuationDebug;
import com.uwyn.rife.continuations.instrument.MetricsClassVisitor;
import com.uwyn.rife.continuations.instrument.ResumableClassAdapter;
import com.uwyn.rife.continuations.instrument.TypesClassVisitor;
import java.util.logging.Level;

/**
 * Abstract class that transforms the bytecode of regular classes so that
 * they support continuations functionalities.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public abstract class ContinuationsBytecodeTransformer
{
	/**
	 * Perform the class transformation.
	 * <p>If the class doesn't implement the marker interface that is setup
	 * in the instrumentation config, the original bytes will be returned.
	 *
	 * @param configInstrument the configuration for the instrumentation
	 * @param rawBytes the raw bytes of the class to instrument
	 * @param classname the name of the class to instrument
	 * @return a byte array with the instrumented bytecode; or
	 * <p>the original raw byte array if the class didn't need to be
	 * instrumented
	 * @throws ClassNotFoundException when an error occurs during the
	 * inspection or transformation
	 * @since 1.6
	 */
	public static byte[] transformIntoResumableBytes(ContinuationConfigInstrument configInstrument, byte[] rawBytes, String classname) throws ClassNotFoundException
	{
		// adapts the class on the fly
		byte[]	resumable_bytes = null;
		int		reader_flags = ClassReader.SKIP_FRAMES;
		try
		{
			ContinuationDebug.LOGGER.finest("METRICS:");
			ClassReader metrics_reader = new ClassReader(rawBytes);
			MetricsClassVisitor metrics_visitor = new MetricsClassVisitor(configInstrument, classname);
			metrics_reader.accept(metrics_visitor, reader_flags);
			ContinuationDebug.LOGGER.finest("\n");
			
			if (metrics_visitor.makeResumable())
			{
				ContinuationDebug.LOGGER.finest("TYPES:");
				ClassReader			types_reader = new ClassReader(rawBytes);
				TypesClassVisitor types_visitor = new TypesClassVisitor(configInstrument, metrics_visitor, classname);
				types_reader.accept(types_visitor, reader_flags);
				ContinuationDebug.LOGGER.finest("\n");
				
				ContinuationDebug.LOGGER.finest("SOURCE:");
				ClassReader		resumable_reader = new ClassReader(rawBytes);
				ClassWriter resumable_writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				ClassVisitor resumable_visitor = new ResumableClassAdapter(configInstrument, metrics_visitor, types_visitor, classname, resumable_writer);
				resumable_reader.accept(resumable_visitor, reader_flags);
				resumable_bytes = resumable_writer.toByteArray();
				ContinuationDebug.LOGGER.finest("\n");
				
				///CLOVER:OFF
				if (ContinuationDebug.LOGGER.isLoggable(Level.FINEST))
				{
					ContinuationDebug.LOGGER.finest("RESULT:");
					ClassReader		reporting_reader = new ClassReader(resumable_bytes);
					ClassVisitor	reporting_visitor = new ResumableClassAdapter(configInstrument, null, null, classname, null);
					reporting_reader.accept(reporting_visitor, reader_flags);
				}
				///CLOVER:ON
			}
		}
		catch (Exception e)
		{
			throw new ClassNotFoundException(classname, e);
		}
		
		return resumable_bytes;
	}
}
