/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep.exceptions;

public class BlockingParticipantExpectedException extends RepException
{
    private static final long serialVersionUID = -4424055831049347952L;
    private String className = null;

    public BlockingParticipantExpectedException(String className)
    {
        super("The participant '" + className + "' doesn't extend BlockingParticipant, which is required to be able to add it to a BlockingRepository.");

        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }
}
