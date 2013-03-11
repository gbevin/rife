/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationGroup.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.uwyn.rife.site.exceptions.ValidationException;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;

public class ValidationGroup<C extends ConstrainedProperty> implements Cloneable
{
	private String					mName = null;
	private ValidatedConstrained	mValidation = null;
	private ArrayList<String>		mSubjects = null;
	private ValidationGroup			mParent = null;
	private ArrayList<String>		mPropertyNames = null;
	
	ValidationGroup(String name, Validation validation)
	{
		mName = name;
		mValidation = validation;
		mSubjects = new ArrayList<String>();
	}
	
	void setParent(ValidationGroup parent)
	{
		mParent = parent;
	}
	
	void setValidation(ValidatedConstrained validation)
	{
		mValidation = validation;
	}
	
	public void reinitializeProperties(Object bean)
	throws ValidationException
	{
		if (null == bean ||
			null == mPropertyNames ||
			0 == mPropertyNames.size())
		{
			return;
		}
		
		Object new_bean;
		try
		{
			new_bean = bean.getClass().newInstance();
		}
		catch (Throwable e)
		{
			throw new ValidationException(e);
		}
		
		String[] property_names = new String[mPropertyNames.size()];
		mPropertyNames.toArray(property_names);
		try
		{
			for (String name : BeanUtils.getPropertyNames(bean.getClass(), property_names, null, null))
			{
				BeanUtils.setPropertyValue(bean, name, BeanUtils.getPropertyValue(new_bean, name));
			}
		}
		catch (BeanUtilsException e)
		{
			throw new ValidationException(e);
		}
	}
	
	public String getName()
	{
		return mName;
	}
	
	public List<String> getPropertyNames()
	{
		return mPropertyNames;
	}
	
	public List<String> getSubjects()
	{
		return mSubjects;
	}
	
	public ValidatedConstrained getValidation()
	{
		return mValidation;
	}

	public ValidationGroup<C> addSubject(String subject)
	{
		addPropertyName(subject);

		if (mSubjects.contains(subject))
		{
			return this;
		}
		
		mSubjects.add(subject);
		
		if (mParent != null)
		{
			mParent.addSubject(subject);
		}
		
		return this;
	}
	
	private void addPropertyName(String name)
	{
		if (null == mPropertyNames)
		{
			mPropertyNames = new ArrayList<String>();
		}

		if (!mPropertyNames.contains(name))
		{
			mPropertyNames.add(name);
		}
	}

	public ValidationGroup<C> addRule(ValidationRule rule)
	{
		mValidation.addRule(rule);
		addSubject(rule.getSubject());

		return this;
	}
	
	public ValidationGroup<C> addConstraint(C constrainedProperty)
	{
		addPropertyName(constrainedProperty.getPropertyName());
			
		List<PropertyValidationRule> rules = mValidation.addConstrainedPropertyRules(constrainedProperty);
		for (ValidationRule rule : rules)
		{
			addSubject(rule.getSubject());
		}

		return this;
	}

	public ValidationGroup<C> addGroup(String name)
	{
		ValidationGroup<C> group = mValidation.addGroup(name);
		group.setParent(this);
		return group;
	}
	
	public ValidationGroup<C> clone()
	{
		ValidationGroup<C> new_validationgroup = null;
		try
		{
			new_validationgroup = (ValidationGroup<C>)super.clone();

			if (mSubjects != null)
			{
				new_validationgroup.mSubjects = new ArrayList<String>(mSubjects);
			}
			if (mPropertyNames != null)
			{
				new_validationgroup.mPropertyNames = new ArrayList<String>(mPropertyNames);
			}
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.site").severe(ExceptionUtils.getExceptionStackTrace(e));
			///CLOVER:ON
		}

		return new_validationgroup;
	}
}
