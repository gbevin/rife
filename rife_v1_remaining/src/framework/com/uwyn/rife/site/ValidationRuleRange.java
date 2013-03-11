/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationRuleRange.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

import static com.uwyn.rife.site.ValidityChecks.checkRange;

public class ValidationRuleRange extends PropertyValidationRule
{
	private Comparable	mBegin = null;
	private Comparable	mEnd = null;
	
	public ValidationRuleRange(String propertyName, Comparable begin, Comparable end)
	{
		super(propertyName);
		
		mBegin = begin;
		mEnd = end;
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
		
		return checkRange(value, mBegin, mEnd);
	}
	
	public ValidationError getError()
	{
		return new ValidationError.INVALID(getSubject());
	}
}
