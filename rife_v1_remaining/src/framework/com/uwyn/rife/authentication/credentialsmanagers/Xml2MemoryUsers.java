/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Xml2MemoryUsers.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.xml.Xml2Data;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.util.LinkedHashMap;
import org.xml.sax.Attributes;

public class Xml2MemoryUsers extends Xml2Data
{
	private LinkedHashMap<String, RoleUserAttributes>	mUsers = null;
	private StringBuilder								mCharacterData = null;
	private RoleUserAttributes							mCurrentAttributes = null;
	
	public LinkedHashMap<String, RoleUserAttributes> getUsers()
	{
		return mUsers;
	}

	protected void clear()
	{
		mUsers = new LinkedHashMap<String, RoleUserAttributes>();
	}
	
	public void startDocument()
	{
		clear();
	}
	
	public void endDocument()
	{
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	{
		if (qName.equals("credentials"))
		{
			// do nothing
		}
		else if (qName.equals("user"))
		{
			String login = atts.getValue("login");
			String userid = atts.getValue("userid");
			
			mCurrentAttributes = new RoleUserAttributes();
			if (userid != null &&
				userid.length() > 0)
			{
				try
				{
					mCurrentAttributes.setUserId(Integer.parseInt(userid));
				}
				catch (NumberFormatException e)
				{
					throw new XmlErrorException("Invalid userid '"+userid+"'", e);
				}
			}
			
			mUsers.put(login, mCurrentAttributes);
		}
		else if (qName.equals("password"))
		{
			mCharacterData = new StringBuilder();
		}
		else if (qName.equals("role"))
		{
			mCharacterData = new StringBuilder();
		}
		else
		{
			throw new XmlErrorException("Unsupport element name '"+qName+"'.");
		}
	}
	
	public void endElement(String namespaceURI, String localName, String qName)
	{
		if (qName.equals("password"))
		{
			String	password = mCharacterData.toString();
			mCurrentAttributes.setPassword(password);
			mCharacterData = new StringBuilder();
		}
		else if (qName.equals("role"))
		{
			String	role = mCharacterData.toString();
			mCurrentAttributes.addRole(role);
			mCharacterData = new StringBuilder();
		}
	}
	
	public void characters(char[] ch, int start, int length)
	{
		if (length > 0)
		{
			mCharacterData.append(String.copyValueOf(ch, start, length));
		}
	}
}

