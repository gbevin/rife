/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.resources.exceptions.CantOpenResourceStreamException;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.InputStreamUser;

import java.net.URL;
import java.util.ArrayList;

/**
 * Allows a group of resource finders to acts as if they are one single
 * resource finders. They will be consecutively used in their order of addition
 * to the group.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see com.uwyn.rife.resources.ResourceFinder
 * @since 1.0
 */
public class ResourceFinderGroup extends AbstractResourceFinder
{
    private ArrayList<ResourceFinder> resourceFinders = new ArrayList<>();

    public ResourceFinderGroup add(ResourceFinder resourceFinder)
    {
        resourceFinders.add(resourceFinder);

        return this;
    }

    public URL getResource(String name)
    {
        URL result;
        for (ResourceFinder resource_finder : resourceFinders)
        {
            result = resource_finder.getResource(name);
            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    public <ResultType> ResultType useStream(URL resource, InputStreamUser user)
    throws ResourceFinderErrorException
    {
        ResultType result;
        for (ResourceFinder resource_finder : resourceFinders)
        {
            try
            {
                result = (ResultType)resource_finder.useStream(resource, user);
            }
            catch (CantOpenResourceStreamException e)
            {
                continue;
            }

            return result;
        }

        throw new CantOpenResourceStreamException(resource, null);
    }

    public String getContent(URL resource, String encoding)
    throws ResourceFinderErrorException
    {
        String result;
        for (ResourceFinder resource_finder : resourceFinders)
        {
            result = resource_finder.getContent(resource, encoding);
            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    public long getModificationTime(URL resource)
    throws ResourceFinderErrorException
    {
        long result;
        for (ResourceFinder resource_finder : resourceFinders)
        {
            result = resource_finder.getModificationTime(resource);
            if (result != -1)
            {
                return result;
            }
        }

        return -1;
    }
}
