/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationRuleLimitedDate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import java.lang.reflect.Array;
import java.util.Date;

import static com.uwyn.rife.site.ValidityChecks.checkLimitedDate;

public class ValidationRuleLimitedDate extends PropertyValidationRule
{
	private Date	mMin = null;
	private Date	mMax = null;
	
	public ValidationRuleLimitedDate(String propertyName, Date min, Date max)
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
		
		if (value.getClass().isArray())
		{
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++)
			{
				if (!checkLimitedDate(Array.get(value, i), mMin, mMax))
				{
					return false;
				}
			}
			
			return true;
		}
		else
		{
			return checkLimitedDate(value, mMin, mMax);
		}
	}
	
	public ValidationError getError()
	{
		return new ValidationError.INVALID(getSubject());
	}
}
