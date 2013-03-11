/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc.exceptions;

public class PropertyValueException extends RuntimeException
{
    private static final long serialVersionUID = 1712906301485959756L;

    public PropertyValueException()
    {
        super();
    }

    public PropertyValueException(String message)
    {
        super(message);
    }

    public PropertyValueException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public PropertyValueException(Throwable cause)
    {
        super(cause);
    }
}
