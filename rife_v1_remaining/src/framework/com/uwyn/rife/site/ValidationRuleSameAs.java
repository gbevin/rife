/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationRuleSameAs.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

import static com.uwyn.rife.site.ValidityChecks.checkEqual;

public class ValidationRuleSameAs extends PropertyValidationRule
{
	private String	mReference = null;
	
	public ValidationRuleSameAs(String propertyName, String reference)
	{
		super(propertyName);
		
		mReference = reference;
	}
	
	public boolean validate()
	{
		Object value;
		Object other;
		try
		{
			value = BeanUtils.getPropertyValue(getBean(), getPropertyName());
			other = BeanUtils.getPropertyValue(getBean(), mReference);
		}
		catch (BeanUtilsException e)
		{
			// an error occurred when obtaining the value of the property
			// just consider it valid to skip over it
			return true;
		}
		
		return checkEqual(value, other);
	}
	
	public ValidationError getError()
	{
		return new ValidationError.NOTSAMEAS(getPropertyName());
	}
}
