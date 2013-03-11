/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;

import java.net.URL;

/**
 * This abstract class offers common implementations of several
 * <code>ResourceFinder</code> methods. This makes it easier to implement
 * specific <code>ResourceFinder</code> classes.
 * <p/>
 * All method implementations here accept resource specification as names and
 * correctly defer the actual logic to the methods that accept resource
 * specification as URLs.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see com.uwyn.rife.resources.ResourceFinder
 * @since 1.0
 */
public abstract class AbstractResourceFinder implements ResourceFinder
{
    public <ResultType> ResultType useStream(String name, InputStreamUser user)
    throws ResourceFinderErrorException, InnerClassException
    {
        if (null == name ||
            null == user)
        {
            return null;
        }

        URL resource = getResource(name);
        if (null == resource)
        {
            return null;
        }

        return (ResultType)useStream(resource, user);
    }

    public String getContent(String name)
    throws ResourceFinderErrorException
    {
        return getContent(name, null);
    }

    public String getContent(String name, String encoding)
    throws ResourceFinderErrorException
    {
        if (null == name)
        {
            return null;
        }

        URL resource = getResource(name);
        if (null == resource)
        {
            return null;
        }

        return getContent(resource, encoding);
    }

    public String getContent(URL resource)
    throws ResourceFinderErrorException
    {
        return getContent(resource, null);
    }

    public long getModificationTime(String name)
    throws ResourceFinderErrorException
    {
        if (null == name)
        {
            return -1;
        }

        URL resource = getResource(name);
        if (null == resource)
        {
            return -1;
        }

        return getModificationTime(resource);
    }
}
