/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SerializationUtilsErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools.exceptions;

public abstract class SerializationUtilsErrorException extends Exception
{
    private static final long serialVersionUID = 5207703400979008703L;

    public SerializationUtilsErrorException(Throwable cause)
    {
        super(cause);
    }

    public SerializationUtilsErrorException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SerializationUtilsErrorException(String message)
    {
        super(message);
    }
}
