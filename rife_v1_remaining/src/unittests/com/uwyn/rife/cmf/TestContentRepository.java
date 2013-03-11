/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestContentRepository.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import junit.framework.TestCase;

public class TestContentRepository extends TestCase
{
	public TestContentRepository(String name)
	{
		super(name);
	}
	
	public void testInstantiation()
	{
		ContentRepository repository = new ContentRepository();
		assertNotNull(repository);
		
		assertNull(repository.getName());
	}
	
	public void testName()
	{
		ContentRepository repository = new ContentRepository();
		repository.setName("anotherone");
		assertEquals("anotherone", repository.getName());
		repository.name("stillonemore");
		assertEquals("stillonemore", repository.getName());
		repository.setName(null);
		assertNull(repository.getName());
	}
	
	public void testValidation()
	{
		ContentRepository repository = new ContentRepository();
		
		repository.resetValidation();
		assertFalse(repository.validate());
		assertFalse(repository.isSubjectValid("name"));
		
		repository.resetValidation();
		repository.setName("anotherone");
		assertTrue(repository.validate());
		assertTrue(repository.isSubjectValid("name"));
		
		repository.resetValidation();
		assertTrue(repository.validate());
		assertTrue(repository.isSubjectValid("name"));
	}
}
