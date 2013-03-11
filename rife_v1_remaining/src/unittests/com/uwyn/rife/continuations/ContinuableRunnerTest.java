/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContinuableRunnerTest.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.continuations;

import com.uwyn.rife.continuations.basic.BasicContinuableClassLoader;
import com.uwyn.rife.continuations.basic.BasicContinuableRunner;

public class ContinuableRunnerTest extends BasicContinuableRunner
{
	private static final ContinuationConfigInstrument CONFIG_INSTRUMENT = new ContinuationConfigInstrumentTests();
	
	private static final ContinuableSupport CONTINUABLE_SUPPORT_DUMMY = new ContinuableSupport();
	
	public ContinuableRunnerTest() throws ClassNotFoundException
	{
        super(CONFIG_INSTRUMENT, new BasicContinuableClassLoader(CONFIG_INSTRUMENT));
		Class.forName(ContinuableSupport.class.getName());
	}
	
	public void beforeExecuteEntryMethodHook(ContinuableObject object)
	{
		if (object instanceof ContinuableSupportAware)
		{
			((ContinuableSupportAware)object).setContinuableSupport(CONTINUABLE_SUPPORT_DUMMY);
		}
	}
}
