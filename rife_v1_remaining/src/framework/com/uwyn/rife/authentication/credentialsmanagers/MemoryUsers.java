/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * Steven Grimm <koreth[remove] at midwinter dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MemoryUsers.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.credentialsmanagers.exceptions.*;

import com.uwyn.rife.authentication.Credentials;
import com.uwyn.rife.authentication.CredentialsManager;
import com.uwyn.rife.authentication.PasswordEncrypting;
import com.uwyn.rife.authentication.credentials.RoleUserCredentials;
import com.uwyn.rife.authentication.credentialsmanagers.RoleUserAttributes;
import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;
import com.uwyn.rife.rep.Participant;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.SortListComparables;
import com.uwyn.rife.tools.StringEncryptor;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MemoryUsers implements CredentialsManager, RoleUsersManager, PasswordEncrypting
{
	public final static String	DEFAULT_PARTICIPANT_NAME = "ParticipantMemoryUsers";

	private Map<Long, String>				mUserIdMapping = new HashMap<Long, String>();
	private Map<String, RoleUserAttributes>	mUsers = new TreeMap<String, RoleUserAttributes>();
	private Map<String, ArrayList<String>>	mRoles = new TreeMap<String, ArrayList<String>>();
	private long							mUserIdSequence = 0;
	private String							mXmlPath = null;
	private ResourceFinder					mResourceFinder = null;
	
	protected StringEncryptor	mPasswordEncryptor = null;
	
	public MemoryUsers()
	{
	}

	public MemoryUsers(String xmlPath, ResourceFinder resourceFinder)
	throws CredentialsManagerException
	{
		if (null == xmlPath)		throw new IllegalArgumentException("xmlPath can't be null.");
		if (0 == xmlPath.length())	throw new IllegalArgumentException("xmlPath can't be empty.");
		if (null == resourceFinder)	throw new IllegalArgumentException("resourceFinder can't be null.");

		mXmlPath = xmlPath;
		mResourceFinder = resourceFinder;

		initialize();
	}
	
	/**
	 * Retrieves the path of the XML document that populated this
	 * {@code MemoryUsers} instance.
	 *
	 * @return the path of the XML document that populated this
	 * {@code MemoryUsers} instance
	 *
	 * @since 1.0
	 */
	public String getXmlPath()
	{
		return mXmlPath;
	}

	
	public StringEncryptor getPasswordEncryptor()
	{
		return mPasswordEncryptor;
	}
	
	public void setPasswordEncryptor(StringEncryptor passwordEncryptor)
	{
		mPasswordEncryptor = passwordEncryptor;
	}

    public static boolean hasRepInstance()
    {
        return Rep.hasParticipant(DEFAULT_PARTICIPANT_NAME);
    }

    public static MemoryUsers getRepInstance()
    {
		Participant	participant = Rep.getParticipant(DEFAULT_PARTICIPANT_NAME);
		if (null == participant)
		{
			return null;
		}
		
        return (MemoryUsers)participant.getObject();
    }

	public long verifyCredentials(Credentials credentials)
	throws CredentialsManagerException
	{
		RoleUserCredentials role_user = null;
		if (credentials instanceof RoleUserCredentials)
		{
			role_user = (RoleUserCredentials)credentials;
		}
		else
		{
			throw new UnsupportedCredentialsTypeException(credentials);
		}
		
		synchronized (this)
		{
			if (null == role_user.getLogin())
			{
				return -1;
			}
			
			RoleUserAttributes user_attributes = mUsers.get(role_user.getLogin());
			
			if (null == user_attributes)
			{
				return -1;
			}
			
			// correctly handle encoded passwords
			String password = null;
			try
			{
				password = StringEncryptor.adaptiveEncrypt(role_user.getPassword(), user_attributes.getPassword());
			}
			catch (NoSuchAlgorithmException e)
			{
				throw new VerifyCredentialsErrorException(credentials, e);
			}
				
			// handle roles
			if (role_user.getRole() != null)
			{
				if (user_attributes.isValid(password, role_user.getRole()))
				{
					return mUsers.get(role_user.getLogin()).getUserId();
				}
			}
			else
			{
				if (user_attributes.isValid(password))
				{
					return mUsers.get(role_user.getLogin()).getUserId();
				}
			}
		}
		
		return -1;
	}
	
	public MemoryUsers addRole(String role)
	throws CredentialsManagerException
	{
		if (null == role ||
			0 == role.length())
		{
			throw new AddRoleErrorException(role);
		}
		
		if (mRoles.containsKey(role))
		{
			throw new DuplicateRoleException(role);
		}

		mRoles.put(role, new ArrayList<String>());
		
		return this;
	}
	
	public long countRoles()
	{
		return mRoles.size();
	}
	
	public boolean containsRole(String role)
	{
		if (null == role ||
			0 == role.length())
		{
			return false;
		}
		
		return mRoles.containsKey(role);
	}
	
	public MemoryUsers addUser(String login, RoleUserAttributes attributes)
	throws CredentialsManagerException
	{
		if (null == login ||
			0 == login.length() ||
			null == attributes)
		{
			throw new AddUserErrorException(login, attributes);
		}

		synchronized (this)
		{
			// throw an exception if the user already exists
			if (mUsers.containsKey(login))
			{
				throw new DuplicateLoginException(login);
			}
			
			// correctly handle implicit and specific user ids
			if (-1 == attributes.getUserId())
			{
				while (mUserIdMapping.containsKey(new Long(mUserIdSequence)))
				{
					// FIXME: check for long overflow
					mUserIdSequence++;
				}
						
				attributes.setUserId(mUserIdSequence);
				attributes.setAutomaticUserId(true);
				mUserIdMapping.put(new Long(mUserIdSequence), login);
			}
			else
			{
				if (mUserIdMapping.containsKey(new Long(attributes.getUserId())))
				{
					throw new DuplicateUserIdException(attributes.getUserId());
				}
				
				mUserIdMapping.put(new Long(attributes.getUserId()), login);
			}
			
			// correctly handle password encoding
			RoleUserAttributes	attributes_clone = attributes.clone();
			if (mPasswordEncryptor != null &&
				!attributes_clone.getPassword().startsWith(mPasswordEncryptor.toString()))
			{
				try
				{
					attributes_clone.setPassword(mPasswordEncryptor.encrypt(attributes_clone.getPassword()));
				}
				catch (NoSuchAlgorithmException e)
				{
					throw new AddUserErrorException(login, attributes, e);
				}
			}
			
			mUsers.put(login, attributes_clone);
			
			// create reverse links from the roles to the logins
			createRoleLinks(login, attributes_clone);
		}
		
		return this;
	}
	
	private void createRoleLinks(String login, RoleUserAttributes attributes)
	throws CredentialsManagerException
	{
		assert login != null;
		assert login.length() > 0;
		
		if (attributes.getRoles() != null &&
			attributes.getRoles().size() > 0)
		{
			ArrayList<String>	logins = null;
			for (String role : attributes.getRoles())
			{
				if (!mRoles.containsKey(role))
				{
					throw new UnknownRoleErrorException(role, login, attributes);
				}
				else
				{
					logins = mRoles.get(role);
					
					if (!logins.contains(login))
					{
						logins.add(login);
					}
				}
			}
		}
	}

	public RoleUserAttributes getAttributes(String login)
	{
		if (null == login ||
			0 == login.length())
		{
			return null;
		}
		
		return mUsers.get(login);
	}
	
	public long countUsers()
	{
		return mUsers.size();
	}
	
	public boolean listRoles(ListRoles processor)
	{
		if (null == processor)
		{
			return false;
		}

		if (0 == mRoles.size())
		{
			return true;
		}
		
		boolean result = false;
		
		for (String role : mRoles.keySet())
		{
			result = true;
			
			if (!processor.foundRole(role))
			{
				break;
			}
		}
		
		return result;
	}
	
	public boolean listUsers(ListUsers processor)
	{
		if (null == processor)
		{
			return false;
		}

		if (0 == mUsers.size())
		{
			return false;
		}
		
		boolean result = false;
		
		RoleUserAttributes	attributes = null;
		for (String login : mUsers.keySet())
		{
			result = true;
			
			attributes = mUsers.get(login);
			
			if (!processor.foundUser(attributes.getUserId(), login, attributes.getPassword()))
			{
				break;
			}
		}
		
		return result;
	}
	
	public boolean listUsers(ListUsers processor, int limit, int offset)
	{
		if (null == processor ||
			limit <= 0 ||
			0 == mUsers.size())
		{
			return false;
		}
		
		boolean result = false;
		
		RoleUserAttributes	attributes = null;
		int count = 0;
		for (String login : mUsers.keySet())
		{
			if (count < offset)
			{
				count++;
				continue;
			}
			
			if (count-offset >= limit)
			{
				break;
			}
			
			count++;
			result = true;
			
			attributes = mUsers.get(login);
			
			if (!processor.foundUser(attributes.getUserId(), login, attributes.getPassword()))
			{
				break;
			}
		}
		
		return result;
	}

	public boolean containsUser(String login)
	{
		if (null == login ||
			0 == login.length())
		{
			return false;
		}
		
		synchronized (this)
		{
			return mUsers.containsKey(login);
		}
	}
	
	public boolean listUsersInRole(ListUsers processor, String role)
	throws CredentialsManagerException
	{
		if (null == processor)
		{
			return false;
		}

		if (null == role ||
			0 == role.length())
		{
			return false;
		}
		
		if (0 == mUsers.size())
		{
			return false;
		}
		
		boolean result = false;
		
		RoleUserAttributes	attributes = null;
		for (String login : mUsers.keySet())
		{
			attributes = mUsers.get(login);
			if (null == attributes.getRoles() ||
				!attributes.getRoles().contains(role))
			{
				continue;
			}
			
			result = true;
			if (!processor.foundUser(attributes.getUserId(), login, attributes.getPassword()))
			{
				break;
			}
		}
		
		return result;
	}
	
	public boolean isUserInRole(long userId, String role)
	{
		if (userId < 0 ||
			null == role ||
			0 == role.length())
		{
			return false;
		}
		
		synchronized (this)
		{
			String login = mUserIdMapping.get(new Long(userId));
			
			if (null == login)
			{
				return false;
			}
			
			RoleUserAttributes user_attributes = mUsers.get(login);
			
			if (null == user_attributes)
			{
				return false;
			}
			
			return user_attributes.isInRole(role);
		}
	}
	
	public String getLogin(long userId)
	{
		if (userId < 0)
		{
			return null;
		}

		String login = null;
		
		synchronized (this)
		{
			login = mUserIdMapping.get(new Long(userId));
		}
		
		return login;
	}
	
	public long getUserId(String login)
	{
		if (null == login ||
			0 == login.length())
		{
			return -1;
		}

		long userid = -1;
		
		synchronized (this)
		{
			RoleUserAttributes	attributes = mUsers.get(login);
			if (attributes != null)
			{
				userid = attributes.getUserId();
			}
		}
		
		return userid;
	}
	
	public boolean updateUser(String login, RoleUserAttributes attributes)
	throws CredentialsManagerException
	{
		if (null == login ||
			0 == login.length() ||
			null == attributes)
		{
			throw new UpdateUserErrorException(login, attributes);
		}
		
		synchronized (this)
		{
			if (!mUsers.containsKey(login))
			{
				return false;
			}
			
			// get the current attributes
			RoleUserAttributes current_attributes = mUsers.get(login);
			
			// set the current password if it has not been provided
			RoleUserAttributes	attributes_clone = attributes.clone();
			if (null == attributes_clone.getPassword())
			{
				attributes_clone.setPassword(current_attributes.getPassword());
			}
			else
			{
				// correctly handle password encoding
				if (mPasswordEncryptor != null &&
					!attributes_clone.getPassword().startsWith(mPasswordEncryptor.toString()))
				{
					try
					{
						attributes_clone.setPassword(mPasswordEncryptor.encrypt(attributes_clone.getPassword()));
					}
					catch (NoSuchAlgorithmException e)
					{
						throw new UpdateUserErrorException(login, attributes, e);
					}
				}
			}

			// ensure that the user id remains the same
			attributes_clone.setUserId(current_attributes.getUserId());
			
			// update the reverse link from the roles collection
			removeRoleLinks(login);
			
			// store the new user attributes
			mUsers.put(login, attributes_clone);
			
			// create reverse links from the roles to the logins
			createRoleLinks(login, attributes_clone);
		}
		
		return true;
	}
	
	public boolean removeUser(String login)
	{
		if (null == login ||
			0 == login.length())
		{
			return false;
		}
		
		synchronized (this)
		{
			// update the reverse link from the roles collection
			removeRoleLinks(login);
			
			// remove the user
			return null != mUsers.remove(login);

		}
	}
	
	public boolean removeUser(long userId)
	{
		if (userId < 0)
		{
			return false;
		}
		
		String login = null;
		
		synchronized (this)
		{
			if (null == mUserIdMapping.get(userId))
			{
				return false;
			}
			
			else
			{
				login = mUserIdMapping.get(userId);
			
				// update the reverse link from the roles collection
				removeRoleLinks(login);
			
				// remove the user
				return null != mUsers.remove(login);

			}
		}
	}
	
	public boolean removeRole(String name)
	{
		if (null == name ||
			0 == name.length())
		{
			return false;
		}
		
		synchronized (this)
		{
			if (mRoles.remove(name) == null)
			{
				return false;
			}
			
			for (String key : mUsers.keySet())
			{
				Collection<String> roles = mUsers.get(key).getRoles();
				
				if (roles != null && roles.contains(name))
				{
					roles.remove(name);
				}
			}
		}
		return true;
	}
	
	private void removeRoleLinks(String login)
	{
		assert login != null;
		assert login.length() > 0;
		
		RoleUserAttributes attributes = mUsers.get(login);
		if (attributes != null &&
			attributes.getRoles() != null &&
			attributes.getRoles().size() > 0)
		{
			// remove the login from the roles it's registered for
			ArrayList<String>	logins = null;
			ArrayList<String>	roles_to_delete = null;
			for (String role : attributes.getRoles())
			{
				logins = mRoles.get(role);
				logins.remove(login);
				if (0 == logins.size())
				{
					if (null == roles_to_delete)
					{
						roles_to_delete = new ArrayList<String>();
					}
					
					roles_to_delete.add(role);
				}
			}
			
			// remove the roles that now don't have any logins anymore
			if (roles_to_delete != null)
			{
				for (String role : roles_to_delete)
				{
					mRoles.remove(role);
				}
			}
		}
	}
	
	public void clearUsers()
	{
		synchronized (this)
		{
			mUsers = new TreeMap<String, RoleUserAttributes>();
			mRoles = new TreeMap<String, ArrayList<String>>();
		}
	}
	
	public boolean listUserRoles(String login, ListRoles processor)
	throws CredentialsManagerException
	{
		if (null == mUsers.get(login))
		{
			return false;
		}
		
		if (null == processor)
		{
			return false;
		}

		if (0 == mRoles.size())
		{
			return true;
		}
		
		boolean result = false;
		
		for (String role : mRoles.keySet())
		{
			RoleUserAttributes attributes = null;
			
			synchronized (this)
			{
				attributes = mUsers.get(login);
			}
			
			if (attributes.isInRole(role))
			{
				result = true;
			
				if (!processor.foundRole(role))
				{
					break;
				}
			}
		}
		
		return result;
	}

	private void initialize()
	throws CredentialsManagerException
	{
		try
		{
			Xml2MemoryUsers xml_memoryusers = new Xml2MemoryUsers();
			xml_memoryusers.processXml(mXmlPath, mResourceFinder);
			synchronized (this)
			{
				RoleUserAttributes attributes;
				for (Map.Entry<String, RoleUserAttributes> user_entry : xml_memoryusers.getUsers().entrySet())
				{
					attributes = user_entry.getValue();
					for (String role : attributes.getRoles())
					{
						if (!containsRole(role))
						{
							addRole(role);
						}
					}
					addUser(user_entry.getKey(), attributes);
				}
			}
		}
		catch (XmlErrorException e)
		{
			throw new InitializationErrorException(mXmlPath, e);
		}
	}

	public String toXml()
	{
		StringBuilder xml_output = new StringBuilder();
		
		xml_output.append("<credentials>\n");

		SortListComparables	arraylist_sort = new SortListComparables();
		ArrayList<String>	logins_list = new ArrayList<String>(mUsers.keySet());
		RoleUserAttributes	user_attributes = null;
		
		arraylist_sort.sort(logins_list);
		for (String login : logins_list)
		{
			user_attributes = mUsers.get(login);
			xml_output.append("\t<user login=\"").append(StringUtils.encodeXml(login)).append("\"");
			if (!user_attributes.isAutomaticUserId())
			{
				xml_output.append(" userid=\"").append(user_attributes.getUserId()).append("\"");
			}
			xml_output.append(">\n");
			xml_output.append("\t\t<password>").append(StringUtils.encodeXml(user_attributes.getPassword())).append("</password>\n");
			if (user_attributes.getRoles() != null &&
				user_attributes.getRoles().size() > 0)
			{
				ArrayList<String>	roles = new ArrayList<String>(user_attributes.getRoles());
				arraylist_sort.sort(roles);
				for (String role : roles)
				{
					xml_output.append("\t\t<role>").append(StringUtils.encodeXml(role)).append("</role>\n");
				}
			}
			xml_output.append("\t</user>\n");
		}
		
		xml_output.append("</credentials>\n");

		return xml_output.toString();
	}

	public void storeToXml()
	throws CredentialsManagerException
	{
		String	xmlpath = null;
		URL		xmlpath_resource = null;
		
		xmlpath = getXmlPath();
		if (null == xmlpath)
		{
			throw new MissingXmlPathException();
		}
		
		xmlpath_resource = mResourceFinder.getResource(xmlpath);
		if (null == xmlpath_resource)
		{
			throw new CantFindXmlPathException(xmlpath);
		}
		
		storeToXml(new File(URLDecoder.decode(xmlpath_resource.getPath())));
	}

	public synchronized void storeToXml(File destination)
	throws CredentialsManagerException
	{
		if (null == destination ||
			destination.exists() &&
			!destination.canWrite())
		{
			throw new CantWriteToDestinationException(destination);
		}
		
		StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		content.append("<!DOCTYPE credentials SYSTEM \"/dtd/users.dtd\">\n");
		content.append(toXml());
		try
		{
			FileUtils.writeString(content.toString(), destination);
		}
		catch (FileUtilsErrorException e)
		{
			throw new StoreXmlErrorException(destination, e);
		}

	}
}

