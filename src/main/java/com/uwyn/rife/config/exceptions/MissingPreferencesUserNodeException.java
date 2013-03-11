/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.config.exceptions;

public class MissingPreferencesUserNodeException extends ConfigErrorException
{
    private static final long serialVersionUID = -22983055927535074L;

    public MissingPreferencesUserNodeException()
    {
        super("No preferences user node has been specified, therefore it's impossible to store the configuration through Java's preferences mechanism.");
    }
}
