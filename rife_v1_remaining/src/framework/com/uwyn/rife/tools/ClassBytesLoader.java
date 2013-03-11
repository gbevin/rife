/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ClassBytesLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import sun.misc.Resource;
import sun.misc.URLClassPath;

/**
 * Utility class to load the bytes of class files.
 * <p>
 * After instantiating it, the {@link #setupSunByteLoading} method can
 * optionally be called to let the class detect if it's possible to
 * interface with a private API that's specific to the Sun JVM. This interface
 * can improve class byte loading performance by 40%.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ClassBytesLoader
{
	private ClassLoader	mClassLoader = null;
	private Object		mSunByteLoading = null;
	private Method		mSunByteLoadingMethod = null;

	/**
	 * Instantiates a new bytes loader for class files.
	 *
	 * @param classLoader the classloader that should be used to search for the
	 * classes
	 * @since 1.6
	 */
	public ClassBytesLoader(ClassLoader classLoader)
	{
		mClassLoader = classLoader;
	}

	/**
	 * Tries to setup an interaction with a private Sun JVM interface that can
	 * speed up class bytes loading by 40%, when available.
	 *
	 * @return {@code true} when the interaction with the private Sun JVM
	 * interface could be setup; or<p>
	 * {@code false} otherwise
	 */
	public boolean setupSunByteLoading()
	{
		// this is an ugly hack to be able to use Sun's implementation of URLClassLoader for
		// speed increases if it's available, otherwise we'll fallback to a regular method of
		// reading the class bytes
		try
		{
			Field ucp_field = URLClassLoader.class.getDeclaredField("ucp");
			ucp_field.setAccessible(true);
			Object ucp = ucp_field.get(mClassLoader);
			if (ucp.getClass().getName().equals("sun.misc.URLClassPath"))
			{
				Class urlclasspath_class = mClassLoader.loadClass("sun.misc.URLClassPath");
				Class byteloading_class = mClassLoader.loadClass(getClass().getName()+"$SunByteLoading");
				Constructor byteloading_constructor = byteloading_class.getDeclaredConstructor(urlclasspath_class);
				byteloading_constructor.setAccessible(true);
				Object byteloading = byteloading_constructor.newInstance(ucp);
				Method byteloading_method = byteloading_class.getDeclaredMethod("getBytes", String.class);
				byteloading_method.setAccessible(true);
				
				mSunByteLoading = byteloading;
				mSunByteLoadingMethod = byteloading_method;
			}
		}
		catch (Throwable e)
		{
			mSunByteLoading = null;
			mSunByteLoadingMethod = null;
		}

		return isUsingSunByteLoading();
	}
	
	/**
	 * Retrieves a byte array that contains the bytecode for a specific Java
	 * class.
	 *
	 * @param classFileName the file name of the class whose bytes should be
	 * loaded, note that this is not the Java FQN ({@code com.uwyn.rife.Version}),
	 * but the real name of the file resource ({@code com/uwyn/rife/Version.java})
	 * @return an array with the bytes of the class; or<p>
	 * {@code null} if no bytes could be loaded
	 * @throws FileUtilsErrorException if an error occurred during the loading
	 * of the class bytes
	 * @see #getClassBytes(String, URL)
	 * @since 1.6
	 */
	public byte[] getClassBytes(String classFileName) throws ClassNotFoundException
	{
		return getClassBytes(classFileName, null);
	}
	

	/**
	 * Retrieves a byte array that contains the bytecode for a specific Java
	 * class.
	 *
	 * @param classFileName the file name of the class whose bytes should be
	 * loaded, note that this is not the Java FQN ({@code com.uwyn.rife.Version}),
	 * but the real name of the file resource ({@code com/uwyn/rife/Version.java})
	 * @param classResource the resource that can be used to load the class
	 * bytes from if it couldn't be obtained by using the file name, if no
	 * resource is provided and the bytes couldn't be loaded by simply using
	 * the class' file name, a resource will be looked up for the file name
	 * through the class loader that was provided to the constructor
	 * @return an array with the bytes of the class; or<p>
	 * {@code null} if no bytes could be loaded
	 * @throws FileUtilsErrorException if an error occurred during the loading
	 * of the class bytes
	 * @see #getClassBytes(String)
	 * @since 1.6
	 */
	public byte[] getClassBytes(String classFileName, URL classResource) throws ClassNotFoundException
	{
		byte[] raw_bytes = null;
		
		// this is a hack to be able to use Sun's implementation of URLClassLoader for
		// speed increases if it's available, otherwise we'll fallback to a regular method of
		// reading the class bytes
		if (classFileName != null &&
			isUsingSunByteLoading())
		{
			try
			{
				raw_bytes = (byte[])mSunByteLoadingMethod.invoke(mSunByteLoading, classFileName);
			}
			catch (IllegalAccessException e)
			{
				raw_bytes = null;
			}
			catch (IllegalArgumentException e)
			{
				raw_bytes = null;
			}
			catch (InvocationTargetException e)
			{
				raw_bytes = null;
			}
		}

		// get the class bytes through a regular method that works on any JVM
		if (null == raw_bytes)
		{
			if (null == classResource &&
				classFileName != null)
			{
				classResource = mClassLoader.getResource(classFileName);
			}
			
			if (classResource != null)
			{
				try
				{
					raw_bytes = FileUtils.readBytes(classResource);
				}
				catch (FileUtilsErrorException e)
				{
					throw new ClassNotFoundException("Unexpected error while reading the bytes of the class resource '"+classResource+"'.", e);
				}
			}
		}
		
		return raw_bytes;
	}

	/**
	 * Indicates whether this class is using the private Sun JVM interface to
	 * speed up class bytes loading.
	 * 
	 * @return {@code true} when this class is using the private Sun JVM
	 * interface; or<p>
	 * {@code false} if this is not the case
	 * @since 1.6
	 */
	public boolean isUsingSunByteLoading()
	{
		return mSunByteLoading != null;
	}

	@SuppressWarnings("unused")
	private static class SunByteLoading
	{
		URLClassPath mUrlClassPath = null;
		
		private SunByteLoading(URLClassPath ucp)
		{
			mUrlClassPath = ucp;
		}
		
		private byte[] getBytes(String path)
		{
			Resource resource = mUrlClassPath.getResource(path, false);
			if (resource != null)
			{
				try
				{
					return resource.getBytes();
				}
				catch (IOException e)
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
	}
}


