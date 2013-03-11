/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc;

/**
 * Holds a single static object property value that doesn't change at runtime.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @since 1.0
 */

public class PropertyValueObject implements PropertyValue
{
    private Object value = null;

    /**
     * The constructor that stores the static object instance.
     *
     * @param value the static object instance
     * @since 1.0
     */
    public PropertyValueObject(Object value)
    {
        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }

    public String getValueString()
    {
        return String.valueOf(value);
    }

    public String toString()
    {
        return getValueString();
    }

    public boolean isNeglectable()
    {
        return null == value || 0 == String.valueOf(value).trim().length();

    }

    public boolean isStatic()
    {
        return true;
    }
}


