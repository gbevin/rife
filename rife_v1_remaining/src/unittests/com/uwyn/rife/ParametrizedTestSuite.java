/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParametrizedTestSuite.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife;

import com.uwyn.rife.tools.ExceptionUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Vector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ParametrizedTestSuite extends TestSuite
{
	public ParametrizedTestSuite(String name)
	{
		super(name);
	}

	public ParametrizedTestSuite(final Class theClass, Class[] argsTypes, ArrayList<Object[]> argsList)
	{
		super(theClass.getName());

		initParametrization(theClass, argsTypes, argsList);
	}
		
	protected void initParametrization(final Class theClass, Class[] argsTypes, ArrayList<Object[]> argsList)
	{
		Constructor constructor = null;
		try
		{
			constructor = getParametrizedConstructor(theClass, argsTypes);
		}
		catch (NoSuchMethodException e)
		{
			addTest(parametrizationWarning("Class " + theClass.getName() + " has no public constructor that fits."));
			return;
		}

		if (!Modifier.isPublic(theClass.getModifiers()))
		{
			addTest(parametrizationWarning("Class " + theClass.getName() + " is not public."));
			return;
		}

		Class superClass = theClass;
		Vector<String> names = new Vector<String>();
		while (Test.class.isAssignableFrom(superClass))
		{
			Method[] methods = superClass.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++)
			{
				addParametrizedTestMethod(methods[i], names, constructor, argsList);
			}
			superClass = superClass.getSuperclass();
		}
		if (countTestCases() == 0)
		{
			addTest(parametrizationWarning("No tests found in " + theClass.getName()));
		}
	}

	protected Constructor getParametrizedConstructor(Class theClass, Class[] argsTypes)
	throws NoSuchMethodException
	{
		return theClass.getConstructor(argsTypes);
	}

	protected Test parametrizationWarning(final String message)
	{
		return new TestCase("warning")
		{
			protected void runTest()
			{
				fail(message);
			}
		};
	}

	protected void addParametrizedTestMethod(Method m, Vector<String> names, Constructor constructor, ArrayList<Object[]> argsList)
	{
		String name = m.getName();
		if (names.contains(name))
		{
			return;
		}
		
		if (isPublicTestMethod(m))
		{
			names.addElement(name);
			
			Object test = null;
			
			for (Object[] args : argsList)
			{
				args[args.length-1] = name;
				try
				{
					test = constructor.newInstance(args);
					if (test instanceof Test)
					{
						addTest((Test)test);
					}
					else
					{
						addTest(parametrizationWarning("Cannot instantiate test case: " + name + " (is no instance of Test but of " + test.getClass().getName() + ")"));
					}
				}
				catch (InstantiationException e)
				{
					addTest(parametrizationWarning("Cannot instantiate test case: " + name + " (" + ExceptionUtils.getExceptionStackTrace(e) + ")"));
				}
				catch (InvocationTargetException e)
				{
					addTest(parametrizationWarning("Exception in constructor: " + name + " (" + ExceptionUtils.getExceptionStackTrace(e.getTargetException()) + ")"));
				}
				catch (IllegalAccessException e)
				{
					addTest(parametrizationWarning("Cannot access test case: " + name + " (" + ExceptionUtils.getExceptionStackTrace(e) + ")"));
				}
			}

		}
		else
		{
			// almost a test method
			if (isTestMethod(m))
			{
				addTest(parametrizationWarning("Test method isn't public: " + m.getName()));
			}
		}
	}

	protected boolean isPublicTestMethod(Method m)
	{
		return isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	}

	protected boolean isTestMethod(Method m)
	{
		String name = m.getName();
		Class[] parameters = m.getParameterTypes();
		Class returnType = m.getReturnType();
		return parameters.length == 0 && name.startsWith("test") && returnType.equals(Void.TYPE);
	}
}
