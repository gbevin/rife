/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteRep.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteRep extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("Rep API test suite");

		suite.addTestSuite(com.uwyn.rife.rep.TestBlockingRepository.class);
		suite.addServersideTestSuite(com.uwyn.rife.rep.TestBlockingRepositoryServerside.class);
		suite.addTestSuite(com.uwyn.rife.rep.TestParticipantConfig.class);

		return suite;
	}
}
