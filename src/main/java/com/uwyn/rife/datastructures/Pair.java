/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.datastructures;

public class Pair<FirstType, SecondType> implements Cloneable
{
    private FirstType first = null;
    private SecondType second = null;

    public Pair()
    {
    }

    public Pair(FirstType first, SecondType second)
    {
        setFirst(first);
        setSecond(second);
    }

    public FirstType getFirst()
    {
        return first;
    }

    public void setFirst(FirstType first)
    {
        this.first = first;
    }

    public SecondType getSecond()
    {
        return second;
    }

    public void setSecond(SecondType second)
    {
        this.second = second;
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

        if (!(other instanceof Pair))
        {
            return false;
        }

        Pair other_pair = (Pair)other;
        if (getFirst() != null || other_pair.getFirst() != null)
        {
            if (null == getFirst() || null == other_pair.getFirst())
            {
                return false;
            }
            if (!other_pair.getFirst().equals(getFirst()))
            {
                return false;
            }
        }
        if (getSecond() != null || other_pair.getSecond() != null)
        {
            if (null == getSecond() || null == other_pair.getSecond())
            {
                return false;
            }
            if (!other_pair.getSecond().equals(getSecond()))
            {
                return false;
            }
        }

        return true;
    }

    public Pair clone()
    throws CloneNotSupportedException
    {
        return (Pair)super.clone();
    }

    public int hashCode()
    {
        return first.hashCode() * second.hashCode();
    }
}
