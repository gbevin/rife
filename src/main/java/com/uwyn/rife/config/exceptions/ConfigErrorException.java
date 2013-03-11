/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.config.exceptions;

public class ConfigErrorException extends Exception
{
    private static final long serialVersionUID = 4090824718922884219L;

    public ConfigErrorException(Throwable cause)
    {
        super(cause);
    }

    public ConfigErrorException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConfigErrorException(String message)
    {
        super(message);
    }
}
