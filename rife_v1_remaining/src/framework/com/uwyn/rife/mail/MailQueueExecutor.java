/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MailQueueExecutor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail;

import com.uwyn.rife.mail.Email;

/**
 * This interface has to be implemented by all {@link
 * com.uwyn.rife.scheduler.Executor Executor}s that will process the mail
 * queue.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.mail.Email
 * @since 1.0
 */
public interface MailQueueExecutor
{
	/**
	 * Checks whether an <code>Email</code> instance is allowed to be
	 * delivered. This is called before sending each message so that it's
	 * possible to filter out email that for some reason is not allowed
	 * for delivery. The email will then simply be skipped.
	 * 
	 * @param email The <code>Email</code> instance that should be
	 * verified
	 * @return <code>true</code> if the email is allowed to be delivered;
	 * or
	 * <p><code>false</code> otherwise
	 * @since 1.0
	 */
	public boolean isDeliveryAllowed(Email email);
}
