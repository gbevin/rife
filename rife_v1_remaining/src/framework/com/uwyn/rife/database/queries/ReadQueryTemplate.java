/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ReadQueryTemplate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.template.Template;

/**
 * An instance of <code>ReadQueryTemplate</code> will obtain a SQL from a
 * {@link Template} block. If the template is provided but no block name, 
 * the entire content of the template will be used as the SQL query.
 * 
 * <p>This allows you to write your custom SQL queries in dedicated templates,
 * to name them, and to use them together with the functionalities that are
 * provided by {@link com.uwyn.rife.database.DbQueryManager}
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ReadQueryTemplate implements ReadQuery
{
	private Template	mTemplate = null;
	private String		mBlock = null;
	
	/**
	 * Creates a new empty instance of <code>ReadQueryTemplate</code>.
	 * @since 1.6
	 */
	public ReadQueryTemplate()
	{
	}
	
	/**
	 * Creates a new instance of <code>ReadQueryTemplate</code> with the
	 * template instance whose content provides the SQL query that will be
	 * executed.
	 *
	 * @param template the template instance
	 * @since 1.6
	 */
	public ReadQueryTemplate(Template template)
	{
		setTemplate(template);
	}
	
	/**
	 * Creates a new instance of <code>ReadQueryTemplate</code> with the
	 * template instance and block name that provide the SQL that will
	 * be executed.
	 *
	 * @param template the template instance
	 * @param block the name of the template block
	 * @since 1.6
	 */
	public ReadQueryTemplate(Template template, String block)
	{
		setTemplate(template);
		setBlock(block);
	}
	
	/**
	 * Sets the template instance.
	 *
	 * @param template the template instance
	 * @return this <code>ReadQueryTemplate</code> instance.
	 * @see #setTemplate
	 * @see #getTemplate
	 * @since 1.6
	 */
	public ReadQueryTemplate template(Template template)
	{
		setTemplate(template);
		return this;
	}
	
	/**
	 * Sets the template instance.
	 *
	 * @param template the template instance
	 * @see #template
	 * @see #getTemplate
	 * @since 1.6
	 */
	public void setTemplate(Template template)
	{
		mTemplate = template;
	}
	
	/**
	 * Retrieves the template instance.
	 *
	 * @return the template instance; or
	 * <p><code>null</code> if no template instance was provided
	 * @see #template
	 * @see #setTemplate
	 * @since 1.6
	 */
	public Template getTemplate()
	{
		return mTemplate;
	}
	
	/**
	 * Sets the name of the template block.
	 *
	 * @param block the name of the template block
	 * @return this <code>ReadQueryTemplate</code> instance.
	 * @see #setBlock
	 * @see #getBlock
	 * @since 1.6
	 */
	public ReadQueryTemplate block(String block)
	{
		setBlock(block);
		return this;
	}
	
	/**
	 * Sets the name of the template block.
	 *
	 * @param block the name of the template block
	 * @see #block
	 * @see #getBlock
	 * @since 1.6
	 */
	public void setBlock(String block)
	{
		mBlock = block;
	}
	
	/**
	 * Retrieves the name of the template block.
	 *
	 * @return the name of the template block; or
	 * <p><code>null</code> if no block name was provided
	 * @see #block
	 * @see #setBlock
	 * @since 1.6
	 */
	public String getBlock()
	{
		return mBlock;
	}
	
	public void clear()
	{
		mTemplate = null;
		mBlock = null;
	}
	
	public String getSql()
	{
		if (null == mTemplate)
		{
			return null;
		}
		
		if (null == mBlock)
		{
			return mTemplate.getContent();
		}
		
		return mTemplate.getBlock(mBlock);
	}
	
	public QueryParameters getParameters()
	{
		return null;
	}
	
	public Capabilities getCapabilities()
	{
		return null;
	}
	
	public void setExcludeUnsupportedCapabilities(boolean flag)
	{
	}
}
