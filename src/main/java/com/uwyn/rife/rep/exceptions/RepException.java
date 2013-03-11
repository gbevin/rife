/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep.exceptions;

public class RepException extends RuntimeException
{
    private static final long serialVersionUID = 7021680328595344152L;

    public RepException()
    {
        super();
    }

    public RepException(String message)
    {
        super(message);
    }

    public RepException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public RepException(Throwable cause)
    {
        super(cause);
    }
}
