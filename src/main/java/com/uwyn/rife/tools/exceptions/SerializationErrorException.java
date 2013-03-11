/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools.exceptions;

import java.io.Serializable;

public class SerializationErrorException extends SerializationUtilsErrorException
{
    private static final long serialVersionUID = -5423479498545742611L;
    private Serializable serializable = null;

    public SerializationErrorException(Serializable serializable, Throwable cause)
    {
        super("Errors occurred during the serialization of '" + serializable + "'.", cause);

        this.serializable = serializable;
    }

    public Serializable getSerializable()
    {
        return serializable;
    }
}
