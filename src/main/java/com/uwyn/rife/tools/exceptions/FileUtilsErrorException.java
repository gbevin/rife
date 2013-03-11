/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools.exceptions;

public class FileUtilsErrorException extends Exception
{
    private static final long serialVersionUID = 5563842867757961501L;

    public FileUtilsErrorException(String message)
    {
        super(message);
    }

    public FileUtilsErrorException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
