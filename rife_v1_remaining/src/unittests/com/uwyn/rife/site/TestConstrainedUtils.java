/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestConstrainedUtils.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;
import com.uwyn.rife.database.ConstrainedClass;
import com.uwyn.rife.database.NotConstrainedClass;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.site.Validation;
import junit.framework.TestCase;

public class TestConstrainedUtils extends TestCase
{
	public TestConstrainedUtils(String name)
	{
		super(name);
	}
	
	public void testGetConstrainedInstance()
	{
		assertNotNull(ConstrainedUtils.getConstrainedInstance(ConstrainedClass.class));
		assertNull(ConstrainedUtils.getConstrainedInstance(NotConstrainedClass.class));
		assertNotNull(ConstrainedUtils.getConstrainedInstance(ConstrainedStaticInnerClass.class));
		assertNull(ConstrainedUtils.getConstrainedInstance(NotConstrainedStaticInnerClass.class));
		assertNull(ConstrainedUtils.getConstrainedInstance(ConstrainedInnerClass.class));
		assertNull(ConstrainedUtils.getConstrainedInstance(NotConstrainedInnerClass.class));
	}

	public static class ConstrainedStaticInnerClass extends Validation
	{
	}
	
	public static class NotConstrainedStaticInnerClass
	{
	}	

	public class ConstrainedInnerClass extends Validation
	{
	}
	
	public class NotConstrainedInnerClass
	{
	}	
}
