/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc.exceptions;

public class ParticipantUnknownException extends PropertyValueException
{
    private static final long serialVersionUID = -7237743954342408170L;
    private String mame = null;

    public ParticipantUnknownException(String name)
    {
        super("The participant '" + name + "' isn't known in the default repository.");

        mame = name;
    }

    public String getName()
    {
        return mame;
    }
}
