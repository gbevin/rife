/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep.exceptions;

public class InitializationErrorException extends RepException
{
    private static final long serialVersionUID = -2561768917098554338L;

    public InitializationErrorException(Throwable cause)
    {
        super("An error occurred during the initialization of the repository.", cause);
    }
}
