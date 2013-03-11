/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaDataTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.instrument;

import com.uwyn.rife.site.instrument.MetaDataInstrumenter;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * This is a bytecode transformer that will modify classes so that they
 * receive the functionalities that are required to support meta-data
 * merging.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class MetaDataTransformer extends RifeTransformer
{
	protected byte[] transformRife(ClassLoader loader, String classNameInternal, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
	{
		return MetaDataInstrumenter.instrument(loader, classNameInternal.replace('/', '.'), classfileBuffer);
	}
}