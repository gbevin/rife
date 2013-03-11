/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestPause.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

import junit.framework.TestCase;

public class TestPause extends TestCase
{
	public TestPause(String name)
	{
		super(name);
	}
	
	public void testPauseInWhile()
	throws Throwable
	{
		final String[] testclasses = new String[] {"TestPauseInWhile", "TestPauseInWhileInterface"};
		for (int i = 0;  i < testclasses.length; i++)
		{
			final String testclass = testclasses[i];

			ContinuableRunnerTest runner = new ContinuableRunnerTest();

			String id1 = runner.start(TestPause.class.getPackage().getName() + "." + testclass);
			assertNotNull(id1);
			ContinuationContext context1 = runner.getManager().getContext(id1);
			assertEquals(5, context1.getLocalVars().getInt(1));

			String id2 = runner.resume(id1);
			assertNotNull(id2);
			assertFalse(id1.equals(id2));
			ContinuationContext context2 = runner.getManager().getContext(id2);
			assertEquals(4, context2.getLocalVars().getInt(1));

			String id3 = runner.resume(id2);
			assertNotNull(id3);
			assertFalse(id1.equals(id3));
			assertFalse(id2.equals(id3));
			ContinuationContext context3 = runner.getManager().getContext(id3);
			assertEquals(3, context3.getLocalVars().getInt(1));

			String id4 = runner.resume(id3);
			assertNotNull(id4);
			assertFalse(id1.equals(id4));
			assertFalse(id2.equals(id4));
			assertFalse(id3.equals(id4));
			ContinuationContext context4 = runner.getManager().getContext(id4);
			assertEquals(2, context4.getLocalVars().getInt(1));

			String id5 = runner.resume(id4);
			assertNotNull(id5);
			assertFalse(id1.equals(id5));
			assertFalse(id2.equals(id5));
			assertFalse(id3.equals(id5));
			assertFalse(id4.equals(id5));
			ContinuationContext context5 = runner.getManager().getContext(id5);
			assertEquals(1, context5.getLocalVars().getInt(1));

			String id6 = runner.resume(id5);
			assertNull(id6);
		}
	}
	
	public void testPauseInWhileClones()
	throws Throwable
	{
		final String[] testclasses = new String[] {"TestPauseInWhile", "TestPauseInWhileInterface"};
		for (int i = 0;  i < testclasses.length; i++)
		{
			final String testclass = testclasses[i];

			ContinuableRunnerTest runner = new ContinuableRunnerTest();

			String id1 = runner.start(TestPause.class.getPackage().getName() + "." + testclass);
			assertNotNull(id1);
			ContinuationContext context1 = runner.getManager().getContext(id1);
			assertEquals(5, context1.getLocalVars().getInt(1));

			String id2a = runner.resume(id1);
			assertNotNull(id2a);
			ContinuationContext context2 = runner.getManager().getContext(id2a);
			assertEquals(4, context2.getLocalVars().getInt(1));

			String id3aa = runner.resume(id2a);
			assertNotNull(id3aa);
			ContinuationContext context3aa = runner.getManager().getContext(id3aa);
			assertEquals(3, context3aa.getLocalVars().getInt(1));

			String id4aa = runner.resume(id3aa);
			assertNotNull(id4aa);
			ContinuationContext context4aa = runner.getManager().getContext(id4aa);
			assertEquals(2, context4aa.getLocalVars().getInt(1));

			String id2b = runner.resume(id1);
			assertNotNull(id2b);
			assertFalse(id2a.equals(id2b));
			ContinuationContext context2b = runner.getManager().getContext(id2b);
			assertEquals(4, context2.getLocalVars().getInt(1));

			String id3ab = runner.resume(id2a);
			assertNotNull(id3ab);
			assertFalse(id3aa.equals(id3ab));
			ContinuationContext context3ab = runner.getManager().getContext(id3ab);
			assertEquals(3, context3ab.getLocalVars().getInt(1));

			String id5aa = runner.resume(id4aa);
			assertNotNull(id5aa);
			ContinuationContext context5aa = runner.getManager().getContext(id5aa);
			assertEquals(1, context5aa.getLocalVars().getInt(1));

			String id3b = runner.resume(id2b);
			assertNotNull(id3b);
			assertFalse(id3aa.equals(id3b));
			assertFalse(id3ab.equals(id3b));
			ContinuationContext context3b = runner.getManager().getContext(id3b);
			assertEquals(3, context3b.getLocalVars().getInt(1));

			String id4ab = runner.resume(id3ab);
			assertNotNull(id4ab);
			assertFalse(id4aa.equals(id4ab));
			ContinuationContext context4ab = runner.getManager().getContext(id4ab);
			assertEquals(2, context4ab.getLocalVars().getInt(1));

			String id4b = runner.resume(id3b);
			assertNotNull(id4b);
			assertFalse(id4aa.equals(id4b));
			assertFalse(id4ab.equals(id4b));
			ContinuationContext context4b = runner.getManager().getContext(id4b);
			assertEquals(2, context4b.getLocalVars().getInt(1));

			String id6aa = runner.resume(id5aa);
			assertNull(id6aa);

			String id5b = runner.resume(id4b);
			assertNotNull(id5b);
			assertFalse(id5aa.equals(id5b));
			ContinuationContext context5b = runner.getManager().getContext(id5b);
			assertEquals(1, context5b.getLocalVars().getInt(1));

			String id5ab = runner.resume(id4ab);
			assertNotNull(id5ab);
			assertFalse(id5aa.equals(id5ab));
			assertFalse(id5b.equals(id5ab));
			ContinuationContext context5ab = runner.getManager().getContext(id5ab);
			assertEquals(1, context5ab.getLocalVars().getInt(1));

			String id6ab = runner.resume(id5ab);
			assertNull(id6ab);

			String id6b = runner.resume(id5b);
			assertNull(id6b);
		}
	}
}