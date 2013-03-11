/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationRuleNotEmpty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

import static com.uwyn.rife.site.ValidityChecks.checkNotEmpty;

public class ValidationRuleNotEmpty extends PropertyValidationRule
{
	public ValidationRuleNotEmpty(String propertyName)
	{
		super(propertyName);
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
		
		return checkNotEmpty(value);
	}
	
	public ValidationError getError()
	{
		return new ValidationError.MANDATORY(getSubject());
	}
}
