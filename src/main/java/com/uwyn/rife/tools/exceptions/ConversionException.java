/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools.exceptions;

public class ConversionException extends Exception
{
    private static final long serialVersionUID = 8951249584169075072L;
    private Object from;
    private Class to;

    public ConversionException(Object from, Class to, Throwable cause)
    {
        super("Impossible to convert " + from + " from " + (null == from ? "unknown" : from.getClass().getName()) + " to " + to.getName(), cause);
        this.from = from;
        this.to = to;
    }

    public Object getFrom()
    {
        return from;
    }

    public Class getTo()
    {
        return to;
    }
}
