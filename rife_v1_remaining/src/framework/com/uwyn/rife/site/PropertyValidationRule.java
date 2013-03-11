/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PropertyValidationRule.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.Collection;

/**
 * This abstract class extends the <code>AbstractValidationRule</code> class
 * to provide common functionality that is useful for all bean property
 * validation rules.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class PropertyValidationRule extends AbstractValidationRule
{
	private String				mPropertyName = null;
	private String				mSubject = null;
	private Collection<String>  mLoadingErrors = null;
	private ConstrainedProperty	mConstrainedProperty = null;
	
	/**
	 * Instantiates a new <code>PropertyValidationRule</code> instance.
	 * 
	 * @param propertyName the name of the property
	 * @since 1.0
	 */
	protected PropertyValidationRule(String propertyName)
	{
		setPropertyName(propertyName);
	}
	
	/**
	 * Set the name of the property.
	 * 
	 * @param propertyName the name of the property
	 * @see #getPropertyName
	 * @since 1.0
	 */
	public <T extends PropertyValidationRule> T setPropertyName(String propertyName)
	{
		mPropertyName = propertyName;
		if (null == mSubject)
		{
			mSubject = propertyName;
		}
		
		return (T)this;
	}
	
	/**
	 * Retrieves the name of the property.
	 * 
	 * @return the name of the property
	 * @see #setPropertyName
	 * @since 1.0
	 */
	public String getPropertyName()
	{
		return mPropertyName;
	}
	
	/**
	 * Set the subject that the property refers to.
	 * 
	 * @param subjectName the subject name of the property
	 * @see #getSubject
	 * @since 1.0
	 */
	public PropertyValidationRule setSubject(String subjectName)
	{
		if (null == subjectName)
		{
			mSubject = mPropertyName;
		}
		else
		{
			mSubject = subjectName;
		}
		
		return this;
	}
	
	/**
	 * Retrieves the subject name of the property.
	 * 
	 * @return the subject name of the property
	 * @see #setSubject
	 * @since 1.0
	 */
	public String getSubject()
	{
		return mSubject;
	}
	
	/**
	 * Set the list of error messages that occurred during the loading of
	 * content data.
	 * 
	 * @param errors the collection of errors messages
	 * @since 1.0
	 */
	protected void setLoadingErrors(Collection<String> errors)
	{
		mLoadingErrors = errors;
	}
	
	/**
	 * Retrieves the list of error messages that occurred during the loading
	 * of content data.
	 * 
	 * @return the collection of errors messages; or
	 * <p><code>null</code> if the data was <code>null</code> or the property
	 * didn't exist
	 * @since 1.0
	 */
	public Collection<String> getLoadingErrors()
	{
		return mLoadingErrors;
	}
	
	void setConstrainedProperty(ConstrainedProperty constrainedProperty)
	{
		mConstrainedProperty = constrainedProperty;
	}
	
	ConstrainedProperty getConstrainedProperty()
	{
		return mConstrainedProperty;
	}
}
