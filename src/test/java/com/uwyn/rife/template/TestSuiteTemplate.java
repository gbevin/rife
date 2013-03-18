/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

import com.uwyn.rife.antlr4.runtime.ANTLRInputStream;
import com.uwyn.rife.antlr4.runtime.CommonTokenStream;
import com.uwyn.rife.antlr4.runtime.tree.ParseTree;
import com.uwyn.rife.antlr4.runtime.tree.ParseTreeWalker;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.template.antlr.TemplateLexer;
import com.uwyn.rife.template.antlr.TemplateParser;
import com.uwyn.rife.template.antlr.TemplateParsingListener;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.IOException;
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
                try
                {
                    ANTLRInputStream input = new ANTLRInputStream(stream);
                    TemplateLexer lexer = new TemplateLexer(input);
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    TemplateParser parser = new TemplateParser(tokens);
                    ParseTree tree = parser.document();
                    ParseTreeWalker walker = new ParseTreeWalker();
                    walker.walk(new TemplateParsingListener(), tree);
                }
                catch (IOException e)
                {
                    throw new InnerClassException(e);
                }

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