/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestValidationError.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.site.ValidationError;
import junit.framework.TestCase;

public class TestValidationError extends TestCase
{
	public TestValidationError(String name)
	{
		super(name);
	}

	public void testMandatory()
	{
		ValidationError error = new ValidationError.MANDATORY("subject1");
		assertEquals(ValidationError.IDENTIFIER_MANDATORY, error.getIdentifier());
		assertEquals("subject1", error.getSubject());
	}

	public void testUnicity()
	{
		ValidationError error = new ValidationError.UNICITY("subject3");
		assertEquals(ValidationError.IDENTIFIER_UNICITY, error.getIdentifier());
		assertEquals("subject3", error.getSubject());
	}

	public void testWrongLength()
	{
		ValidationError error = new ValidationError.WRONGLENGTH("subject4");
		assertEquals(ValidationError.IDENTIFIER_WRONGLENGTH, error.getIdentifier());
		assertEquals("subject4", error.getSubject());
	}

	public void testWrongFormat()
	{
		ValidationError error = new ValidationError.WRONGFORMAT("subject5");
		assertEquals(ValidationError.IDENTIFIER_WRONGFORMAT, error.getIdentifier());
		assertEquals("subject5", error.getSubject());
	}

	public void testNotNumeric()
	{
		ValidationError error = new ValidationError.NOTNUMERIC("subject6");
		assertEquals(ValidationError.IDENTIFIER_NOTNUMERIC, error.getIdentifier());
		assertEquals("subject6", error.getSubject());
	}

	public void testUnexpected()
	{
		ValidationError error = new ValidationError.UNEXPECTED("subject7");
		assertEquals(ValidationError.IDENTIFIER_UNEXPECTED, error.getIdentifier());
		assertEquals("subject7", error.getSubject());
	}

	public void testIncomplete()
	{
		ValidationError error = new ValidationError.INCOMPLETE("subject8");
		assertEquals(ValidationError.IDENTIFIER_INCOMPLETE, error.getIdentifier());
		assertEquals("subject8", error.getSubject());
	}

	public void testInvalid()
	{
		ValidationError error = new ValidationError.INVALID("subject9");
		assertEquals(ValidationError.IDENTIFIER_INVALID, error.getIdentifier());
		assertEquals("subject9", error.getSubject());
	}

	public void testCustom()
	{
		ValidationError error = new CustomError();
		assertEquals("CUSTOM", error.getIdentifier());
		assertEquals("customsubject", error.getSubject());
	}
	
	class CustomError extends ValidationError
	{
		CustomError()
		{
			super("CUSTOM", "customsubject");
		}
	}
}
