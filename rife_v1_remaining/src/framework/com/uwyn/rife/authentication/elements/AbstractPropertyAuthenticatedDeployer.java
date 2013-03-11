/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com> and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractPropertyAuthenticatedDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.authentication.PasswordEncrypting;
import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.elements.exceptions.UnknownCredentialsClassException;
import com.uwyn.rife.authentication.elements.exceptions.UnsupportedEncryptionException;
import com.uwyn.rife.authentication.remembermanagers.PurgingRememberManager;
import com.uwyn.rife.authentication.sessionmanagers.PurgingSessionManager;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.PropertyRequiredException;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.StringEncryptor;

/**
 * Deployer for {@link Authenticated} elements that configures the various
 * authentication managers through properties.
 * 
 * <p>Element properties used:
 * <dl>
 * <dt>{@value #PROPERTYNAME_CREDENTIALS_CLASS} (required)</dt>
 * <dd>The fully qualified name of the class that will be used to store the
 * credentials, this is typically {@link com.uwyn.rife.authentication.credentials.RoleUser}</dd>
 * <dt>{@value #PROPERTYNAME_ENABLE_PURGING}</dt>
 * <dd>When {@code true}, the appropriate authentication managers will
 * be wrapped with proxy that purges outdated data on-the-fly without having
 * to run an asynchronous purge thread.</dd>
 * <dt>{@value #PROPERTYNAME_PASSWORD_ENCRYPTION}</dt>
 * <dd>The encryption method that will be used for the password, this has to
 * be a valid identifier of a {@link StringEncryptor}</dd>
 * <dt>{@value #PROPERTYNAME_SESSION_DURATION}</dt>
 * <dd>The duration of an authentication session in milliseconds. This defaults
 * to the global authentication session duration that has been setup in the
 * configuration participant.</dd>
 * <dt>{@value #PROPERTYNAME_REMEMBER_DURATION}</dt>
 * <dd>The duration that credential remember IDs are preserved in
 * milliseconds. This defaults to the global remember duration that has been
 * setup in the configuration participant.</dd>
 * <dt>{@value #PROPERTYNAME_SESSION_PURGE_FREQUENCY}</dt>
 * <dd>The purge frequency of the authentication session purging when purging
 * is enabled.</dd>
 * <dt>{@value #PROPERTYNAME_SESSION_PURGE_SCALE}</dt>
 * <dd>The purge scale of the authentication session purging when purging
 * is enabled.</dd>
 * <dt>{@value #PROPERTYNAME_REMEMBER_PURGE_FREQUENCY}</dt>
 * <dd>The purge frequency of the remember-me purging when purging
 * is enabled.</dd>
 * <dt>{@value #PROPERTYNAME_REMEMBER_PURGE_SCALE}</dt>
 * <dd>The purge scale of the authentication session purging when purging
 * is enabled.</dd>
 * </dl>
 * <p>
 * The frequency of purging is controlled by two properties, "frequency" and
 * "scale". Every (frequency / scale) requests, a purge is performed. For
 * example, if frequency is 1 and scale is 2, a purge is performed on roughly
 * half of requests. If frequency is 2 and scale is 100, a purge is performed
 * on 2 percent of requests.
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public abstract class AbstractPropertyAuthenticatedDeployer extends AuthenticatedDeployer
{
	public final static String PROPERTYNAME_CREDENTIALS_CLASS = "credentials_class";
	public final static String PROPERTYNAME_ENABLE_PURGING = "enable_purging";
	public final static String PROPERTYNAME_PASSWORD_ENCRYPTION = "password_encryption";
	public final static String PROPERTYNAME_SESSION_DURATION = "session_duration";
	public final static String PROPERTYNAME_REMEMBER_DURATION = "remember_duration";
	public final static String PROPERTYNAME_SESSION_PURGE_FREQUENCY = "session_purge_frequency";
	public final static String PROPERTYNAME_SESSION_PURGE_SCALE = "session_purge_scale";
	public final static String PROPERTYNAME_REMEMBER_PURGE_FREQUENCY = "remember_purge_frequency";
	public final static String PROPERTYNAME_REMEMBER_PURGE_SCALE = "remember_purge_scale";
	
	public AbstractPropertyAuthenticatedDeployer()
	{
	}
	
	/**
	 * Creates a {@code SessionManager}.
	 *
	 * @return a {@code SessionManager} instance
	 * @since 1.6
	 */
	public abstract SessionManager createSessionManager();
	
	/**
	 * Creates a {@code SessionValidator}.
	 *
	 * @return a {@code SessionValidator} instance
	 * @since 1.6
	 */
	public abstract SessionValidator createSessionValidator();
	
	/**
	 * Creates a {@code CredentialsManager}.
	 *
	 * @return a {@code CredentialsManager} instance
	 * @since 1.6
	 */
	public abstract CredentialsManager createCredentialsManager();
	
	/**
	 * Creates a {@code RememberManager}.
	 *
	 * @return a {@code RememberManager} instance
	 * @since 1.6
	 */
	public abstract RememberManager createRememberManager();
	
	public void deploy()
	throws EngineException
	{
		if (!getElementInfo().containsProperty(PROPERTYNAME_CREDENTIALS_CLASS))
		{
			throw new PropertyRequiredException(getElementInfo().getDeclarationName(), PROPERTYNAME_CREDENTIALS_CLASS);
		}

		String	credentials_class_name = getElementInfo().getPropertyString(PROPERTYNAME_CREDENTIALS_CLASS);
		Class	credentials_class = null;
		try
		{
			credentials_class = Class.forName(credentials_class_name);
			setCredentialsClass(credentials_class);
		}
		catch (ClassNotFoundException e)
		{
			throw new UnknownCredentialsClassException(credentials_class_name, e);
		}

		SessionValidator	validator = createSessionValidator();
		
		// set up the authentication handlers
		SessionManager		session_manager = createSessionManager();
		CredentialsManager	credentials_manager = createCredentialsManager();
		RememberManager		remember_manager = createRememberManager();

		validator.setCredentialsManager(credentials_manager);
		validator.setSessionManager(session_manager);
		validator.setRememberManager(remember_manager);
		
		// handle purging
		if (Convert.toBoolean(getElementInfo().getProperty(PROPERTYNAME_ENABLE_PURGING), false))
		{
			PurgingSessionManager purging_session_manager = new PurgingSessionManager(validator.getSessionManager());
			validator.setSessionManager(purging_session_manager);
	
			if (!getElementInfo().isPropertyEmpty(PROPERTYNAME_SESSION_PURGE_FREQUENCY))
			{
				purging_session_manager
					.setSessionPurgeFrequency(Convert.toInt(getElementInfo().getProperty(PROPERTYNAME_SESSION_PURGE_FREQUENCY), RifeConfig.Authentication.getSessionPurgeFrequency()));
			}
			if (!getElementInfo().isPropertyEmpty(PROPERTYNAME_SESSION_PURGE_SCALE))
			{
				purging_session_manager
					.setSessionPurgeScale(Convert.toInt(getElementInfo().getProperty(PROPERTYNAME_SESSION_PURGE_SCALE), RifeConfig.Authentication.getSessionPurgeScale()));
			}
	
			RememberManager current_remember_manager = validator.getRememberManager();
			if (null != current_remember_manager)
			{
				PurgingRememberManager purging_remember_manager = new PurgingRememberManager(current_remember_manager);
				validator.setRememberManager(purging_remember_manager);
	
				if (!getElementInfo().isPropertyEmpty(PROPERTYNAME_REMEMBER_PURGE_FREQUENCY))
				{
					purging_remember_manager
						.setRememberPurgeFrequency(Convert.toInt(getElementInfo().getProperty(PROPERTYNAME_REMEMBER_PURGE_FREQUENCY), RifeConfig.Authentication.getRememberPurgeFrequency()));
				}
				if (!getElementInfo().isPropertyEmpty(PROPERTYNAME_REMEMBER_PURGE_SCALE))
				{
					purging_remember_manager
						.setRememberPurgeScale(Convert.toInt(getElementInfo().getProperty(PROPERTYNAME_REMEMBER_PURGE_SCALE), RifeConfig.Authentication.getRememberPurgeScale()));
				}
			}
		}

		// register the session validator
		setSessionValidator(validator);

		// handle encryption settings
		if (credentials_manager instanceof PasswordEncrypting &&
			!getElementInfo().isPropertyEmpty(PROPERTYNAME_PASSWORD_ENCRYPTION))
		{
			String			encryption = getElementInfo().getPropertyString(PROPERTYNAME_PASSWORD_ENCRYPTION);
			StringEncryptor	encryptor = StringEncryptor.getEncryptor(encryption);
			if (null == encryptor)
			{
				throw new UnsupportedEncryptionException(encryption);
			}
			((PasswordEncrypting)credentials_manager).setPasswordEncryptor(encryptor);
		}

		// handle authentication duration settings
		if (!getElementInfo().isPropertyEmpty(PROPERTYNAME_SESSION_DURATION))
		{
			session_manager.setSessionDuration(Convert.toLong(getElementInfo().getProperty(PROPERTYNAME_SESSION_DURATION), RifeConfig.Authentication.getSessionDuration()));
		}

		// handle remember duration settings
		if (null != remember_manager &&
			!getElementInfo().isPropertyEmpty(PROPERTYNAME_REMEMBER_DURATION))
		{
			remember_manager.setRememberDuration(Convert.toLong(getElementInfo().getProperty(PROPERTYNAME_REMEMBER_DURATION), RifeConfig.Authentication.getRememberDuration()));
		}
	}
}
