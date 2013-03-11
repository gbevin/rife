/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.tools.exceptions.ClasspathUtilsErrorException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class ClasspathComponents
{
    private ArrayList<URL> classpathComponents = null;

    ClasspathComponents()
    {
        init();
    }

    ArrayList<URL> getClasspathComponents()
    {
        return classpathComponents;
    }

    private void init()
    throws ClasspathUtilsErrorException
    {
        String classpath = System.getProperty("java.class.path");
        List<String> paths = StringUtils.split(classpath, File.pathSeparator);

        ArrayList<URL> urls = new ArrayList<>();
        File path_file = null;
        URL path_url;
        try
        {
            for (String path : paths)
            {
                path_file = new File(path);
                path_url = path_file.toURI().toURL();
                urls.add(path_url);
            }
        }
        catch (MalformedURLException e)
        {
            throw new ClasspathUtilsErrorException("Unable to parse the class path '" + path_file + "'.", e);
        }

        classpathComponents = urls;
    }
}


