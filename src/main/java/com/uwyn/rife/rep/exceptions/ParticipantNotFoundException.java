/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep.exceptions;

public class ParticipantNotFoundException extends RepException
{
    private static final long serialVersionUID = -8275368982188954410L;
    private String name = null;

    public ParticipantNotFoundException(String name)
    {
        super("The participant '" + name + "' couldn't be found.");

        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
