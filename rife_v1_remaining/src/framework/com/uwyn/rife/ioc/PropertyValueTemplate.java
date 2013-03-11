/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PropertyValueTemplate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.ioc;

import com.uwyn.rife.ioc.PropertyValue;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.ioc.exceptions.TemplateFactoryUnknownException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.TemplateFactory;

/**
 * Retrieves a property value as template instance of a particular type.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class PropertyValueTemplate implements PropertyValue
{
	private String  mType = null;
	private String  mName = null;
	
	/**
	 * The constructor that stores the retrieval parameters.
	 * The template type will be set to "enginehtml"
	 * 
	 * @param name the template name
	 * @since 1.4
	 */
	public PropertyValueTemplate(String name)
	{
		this(null, name);
	}
	
	/**
	 * The constructor that stores the retrieval parameters.
	 * 
	 * @param type the template factory type; if this argument is <code>null</code>
	 * the template type will be "enginehtml"
	 * @param name the template name
	 * @since 1.0
	 */
	public PropertyValueTemplate(String type, String name)
	{
		if (null == type)
		{
			type= "enginehtml";
		}
		mType = type;
		mName = name;
	}
	
	public Template getValue()
	throws PropertyValueException
	{
		TemplateFactory factory = TemplateFactory.getFactory(mType);
		if (null == factory)
		{
			throw new TemplateFactoryUnknownException(mType);
		}
		return factory.get(mName);
	}
	
	public String getValueString()
	throws PropertyValueException
	{
		return getValue().getContent();
	}
	
	public String toString()
	{
		return getValueString();
	}
	
	public boolean isNeglectable()
	{
		return false;
	}
	
	public boolean isStatic()
	{
		return false;
	}
}
