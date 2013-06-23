/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.InputStream;

public class TestSuiteTemplate extends TestSuite
{
    public static Test suite() throws ResourceFinderErrorException
    {
        TestSuite suite = new TestSuite("Template engine test suite");

        ResourceFinder resource_finder = ResourceFinderClasspath.getInstance();
        resource_finder.useStream("templates/basic_gaps_snippets.html", new InputStreamUser()
        {
            public Object useInputStream(InputStream stream) throws InnerClassException
            {

                return null;
            }
        });

//        suite.addTestSuite(com.uwyn.rife.template.TestTemplate.class);
//        suite.addTestSuite(com.uwyn.rife.template.TestParsed.class);
//        suite.addTestSuite(com.uwyn.rife.template.TestParser.class);
//        suite.addTestSuite(com.uwyn.rife.template.TestTemplateFactory.class);

        return suite;
    }
}