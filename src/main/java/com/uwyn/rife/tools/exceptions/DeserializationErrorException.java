/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DeserializationErrorException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools.exceptions;

public class DeserializationErrorException extends SerializationUtilsErrorException
{
    private static final long serialVersionUID = -2436363754313490329L;

    public DeserializationErrorException(Throwable cause)
    {
        super("Errors occurred during deserialization.", cause);
    }
}
