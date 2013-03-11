/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationRuleFormat.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import static com.uwyn.rife.site.ValidityChecks.checkFormat;

import java.lang.reflect.Array;
import java.text.Format;

import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

public class ValidationRuleFormat extends PropertyValidationRule
{
	private Format	mFormat = null;
	
	public ValidationRuleFormat(String propertyName, Format format)
	{
		super(propertyName);
		
		mFormat = format;
	}
	
	public boolean validate()
	{
		Object value;
		try
		{
			value = BeanUtils.getPropertyValue(getBean(), getPropertyName());
		}
		catch (BeanUtilsException e)
		{
			// an error occurred when obtaining the value of the property
			// just consider it valid to skip over it
			return true;
		}
		
		if (null == value)
		{
			return true;
		}
		
		if (value.getClass().isArray())
		{
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++)
			{
				if (!checkFormat(Array.get(value, i), mFormat))
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			return checkFormat(value, mFormat);
		}
	}
	
	public ValidationError getError()
	{
		return new ValidationError.INVALID(getSubject());
	}
}
