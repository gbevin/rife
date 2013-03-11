/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestCallSimpleCallTarget2.java 3941 2008-04-26 21:28:32Z gbevin $
 */
package com.uwyn.rife.continuations;

public class TestCallSimpleCallTarget2 extends AbstractContinuableObject
{
	public void execute()
	{
		String answer = "during call target 2\n";
		answer(answer);
	}
}