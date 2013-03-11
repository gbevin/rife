/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuationsTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.continuations.ContinuationConfigInstrument;
import com.uwyn.rife.instrument.RifeTransformer;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * A bytecode transformer that will modify classes so that they
 * receive the functionalities that are required to support the continuations
 * functionalities as they are provided by RIFE's web engine.
 * <p>This transformer is internally used by the {@link ContinuationsAgent}.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ContinuationsTransformer extends RifeTransformer
{
	private ContinuationConfigInstrument	mConfigInstrument;

	/**
	 * Creates a new transformer.
	 *
	 * @param configInstrument the instance of the instrumentation
	 * configuration that will be used for the transformation
	 * @since 1.6
	 */
	public ContinuationsTransformer(ContinuationConfigInstrument configInstrument)
	{
		mConfigInstrument = configInstrument;
	}
	
	protected byte[] transformRife(ClassLoader loader, String classNameInternal, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
	{
		try
		{
			byte[] result = ContinuationsBytecodeTransformer.transformIntoResumableBytes(mConfigInstrument, classfileBuffer, classNameInternal.replace('/', '.'));
			if (result != null)
			{
				return result;
			}
		}
		catch (ClassNotFoundException e)
		{
			return classfileBuffer;
		}
		
		return classfileBuffer;
	}
}
