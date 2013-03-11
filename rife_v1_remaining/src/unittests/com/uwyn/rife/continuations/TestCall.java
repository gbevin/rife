/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCall.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

import com.uwyn.rife.tools.ExceptionUtils;
import junit.framework.TestCase;

import java.lang.reflect.Method;

public class TestCall extends TestCase
{
	public TestCall(String name)
	{
		super(name);
	}
	
	public void testSimpleCall()
	throws Throwable
	{
		final String[] testclasses = new String[] {"TestCallSimpleCallSource", "TestCallSimpleCallInterfaceSource"};
		for (int i = 0;  i < testclasses.length; i++)
		{
			final String testclass = testclasses[i];

			ContinuableRunnerTest runner = new ContinuableRunnerTest();

			String id1 = runner.start(TestCall.class.getPackage().getName()+"."+testclass);
			assertNull(id1);
			ContinuableObject continuable = runner.getCurrentContinuable();
			assertNotNull(continuable);
			Method method_getresult = continuable.getClass().getMethod("getResult", new Class[0]);
			assertEquals("before call\nduring call target 1\nduring call target 2\nafter call", method_getresult.invoke(continuable, new Object[0]));
		}
	}
	
	public void testAnswerInOtherThread()
	throws Throwable
	{
		final String[] testclasses = new String[] {"TestCallAnswerInOtherThreadCallSource", "TestCallAnswerInOtherThreadCallInterfaceSource"};
		for (int i = 0;  i < testclasses.length; i++)
		{
			final String testclass = testclasses[i];

			final ContinuableRunnerTest runner = new ContinuableRunnerTest();
			final ContinuableObject[] continuables = new ContinuableObject[2];
			final String[] ids = new String[2];

			Thread thread1 = new Thread() {
				public void run()
				{
					try
					{
						String id = runner.start(TestCall.class.getPackage().getName()+"."+testclass);
						assertNull(id);
						ids[0] = ContinuationContext.getLastContext().getId();
						continuables[0] = ContinuationContext.getLastContext().getContinuable();
						assertNotNull(continuables[0]);
						Method method_setdoanswer = continuables[0].getClass().getMethod("setDoAnswer", new Class[] {boolean.class});
						method_setdoanswer.invoke(continuables[0], new Object[] {Boolean.TRUE});
					}
					catch (Throwable e)
					{
						fail(ExceptionUtils.getExceptionStackTrace(e));
					}
					finally
					{
						synchronized (this)
						{
							this.notifyAll();
						}
					}
				}
			};

			Thread thread2 = new Thread() {
				public void run()
				{
					try
					{
						String id = runner.run(ids[0]);
						assertNull(id);
						continuables[1] = runner.getCurrentContinuable();
						assertNotNull(continuables[1]);
						Method method_getresult = continuables[1].getClass().getMethod("getResult", new Class[0]);
						assertEquals("before call\ntrue\nafter call", method_getresult.invoke(continuables[1], new Object[0]));
					}
					catch (Throwable e)
					{
						fail(ExceptionUtils.getExceptionStackTrace(e));
					}
					finally
					{
						synchronized (this)
						{
							this.notifyAll();
						}
					}
				}
			};

			synchronized (thread1)
			{
				thread1.start();
				while (thread1.isAlive())
				{
					thread1.wait();
				}
			}

			synchronized (thread2)
			{
				thread2.start();
				while (thread2.isAlive())
				{
					thread2.wait();
				}
			}
		}
	}

	public void testMultipleAnswersWithTryCatch()
	throws Throwable
	{
		final String[] testclasses = new String[] {"TestCallMultipleAnswersWithTryCatchSource", "TestCallSimpleCallInterfaceSource"};
		for (int i = 0;  i < testclasses.length; i++)
		{
			final String testclass = testclasses[i];

			ContinuableRunnerTest runner = new ContinuableRunnerTest();

			String id1 = runner.start(TestCall.class.getPackage().getName()+"."+testclass);
			assertNull(id1);
			ContinuableObject continuable = runner.getCurrentContinuable();
			assertNotNull(continuable);
			Method method_getresult = continuable.getClass().getMethod("getResult", new Class[0]);
			assertEquals("before call\nduring call target 1\nduring call target 2\nafter call", method_getresult.invoke(continuable, new Object[0]));
		}
	}
}
