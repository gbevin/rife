/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.datastructures;

import com.uwyn.rife.tools.ExceptionUtils;

import java.util.logging.Logger;

public class KeyValue implements Cloneable
{
    private String key = null;
    private String value = null;

    public KeyValue(String key, String value)
    {
        setKey(key);
        setValue(value);
    }

    public String getKey()
    {
        return (key);
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String toString()
    {
        return value;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }

        if (null == other)
        {
            return false;
        }

        if (!(other instanceof KeyValue))
        {
            return false;
        }

        KeyValue other_keyvalue = (KeyValue)other;
        if (getKey() != null || other_keyvalue.getKey() != null)
        {
            if (null == getKey() || null == other_keyvalue.getKey())
            {
                return false;
            }
            if (!other_keyvalue.getKey().equals(getKey()))
            {
                return false;
            }
        }
        if (getValue() != null || other_keyvalue.getValue() != null)
        {
            if (null == getValue() || null == other_keyvalue.getValue())
            {
                return false;
            }
            if (!other_keyvalue.getValue().equals(getValue()))
            {
                return false;
            }
        }

        return true;
    }

    public KeyValue clone()
    {
        KeyValue new_keyvalue = null;
        try
        {
            new_keyvalue = (KeyValue)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            // do nothing, this should never happen
            Logger.getLogger("com.uwyn.rife.datastructures").severe(ExceptionUtils.getExceptionStackTrace(e));
        }

        return new_keyvalue;
    }

    public int hashCode()
    {
        return key.hashCode() * value.hashCode();
    }
}

