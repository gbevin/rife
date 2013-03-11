/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationRuleInList.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import static com.uwyn.rife.site.ValidityChecks.checkInList;

import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

public class ValidationRuleInList extends PropertyValidationRule
{
	private String[]	mList = null;
	
	public ValidationRuleInList(String propertyName, String[] list)
	{
		super(propertyName);
		
		mList = list;
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
		
		return checkInList(value, mList);
	}
	
	public ValidationError getError()
	{
		return new ValidationError.INVALID(getSubject());
	}
}
