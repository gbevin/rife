/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseMailQueue.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail.dam;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManager;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerFactory;
import com.uwyn.rife.mail.Email;
import com.uwyn.rife.mail.MailQueueManager;
import com.uwyn.rife.mail.exceptions.InstallMailQueueErrorException;
import com.uwyn.rife.mail.exceptions.MailQueueManagerException;
import com.uwyn.rife.mail.exceptions.RemoveMailQueueErrorException;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.tools.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class DatabaseMailQueue extends DbQueryManager implements MailQueueManager
{
	protected GenericQueryManager<Email>	mManager = null;
	
    protected DatabaseMailQueue(Datasource datasource)
    {
        super(datasource);
		
		mManager = GenericQueryManagerFactory.getInstance(datasource, Email.class);
    }
	
	public boolean install()
	throws MailQueueManagerException
	{
		try
		{
			mManager.install();
		}
		catch (DatabaseException e)
		{
			throw new InstallMailQueueErrorException(e);
		}

		return true;
	}
	
	public boolean remove()
	throws MailQueueManagerException
	{
		try
		{
			mManager.remove();
		}
		catch (DatabaseException e)
		{
			throw new RemoveMailQueueErrorException(e);
		}

		return true;
	}
	
	public boolean queue(Email email)
	throws MailQueueManagerException
	{
		if (null == email)
		{
			return false;
		}
		
		// store the email in the database if it's not already in the queue
		email.addConstraint(new ConstrainedBean().unique("fromAddress", "toAddresses", "ccAddresses", "bccAddresses", "subject", "body"));
		
		if (!email.validate(mManager))
		{
			return false;
		}
		
		try
		{
			mManager.save(email);
		}
		catch (DatabaseException e)
		{
			throw new MailQueueManagerException(e);
		}
		
		return true;
	}
	
	public boolean queue(MimeMessage email)
	throws MailQueueManagerException
	{
		if (null == email)
		{
			return false;
		}
		
		// converting the email to a base-64 encoded string
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			email.writeTo(out);
		}
		catch (MessagingException e)
		{
			throw new MailQueueManagerException(e);
		}
		catch (IOException e)
		{
			throw new MailQueueManagerException(e);
		}
		String body = Base64.encodeToString(out.toByteArray(), false);
		
		// construct a temporary email message with the encoded javamail message
		Email tmp_email = new Email()
			.from(MimeMessage.class.getName())
			.to(MimeMessage.class.getName())
			.subject(MimeMessage.class.getName())
			.body(body);
		
		// store the email in the database if it's not already in the queue
		tmp_email.addConstraint(new ConstrainedBean().unique("fromAddress", "toAddresses", "subject", "body"));
		
		if (!tmp_email.validate(mManager))
		{
			return false;
		}
		
		try
		{
			mManager.save(tmp_email);
		}
		catch (DatabaseException e)
		{
			throw new MailQueueManagerException(e);
		}
		
		return true;
	}
}

