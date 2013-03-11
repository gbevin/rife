/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools.exceptions;

public class ClasspathUtilsErrorException extends RuntimeException
{
    private static final long serialVersionUID = 4718810897697157698L;

    public ClasspathUtilsErrorException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
