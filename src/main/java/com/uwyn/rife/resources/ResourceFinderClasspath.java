/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.resources.exceptions.CantOpenResourceStreamException;
import com.uwyn.rife.resources.exceptions.CantRetrieveResourceContentException;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import com.uwyn.rife.tools.exceptions.InnerClassException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class offers <code>ResourceFinder</code> capabilities for resources that
 * are available through the classloader. This is done for directories as well
 * as for jar files. Basically, this corresponds to the resources that are
 * available through the classpath.
 * <p/>
 * Since the application's classloader isn't supposed to change in a global way,
 * the <code>ResourceFinderClasspath</code> class can only be instantiated
 * through the static <code>getInstance()</code> method that always returns
 * the same instance as a singleton.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see com.uwyn.rife.resources.ResourceFinder
 * @since 1.0
 */
public class ResourceFinderClasspath extends AbstractResourceFinder
{
    protected ResourceFinderClasspath()
    {
    }

    /**
     * Returns the shared singleton instance of the
     * <code>ResourceFinderClasspath</code> class.
     *
     * @return the singleton <code>ResourceFinderClasspath</code> instance
     * @since 1.0
     */
    public static ResourceFinderClasspath getInstance()
    {
        return ResourceFinderClasspathSingleton.INSTANCE;
    }

    public URL getResource(String name)
    {
        URL resource;

        if (this.getClass().getClassLoader() != null)
        {
            // Try the class loader that loaded this class.
            resource = this.getClass().getClassLoader().getResource(name);
        }
        else
        {
            // Try the system class loader.
            resource = ClassLoader.getSystemClassLoader().getResource(name);
        }

        if (null == resource)
        {
            // if not found in classpath fall back to default
            resource = this.getClass().getResource(name);
        }

        return resource;
    }

    public <ResultType> ResultType useStream(URL resource, InputStreamUser user)
    throws ResourceFinderErrorException, InnerClassException
    {
        if (null == resource ||
            null == user)
        {
            return null;
        }

        try
        {
            URLConnection connection = resource.openConnection();
            connection.setUseCaches(false);
            try (InputStream stream = connection.getInputStream())
            {
                return (ResultType)user.useInputStream(stream);
            }
        }
        catch (IOException e)
        {
            throw new CantOpenResourceStreamException(resource, e);
        }
    }

    public String getContent(URL resource, String encoding)
    throws ResourceFinderErrorException
    {
        if (null == resource)
        {
            return null;
        }

        try
        {
            return FileUtils.readString(resource, encoding);
        }
        catch (FileUtilsErrorException e)
        {
            throw new CantRetrieveResourceContentException(resource, encoding, e);
        }
    }

    public long getModificationTime(URL resource)
    throws ResourceFinderErrorException
    {
        return ModificationTimeClasspath.getModificationTime(resource);
    }
}
