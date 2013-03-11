/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools.exceptions;

public class InnerClassException extends RuntimeException
{
    private static final long serialVersionUID = -2692374153192760509L;

    public InnerClassException(Exception cause)
    {
        super(cause);
    }

    public Exception getCause()
    {
        return (Exception)super.getCause();
    }
}
