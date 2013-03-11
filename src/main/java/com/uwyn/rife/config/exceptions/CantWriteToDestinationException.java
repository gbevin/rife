/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.config.exceptions;

import java.io.File;

public class CantWriteToDestinationException extends ConfigErrorException
{
    private static final long serialVersionUID = 2484476576384120796L;
    private File destination = null;

    public CantWriteToDestinationException(File destination)
    {
        super("The destination file for the xml data '" + destination.getAbsolutePath() + "' is not writable.");

        this.destination = destination;
    }

    public File getDestination()
    {
        return destination;
    }
}
