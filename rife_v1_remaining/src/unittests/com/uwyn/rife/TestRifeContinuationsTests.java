/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestRifeContinuationsTests.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestRifeContinuationsTests extends TestSuite
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Uwyn RIFE/Continuations tests");
		
		Logger.getLogger("com.uwyn.rife").setLevel(Level.WARNING);
		
		suite.addTest(com.uwyn.rife.continuations.TestSuiteContinuations.suite());

		return suite;
	}
}
