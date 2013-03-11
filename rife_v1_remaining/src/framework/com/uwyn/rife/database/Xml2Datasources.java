/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Xml2Datasources.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.xml.Xml2Data;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.util.HashMap;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.xml.sax.Attributes;

/**
 * This class parses an XML file to create a set of {@link Datasource}
 * objects.
 * <p>An example of the parsable datasource file:
 * <pre>&lt;datasources&gt;
 *  &lt;datasource name="postgres"&gt;
 *    &lt;driver&gt;org.postgresql.Driver&lt;/driver&gt;
 *    &lt;url&gt;jdbc:postgres://localhost/database&lt;/url&gt;
 *    &lt;user&gt;username&lt;/user&gt;
 *    &lt;password&gt;password&lt;/user&gt;
 *    &lt;poolsize&gt;5&lt;/poolsize&gt;
 *  &lt;/datasource&gt;
 *&lt;/datasources&gt;</pre>
 * <p>An explaination of terms:
 * <ul>
 * <li>datasource name: used to uniquely identify the datasource
 * <li>driver: the classname of the driver to use to connect
 * <li>url: the connection url used to connect
 * <li>user: the username needed for authentication
 * <li>password: the password needed for authentication
 * <li>poolsize: the number of connections to always keep connected
 * </ul>
 * Multiple datasource definitions are supported and can be used at anytime.
 *
 * @author JR Boyens (jboyens[remove] at uwyn dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class Xml2Datasources extends Xml2Data
{
	private Datasource					mDatasource = null;
	private HashMap<String, Datasource>	mDatasources = null;
	private String						mNameAttribute = null;
	private StringBuilder				mCharacterData = null;

	/**
	 * Return the created datasources.
	 *
	 * @return the datasources created after parsing
	 * @since 1.0
	 */
	public HashMap<String, Datasource> getDatasources()
	{
		return mDatasources;
	}

	/**
	 * Clears the information in this datasource.
	 *
	 * @since 1.0
	 */
	protected void clear()
	{
		mDatasource = null;
		mDatasources = new HashMap<String, Datasource>();
		mNameAttribute = null;
		mCharacterData = null;
	}

	/**
	 * Called when the beginng of the document to be parsed is found
	 *
	 * @since 1.0
	 */
	public void startDocument()
	{
		clear();
	}

	/**
	 * Called when the start tag of an XML element is encountered
	 *
	 * @param namespaceURI the URI of the namespace of the start tag
	 * @param localName the local name of the starting element
	 * @param qName the qualified name of the starting element
	 * @param atts the attributes of the starting element
	 * @since 1.0
	 */
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	{
		if (qName.equals("datasource"))
		{
			mNameAttribute = atts.getValue("name");

			mDatasource = new Datasource();
		}
		else if (qName.equals("driver") ||
				 qName.equals("url") ||
				 qName.equals("user") ||
				 qName.equals("password") ||
				 qName.equals("poolsize") ||
				 qName.equals("jndi"))
		{
			mCharacterData = new StringBuilder();
		}
		else if (qName.equals("config"))
		{
			if (mCharacterData != null &&
				Config.hasRepInstance())
			{
				mCharacterData.append(Config.getRepInstance().getString(atts.getValue("param"), ""));
			}
		}
		else if (qName.equals("datasources"))
		{
			// do nothing
		}
		else
		{
			throw new XmlErrorException("Unsupport element name '"+qName+"'.");
		}
	}

	/**
	 * Called when the end tag of an XML element is encountered
	 *
	 * @param namespaceURI the URI of the namespace of the ending element
	 * @param localName the local name of the ending element
	 * @param qName the qualified name of the ending element
	 * @since 1.0
	 */
	public void endElement(String namespaceURI, String localName, String qName)
	{
		if (qName.equals("datasource"))
		{
			mDatasources.put(mNameAttribute, mDatasource);
		}
		else if (qName.equals("driver"))
		{
			mDatasource.setDriver(mCharacterData.toString());
		}
		else if (qName.equals("url"))
		{
			mDatasource.setUrl(mCharacterData.toString());
		}
		else if (qName.equals("user"))
		{
			mDatasource.setUser(mCharacterData.toString());
		}
		else if (qName.equals("password"))
		{
			mDatasource.setPassword(mCharacterData.toString());
		}
		else if (qName.equals("jndi"))
		{
			Context ctx;
			try
			{
				ctx = new InitialContext();

				Object ds = ctx.lookup(mCharacterData.toString());
				if (null == ds)
				{
					throw new XmlErrorException("The '"+mCharacterData.toString()+"' JNDI entry returned 'null'.");
				}
				if (!(ds instanceof DataSource))
				{
					throw new XmlErrorException("The '"+mCharacterData.toString()+"' JNDI entry isn't a DataSource.");
				}

				mDatasource.setDataSource((DataSource)ds);
			}
			catch (NamingException e)
			{
				throw new XmlErrorException("Unexpected error while looking up the '"+mCharacterData.toString()+"' JNDI entry.", e);
			}

		}
		else if (qName.equals("poolsize"))
		{
			try
			{
				mDatasource.setPoolsize(Integer.parseInt(mCharacterData.toString()));
			}
			catch (NumberFormatException e)
			{
				throw new XmlErrorException("The value of the poolsize isn't an integer.", e);
			}
		}
	}

	/**
	 * Called when text data is encountered, usually between tags.
	 *
	 * @param ch a character array of the encountered text content
	 * @param start the index in the array at which the content starts
	 * @param length the length of the data stored in the character array
	 * @since 1.0
	 */
	public void characters(char[] ch, int start, int length)
	{
		if (length > 0)
		{
			mCharacterData.append(String.copyValueOf(ch, start, length));
		}
	}
}

