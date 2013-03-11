/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuableDetector.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations.instrument;

import com.uwyn.rife.continuations.ContinuationConfigInstrument;
import com.uwyn.rife.instrument.ClassBytesProvider;
import com.uwyn.rife.instrument.ClassInterfaceDetector;

/**
 * Detects whether a class implements the continuable marker interface that is
 * setup in the provided instrumentation config.
 * <p>This is done without actually loading the class and by analyzing the
 * bytecode itself. It's important to not load the class because the class is
 * supposed to be instrumented before actually loading it, if it implements
 * the marker interface.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ContinuableDetector extends ClassInterfaceDetector
{
	/**
	 * Creates a new instance of the detector.
	 *
	 * @param config the instrumentation configuration
	 * @param bytesProvider the bytecode provider that will be used to load
	 * the bytes relevant classes and interfacse
	 * @since 1.6
	 */
	public ContinuableDetector(ContinuationConfigInstrument config, ClassBytesProvider bytesProvider)
	{
		super(bytesProvider, config.getContinuableMarkerInterfaceName().replace('.', '/').intern());
	}
}
