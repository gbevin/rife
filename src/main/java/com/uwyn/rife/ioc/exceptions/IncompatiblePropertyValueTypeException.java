/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc.exceptions;

public class IncompatiblePropertyValueTypeException extends PropertyValueException
{
    private static final long serialVersionUID = 6336950082309925343L;
    private String propertyName = null;
    private Class expectedType = null;
    private Class actualType = null;

    public IncompatiblePropertyValueTypeException(String propertyName, Class expectedType, Class actualType, Throwable e)
    {
        super("The property '" + propertyName + "' was expected to have the type '" + expectedType.getName() + "', however it's actual type '" + actualType.getName() + "' couldn't be cast to it.", e);

        this.propertyName = propertyName;
        this.expectedType = expectedType;
        this.actualType = actualType;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public Class getExpectedType()
    {
        return expectedType;
    }

    public Class getActualType()
    {
        return actualType;
    }
}
