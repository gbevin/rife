/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestExecutor2.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler;

public class TestExecutor2 extends Executor
{
	public boolean executeTask(Task task)
	{
		return true;
	}
	
	public String getHandledTasktype()
	{
		return "test_executor2";
	}
}
