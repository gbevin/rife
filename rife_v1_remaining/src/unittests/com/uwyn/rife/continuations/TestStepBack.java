/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestStepBack.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

import junit.framework.TestCase;

import java.lang.reflect.Method;

public class TestStepBack extends TestCase
{
	public TestStepBack(String name)
	{
		super(name);
	}
	
	public void testStepBackInWhile()
	throws Throwable
	{
		final String[] testclasses = new String[] {"TestStepBackCounter", "TestStepBackCounterInterface"};
		for (int i = 0;  i < testclasses.length; i++)
		{
			final String testclass = testclasses[i];

			ContinuableRunnerTest runner = new ContinuableRunnerTest();

			String id1 = runner.start(TestStepBack.class.getPackage().getName() + "." + testclass);
			assertNotNull(id1);
			ContinuationContext context1 = runner.getManager().getContext(id1);

			ContinuableObject continuable1 = context1.getContinuable();
			Method start_method = continuable1.getClass().getMethod("setStart", new Class[] {boolean.class});
			Method total_method = continuable1.getClass().getMethod("getTotal", new Class[0]);
			Method answer_method = continuable1.getClass().getMethod("setAnswer", new Class[] {int.class});

			start_method.invoke(continuable1, new Object[] {Boolean.TRUE});
			String id2 = runner.resume(id1);
			assertNotNull(id2);
			assertFalse(id1.equals(id2));
			ContinuationContext context2 = runner.getManager().getContext(id2);
			ContinuableObject continuable2 = context2.getContinuable();
			int total2 = ((Integer)total_method.invoke(continuable2, new Object[0])).intValue();
			assertEquals(0, total2);

			answer_method.invoke(continuable2, new Object[] {new Integer(12)});
			String id3 = runner.resume(id2);
			assertNotNull(id3);
			assertFalse(id1.equals(id3));
			assertFalse(id2.equals(id3));
			ContinuationContext context3 = runner.getManager().getContext(id3);
			ContinuableObject continuable3 = context3.getContinuable();
			int total3 = ((Integer)total_method.invoke(continuable3, new Object[0])).intValue();
			assertEquals(12, total3);

			answer_method.invoke(continuable3, new Object[] {new Integer(32)});
			String id4 = runner.resume(id3);
			assertNotNull(id4);
			assertFalse(id1.equals(id4));
			assertFalse(id2.equals(id4));
			assertFalse(id3.equals(id4));
			ContinuationContext context4 = runner.getManager().getContext(id4);
			ContinuableObject continuable4 = context4.getContinuable();
			int total4 = ((Integer)total_method.invoke(continuable4, new Object[0])).intValue();
			assertEquals(44, total4);

			answer_method.invoke(continuable4, new Object[] {new Integer(41)});
			String id5 = runner.resume(id4);
			assertNull(id5);
		}
	}
}
