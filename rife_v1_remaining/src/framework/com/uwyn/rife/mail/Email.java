/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Email.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail;

import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;

/**
 * Contains the details of an email message that will be sent through the mail
 * queue.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class Email extends Validation<ConstrainedBean, ConstrainedProperty>
{
	private int     mId = -1;
	private String  mFromAddress = null;
	private String  mToAddresses = null;
	private String  mSubject = null;
	private String  mBody = null;
	private String  mCcAddresses = null;
	private String  mBccAddresses = null;
	
	private boolean mQueueFlag = false;
	
	protected void activateValidation()
	{
		addConstraint(new ConstrainedProperty("id").notNull(true).identifier(true));
		addConstraint(new ConstrainedProperty("fromAddress").notNull(true).notEmpty(true).maxLength(255));
		addConstraint(new ConstrainedProperty("toAddresses").notNull(true).notEmpty(true).maxLength(255));
		addConstraint(new ConstrainedProperty("subject").notNull(true).notEmpty(true).maxLength(255));
		addConstraint(new ConstrainedProperty("body").notNull(true).notEmpty(true));
		addConstraint(new ConstrainedProperty("ccAddresses").maxLength(255));
		addConstraint(new ConstrainedProperty("bccAddresses").maxLength(255));
	}
	
	/**
	 * Creates a new instance.
	 *
	 * @since 1.0
	 */
	public Email()
	{
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the id
	 * @return the <code>Email</code> instance
	 */
	public Email id(int id)
	{
		mId = id;
		
		return this;
	}
	
	/**
	 * Sets the id of this instance.
	 *
	 * @param id the id
	 */
	public void setId(int id)
	{
		mId = id;
	}
	
	/**
	 * Retrieves the id of this instance.
	 *
	 * @return the requested id
	 */
	public int getId()
	{
		return mId;
	}
	
	/**
	 * Sets the <code>from</code> email address.
	 *
	 * @param from an email address
	 * @return the <code>Email</code> instance
	 */
	public Email from(String from)
	{
		mFromAddress = from;
		
		return this;
	}
	
	/**
	 * Sets the <code>from</code> email address.
	 *
	 * @param from an email address
	 */
	public void setFromAddress(String from)
	{
		mFromAddress = from;
	}
	
	/**
	 * Retrieves the <code>from</code> email address.
	 *
	 * @return a String
	 */
	public String getFromAddress()
	{
		return mFromAddress;
	}
	
	/**
	 * Adds a <code>to</code> email address.
	 *
	 * @param toAddress an email address
	 * @return the <code>Email</code> instance
	 */
	public Email to(String toAddress)
	{
		addTo(toAddress);
		return this;
	}
	
	/**
	 * Adds a <code>to</code> email address.
	 *
	 * @param toAddress an email address
	 */
	public void addTo(String toAddress)
	{
		if (null == mToAddresses)
		{
			mToAddresses = toAddress;
		}
		else
		{
			mToAddresses += "," + toAddress;
		}
	}
	
	/**
	 * Sets the <code>to</code> email addresses. This replaces all
	 * previous ones.
	 * <p>The email addresses need to be separated by commas.
	 *
	 * @param toAddresses the email addresses, separated by commas
	 * @return the <code>Email</code> instance
	 */
	public Email toAddresses(String toAddresses)
	{
		setToAddresses(toAddresses);
		return this;
	}
	
	/**
	 * Sets the <code>to</code> email addresses. This replaces all
	 * previous ones.
	 * <p>The email addresses need to be separated by commas.
	 *
	 * @param toAddresses the email addresses, separated by commas
	 */
	public void setToAddresses(String toAddresses)
	{
		mToAddresses = toAddresses;
	}
	
	/**
	 * Retrieves the <code>to</code> email addresses.
	 *
	 * @return the <code>to</code> email addresses, separated by commas
	 */
	public String getToAddresses()
	{
		return mToAddresses;
	}
	
	/**
	 * Sets the subject.
	 *
	 * @param subject the subject
	 * @return the <code>Email</code> instance
	 */
	public Email subject(String subject)
	{
		mSubject = subject;
		
		return this;
	}
	
	/**
	 * Sets the subject .
	 *
	 * @param subject the subject
	 */
	public void setSubject(String subject)
	{
		mSubject = subject;
	}
	
	/**
	 * Retrieves the subject.
	 *
	 * @return the subject
	 */
	public String getSubject()
	{
		return mSubject;
	}
	
	/**
	 * Sets the body.
	 *
	 * @param body the body
	 * @return the <code>Email</code> instance
	 */
	public Email body(String body)
	{
		mBody = body;
		
		return this;
	}
	
	/**
	 * Sets the body.
	 *
	 * @param body the body
	 */
	public void setBody(String body)
	{
		mBody = body;
	}
	
	/**
	 * Retrieves the body.
	 *
	 * @return the body
	 */
	public String getBody()
	{
		return mBody;
	}
	
	/**
	 * Adds a <code>cc</code> email address.
	 *
	 * @param ccAddress an email address
	 * @return the <code>Email</code> instance
	 */
	public Email cc(String ccAddress)
	{
		addCc(ccAddress);
		return this;
	}
	
	/**
	 * Adds a <code>cc</code> email address.
	 *
	 * @param ccAddress an email address
	 */
	public void addCc(String ccAddress)
	{
		if (null == mCcAddresses)
		{
			mCcAddresses = ccAddress;
		}
		else
		{
			mCcAddresses += "," + ccAddress;
		}
	}
	
	/**
	 * Sets the <code>cc</code> email addresses. This replaces all
	 * previous ones.
	 * <p>The email addresses need to be separated by commas.
	 *
	 * @param ccAddresses the email addresses, separated by commas
	 * @return the <code>Email</code> instance
	 */
	public Email ccAddresses(String ccAddresses)
	{
		setCcAddresses(ccAddresses);
		return this;
	}
	
	/**
	 * Sets the <code>cc</code> email addresses. This replaces all
	 * previous ones.
	 * <p>The email addresses need to be separated by commas.
	 *
	 * @param ccAddresses the email addresses, separated by commas
	 */
	public void setCcAddresses(String ccAddresses)
	{
		mCcAddresses = ccAddresses;
	}
	
	/**
	 * Retrieves the <code>cc</code> email addresses.
	 *
	 * @return the <code>cc</code> email addresses, separated by commas
	 */
	public String getCcAddresses()
	{
		return mCcAddresses;
	}
	
	/**
	 * Adds a <code>bcc</code> email address.
	 *
	 * @param bccAddress an email address
	 * @return the <code>Email</code> instance
	 */
	public Email bcc(String bccAddress)
	{
		addBcc(bccAddress);
		return this;
	}
	
	/**
	 * Adds a <code>bcc</code> email address.
	 *
	 * @param bccAddress an email address
	 */
	public void addBcc(String bccAddress)
	{
		if (null == mBccAddresses)
		{
			mBccAddresses = bccAddress;
		}
		else
		{
			mBccAddresses += "," + bccAddress;
		}
	}
	
	/**
	 * Sets the <code>bcc</code> email addresses. This replaces all
	 * previous ones.
	 * <p>The email addresses need to be separated by commas.
	 *
	 * @param bccAddresses the email addresses, separated by commas
	 * @return the <code>Email</code> instance
	 */
	public Email bccAddresses(String bccAddresses)
	{
		setBccAddresses(bccAddresses);
		return this;
	}
	
	/**
	 * Sets the <code>bcc</code> email addresses. This replaces all
	 * previous ones.
	 * <p>The email addresses need to be separated by commas.
	 *
	 * @param bccAddresses the email addresses, separated by commas
	 */
	public void setBccAddresses(String bccAddresses)
	{
		mBccAddresses = bccAddresses;
	}
	
	/**
	 * Retrieves the <code>bcc</code> email addresses.
	 *
	 * @return the bcc email addresses, separated by commas
	 */
	public String getBccAddresses()
	{
		return mBccAddresses;
	}
	
	/**
	 * Retrieves the queue flag, this is only for internal use.
	 *
	 * @return <code>true</code> if the message is queued; and
	 * <p><code>false</code> otherwise
	 */
	public boolean getQueueFlag()
	{
		return mQueueFlag;
	}
	
	/**
	 * Sets the queue flag, this is only for internal use.
	 *
	 * @param queueFlag the queue flag
	 */
	public void setQueueFlag(boolean queueFlag)
	{
		mQueueFlag = queueFlag;
	}
	
	public int hashCode()
	{
		int result;
		
		result = 0;
		result = 29 * result + (mFromAddress != null ? mFromAddress.hashCode() : 0);
		result = 29 * result + (mToAddresses != null ? mToAddresses.hashCode() : 0);
		result = 29 * result + (mSubject != null ? mSubject.hashCode() : 0);
		result = 29 * result + (mBody != null ? mBody.hashCode() : 0);
		result = 29 * result + (mCcAddresses != null ? mCcAddresses.hashCode() : 0);
		result = 29 * result + (mBccAddresses != null ? mBccAddresses.hashCode() : 0);
		
		return result;
	}
	
	public boolean equals(Object other)
	{
		if (null == other)
		{
			return false;
		}
		
		if (other == this)
		{
			return true;
		}
		
		if (!(other instanceof Email))
		{
			return false;
		}
		
		Email other_email = (Email)other;
		
		if (other_email.getFromAddress() != null || getFromAddress() != null)
		{
			if (null == other_email.getFromAddress() || null == getFromAddress())
			{
				return false;
			}
			if (!other_email.getFromAddress().equals(getFromAddress()))
			{
				return false;
			}
		}
		
		if (other_email.getToAddresses() != null || getToAddresses() != null)
		{
			if (null == other_email.getToAddresses() || null == getToAddresses())
			{
				return false;
			}
			if (!other_email.getToAddresses().equals(getToAddresses()))
			{
				return false;
			}
		}
		
		if (other_email.getSubject() != null || getSubject() != null)
		{
			if (null == other_email.getSubject() || null == getSubject())
			{
				return false;
			}
			if (!other_email.getSubject().equals(getSubject()))
			{
				return false;
			}
		}
		
		if (other_email.getBody() != null || getBody() != null)
		{
			if (null == other_email.getBody() || null == getBody())
			{
				return false;
			}
			if (!other_email.getBody().equals(getBody()))
			{
				return false;
			}
		}
		
		if (other_email.getCcAddresses() != null || getCcAddresses() != null)
		{
			if (null == other_email.getCcAddresses() || null == getCcAddresses())
			{
				return false;
			}
			if (!other_email.getCcAddresses().equals(getCcAddresses()))
			{
				return false;
			}
		}
		
		if (other_email.getBccAddresses() != null || getBccAddresses() != null)
		{
			if (null == other_email.getBccAddresses() || null == getBccAddresses())
			{
				return false;
			}
			if (!other_email.getBccAddresses().equals(getBccAddresses()))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public String toString()
	{
		return "Email{" +
			"mId=" + mId +
			", mFromAddress=" + mFromAddress +
			", mToAddresses=" + mToAddresses +
			", mSubject=" + mSubject +
			", mBody=" + mBody +
			", mCcAddresses=" + mCcAddresses +
			", mBccAddresses=" + mBccAddresses +
			"}";
	}
}


