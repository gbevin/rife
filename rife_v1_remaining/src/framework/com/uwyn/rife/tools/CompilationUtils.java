/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CompilationUtils.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.tools.exceptions.CompilationFailedException;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class CompilationUtils
{
	public static File compile(String filenameJava, File fileClass, String generationPath, String classpath)
	throws CompilationFailedException
	{
		try
		{
			// compile the java source file to a class file
			if ((!RifeConfig.Global.isJavaCompilerPathSet() || RifeConfig.Global.getJavaCompilerInternal()) &&
				RifeConfig.Global.isInternalJavaCompilerAvailable())
			{
				String[]				args = new String[] {"-g", "-classpath", classpath, "-d", generationPath, filenameJava};
				Method					compile_method = null;
				ByteArrayOutputStream	errors_outputstream = new ByteArrayOutputStream();
				PrintWriter				errors_printwriter = new PrintWriter(errors_outputstream);
				
				// try to compile with printwriter support for error handling
				// otherwise use the regular compile method
				// this is needed for ibm jdk support
				try
				{
					compile_method = com.sun.tools.javac.Main.class.getDeclaredMethod("compile", new Class[] {String[].class, PrintWriter.class});
				}
				catch (Throwable e)
				{
					compile_method = null;
				}
				
				// use the compile method with error reporting if it exists
				if (compile_method != null)
				{
					try
					{
						Object status = compile_method.invoke(null, new Object[] {args, errors_printwriter});
						if (Integer.parseInt(String.valueOf(status)) != 0)
						{
							throw new CompilationFailedException(filenameJava, errors_outputstream.toString(), null);
						}
					}
					catch (IllegalAccessException e)
					{
						compile_method = null;
					}
					catch (InvocationTargetException e)
					{
						compile_method = null;
					}
					catch (IllegalArgumentException e)
					{
						compile_method = null;
					}
				}
				
				// use the regular compile method
				if (null == compile_method)
				{
					int status = com.sun.tools.javac.Main.compile(args);
					if (status != 0)
					{
						throw new CompilationFailedException(filenameJava, "<unknown error>", null);
					}
				}
			}
			else
			{
				// setup the command
				List<String> command_elements = new ArrayList<String>();
				command_elements.add(RifeConfig.Global.getJavaCompilerPath());
				if (RifeConfig.Global.areJavaCompilerArgsSet())
				{
					command_elements.addAll(RifeConfig.Global.getJavaCompilerArgs());
				}
				command_elements.add("-g");
				command_elements.add("-d");
				command_elements.add(generationPath);
				command_elements.add(filenameJava);
				
				String[] commands = new String[command_elements.size()];
				command_elements.toArray(commands);
				
				// execute the command
				Process javac = ExecUtils.exec(commands, new String[] {"CLASSPATH="+classpath});
				
				if (javac.exitValue() != 0 ||
					!fileClass.exists())
				{
					String error_message = null;
					try
					{
						error_message = FileUtils.readString(javac.getErrorStream());
					}
					catch (FileUtilsErrorException e)
					{
						error_message = "<unknown error>";
					}
					
					if (fileClass.exists())
					{
						fileClass.delete();
					}
					throw new CompilationFailedException(filenameJava, error_message, null);
				}
			}
		}
		catch (IOException e)
		{
			if (fileClass.exists())
			{
				fileClass.delete();
			}
			throw new CompilationFailedException(filenameJava, "An error occurred while launching the Java compiler.\n\nThere can be several reasons for this:\n- the JDK is maybe not installed; or\n- the compiler can't be found through your operating system's PATH environment variable; or\n- your operating system can't find the compiler path '"+RifeConfig.Global.getJavaCompilerPath()+"', try setting it up through the JAVA_COMPILER_PATH configuration parameter; or\n- the tools.jar file from the JDK isn't present in the classpath of your execution environment (servlet container).", e);
		}
		catch (InterruptedException e)
		{
			if (fileClass.exists())
			{
				fileClass.delete();
			}
			throw new CompilationFailedException(filenameJava, null, e);
		}
		
		return fileClass;
	}
}


