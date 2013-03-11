/*
 * Copyright 2001-2008 Uwyn bvba/sprl <info[remove] at uwyn dot com>
 * Distributed under the terms of the GNU General Public License, v2 or later
 * $Id: DatabaseMailQueueExecutor.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail.executors;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.database.DbBeanFetcher;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.queries.Update;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManager;
import com.uwyn.rife.database.querymanagers.generic.GenericQueryManagerFactory;
import com.uwyn.rife.mail.Email;
import com.uwyn.rife.mail.MailQueueExecutor;
import com.uwyn.rife.scheduler.Executor;
import com.uwyn.rife.scheduler.Task;
import com.uwyn.rife.scheduler.exceptions.SchedulerException;
import com.uwyn.rife.tools.Base64;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringUtils;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DatabaseMailQueueExecutor extends Executor implements MailQueueExecutor
{
	private final static Object RUNNING_MONITOR = new Object();
	
	private static boolean		sRunning = false;

	private String mMailqueueName = "";

	public boolean isDeliveryAllowed(Email email)
	{
		return true;
	}

	public boolean executeTask(Task task)
	{
		// obtain the mail queue name
		try
		{
			mMailqueueName = task.getTaskoptionValue("name");
		}
		catch (SchedulerException e)
		{
			mMailqueueName = null;
		}
		if (null == mMailqueueName)
		{
			mMailqueueName = "";
		}
		else
		{
			mMailqueueName += " : ";
		}
		
		Logger.getLogger("com.uwyn.rife.mail").finest(mMailqueueName+"Running MailQueueExecutor.");
		
		// obtain the datasource name
		String datasource_name = null;
		try
		{
			datasource_name = task.getTaskoptionValue("datasource");
		}
		catch (SchedulerException e)
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error while obtaining the MailQueueExecutor's 'datasource' task option value.\n"+ExceptionUtils.getExceptionStackTrace(e));
		}
		if (null == datasource_name)
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Missing 'datasource' task option for the MailQueueExecutor.");
			return false;
		}
		
		// obtain the smtp server name
		String smtp_server = null;
		try
		{
			smtp_server = task.getTaskoptionValue("smtp_server");
		}
		catch (SchedulerException e)
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error while obtaining the MailQueueExecutor's 'smtp_server' task option value.\n"+ExceptionUtils.getExceptionStackTrace(e));
			return false;
		}
		
		// obtain the smtp ssl
		boolean smtp_ssl = false;
		try
		{
			smtp_ssl = StringUtils.convertToBoolean(task.getTaskoptionValue("smtp_ssl"));
		}
		catch (SchedulerException e)
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error while obtaining the MailQueueExecutor's 'smtp_ssl' task option value.\n"+ExceptionUtils.getExceptionStackTrace(e));
		}
		
		// obtain the smtp server port
		String smtp_port = null;
		try
		{
			smtp_port = task.getTaskoptionValue("smtp_port");
		}
		catch (SchedulerException e)
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error while obtaining the MailQueueExecutor's 'smtp_port' task option value, using the default value.\n"+ExceptionUtils.getExceptionStackTrace(e));
		}

		if (null == smtp_port)
		{
			if (smtp_ssl)
			{
				smtp_port = "465";
			}
			else
			{
				smtp_port = "25";
			}
		}
		
		// obtain the smtp username
		// obtain the smtp password
		// obtained together, because they must both be provided to be valid
		String smtp_username = null;
		String smtp_password = null;
		try
		{
			smtp_username = task.getTaskoptionValue("smtp_username");
		}
		catch (SchedulerException e)
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error while obtaining the MailQueueExecutor's 'smtp_username' task option value.\n"+ExceptionUtils.getExceptionStackTrace(e));
			return false;
		}
		try
		{
			smtp_password = task.getTaskoptionValue("smtp_password");
		}
		catch (SchedulerException e)
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error while obtaining the MailQueueExecutor's 'smtp_password' task option value.\n"+ExceptionUtils.getExceptionStackTrace(e));
			return false;
		}
		if ((smtp_username != null || smtp_password != null) &&
			(null == smtp_username || null == smtp_password))
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error while obtaining the MailQueueExecutor's 'smtp_username' and 'smtp_password' task option values, they must be provided together.\n");
			return false;
		}
		
		Datasource datasource = Datasources.getRepInstance().getDatasource(datasource_name);
		if (null == datasource)
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error: the MailQueueExecutor's datasource '"+datasource+"' could not be found.");
			return false;
		}
		
		String smtp_from = null;
		try
		{
			smtp_from = task.getTaskoptionValue("smtp_from");
		}
		catch (SchedulerException e)
		{
			Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error while obtaining the MailQueueExecutor's 'smtp_from' task option value.\n"+ExceptionUtils.getExceptionStackTrace(e));
			return false;
		}

		GenericQueryManager<Email> emails = GenericQueryManagerFactory.getInstance(datasource, Email.class);
		synchronized (RUNNING_MONITOR)
		{
			if (!sRunning)
			{
				Logger.getLogger("com.uwyn.rife.mail").finest(mMailqueueName+"MailQueueExecutor not currently running, starting a new one.");

				// not currrently running, start a new run
				sRunning = true;

				try
				{
					// flag messages for sending
					Update flag_messages = new Update(datasource)
						.table(emails.getTable())
						.field("queueFlag", true);
					new DbQueryManager(datasource).executeUpdate(flag_messages);
					Logger.getLogger("com.uwyn.rife.mail").finest(mMailqueueName+"MailQueueExecutor's flags set, starting processing.");

					Session session = null;
					// connect to the SMTP server
					if (smtp_server != null && smtp_server.length() > 0)
					{
						Properties props = new Properties();
						props.put("mail.transport", "smtp");
						props.put("mail.host", smtp_server);
						props.put("mail.port", smtp_port);
						props.put("mail.smtp.host", smtp_server);
						props.put("mail.smtp.port", smtp_port);

						if (smtp_username != null && smtp_password != null)
						{
							if (smtp_ssl)
							{
								props.put("mail.smtps.auth", "true");
							}
							else
							{
								props.put("mail.smtp.auth", "true");
							}
						}
						
						if (smtp_from != null)
						{
							props.put("mail.smtp.from", smtp_from);
						}

						session = Session.getDefaultInstance(props, null);
					}

					// send the flagged messages
					SendEmails send_emails = new SendEmails(datasource, session, emails, smtp_server, Integer.parseInt(smtp_port), smtp_username, smtp_password, smtp_ssl);
					emails.restore(emails.getRestoreQuery()
						.where("queueFlag", "=", true), send_emails);
					Logger.getLogger("com.uwyn.rife.mail").info(String.valueOf(mMailqueueName)+send_emails.getCount()+" mails were sent");
				}
				finally
				{
					sRunning = false;
				}
			}
			else
			{
				Logger.getLogger("com.uwyn.rife.mail").finest(mMailqueueName+"MailQueueExecutor is currently RUNNING, not running another");
			}
		}

		Logger.getLogger("com.uwyn.rife.mail").finest(mMailqueueName+"MailQueueExecutor run finished.");

		return true;
	}

	public String getHandledTasktype()
	{
		return "MailQueue";
	}

	class SendEmails extends DbBeanFetcher<Email>
	{
		private Session						mSession = null;
		private GenericQueryManager<Email>	mEmailManager = null;
		private int							mCount = 0;
		private String						mHost = null;
		private int							mPort = 0;
		private String						mUsername = null;
		private String						mPassword = null;
		private boolean						mSsl = false;

		SendEmails(Datasource datasource, Session session, GenericQueryManager<Email> emails, String host, int port, String username, String password, boolean ssl)
		{
			super(datasource, Email.class);
			mSession = session;
			mEmailManager = emails;
			mHost = host;
			mPort = port;
			mUsername = username;
			mPassword = password;
			mSsl = ssl;
		}

		int getCount()
		{
			return mCount;
		}

		public boolean gotBeanInstance(Email email)
		{
			if (null == mSession)
			{
				Logger.getLogger("com.uwyn.rife.mail").warning(mMailqueueName+"Email not sent since no 'smtp_server' task option has not been provided to the MailQueueExecutor.\n"+email.toString());
				return false;
			}
			
			if (!isDeliveryAllowed(email))
			{
				return false;
			}
	
			MimeMessage message;
			try
			{
				// try to detect if a raw javamail message has been queued
				if (email.getFromAddress().equals(MimeMessage.class.getName()) &&
					email.getToAddresses().equals(MimeMessage.class.getName()) &&
					email.getSubject().equals(MimeMessage.class.getName()))
				{
					ByteArrayInputStream in = new ByteArrayInputStream(Base64.decode(email.getBody()));
					message = new MimeMessage(mSession, in);
				}
				else
				{
					message = new MimeMessage(mSession);
					
					// build the mail message
					message.setFrom(new InternetAddress(email.getFromAddress(), false));
					message.setSubject(email.getSubject());
					message.setText(email.getBody(), "UTF-8");
					message.setSentDate(new Date());
					
					int to_count = 0;
					Collection<String>	to_list = StringUtils.split(email.getToAddresses(), ",");
					InternetAddress[]	to_array = new InternetAddress[to_list.size()];
					for (String to : to_list)
					{
						to_array[to_count++] = new InternetAddress(to, false);
					}
					message.setRecipients(Message.RecipientType.TO, to_array);
					
					int cc_count = 0;
					Collection<String>	cc_list = StringUtils.split(email.getCcAddresses(), ",");
					InternetAddress[]	cc_array = new InternetAddress[cc_list.size()];
					for (String cc : cc_list)
					{
						cc_array[cc_count++] = new InternetAddress(cc, false);
					}
					message.setRecipients(Message.RecipientType.CC, cc_array);
					
					int bcc_count = 0;
					Collection<String>	bcc_list = StringUtils.split(email.getBccAddresses(), ",");
					InternetAddress[]	bcc_array = new InternetAddress[bcc_list.size()];
					for (String bcc : bcc_list)
					{
						bcc_array[bcc_count++] = new InternetAddress(bcc, false);
					}
					message.setRecipients(Message.RecipientType.BCC, bcc_array);
				}
			}
			catch (MessagingException e)
			{
				message = null;
				Logger.getLogger("com.uwyn.rife.mail").warning(mMailqueueName+"Email not sent due to an error while building the message.\n"+email.toString()+"\n"+ExceptionUtils.getExceptionStackTrace(e));
			}
			
			// send the mail
			if (message != null)
			{
				try
				{
					if (mUsername != null && mPassword != null)
					{
						Transport transport = mSession.getTransport(mSsl ? "smtps" : "smtp");
						transport.connect(mHost, mPort, mUsername, mPassword);
						message.saveChanges();
						transport.sendMessage(message, message.getAllRecipients());
					}
					else
					{
						Transport.send(message);
					}
					
					// removed the message
					mEmailManager.delete(email.getId());

					mCount++;
					try
					{
						Thread.sleep(300);
						if (0 == (mCount % 100))
						{
							Thread.sleep(10000);
						}
					}
					catch (InterruptedException e3)
					{
						// do nothing
					}
				}
				catch (MessagingException e)
				{
					Logger.getLogger("com.uwyn.rife.mail").severe(mMailqueueName+"Unexpected error while sending the MailQueueExecutor's email message \n"+email.toString()+"\n"+ExceptionUtils.getExceptionStackTrace(e));
				}
			}
			
			return true;
		}
	}
}
