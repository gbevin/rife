/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.resources;

import com.uwyn.rife.resources.exceptions.*;
import com.uwyn.rife.tools.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModificationTimeClasspath
{
    public static long getModificationTime(URL resource)
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

        // handle Jetty's custom tx protocol
        if (resource_protocol.equals("tx"))
        {
            resource_protocol = "file";
            resource_filename = StringUtils.stripFromFront(resource_filename, "file:");
        }

        switch (resource_protocol)
        {
            case "jar":
                String prefix = "file:";
                String jar_filename = resource_filename.substring(prefix.length(), resource_filename.indexOf('!'));
                String jar_entryname = resource_filename.substring(resource_filename.indexOf('!') + 2);
                File jar_regularfile = new File(jar_filename);
                if (jar_regularfile.exists() &&
                    jar_regularfile.canRead())
                {
                    try
                    {
                        JarFile jar_file = new JarFile(jar_regularfile);
                        JarEntry jar_entry = jar_file.getJarEntry(jar_entryname);
                        if (null != jar_entry)
                        {
                            modification_time = jar_entry.getTime();
                        }
                        else
                        {
                            throw new CantFindResourceJarEntryException(jar_filename, jar_entryname, null);
                        }
                    }
                    catch (IOException e)
                    {
                        throw new CantFindResourceJarEntryException(jar_filename, jar_entryname, e);
                    }
                }
                else
                {
                    throw new CouldntAccessResourceJarException(jar_filename, jar_entryname);
                }
                break;
            case "file":
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
                break;
            // support orion's classloader resource url
            case "classloader":
                modification_time = -1;
                break;
            // support weblogic's classloader resource url
            case "zip":
                modification_time = -1;
                break;
            default:
                throw new UnsupportedResourceProtocolException(resource_filename, resource_protocol);
        }

        return modification_time;
    }
}
