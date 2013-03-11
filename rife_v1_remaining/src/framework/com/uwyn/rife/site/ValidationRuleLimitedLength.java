/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationRuleLimitedLength.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import java.lang.reflect.Array;

import static com.uwyn.rife.site.ValidityChecks.checkLength;

public class ValidationRuleLimitedLength extends PropertyValidationRule
{
	private int	mMin = -1;
	private int	mMax = -1;
	
	public ValidationRuleLimitedLength(String propertyName, int min, int max)
	{
		super(propertyName);

		mMin = min;
		mMax = max;
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
		
		ConstrainedProperty constrained_property = ConstrainedUtils.getConstrainedProperty(getBean(), getPropertyName());
		if (value.getClass().isArray())
		{
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++)
			{
				if (!checkLength(BeanUtils.formatPropertyValue(Array.get(value, i), constrained_property), mMin, mMax))
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			return checkLength(BeanUtils.formatPropertyValue(value, constrained_property), mMin, mMax);
		}
	}
	
	public ValidationError getError()
	{
		return new ValidationError.WRONGLENGTH(getSubject());
	}
}
