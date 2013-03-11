/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MailQueueManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail;

import com.uwyn.rife.mail.Email;
import com.uwyn.rife.mail.exceptions.MailQueueManagerException;
import javax.mail.internet.MimeMessage;

/**
 * This interface defines the methods that classes with
 * <code>MailQueueManager</code> functionalities have to implement.
 * <p>A <code>MailQueueManager</code> provides methods that allow you to store
 * email messages in a queue. That queue is intended to be processed at
 * regular intervals by a <code>MailQueueExecutor</code>.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.mail.MailQueueExecutor
 * @since 1.0
 */
public interface MailQueueManager
{
	/**
	 * Adds the provided email to the queue. If an identical mail is
	 * already present or any other validation for the <code>Email</code>
	 * instance fails, the data will not be added to the queue.
	 * <p>Details about the failure can be obtained throught the
	 * <code>ValidationError</code>s of the <code>Email</code> instance.
	 * 
	 * @param email The <code>Email</code> instance that needs to be
	 * queued.
	 * @return <code>true</code> if the email was successfully queued; or
	 * <p><code>false</code> if an email validation failed (for instance a
	 * duplicate entry)
	 * @exception MailQueueManagerException If an error occurred while
	 * adding the email to the queue
	 * @see #queue(MimeMessage)
	 * @since 1.0
	 */
	public boolean queue(Email email) throws MailQueueManagerException;
	
	/**
	 * Adds a fully setup JavaMail email to the queue. The presence of identical
	 * mails can not be detected due to the fact that each message already contains
	 * a unique message ID. Duplicates with thus be sent as many times as they are
	 * added.
	 * <p>This complements the quick way of adding Email instances by providing
	 * a way to queue even HTML emails, mails with attachments, etc ...
	 * 
	 * @param email The <code>MimeMessage</code> instance that needs to be
	 * queued.
	 * @return <code>true</code> if the email was successfully queued; or
	 * <p><code>false</code> otherwise
	 * @exception MailQueueManagerException If an error occurred while
	 * adding the email to the queue
	 * @see #queue(Email)
	 * @since 1.0
	 */
	public boolean queue(MimeMessage email) throws MailQueueManagerException;
}
