/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Authenticated.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.elements;

import com.uwyn.rife.authentication.Credentials;
import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.authentication.SessionAttributes;
import com.uwyn.rife.authentication.SessionManager;
import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.authentication.credentials.RememberMe;
import com.uwyn.rife.authentication.elements.exceptions.UndefinedAuthenticationRememberManagerException;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.authentication.exceptions.RememberManagerException;
import com.uwyn.rife.authentication.exceptions.SessionManagerException;
import com.uwyn.rife.authentication.exceptions.SessionValidatorException;
import com.uwyn.rife.engine.ElementDeployer;
import com.uwyn.rife.engine.ElementInfo;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.engine.exceptions.PropertyRequiredException;
import com.uwyn.rife.engine.exceptions.UnsupportedTemplateTypeException;
import com.uwyn.rife.site.ValidationError;
import com.uwyn.rife.site.ValidationFormatter;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.StringUtils;
import javax.servlet.http.Cookie;

/**
 * Requires that the user have a valid authentication session before access
 * to a child element is allowed. This class contains the logic for restoring
 * remembered sessions and displaying a template (typically a login form)
 * if the user is not authenticated.
 * <p>
 * The following properties may be set:
 * <p>
 * <dl>
 * <dt>enforce_authenticated (default = true)</dt>
 * <dd>Controls whether access to child elements is allowed for users who
 * don't have valid authentication sessions. If this property is false,
 * a user with no authentication session is allowed to access the
 * child element, but there is no user identity information available.
 * <p>
 * The child element implementation may distinguish an anonymous user
 * from an authenticated one by calling
 * {@code {@link #getRequestAttribute(String) 
 * getRequestAttribute(Identified.IDENTITY_ATTRIBUTE_NAME)}}.
 * <p>
 * This is similar to using an {@code {@link Identified}} element,
 * but expired sessions will automatically be recreated if the user has
 * the appropriate "remember me" cookie set and "remember me" is enabled.</dd>
 * </dl>
 * <p>
 * To customize the behavior of the authentication, it's the easiest to override
 * one of the hook methods.
 *
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public abstract class Authenticated extends Identified implements SessionAttributes
{
	protected String	mTemplateName = null;

	protected Authenticated()
	{
	}

	/**
	 * Returns the ID of this authentication element.
	 *
	 * @return this authentication element's ID
	 * @since 1.0
	 */
	public String getAuthenticatedElementId()
	{
		return getElementInfo().getId();
	}

	/**
	 * Returns the <code>ElementInfo</code> of this authentication element.
	 *
	 * @return this authentication element's <code>ElementInfo</code>
	 * @since 1.0
	 */
	public ElementInfo getAuthElement()
	{
		return getElementInfo();
	}

	/**
	 * Returns the class that is used for handling the credentials.
	 *
	 * @return this credentials' class
	 * @since 1.0
	 */
	public Class<? extends Credentials> getCredentialsClass()
	{
		ElementDeployer deployer = getDeployer();

		if (deployer instanceof AuthenticatedDeployer)
		{
			return ((AuthenticatedDeployer)deployer).getCredentialsClass();
		}

		return null;
	}

	/**
	 * Returns the class that is used for handling the credentials.
	 *
	 * @return the credentials' class
	 * @since 1.0
	 */
	public SessionValidator getSessionValidator()
	{
		ElementDeployer deployer = getDeployer();

		if (deployer instanceof AuthenticatedDeployer)
		{
			return ((AuthenticatedDeployer)deployer).getSessionValidator();
		}

		return null;
	}

	/**
	 * Allows a custom template name to be set.
	 * <p>
	 * This method is typically called during the implementation of method hooks
	 * to change the template that will be used by this authentication element.
	 *
	 * @param name the name of the template
	 * @since 1.0
	 */
	protected void setTemplateName(String name)
	{
		mTemplateName = name;
	}
	
	/**
	 * Hook method that is called at the start of the element's execution.
	 *
	 * @since 1.0
	 */
	protected void initializeAuthentication()
	{
	}

	/**
	 * Hook method that is called after the template instance has been instantiated.
	 *
	 * @param template the template instance that has been instantiated
	 * @since 1.0
	 */
	protected void entrance(Template template)
	{
	}

	/**
	 * Hook method that is called on login form submission when validation of the
	 * credentials produces validation errors.
	 *
	 * @param template this authentication element's template
	 * @param credentials the credentials object that was invalid
	 * @since 1.0
	 */
	protected void unvalidatedCredentials(Template template, Credentials credentials)
	{
	}

	/**
	 * Hook method that is called on login form submission when the credentials
	 * are validated without errors
	 *
	 * @param credentials the credentials object that was valid
	 * @since 1.0
	 */
	protected void validatedCredentials(Credentials credentials)
	{
	}

	/**
	 * Hook method that is called when valid credentials have been accepted by the
	 * <code>CredentialsManager</code> that backs this authentication element.
	 *
	 * @param credentials the credentials object that was accepted
	 * @since 1.0
	 */
	protected void acceptedCredentials(Credentials credentials)
	{
	}

	/**
	 * Hook method that is called after a new authentication session has been
	 * successfully created.
	 *
	 * @param userId the user ID of the user that was successfully authenticated
	 * @since 1.0
	 */
	protected void authenticated(long userId)
	{
	}

	/**
	 * Hook method that is called when valid credentials have been rejected by the
	 * <code>CredentialsManager</code> that backs this authentication element.
	 * <p>
	 * This can for example happen when the password is not correct.
	 * <p>
	 * Note that there is already a default implementation of this hook method that
	 * simply adds a validation error to the credentials object. If you want to
	 * preserve this when you implement your own hook method, you need to call the
	 * super class's method in your implementation.
	 *
	 * @param template this authentication element's template
	 * @param credentials the credentials object that was rejected
	 * @since 1.0
	 */
	@SuppressWarnings("deprecation")
	protected void refusedCredentials(Template template, Credentials credentials)
	{
		if (template.hasValueId(ValidationFormatter.DEFAULT_ERROR_AREA_ID))
		{
			// this is for backwards compatibility with the deprecated ValidationFormatter class
			String message = null;
			if (template.hasBlock("INVALID_CREDENTIALS"))
			{
				message = template.getBlock("INVALID_CREDENTIALS");
			}
			else
			{
				message = "INVALID_CREDENTIALS";
			}

			ValidationFormatter.setErrorArea(template, message);
		}
		else
		{
			// all new code should use this version of the validation errors
			credentials.addValidationError(new ValidationError.INVALID("credentials"));
		}
	}

	/**
	 * Hook method that is called when the <code>SessionManager</code> couldn't
	 * create a new authentication session of valid and accepted credentials.
	 * <p>
	 * Note that there is already a default implementation of this hook method that
	 * simply adds a validation error to the credentials object. If you want to
	 * preserve this when you implement your own hook method, you need to call the
	 * super class's method in your implementation.
	 *
	 * @param template this authentication element's template
	 * @param credentials the credentials object that was used when creating the
	 * authentication session
	 * @since 1.0
	 */
	@SuppressWarnings("deprecation")
	protected void sessionCreationError(Template template, Credentials credentials)
	{
		if (template.hasValueId(ValidationFormatter.DEFAULT_ERROR_AREA_ID))
		{
			// this is for backwards compatibility with the deprecated ValidationFormatter class
			String message = null;
			if (template.hasBlock("CANT_CREATE_SESSION"))
			{
				message = template.getBlock("CANT_CREATE_SESSION");
			}
			else
			{
				message = "CANT_CREATE_SESSION";
			}

			ValidationFormatter.setErrorArea(template, message);
		}
		else
		{
			// all new code should use this version of the validation errors
			credentials.addValidationError(new ValidationError.UNEXPECTED("sessioncreation"));
		}
	}

	/**
	 * Hook method that is called when the <code>SessionValidator</code> doesn't
	 * accept the authentication ID that a user provides after having been logged
	 * in.
	 * <p>
	 * This can happen for example happen when the maximum duration has expired,
	 * when the authentication ID has been tampered with, or when the
	 * authentication ID isn't known anymore by the backing store.
	 *
	 * @param childTriggerName the name of the child trigger that contains
	 * the authentication ID
	 * @param childTriggerValues the values of the child trigger with the
	 * authentication ID
	 * @param validityId a number that indicates the validation state of the
	 * session, as used by the <code>SessionValidator</code>, more information can
	 * be found here: {@link SessionValidator#validateSession}
	 * @since 1.0
	 */
	protected void sessionNotValid(String childTriggerName, String[] childTriggerValues, int validityId)
	{
	}

	private Template getTemplateInstance(String type, String name, String encoding)
	{
		TemplateFactory template_factory = TemplateFactory.getFactory(type);
		if (null == template_factory)
		{
			throw new UnsupportedTemplateTypeException(type);
		}

		Template template = template_factory.get(name, encoding, null);
		entrance(template);
		return template;
	}

	public void processElement()
	{
		Class<? extends Credentials>	credentials_class = getCredentialsClass();
		SessionValidator				session_validator = getSessionValidator();

		assert credentials_class != null;
		assert session_validator != null;

		initializeAuthentication();

		if (!hasProperty("template_name") &&
			null == mTemplateName)
		{
			throw new PropertyRequiredException(getDeclarationName(), "template_name");
		}
		if (!hasProperty("submission_name"))
		{
			throw new PropertyRequiredException(getDeclarationName(), "submission_name");
		}
		if (!hasProperty("authvar_name"))
		{
			throw new PropertyRequiredException(getDeclarationName(), "authvar_name");
		}
		if (!hasProperty("authvar_type"))
		{
			throw new PropertyRequiredException(getDeclarationName(), "authvar_type");
		}
		if (!hasProperty("remembervar_name"))
		{
			throw new PropertyRequiredException(getDeclarationName(), "remembervar_name");
		}
		if (!hasProperty("prohibit_remember"))
		{
			throw new PropertyRequiredException(getDeclarationName(), "prohibit_remember");
		}

		// obtain the optional template_type property
		String template_type = null;
		if (hasProperty("template_type"))
		{
			template_type = getPropertyString("template_type");
		}
		else
		{
			template_type = "enginehtml";
		}

		// obtain the optional template_encoding property
		String template_encoding = null;
		if (hasProperty("template_encoding"))
		{
			template_encoding = getPropertyString("template_encoding");
		}

		// obtain the mandatory template_name property
		String template_name = null;
		if (mTemplateName != null)
		{
			template_name = mTemplateName;
		}
		else
		{
			template_name = getPropertyString("template_name");
		}

		// obtain the optional allow_anonymous property
		boolean enforce_authenticated = true;
		if (hasProperty("enforce_authenticated"))
		{
			enforce_authenticated = StringUtils.convertToBoolean(getPropertyString("enforce_authenticated"));
		}

		Template template = null;

		// check if a remember id is provided
		String rememberid = null;
		String remembervar_name = getPropertyString("remembervar_name");
		if (getElementInfo().containsIncookiePossibility(remembervar_name) &&
			hasCookie(remembervar_name))
		{
			Cookie remembercookie = getCookie(remembervar_name);
			rememberid = remembercookie.getValue();
		}

		if (rememberid != null)
		{
			long userid = -1;
			try
			{
				RememberManager remember_manager = session_validator.getRememberManager();
				if (null == remember_manager)
				{
					throw new UndefinedAuthenticationRememberManagerException();
				}

				userid = remember_manager.getRememberedUserId(rememberid);
				remember_manager.eraseRememberId(rememberid);
			}
			catch (RememberManagerException e)
			{
				throw new EngineException(e);
			}

			// only start a new session if the userid could be retrieved
			if (userid != -1)
			{
				// try to start a new session, if it hasn't succeeded, the child trigger
				// will not be activated and regular authentication will kick in
				startNewSession(userid, true, true);
			}
		}

		// handle a credentials submission
		if (hasSubmission(getPropertyString("submission_name")))
		{
			Credentials credentials = getSubmissionBean(getPropertyString("submission_name"), credentials_class);

			if (!credentials.validate())
			{
				template = getTemplateInstance(template_type, template_name, template_encoding);

				unvalidatedCredentials(template, credentials);

				if (template.hasValueId(ValidationFormatter.DEFAULT_ERROR_AREA_ID))
				{
					ValidationFormatter.setValidationErrors(template, credentials.getValidationErrors());
				}

				generateForm(template, credentials);
			}
			else
			{
				validatedCredentials(credentials);

				long userid = -1;

				try
				{
					userid = session_validator.getCredentialsManager().verifyCredentials(credentials);
				}
				catch (CredentialsManagerException e)
				{
					throw new EngineException(e);
				}

				// verify login attempt
				if (userid >= 0)
				{
					acceptedCredentials(credentials);

					// if the session has to be remembered, do so
					boolean remember = false;
					if (credentials instanceof RememberMe)
					{
						remember = ((RememberMe)credentials).getRemember();
					}

					// start a new session
					if (!startNewSession(userid, remember, false))
					{
						template = getTemplateInstance(template_type, template_name, template_encoding);

						// errors occurred, notify user
						sessionCreationError(template, credentials);
					}
					else
					{
						template = getTemplateInstance(template_type, template_name, template_encoding);
					}
				}
				else
				{
					template = getTemplateInstance(template_type, template_name, template_encoding);
				}

				refusedCredentials(template, credentials);

				generateForm(template, credentials);
			}
		}
		else
		{
			if (enforce_authenticated)
			{
				template = getTemplateInstance(template_type, template_name, template_encoding);
				generateEmptyForm(template, credentials_class);
			}
			else
			{
				child();
			}
		}

		if (null != template)
		{
			print(template);
		}
	}

	private boolean startNewSession(long userid, boolean remember, boolean remembered)
	throws EngineException
	{
		if (remember)
		{
			String rememberid = null;
			try
			{
				RememberManager remember_manager = getSessionValidator().getRememberManager();
				if (null == remember_manager)
				{
					throw new UndefinedAuthenticationRememberManagerException();
				}

				rememberid = remember_manager.createRememberId(userid, getRemoteAddr());
			}
			catch (RememberManagerException e)
			{
				throw new EngineException(e);
			}

			if (rememberid != null)
			{
				Cookie remembercookie = new Cookie(getPropertyString("remembervar_name"), rememberid);
				remembercookie.setPath("/");
				remembercookie.setMaxAge(60*60*24*30*3); // three months
				setCookie(remembercookie);
			}
		}

		String authid = null;
		try
		{
			authid = getSessionValidator().getSessionManager().startSession(userid, getRemoteAddr(), remembered);
		}
		catch (SessionManagerException e)
		{
			throw new EngineException(e);
		}

		if (null == authid)
		{
			return false;
		}
		else
		{
			authenticated(userid);

			// defer to child
			if (getPropertyString("authvar_type").equals("input"))
			{
				setOutput(getPropertyString("authvar_name"), authid);
			}
			else if (getPropertyString("authvar_type").equals("cookie"))
			{
				Cookie authcookie = new Cookie(getPropertyString("authvar_name"), authid);
				authcookie.setPath("/");
				setCookie(authcookie);
			}
		}

		return true;
	}

	public boolean childTriggered(String name, String[] values)
	{
		boolean result = false;

		if (name.equals(getPropertyString("authvar_name")))
		{
			String authentication_request_attribute = createAuthenticationRequestAttributeName(getElementInfo(), name, values[0]);
			
			if (hasRequestAttribute(authentication_request_attribute))
			{
				result = true;
			}
			else
			{
				SessionValidator	session_validator = getSessionValidator();
				
				assert session_validator != null;
				
				// validate the session
				String	auth_id = values[0];
				int		session_validity_id = -1;
				try
				{
					session_validity_id = session_validator.validateSession(auth_id, getRemoteAddr(), this);
				}
				catch (SessionValidatorException e)
				{
					throw new EngineException(e);
				}
				
				// check if the validation allows access
				if (session_validator.isAccessAuthorized(session_validity_id))
				{
					SessionManager session_manager = session_validator.getSessionManager();
					
					try
					{
						// prohibit access if the authentication session was
						// started through rememberd credentials and that
						// had been set to not allowed
						if (Convert.toBoolean(getProperty("prohibit_remember"), false) &&
							session_manager.wasRemembered(auth_id))
						{
							sessionNotValid(name, values, session_validity_id);
						}
						// continue the session
						else
						{
							result = session_manager.continueSession(auth_id);
							if (result)
							{
								setRequestAttribute(authentication_request_attribute, true);
							}
						}
					}
					catch (SessionManagerException e)
					{
						throw new EngineException(e);
					}
				}
				else
				{
					sessionNotValid(name, values, session_validity_id);
				}
			}
		}

		if (!result)
		{
			return false;
		}

		super.childTriggered(name, values);

		return true;
	}

	/**
	 * Creates a name for the current authentication context that can be used to
	 * cache the authentication process' result as a request attribute. This name
	 * is built from the authentication element's ID, the name of the
	 * authentication var and its value.
	 *
	 * @param elementInfo the authentication element information
	 * @param name the name of the authentication variable
	 * @param value the value of the authentication variable
	 *
	 * @return the created name
	 * 
	 * @since 1.5
	 */
	public static String createAuthenticationRequestAttributeName(ElementInfo elementInfo, String name, String value) throws EngineException
	{
		return elementInfo.getId() + "\t" + name + "\t" + value;
	}
}



