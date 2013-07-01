/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

import junit.framework.TestCase;

public class TestParser extends TestCase
{
    public TestParser(String name)
    {
        super(name);
    }

    public void testInstantiation()
    {
        Parser p = new Parser();
        p.parse("<!--  gap   identifier  /-->");
    }
}