/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentRepository.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.Validation;

/**
 * Contains the information that's required to describe a content repository.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class ContentRepository extends Validation
{
	public final static String DEFAULT = "default";
	
	private String  mName = null;
	
	/**
	 * Instantiates a new <code>ContentRepository</code> instance.
	 * 
	 * @since 1.0
	 */
	public ContentRepository()
	{
	}
	
	public void activateValidation()
	{
		addConstraint(new ConstrainedProperty("name")
					  .notNull(true)
					  .notEmpty(true)
					  .maxLength(100)
					  .unique(true));
	}
	
	/**
	 * Sets the name of the content repository.
	 * 
	 * @param name the name
	 * @return the current <code>ContentRepository</code> instance
	 * @see #setName(String)
	 * @see #getName()
	 * @since 1.0
	 */
	public ContentRepository name(String name)
	{
		setName(name);
		
		return this;
	}
	
	/**
	 * Sets the name of the content repository.
	 * 
	 * @param name the name
	 * @see #name(String)
	 * @see #getName()
	 * @since 1.0
	 */
	public void setName(String name)
	{
		mName = name;
	}
	
	/**
	 * Retrieves the name of the content repository.
	 * 
	 * @return <code>null</code> if the stored <code>Content</code> instance
	 * has no name; or
	 * <p>the name of the content
	 * @see #name(String)
	 * @see #setName(String)
	 * @since 1.0
	 */
	public String getName()
	{
		return mName;
	}
}
