/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteIoc extends TestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite("IoC test suite");

        suite.addTestSuite(com.uwyn.rife.ioc.TestHierarchicalProperties.class);
// TODO
//		suite.addTestSuite(com.uwyn.rife.ioc.TestPropertyValueList.class);
        suite.addTestSuite(com.uwyn.rife.ioc.TestPropertyValueObject.class);
//      suite.addTestSuite(com.uwyn.rife.ioc.TestPropertyValueParticipant.class);
//		suite.addTestSuite(com.uwyn.rife.ioc.TestPropertyValueTemplate.class);

        return suite;
    }
}
