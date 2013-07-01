/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

public abstract class ParseSteps
{
    public static ParseStep chr(char chr)
    {
        return new ParseStep(new SingleChar(chr));
    }

    public static ParseStep ws()
    {
        return new ParseStep(new Whitespace());
    }
}