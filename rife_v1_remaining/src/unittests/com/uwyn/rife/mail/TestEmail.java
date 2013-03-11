/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestEmail.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail;

import com.uwyn.rife.mail.Email;
import com.uwyn.rife.site.ValidationError;
import java.util.Iterator;
import junit.framework.TestCase;

public class TestEmail extends TestCase
{
	public TestEmail(String name)
	{
		super(name);
	}

	public void testInstantiation()
	{
		Email email = new Email();
		assertEquals(-1, email.getId());
		assertNull(email.getFromAddress());
		assertNull(email.getToAddresses());
		assertNull(email.getSubject());
		assertNull(email.getBody());
		assertNull(email.getCcAddresses());
		assertNull(email.getBccAddresses());
		assertFalse(email.getQueueFlag());
		assertEquals(0, email.hashCode());
		
		assertEquals(email, new Email());
	}

	public void testPopulation()
	{
		Email email1 = new Email();
		email1.setId(2);
		email1.setFromAddress("info@uwyn.com");
		email1.setToAddresses("test@uwyn.com,test2@uwyn.com");
		email1.setCcAddresses("test3@uwyn.com,test4@uwyn.com,test5@uwyn.com");
		email1.setBccAddresses("test6@uwyn.com,test7@uwyn.com");
		email1.setSubject("subject");
		email1.setBody("body");
		
		assertEquals(2, email1.getId());
		assertEquals("info@uwyn.com", email1.getFromAddress());
		assertEquals("test@uwyn.com,test2@uwyn.com", email1.getToAddresses());
		assertEquals("test3@uwyn.com,test4@uwyn.com,test5@uwyn.com", email1.getCcAddresses());
		assertEquals("test6@uwyn.com,test7@uwyn.com", email1.getBccAddresses());
		assertEquals("subject", email1.getSubject());
		assertEquals("body", email1.getBody());

		Email email2 = new Email();
		email2
			.id(27)
			.from("infob@uwyn.com")
			.toAddresses("testb@uwyn.com")
			.ccAddresses("test2b@uwyn.com,test3b@uwyn.com")
			.bccAddresses("test4b@uwyn.com")
			.subject("subjectb")
			.body("bodyb");
		
		assertEquals(27, email2.getId());
		assertEquals("infob@uwyn.com", email2.getFromAddress());
		assertEquals("testb@uwyn.com", email2.getToAddresses());
		assertEquals("test2b@uwyn.com,test3b@uwyn.com", email2.getCcAddresses());
		assertEquals("test4b@uwyn.com", email2.getBccAddresses());
		assertEquals("subjectb", email2.getSubject());
		assertEquals("bodyb", email2.getBody());
		
		email2
			.to("info2b@uwyn.com")
			.cc("test5b@uwyn.com")
			.bcc("test6b@uwyn.com");
		email2.addTo("info3b@uwyn.net");
		email2.addCc("info5b@uwyn.net");
		email2.addBcc("info6b@uwyn.net");
		
		assertEquals(27, email2.getId());
		assertEquals("infob@uwyn.com", email2.getFromAddress());
		assertEquals("testb@uwyn.com,info2b@uwyn.com,info3b@uwyn.net", email2.getToAddresses());
		assertEquals("test2b@uwyn.com,test3b@uwyn.com,test5b@uwyn.com,info5b@uwyn.net", email2.getCcAddresses());
		assertEquals("test4b@uwyn.com,test6b@uwyn.com,info6b@uwyn.net", email2.getBccAddresses());
		assertEquals("subjectb", email2.getSubject());
		assertEquals("bodyb", email2.getBody());
	}

	public void testValidation()
	{
		Email email = new Email();

		Iterator<ValidationError>	validationerrors_it = null;
		ValidationError				validationerror = null;
		
		assertTrue(false == email.validate());
		
		validationerrors_it = email.getValidationErrors().iterator();
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("fromAddress", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("toAddresses", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("subject", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("body", validationerror.getSubject());
		assertTrue(false == validationerrors_it.hasNext());
		
		email.resetValidation();

		email
			.from("")
			.toAddresses("")
			.subject("")
			.body("");
		
		assertTrue(false == email.validate());
		
		validationerrors_it = email.getValidationErrors().iterator();
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("fromAddress", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("toAddresses", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("subject", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("MANDATORY", validationerror.getIdentifier());
		assertEquals("body", validationerror.getSubject());
		assertTrue(false == validationerrors_it.hasNext());
		
		email.resetValidation();

		email
			.from("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345")
			.toAddresses("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345")
			.ccAddresses("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345")
			.bccAddresses("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345")
			.subject("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345")
			.body("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345");
		
		assertTrue(false == email.validate());
		
		validationerrors_it = email.getValidationErrors().iterator();
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("WRONGLENGTH", validationerror.getIdentifier());
		assertEquals("fromAddress", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("WRONGLENGTH", validationerror.getIdentifier());
		assertEquals("toAddresses", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("WRONGLENGTH", validationerror.getIdentifier());
		assertEquals("subject", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("WRONGLENGTH", validationerror.getIdentifier());
		assertEquals("ccAddresses", validationerror.getSubject());
		assertTrue(validationerrors_it.hasNext());
		validationerror = validationerrors_it.next();
		assertEquals("WRONGLENGTH", validationerror.getIdentifier());
		assertEquals("bccAddresses", validationerror.getSubject());
		assertTrue(false == validationerrors_it.hasNext());
	}

	public void testEquals()
	{
		Email email1 = new Email();
		email1.setId(2);
		email1.setFromAddress("info@uwyn.com");
		email1.setToAddresses("test@uwyn.com");
		email1.setCcAddresses("test2@uwyn.com");
		email1.setBccAddresses("test3@uwyn.com");
		email1.setSubject("subject");
		email1.setBody("body");

		Email email2 = new Email();
		email2.setId(2);
		email2.setFromAddress("info@uwyn.com");
		email2.setToAddresses("test@uwyn.com");
		email2.setCcAddresses("test2@uwyn.com");
		email2.setBccAddresses("test3@uwyn.com");
		email2.setSubject("subject");
		email2.setBody("body");
		
		assertEquals(email1, email2);
		
		email1.setId(3);
		assertTrue(email1.equals(email2));
		
		email1.setQueueFlag(true);
		assertTrue(email1.equals(email2));
		
		email1.setFromAddress("infob@uwyn.com");
		assertFalse(email1.equals(email2));
		email2.setFromAddress("infob@uwyn.com");
		assertTrue(email1.equals(email2));
		
		email1.setToAddresses("testb@uwyn.com");
		assertFalse(email1.equals(email2));
		email2.setToAddresses("testb@uwyn.com");
		assertTrue(email1.equals(email2));
		
		email1.setCcAddresses("test2b@uwyn.com");
		assertFalse(email1.equals(email2));
		email2.setCcAddresses("test2b@uwyn.com");
		assertTrue(email1.equals(email2));
		
		email1.setBccAddresses("test3b@uwyn.com");
		assertFalse(email1.equals(email2));
		email2.setBccAddresses("test3b@uwyn.com");
		assertTrue(email1.equals(email2));
		
		email1.setSubject("subject1");
		assertFalse(email1.equals(email2));
		email2.setSubject("subject1");
		assertTrue(email1.equals(email2));
		
		email1.setBody("body1");
		assertFalse(email1.equals(email2));
		email2.setBody("body1");
		assertTrue(email1.equals(email2));
	}

	public void testHashcode()
	{
		Email email1 = new Email();
		email1.setId(2);
		email1.setFromAddress("info@uwyn.com");
		email1.setToAddresses("test@uwyn.com");
		email1.setCcAddresses("test2@uwyn.com");
		email1.setBccAddresses("test3@uwyn.com");
		email1.setSubject("subject");
		email1.setBody("body");

		Email email2 = new Email();
		email2.setId(2);
		email2.setFromAddress("info@uwyn.com");
		email2.setToAddresses("test@uwyn.com");
		email2.setCcAddresses("test2@uwyn.com");
		email2.setBccAddresses("test3@uwyn.com");
		email2.setSubject("subject");
		email2.setBody("body");
		
		assertTrue(email1.hashCode() == email2.hashCode());
		
		email1.setId(3);
		assertTrue(email1.hashCode() == email2.hashCode());
		
		email1.setQueueFlag(true);
		assertTrue(email1.hashCode() == email2.hashCode());
		
		email1.setFromAddress("infob@uwyn.com");
		assertFalse(email1.hashCode() == email2.hashCode());
		email2.setFromAddress("infob@uwyn.com");
		assertTrue(email1.hashCode() == email2.hashCode());
		
		email1.setToAddresses("testb@uwyn.com");
		assertFalse(email1.hashCode() == email2.hashCode());
		email2.setToAddresses("testb@uwyn.com");
		assertTrue(email1.hashCode() == email2.hashCode());
		
		email1.setCcAddresses("test2b@uwyn.com");
		assertFalse(email1.hashCode() == email2.hashCode());
		email2.setCcAddresses("test2b@uwyn.com");
		assertTrue(email1.hashCode() == email2.hashCode());
		
		email1.setBccAddresses("test3b@uwyn.com");
		assertFalse(email1.hashCode() == email2.hashCode());
		email2.setBccAddresses("test3b@uwyn.com");
		assertTrue(email1.hashCode() == email2.hashCode());
		
		email1.setSubject("subject1");
		assertFalse(email1.hashCode() == email2.hashCode());
		email2.setSubject("subject1");
		assertTrue(email1.hashCode() == email2.hashCode());
		
		email1.setBody("body1");
		assertFalse(email1.hashCode() == email2.hashCode());
		email2.setBody("body1");
		assertTrue(email1.hashCode() == email2.hashCode());
	}
}

