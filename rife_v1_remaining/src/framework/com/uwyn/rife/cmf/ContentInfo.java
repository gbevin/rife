/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentInfo.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;
import com.uwyn.rife.tools.Localization;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Map;

/**
 * This class represents all the information that is stored in the backend
 * about a certain {@link com.uwyn.rife.cmf.Content Content} instance.
 * <p>The setters of this class are only present to make it possible for the
 * back-ends to automatically populate the information.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class ContentInfo extends Validation
{
	private String              mPath = null;
	private int                 mVersion = -1;
	private Timestamp           mCreated = null;
	private String              mMimeType = null;
	private boolean             mFragment = false;
	private String              mName = null;
	private Map<String, String> mAttributes = null;
	private int                 mSize = -1;
	private Map<String, String> mProperties = null;
	
	/**
	 * Instantiates a new <code>ContentInfo</code> instance.
	 */
	public ContentInfo()
	{
	}
	
	public void activateValidation()
	{
		addConstraint(new ConstrainedProperty("path")
					  .notNull(true)
					  .notEmpty(true)
					  .maxLength(255));
		addConstraint(new ConstrainedProperty("mimeType")
					  .notNull(true)
					  .notEmpty(true)
					  .maxLength(80));
		addConstraint(new ConstrainedProperty("version")
					  .notNull(true)
					  .rangeBegin(0)
					  .saved(false));
		addConstraint(new ConstrainedProperty("name")
					  .maxLength(100));
		addConstraint(new ConstrainedProperty("created")
					  .notNull(true)
					  .saved(false));
		addConstraint(new ConstrainedProperty("attributes")
					  .persistent(false));
		addConstraint(new ConstrainedProperty("size")
					  .persistent(false));
		addConstraint(new ConstrainedProperty("properties")
					  .persistent(false));
	}
	
	/**
	 * Sets the path of the stored <code>Content</code> instance.
	 * <p>The path has to be unique and will be used to retrieve this
	 * particular <code>Content</code>.
	 *
	 * @param path the absolute and unique path
	 * @see #getPath()
	 * @since 1.0
	 */
	public void setPath(String path)
	{
		mPath = path;
	}
	
	/**
	 * Retrieves the path of the stored <code>Content</code> instance.
	 *
	 * @return the <code>Content</code>'s path
	 * @see #setPath(String)
	 * @see #getOptimalPath()
	 * @since 1.0
	 */
	public String getPath()
	{
		return mPath;
	}
	
	/**
	 * Retrieves the path of the stored <code>Content</code> instance in the
	 * most optimal form for usage in the cmf.
	 *
	 * @return the <code>Content</code>'s most optimal path
	 * @see #getPath()
	 * @since 1.0
	 */
	public String getOptimalPath()
	{
		if (null == mPath)
		{
			return null;
		}
		
		if (null == mName)
		{
			return mPath;
		}
		
		StringBuilder result = new StringBuilder(mPath);
		result.append("/");
		result.append(mName);
		return result.toString();
	}
	
	/**
	 * Sets the version of the stored <code>Content</code> instance.
	 * <p>Version numbers are unique and should be increased successively when
	 * the data on a certain <code>Content</code> is updated.
	 * <p>The path and the version together identify exactly one particular
	 * <code>Content</code> with one particular data.
	 *
	 * @param version the version as a unique integer
	 * @see #getVersion()
	 * @since 1.0
	 */
	public void setVersion(int version)
	{
		mVersion = version;
	}
	
	/**
	 * Retrieves the version of the stored <code>Content</code> instance.
	 *
	 * @return the <code>Content</code>'s version
	 * @see #setVersion(int)
	 * @since 1.0
	 */
	public int getVersion()
	{
		return mVersion;
	}
	
	/**
	 * Sets the mime type of the stored <code>Content</code> instance.
	 *
	 * @param mimeType the <code>String</code> that identifies the mime type
	 * @see #getMimeType()
	 * @since 1.0
	 */
	public void setMimeType(String mimeType)
	{
		mMimeType = mimeType;
	}
	
	/**
	 * Retrieves the mime type of the stored <code>Content</code> instance.
	 *
	 * @return the <code>Content</code>'s mime type textual identifier
	 * @see #setMimeType(String)
	 * @since 1.0
	 */
	public String getMimeType()
	{
		return mMimeType;
	}
	
	/**
	 * Sets wether the stored <code>Content</code> instance is a fragment or
	 * not.
	 *
	 * @param fragment <code>true</code> if it's a fragment; or
	 * <p><code>false</code> otherwise
	 * @see #isFragment()
	 * @since 1.0
	 */
	public void setFragment(boolean fragment)
	{
		mFragment = fragment;
	}
	
	/**
	 * Retrieves wether the stored <code>Content</code> instance is a fragment
	 * or not.
	 *
	 * @return <code>true</code> if it's a fragment; or
	 * <p><code>false</code> otherwise
	 * @see #setFragment(boolean)
	 * @since 1.0
	 */
	public boolean isFragment()
	{
		return mFragment;
	}
	
	/**
	 * Sets the name of the stored <code>Content</code> instance.
	 *
	 * @param name the name
	 * @see #getName()
	 * @see #hasName()
	 * @since 1.0
	 */
	public void setName(String name)
	{
		mName = name;
	}
	
	/**
	 * Retrieves the name of the stored <code>Content</code> instance.
	 *
	 * @return <code>null</code> if the stored <code>Content</code> instance
	 * has no name; or
	 * <p>the name of the content
	 * @see #setName(String)
	 * @see #hasName()
	 * @since 1.0
	 */
	public String getName()
	{
		return mName;
	}
	
	/**
	 * Indicates whether the stored <code>Content</code> instance has a name.
	 *
	 * @return <code>true</code> if it has a name; or
	 * <p><code>false</code> otherwise
	 * @see #setName(String)
	 * @see #getName()
	 * @since 1.0
	 */
	public boolean hasName()
	{
		return mName != null;
	}
	
	/**
	 * Sets the moment when the <code>Content</code> instance was stored.
	 *
	 * @param created the moment of creation
	 * @see #getCreated()
	 * @since 1.0
	 */
	public void setCreated(Timestamp created)
	{
		mCreated = created;
	}
	
	/**
	 * Retrieves the moment when the <code>Content</code> instance was stored.
	 *
	 * @return the moment of creation
	 * @see #setCreated(Timestamp)
	 * @since 1.0
	 */
	public Timestamp getCreated()
	{
		return mCreated;
	}
	
	/**
	 * Sets the attributes map of the stored <code>Content</code> instance.
	 *
	 * @param attributes the attributes map with <code>String</code> keys and
	 * value.
	 * @see #getAttributes()
	 * @since 1.0
	 */
	public void setAttributes(Map<String, String> attributes)
	{
		mAttributes = attributes;
	}
	
	/**
	 * Retrieves the attributes map of the stored <code>Content</code>
	 * instance.
	 *
	 * @return the attributes map
	 * @see #setAttributes(Map)
	 * @since 1.0
	 */
	public Map<String, String> getAttributes()
	{
		return mAttributes;
	}
	
	/**
	 * Indicates whether named content attributes are present.
	 *
	 * @return <code>true</code> if named content attributes are present; or
	 * <p><code>false</code> otherwise
	 * @see #setAttributes(Map)
	 * @see #getAttributes()
	 * @since 1.0
	 */
	public boolean hasAttributes()
	{
		return mAttributes != null && mAttributes.size() > 0;
	}
	
	/**
	 * Indicates whether a specific named content attribute is present.
	 *
	 * @param name the name of the attribute
	 * @return <code>true</code> if the name content attribute is present; or
	 * <p><code>false</code> otherwise
	 * @see #getAttribute(String)
	 * @since 1.0
	 */
	public boolean hasAttribute(String name)
	{
		if (null == mAttributes)
		{
			return false;
		}
		
		return mAttributes.containsKey(name);
	}
	
	/**
	 * Retrieves the value of a named content attribute.
	 *
	 * @param name the name of the attribute
	 * @return the value of the named content attribute; or
	 * <p><code>null</code> if no such attribute could be found
	 * @see #hasAttribute(String)
	 * @since 1.0
	 */
	public String getAttribute(String name)
	{
		if (null == mAttributes)
		{
			return null;
		}
		
		return mAttributes.get(name);
	}
	
	/**
	 * Sets the size of the stored <code>Content</code> instance.
	 *
	 * @param size the size of the cotent data
	 * @see #getSize()
	 * @since 1.0
	 */
	public void setSize(int size)
	{
		mSize = size;
	}
	
	/**
	 * Retrieves the size of the stored <code>Content</code> instance.
	 *
	 * @return the <code>Content</code>'s size
	 * @see #setSize(int)
	 * @see #getFormattedSize()
	 * @since 1.0
	 */
	public int getSize()
	{
		return mSize;
	}
	
	/**
	 * Retrieves the size of the stored <code>Content</code> instance as a
	 * formatted string.
	 *
	 * @return the <code>Content</code>'s formatted size
	 * @see #getSize()
	 * @since 1.0
	 */
	public String getFormattedSize()
	{
		NumberFormat format = NumberFormat.getNumberInstance(Localization.getLocale());
		double size = getSize();
		double size_kb = size / 1024;
		if (size_kb >= 1024)
		{
			double size_mb = size_kb / 1024;
			if (size_mb >= 1024)
			{
				double size_gb = size_mb / 1024;
				format.setMaximumFractionDigits(2);
				return format.format(size_gb) + "GB";
			}
			else
			{
				format.setMaximumFractionDigits(2);
				return format.format(size_mb) + "MB";
			}
		}
		else
		{
			if (size_kb >= 100)
			{
				format.setMaximumFractionDigits(0);
			}
			else
			{
				format.setMaximumFractionDigits(2);
			}
			return format.format(size_kb) + "KB";
		}
	}
	
	/**
	 * Sets the content data properties of the stored <code>Content</code>
	 * instance.
	 *
	 * @param properties the content data properties
	 * @see #hasProperties()
	 * @see #getProperties()
	 * @since 1.0
	 */
	public void setProperties(Map<String, String> properties)
	{
		mProperties = properties;
	}
	
	/**
	 * Indicates whether content data properties are present for the stored
	 * <code>Content</code> instance.
	 *
	 * @return <code>true</code> if properties are present; or
	 * <p><code>false</code> otherwise
	 * @see #setProperties(Map)
	 * @see #getProperties()
	 * @since 1.0
	 */
	public boolean hasProperties()
	{
		return mProperties != null && mProperties.size() > 0;
	}
	
	/**
	 * Indicates whether a specific named content property is present.
	 *
	 * @param name the name of the property
	 * @return <code>true</code> if the name content property is present; or
	 * <p><code>false</code> otherwise
	 * @see #getProperty(String)
	 * @since 1.0
	 */
	public boolean hasProperty(String name)
	{
		if (null == mProperties)
		{
			return false;
		}
		
		return mProperties.containsKey(name);
	}
	
	/**
	 * Retrieves the value of a named content property.
	 *
	 * @param name the name of the property
	 * @return the value of the named content property; or
	 * <p><code>null</code> if no such property could be found
	 * @see #hasProperty(String)
	 * @since 1.0
	 */
	public String getProperty(String name)
	{
		if (null == mProperties)
		{
			return null;
		}
		
		return mProperties.get(name);
	}
	
	/**
	 * Retrieves the content data properties of the stored
	 * <code>Content</code> instance.
	 *
	 * @return the content data properties; or
	 * <p><code>null</code> if no content data properties are present
	 * @see #setProperties(Map)
	 * @see #hasProperties()
	 * @since 1.0
	 */
	public Map<String, String> getProperties()
	{
		return mProperties;
	}
}
