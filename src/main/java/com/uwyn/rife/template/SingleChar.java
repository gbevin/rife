/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.template;

class SingleChar implements ParseCondition
{
    private final int expected;

    SingleChar(int expected)
    {
        this.expected = expected;
    }

    @Override
    public boolean isValid(int codePoint)
    {
        return expected == codePoint;
    }

    @Override
    public boolean isRepeatable()
    {
        return false;
    }
}
