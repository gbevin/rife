/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteContinuations.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteContinuations extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Continuations API test suite");
		
		suite.addTestSuite(com.uwyn.rife.continuations.TestPause.class);
		suite.addTestSuite(com.uwyn.rife.continuations.TestStepBack.class);
		suite.addTestSuite(com.uwyn.rife.continuations.TestCall.class);
		
		return suite;
	}
}