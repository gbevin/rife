/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RunWithEngineClassLoader.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import com.uwyn.rife.engine.EngineClassLoader;
import java.lang.reflect.Method;

/**
 * Ensures that all the classes in an application are loaded by {@link
 * EngineClassLoader}, this is needed when continuations are used in elements.
 * <p>This usage is very simple, you simply prefix your current commandline
 * with this class. For example:
 * <pre>java com.uwyn.rife.test.RunWithEngineClassloader my.pakkage.MyMainClass arg1 arg2</pre>
 * <p>This class will correctly execute your application with the provided
 * arguments.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public class RunWithEngineClassLoader
{
	public static void main(String[] args)
	{
		ClassLoader classloader = new EngineClassLoader(RunWithEngineClassLoader.class.getClassLoader());
		Thread.currentThread().setContextClassLoader(classloader);

		try
		{
			if (args != null &&
				args.length > 0)
			{
				Class main_class = classloader.loadClass(args[0]);
				Method main_method = main_class.getMethod("main", new Class[] {String[].class});

				String[] new_args = new String[args.length-1];
				System.arraycopy(args, 1, new_args, 0, args.length-1);
				
				main_method.invoke(null, new Object[] {new_args});
			}
		}
		catch (Throwable e)
		{
			throw new RuntimeException(e);
		}
	}
}
