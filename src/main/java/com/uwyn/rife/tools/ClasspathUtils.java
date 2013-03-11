/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ClasspathUtils.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.config.RifeConfig;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public abstract class ClasspathUtils
{
	public static ArrayList<String> getResourcesInDirectory(String directory)
	{
		return getResourcesInDirectory(directory, true, null);
	}

	public static ArrayList<String> getResourcesInDirectory(String directory, FileFilter filter)
	{
		return getResourcesInDirectory(directory, true, filter);
	}

	public static ArrayList<String> getResourcesInDirectory(String directory, boolean excludeJavaHomePaths)
	{
		return getResourcesInDirectory(directory, excludeJavaHomePaths, null);
	}

	public static ArrayList<String> getResourcesInDirectory(String directory, boolean excludeJavaHomePaths, FileFilter filter)
	{
		ArrayList<String>	resources = new ArrayList<String>();
		ArrayList<URL>		classpath_components = ClasspathComponentsSingleton.INSTANCE.getClasspathComponents();
		String				classpath_component_filename = null;
		File				classpath_component_file = null;
		String				java_home = System.getProperty("java.home");

		for (URL classpath_component : classpath_components)
		{
			try
			{
				classpath_component_filename = URLDecoder.decode(classpath_component.getFile(), "ISO-8859-1");
			}
			catch (UnsupportedEncodingException e)
			{
				// should never fail, it's a standard encoding
			}

			if (null != classpath_component_filename  &&
                (excludeJavaHomePaths && !classpath_component_filename.startsWith(java_home)))
			{
				classpath_component_file = new File(classpath_component_filename);

				Pattern[] included = new Pattern[1];
				if (directory != null)
				{
					if (File.separatorChar != '/')
					{
						directory = directory.replace('/', File.separatorChar);
					}
					
					included[0] = Pattern.compile("^"+StringUtils.encodeRegexp(directory)+".*");
				}
				
				Pattern[] excluded = new Pattern[1];
				if (directory != null)
				{
					excluded[0] = Pattern.compile(".*\\.svn.*");
				}
				
				if ((null == filter || filter.accept(classpath_component_file)) &&
					classpath_component_file.canRead())
				{
					ArrayList<String> filelist = null;
					if (classpath_component_file.isFile() &&
                        classpath_component_filename.endsWith(".jar"))
					{
						try
						{
							JarFile					jar_file = new JarFile(classpath_component_file);
							Enumeration<JarEntry>	jar_entries = jar_file.entries();
							JarEntry				jar_entry = null;
							String					entry_name = null;
							filelist = new ArrayList<String>(jar_file.size());
							while (jar_entries.hasMoreElements())
							{
								jar_entry = jar_entries.nextElement();
								if (!jar_entry.isDirectory())
								{
									entry_name = jar_entry.getName();
									if (StringUtils.filter(entry_name, included, excluded))
									{
										filelist.add(entry_name);
									}
								}
							}
						}
						catch (IOException e)
						{
							// don't propagate this since this classpath entry will just be ignored when an error occurs
						}
					}
					else if (classpath_component_file.isDirectory())
					{
						filelist = FileUtils.getFileList(classpath_component_file, included, excluded);
					}

					if (null != filelist)
					{
						for (String filelist_entry : filelist)
						{
							filelist_entry = filelist_entry.replace(File.separatorChar, '/');
							if (!resources.contains(filelist_entry))
							{
								resources.add(filelist_entry);
							}
						}
					}
				}
			}
		}

		return resources;
	}
	
	private static ArrayList<String> getClassPathCandidates(ClassLoader classloader)
	{
		// try to obtain the base resin classloader class
		Class resin_classloader = null;
		try
		{
			resin_classloader = classloader.loadClass("com.caucho.util.DynamicClassLoader");
		}
		catch (ClassNotFoundException e)
		{
			resin_classloader = null;
		}

		ArrayList<String> 	classpath_candidates = new ArrayList<String>();
		URL[]				classloader_urls = null;
		do
		{
			// handle regular URLClassLoader classes
			if (classloader instanceof URLClassLoader)
			{
				classloader_urls = ((URLClassLoader)classloader).getURLs();
				for (URL classloader_url : classloader_urls)
				{
					if (classloader_url.getProtocol().equals("file"))
					{
						try
						{
							classpath_candidates.add(URLDecoder.decode(classloader_url.getPath(), "ISO-8859-1"));
						}
						catch (UnsupportedEncodingException e)
						{
							// should never fail, it's a standard encoding
						}
					}
				}
			}
			// handle resin classloaders
			else if (resin_classloader != null &&
					 resin_classloader.isAssignableFrom(classloader.getClass()))
			{
				try
				{
					Method method = resin_classloader.getDeclaredMethod("getClassPath", (Class[])null);
					String resin_classpath = (String)method.invoke(classloader, (Object[])null);
					classpath_candidates.addAll(StringUtils.split(resin_classpath, File.pathSeparator));
				}
				catch (SecurityException e)
				{
					// do nothing
				}
				catch (NoSuchMethodException e)
				{
					// do nothing
				}
				catch (IllegalArgumentException e)
				{
					// do nothing
				}
				catch (InvocationTargetException e)
				{
					// do nothing
				}
				catch (IllegalAccessException e)
				{
					// do nothing
				}
			}
			classloader = classloader.getParent();
		}
		while (classloader != null);
		
		return classpath_candidates;
	}
	
	public static String getClassPath(Class reference)
	{
		// construct the classpath string from the list
		return StringUtils.join(getClassPathAsList(reference), File.pathSeparator);
	}
	
	public static List<String> getClassPathAsList(Class reference)
	{
		ArrayList<String> 	classpath = new ArrayList<String>();

		// try to obtain the classpath element through the hierachy
		// of classloaders
		ArrayList<String> 	classpath_candidates = new ArrayList<String>();
		classpath_candidates.addAll(getClassPathCandidates(Thread.currentThread().getContextClassLoader()));
		classpath_candidates.addAll(getClassPathCandidates(reference.getClassLoader()));

		// try to detect the location of the RIFE classes
		String	current_class_file = ClasspathUtils.class.getName().replace('.','/')+".class";
		URL		rife_classpath_resource = reference.getClassLoader().getResource(current_class_file);
		if (rife_classpath_resource != null)
		{
			String	rife_classpath_entry = rife_classpath_resource.toExternalForm();
			if (rife_classpath_entry.startsWith("jar:file:"))
			{
				rife_classpath_entry = rife_classpath_entry.substring("jar:file:".length());
				rife_classpath_entry = rife_classpath_entry.substring(0, rife_classpath_entry.indexOf("!"));
			}
			else if (rife_classpath_entry.startsWith("file:"))
			{
				rife_classpath_entry = rife_classpath_entry.substring("file:".length());
				rife_classpath_entry = rife_classpath_entry.substring(0, rife_classpath_entry.indexOf(current_class_file));
			}
			else
			{
				rife_classpath_entry = null;
			}
			
			if (rife_classpath_entry != null)
			{
				classpath_candidates.add(rife_classpath_entry);
			}
		}
		
		// include the system-wide classpath properties for completeness
		classpath_candidates.addAll(StringUtils.split(RifeConfig.Global.getApplicationClassPath(), File.pathSeparator));
		classpath_candidates.addAll(StringUtils.split(System.getProperty("sun.boot.class.path"), File.pathSeparator));
		classpath_candidates.addAll(StringUtils.split(System.getProperty("java.class.path"), File.pathSeparator));

		// iterate over the classpath candidate entries, validate them
		// and add them to the classpath if they're no already present
		File	path_file = null;
		for (String path : classpath_candidates)
		{
			if (!classpath.contains(path))
			{
				path_file = new File(path);
				if (path_file.exists() &&
					path_file.canRead() &&
					(path_file.isDirectory() ||
					 (path_file.isFile() && path.endsWith(".jar"))))
				{
					classpath.add(path);
				}
			}
		}
		
		return classpath;
	}
}


