/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife;

import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;

public class Version
{
    private String version = null;

    Version()
    {
        ResourceFinderClasspath resource_finder = ResourceFinderClasspath.getInstance();
        try
        {
            version = resource_finder.getContent("RIFE_VERSION");
        }
        catch (ResourceFinderErrorException e)
        {
            version = null;
        }

        if (version != null)
        {
            version = version.trim();
        }
        if (null == version)
        {
            version = "unknown version";
        }
    }

    public static String getVersion()
    {
        return VersionSingleton.INSTANCE.getVersionString();
    }

    private String getVersionString()
    {
        return version;
    }
}

