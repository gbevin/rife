/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.resources.exceptions.*;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import com.uwyn.rife.tools.exceptions.InnerClassException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * This class offers <code>ResourceFinder</code> capabilities for resources that
 * are available through a collection of directories.
 * <p/>
 * The resources are looked up in the same order as the order in which the
 * directories were specified. This means that if a resource is found in the
 * first directory but it is also present in the second, only the first one
 * will match.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see com.uwyn.rife.resources.ResourceFinder
 * @since 1.0
 */
public class ResourceFinderDirectories extends AbstractResourceFinder
{
    private ArrayList<File> directories = null;

    /**
     * Creates a new instance for the provided array of directories.
     *
     * @param directories the directories where the resources should be
     *                    searched in.
     * @since 1.0
     */
    public ResourceFinderDirectories(File[] directories)
    {
        this.directories = new ArrayList<>();

        if (directories != null)
        {
            for (File directory : directories)
            {
                if (directory != null &&
                    directory.canRead() &&
                    directory.isDirectory())
                {
                    this.directories.add(directory);
                }
            }
        }
    }

    public URL getResource(String name)
    {
        File resource;
        for (File directory : directories)
        {
            String local_name = name.replace('/', File.separatorChar);
            resource = new File(directory.getAbsolutePath() + File.separator + local_name);
            if (resource.exists() &&
                resource.canRead() &&
                resource.isFile())
            {
                try
                {
                    return resource.toURI().toURL();
                }
                catch (IOException ignore)
                {
                }
            }
        }

        return null;
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
        if (null == resource)
        {
            return -1;
        }

        long modification_time;

        String resource_protocol = resource.getProtocol();
        String resource_filename = null;
        try
        {
            resource_filename = URLDecoder.decode(resource.getFile(), StringUtils.ENCODING_UTF_8);
        }
        catch (UnsupportedEncodingException ignored)
        {
            // UTF-8 is always supported
        }
        if (resource_protocol.equals("file"))
        {
            File resource_file = new File(resource_filename);
            if (resource_file.exists() &&
                resource_file.canRead())
            {
                modification_time = resource_file.lastModified();
            }
            else
            {
                throw new CouldntAccessResourceFileException(resource_filename);
            }
        }
        else
        {
            throw new UnsupportedResourceProtocolException(resource_filename, resource_protocol);
        }

        return modification_time;
    }
}
