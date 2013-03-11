/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.config.exceptions;

public class DateFormatInitializationException extends RuntimeException
{
    private static final long serialVersionUID = 3704524567671436091L;

    public DateFormatInitializationException(Throwable cause)
    {
        super(cause);
    }

    public DateFormatInitializationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public DateFormatInitializationException(String message)
    {
        super(message);
    }
}
