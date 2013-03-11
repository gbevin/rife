/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools.exceptions;

public class CompilationFailedException extends Exception
{
    private static final long serialVersionUID = -8668620272055200815L;
    private String sourceFilename = null;
    private String errors = null;

    public CompilationFailedException(String sourceFilename, String errors, Throwable cause)
    {
        super(errors, cause);

        this.sourceFilename = sourceFilename;
        this.errors = errors;
    }

    public String getSourceFilename()
    {
        return sourceFilename;
    }

    public String getErrors()
    {
        return errors;
    }
}
