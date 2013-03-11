/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestDatabaseMailQueueExecutor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail.executors;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.mail.Email;
import com.uwyn.rife.mail.dam.DatabaseMailQueue;
import com.uwyn.rife.mail.dam.DatabaseMailQueueFactory;
import com.uwyn.rife.scheduler.Task;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import junit.framework.TestCase;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public class TestDatabaseMailQueueExecutor extends TestCase
{
	private Datasource  mDatasource = null;
	private String 		mDatasourceName = null;
    
	public TestDatabaseMailQueueExecutor(Datasource datasource, String datasourceName, String name)
	{
		super(name);
        mDatasource = datasource;
		mDatasourceName = datasourceName;
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

	public void testQueue()
	throws Exception
	{
		DatabaseMailQueue manager = DatabaseMailQueueFactory.getInstance(mDatasource);
		assertTrue(manager.queue(new Email()
			.from("info@uwyn.com")
			.toAddresses("test@uwyn.com")
			.ccAddresses("test2@uwyn.com,test3@uwyn.com")
			.bccAddresses("test4@uwyn.com")
			.subject("Test subject")
			.body("Test body")));
		assertTrue(manager.queue(new Email()
			.from("infob@uwyn.com")
			.toAddresses("testb@uwyn.com")
			.ccAddresses("test2b@uwyn.com,test3b@uwyn.com")
			.bccAddresses("test4b@uwyn.com")
			.subject("subjectb")
			.body("bodyb")));
		
		Wiser wiser = new Wiser();
		wiser.setPort(8025);
		wiser.start();

		DatabaseMailQueueExecutor executor = new DatabaseMailQueueExecutor();
		Task task = new ProcessQueueTask();
		executor.executeTask(task);
		
		List<WiserMessage> messages = wiser.getMessages();
		
		assertEquals(8, messages.size());

		int first_count = 0;
		int second_count = 0;
		MimeMessage mime_email;
		for (WiserMessage email : messages)
		{
			mime_email = email.getMimeMessage();
			if (email.getEnvelopeSender().equals("info@uwyn.com"))
			{
				assertEquals("test@uwyn.com", mime_email.getHeader("To", null));
				assertEquals("test2@uwyn.com, test3@uwyn.com", mime_email.getHeader("Cc", null));
				assertEquals("Test subject", mime_email.getSubject());
				assertEquals("Test body", mime_email.getContent());
				first_count++;
			}
			
			if (email.getEnvelopeSender().equals("infob@uwyn.com"))
			{
				assertEquals("testb@uwyn.com", mime_email.getHeader("To", null));
				assertEquals("test2b@uwyn.com, test3b@uwyn.com", mime_email.getHeader("Cc", null));
				assertEquals("subjectb", mime_email.getSubject());
				assertEquals("bodyb", mime_email.getContent());
				second_count++;
			}
		}
		assertEquals(4, first_count);
		assertEquals(4, second_count);
		
		wiser.stop();
	}
	
	public void testQueueJavaMail()
	throws Exception
	{
		DatabaseMailQueue manager = DatabaseMailQueueFactory.getInstance(mDatasource);
		
		Session mail_session = Session.getDefaultInstance(new Properties());
		
		MimeMessage message1 = new MimeMessage(mail_session);
		message1.setFrom(new InternetAddress("info@uwyn.com"));
		message1.addRecipient(Message.RecipientType.TO, new InternetAddress("test@uwyn.com"));
		message1.addRecipients(Message.RecipientType.CC, new InternetAddress[] {new InternetAddress("test2@uwyn.com"), new InternetAddress("test3@uwyn.com")});
		message1.addRecipient(Message.RecipientType.BCC, new InternetAddress("test4@uwyn.com"));
		message1.setSubject("Test subject");
		message1.setText("Test body");
		
		MimeMessage message2 = new MimeMessage(mail_session);
		message2.setFrom(new InternetAddress("infob@uwyn.com"));
		message2.addRecipient(Message.RecipientType.TO, new InternetAddress("testb@uwyn.com"));
		message2.addRecipients(Message.RecipientType.CC, new InternetAddress[] {new InternetAddress("test2b@uwyn.com"), new InternetAddress("test3b@uwyn.com")});
		message2.addRecipient(Message.RecipientType.BCC, new InternetAddress("test4b@uwyn.com"));
		message2.setSubject("subjectb");
		message2.setText("bodyb");
		
		assertTrue(manager.queue(message1));
		assertTrue(manager.queue(message2));
		
		Wiser wiser = new Wiser();
		wiser.setPort(8025);
		wiser.start();
		
		DatabaseMailQueueExecutor executor = new DatabaseMailQueueExecutor();
		Task task = new ProcessQueueTask();
		executor.executeTask(task);
		
		List<WiserMessage> messages = wiser.getMessages();
		
		assertEquals(8, messages.size());
		
		int first_count = 0;
		int second_count = 0;
		MimeMessage mime_email;
		for (WiserMessage email : messages)
		{
			mime_email = email.getMimeMessage();
			if (email.getEnvelopeSender().equals("info@uwyn.com"))
			{
				assertEquals("test@uwyn.com", mime_email.getHeader("To", null));
				assertEquals("test2@uwyn.com, test3@uwyn.com", mime_email.getHeader("Cc", null));
				assertEquals("Test subject", mime_email.getSubject());
				assertEquals("Test body", mime_email.getContent());
				first_count++;
			}
			
			if (email.getEnvelopeSender().equals("infob@uwyn.com"))
			{
				assertEquals("testb@uwyn.com", mime_email.getHeader("To", null));
				assertEquals("test2b@uwyn.com, test3b@uwyn.com", mime_email.getHeader("Cc", null));
				assertEquals("subjectb", mime_email.getSubject());
				assertEquals("bodyb", mime_email.getContent());
				second_count++;
			}
		}
		assertEquals(4, first_count);
		assertEquals(4, second_count);
		
		wiser.stop();
	}
	
	class ProcessQueueTask extends Task
	{
		public String getTaskoptionValue(String  name)
		{
			if ("smtp_server".equals(name))
			{
				return "localhost";
			}

			if ("smtp_port".equals(name))
			{
				return "8025";
			}

			if ("datasource".equals(name))
			{
				return mDatasourceName;
			}
			
			return null;
		}
	}
}


