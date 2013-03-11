/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Datasources.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.*;

import com.uwyn.rife.rep.Participant;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.selector.XmlSelectorResolver;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.SortListComparables;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import com.uwyn.rife.xml.exceptions.XmlErrorException;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Contains a collection of <code>Datasource</code> instances that have been
 * created from an XML definition. A <code>Datasources</code> instance can
 * either be created by calling the public constructor or by executing the
 * <code>ParticipantDatasources</code> which participates in the
 * application-wide repository.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.database.Datasource
 * @see com.uwyn.rife.database.Xml2Datasources
 * @see com.uwyn.rife.rep.Rep
 * @see com.uwyn.rife.rep.participants.ParticipantDatasources
 * @since 1.0
 */
public class Datasources
{
	public static final String	DEFAULT_PARTICIPANT_NAME = "ParticipantDatasources";
	
	private HashMap<String, Datasource>	mDatasources = null;
	private String			            mXmlPath = null;
	private ResourceFinder	            mResourceFinder = null;

	/**
	 * Creates a new empty <code>Datasources</code> instance.
	 *
	 * @since 1.0
	 */
	public Datasources()
	throws DatasourcesException
	{
		mDatasources = new HashMap<String, Datasource>();
	}
	
	/**
	 * Creates a new <code>Datasources</code> instance from the definitions in
	 * an XML file.
	 *
	 * @param xmlPath the path of the XML resource that will be used for the
	 * population
	 * @param resourceFinder a <code>ResourceFinder</code> instance that will be
	 * used to find the file that corresponds to the provided
	 * <code>xmlPath</code>
	 *
	 * @throws DatasourcesException when an exception occured during the
	 * obtainance of the resource's modification time or during the processing
	 * of the XML file
	 *
	 * @since 1.0
	 */
	public Datasources(String xmlPath, ResourceFinder resourceFinder)
	throws DatasourcesException, DatasourceNotFoundException
	{
		if (null == xmlPath)		throw new IllegalArgumentException("xmlPath can't be null.");
		if (0 == xmlPath.length())	throw new IllegalArgumentException("xmlPath can't be empty.");
		if (null == resourceFinder)	throw new IllegalArgumentException("resourceFinder can't be null.");
		
		mResourceFinder = resourceFinder;
		
		String datasource_resolved = XmlSelectorResolver.resolve(xmlPath, mResourceFinder, "rep/datasources-");
		if( null == datasource_resolved )
		{
			throw new DatasourceNotFoundException(xmlPath);
		}
		
		URL datasource_resource = mResourceFinder.getResource(datasource_resolved);
		if( null == datasource_resource )
		{
			throw new DatasourceNotFoundException(xmlPath, datasource_resolved);
		}

		mXmlPath = datasource_resolved;
		
		initialize();

		assert mResourceFinder != null;
		assert mXmlPath != null;
		assert mXmlPath.length() > 0;
	}

	/**
	 * Checks if a <code>ParticipantDatasources</code> participant has been
	 * initialized and is available from the application-wide repository.
	 *
	 * @return <code>true</code> if this participant is available; or
	 * <p>
	 * <code>false</code> otherwise
	 *
	 * @see #getRepInstance()
	 *
	 * @since 1.0
	 */
    public static boolean hasRepInstance()
    {
        return Rep.hasParticipant(DEFAULT_PARTICIPANT_NAME);
    }

	/**
	 * Retrieves the <code>Datasources</code> instance that is initialized by
	 * the <code>ParticipantDatasources</code> participant in the
	 * application-wide repository.
	 *
	 * @return the requested <code>Datasources</code> instances; or
	 * <p>
	 * <code>null</code> if the <code>ParticipantDatasources</code> couldn't be
	 * found
	 *
	 * @see #hasRepInstance()
	 *
	 * @since 1.0
	 */
    public static Datasources getRepInstance()
    {
		Participant	participant = Rep.getParticipant(DEFAULT_PARTICIPANT_NAME);
		if (null == participant)
		{
			return null;
		}
		
        return (Datasources)participant.getObject();
    }

	/**
	 * Retrieves the <code>Datasource</code> that corresponds to a provided
	 * name.
	 *
	 * @param name a <code>String</code> that identifies the
	 * <code>Datasource</code> that has to be retrieved
	 *
	 * @return the requested <code>Datasource</code> instance; or
	 * <p>
	 * <code>null</code> if name isn't known
	 *
	 * @since 1.0
	 */
	public Datasource getDatasource(String name)
	{
		return mDatasources.get(name);
	}

	/**
	 * Stores a <code>Datasource</code> with a provided name to be able to
	 * reference it later.
	 *
	 * @param name a <code>String</code> that identifies the
	 * <code>Datasource</code>
	 * @param datasource the <code>Datasource</code> instance that has to be
	 * stored
	 *
	 * @since 1.0
	 */
	public void setDatasource(String name, Datasource datasource)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (0 == name.length())	throw new IllegalArgumentException("name can't be empty.");
		if (null == datasource)	throw new IllegalArgumentException("datasource can't be null.");
		
		mDatasources.put(name, datasource);
	}

	/**
	 * Retrieves a collection of all the <code>Datasource</code> names that are
	 * known by this <code>Datasources</code> instance.
	 *
	 * @return the requested <code>Collection</code>
	 *
	 * @since 1.0
	 */
    public Collection<String> getDatasourceNames()
	{
		return mDatasources.keySet();
	}

	/**
	 * Retrieves the path of the XML document that populated this
	 * <code>DataSources</code> instance
	 *
	 * @return the path of the XML document that populated this
	 * <code>DataSources</code> instance
	 *
	 * @since 1.0
	 */
	public String getXmlPath()
	{
		return mXmlPath;
	}

	/**
	 * Creates an XML document with all the data in the current
	 * <code>Datasources</code> instance
	 *
	 * @return the constructed XML document as a <code>String</code>
	 *
	 * @since 1.0
	 */
	public String toXml()
	{
		StringBuilder xml_output = new StringBuilder();

		xml_output.append("<datasources>\n");

		ArrayList<String> datasource_keys_arraylist = new ArrayList<String>();
		for (String datasource_key : mDatasources.keySet())
		{
			datasource_keys_arraylist.add(datasource_key);
		}

		(new SortListComparables()).sort(datasource_keys_arraylist);

		Datasource datasource = null;
		for (String datasource_key : datasource_keys_arraylist)
		{
			datasource = mDatasources.get(datasource_key);

			xml_output.append("\t<datasource name=\"").append(StringUtils.encodeXml(datasource_key)).append("\">\n");
			xml_output.append("\t\t<driver>").append(StringUtils.encodeXml(datasource.getDriver())).append("</driver>\n");
			xml_output.append("\t\t<url>").append(StringUtils.encodeXml(datasource.getUrl())).append("</url>\n");
			if (null != datasource.getUser())
			{
				xml_output.append("\t\t<user>").append(StringUtils.encodeXml(datasource.getUser())).append("</user>\n");
			}
			if (null != datasource.getPassword())
			{
				xml_output.append("\t\t<password>").append(StringUtils.encodeXml(datasource.getPassword())).append("</password>\n");
			}
			if (datasource.getPoolsize() > 0)
			{
				xml_output.append("\t\t<poolsize>").append(datasource.getPoolsize()).append("</poolsize>\n");
			}
			xml_output.append("\t</datasource>\n");
		}

		xml_output.append("</datasources>\n");

		assert xml_output.length() > 0;

		return xml_output.toString();
	}

	/**
	 * Performs the initialization logic.
	 *
	 * @throws DatasourcesException when an error occurred during the
	 * initialization
	 *
	 * @since 1.0
	 */
	private void initialize()
	throws DatasourcesException
	{
		try
		{
			Xml2Datasources	xml_datasources = new Xml2Datasources();
			xml_datasources.processXml(mXmlPath, mResourceFinder);
			synchronized (this)
			{
				mDatasources = xml_datasources.getDatasources();
			}
		}
		catch (XmlErrorException e)
		{
			throw new InitializationErrorException(mXmlPath, e);
		}

		assert mDatasources != null;
	}

	/**
	 * Stores the XML document with all the data in the current
	 * <code>Datasources</code> instance to the same file that populated
	 * this instance.
	 *
	 * @throws DatasourcesException when an error occurred during the
	 * storage
	 *
	 * @since 1.0
	 */
	public void storeToXml()
	throws DatasourcesException
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

	/**
	 * Stores the XML document with all the data in the current
	 * <code>Datasources</code> instance to the provided file.
	 *
	 * @param destination the <code>File</code> in which the data will be stored
	
	 * @throws DatasourcesException when an error occurred during the
	 * storage
	 */
	public synchronized void storeToXml(File destination)
	throws DatasourcesException
	{
		if (null == destination)	throw new IllegalArgumentException("destination can't be null");
		
		if (destination.exists() &&
			!destination.canWrite())
		{
			throw new CantWriteToDestinationException(destination);
		}
		
		StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		content.append("<!DOCTYPE datasources SYSTEM \"/dtd/datasources.dtd\">\n");
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
	
	/**
	 * Cleans up all connections that have been reserved by this datasource.
	 *
	 * @throws DatabaseException when an error occured during the cleanup
	 *
	 * @since 1.0
	 */
	public void cleanup()
	throws DatabaseException
	{
		synchronized (this)
		{
			if (null == mDatasources)
			{
				return;
			}
			
			HashMap<String, Datasource> datasoures = mDatasources;
			mDatasources = null;
			
			for (Datasource datasource : datasoures.values())
			{
				datasource.cleanup();
			}
		}
	}
}
