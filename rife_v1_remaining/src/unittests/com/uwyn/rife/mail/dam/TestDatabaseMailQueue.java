/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseMailQueue.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail.dam;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.mail.Email;
import com.uwyn.rife.mail.exceptions.InstallMailQueueErrorException;
import com.uwyn.rife.mail.exceptions.RemoveMailQueueErrorException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import junit.framework.TestCase;

public class TestDatabaseMailQueue extends TestCase
{
	private Datasource  mDatasource = null;
    
	public TestDatabaseMailQueue(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
	}
	
    public void setUp()
	throws Exception
	{
		DatabaseMailQueueFactory.getInstance(mDatasource).install();
	}
	
    public void tearDown()
	throws Exception
	{
		try
		{
			DatabaseMailQueueFactory.getInstance(mDatasource).remove();
		}
		catch (Throwable e)
		{
			// discart errors
		}
	}
	
	public void testInstallError()
	{
		DatabaseMailQueue manager = DatabaseMailQueueFactory.getInstance(mDatasource);

		try
		{
			manager.install();
			fail();
		}
		catch (InstallMailQueueErrorException e)
		{
		    assertNotNull(e.getCause());
		}
	}

	public void testRemoveError()
	throws Exception
	{
		DatabaseMailQueueFactory.getInstance(mDatasource).remove();

		DatabaseMailQueue manager = DatabaseMailQueueFactory.getInstance(mDatasource);

		try
		{
			manager.remove();
			fail();
		}
		catch (RemoveMailQueueErrorException e)
		{
		    assertNotNull(e.getCause());
		}
	}

	public void testQueue()
	throws Exception
	{
		DatabaseMailQueue manager = DatabaseMailQueueFactory.getInstance(mDatasource);
	
		assertTrue(manager.queue(new Email()
			.from("info@uwyn.com")
			.toAddresses("test@uwyn.com")
			.ccAddresses("test2@uwyn.com,test3@uwyn.com")
			.bccAddresses("test4@uwyn.com")
			.subject("subject")
			.body("body")));

		assertFalse(manager.queue(new Email()
			.from("info@uwyn.com")
			.toAddresses("test@uwyn.com")
			.ccAddresses("test2@uwyn.com,test3@uwyn.com")
			.bccAddresses("test4@uwyn.com")
			.subject("subject")
			.body("body")));
	
		assertTrue(manager.queue(new Email()
			.from("infob@uwyn.com")
			.toAddresses("test@uwyn.com")
			.ccAddresses("test2@uwyn.com,test3@uwyn.com")
			.bccAddresses("test4@uwyn.com")
			.subject("subject")
			.body("body")));
	}
	
	public void testQueueJavamail()
	throws Exception
	{
		DatabaseMailQueue manager = DatabaseMailQueueFactory.getInstance(mDatasource);
		
		Session mail_session = Session.getDefaultInstance(new Properties());
		
		MimeMessage message1 = new MimeMessage(mail_session);
		message1.setFrom(new InternetAddress("info@uwyn.com"));
		message1.addRecipient(Message.RecipientType.TO, new InternetAddress("test@uwyn.com"));
		message1.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@uwyn.com"));
		message1.addRecipient(Message.RecipientType.CC, new InternetAddress("test3@uwyn.com"));
		message1.addRecipient(Message.RecipientType.BCC, new InternetAddress("test4@uwyn.com"));
		message1.setSubject("subject");
		message1.setText("body");
		
		MimeMessage message2 = new MimeMessage(mail_session);
		message2.setFrom(new InternetAddress("info@uwyn.com"));
		message2.addRecipient(Message.RecipientType.TO, new InternetAddress("test@uwyn.com"));
		message2.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@uwyn.com"));
		message2.addRecipient(Message.RecipientType.CC, new InternetAddress("test3@uwyn.com"));
		message2.addRecipient(Message.RecipientType.BCC, new InternetAddress("test4@uwyn.com"));
		message2.setSubject("subject");
		message2.setText("body");
		
		MimeMessage message3 = new MimeMessage(mail_session);
		message3.setFrom(new InternetAddress("infob@uwyn.com"));
		message3.addRecipient(Message.RecipientType.TO, new InternetAddress("test@uwyn.com"));
		message3.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@uwyn.com"));
		message3.addRecipient(Message.RecipientType.CC, new InternetAddress("test3@uwyn.com"));
		message3.addRecipient(Message.RecipientType.BCC, new InternetAddress("test4@uwyn.com"));
		message3.setSubject("subject");
		message3.setText("body");
		
		assertTrue(manager.queue(message1));
		assertTrue(manager.queue(message2));
		assertTrue(manager.queue(message3));
	}
}


