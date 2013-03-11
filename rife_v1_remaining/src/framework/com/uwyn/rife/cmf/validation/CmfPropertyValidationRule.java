/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CmfPropertyValidationRule.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.validation;

import com.uwyn.rife.site.Constrained;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.ConstrainedUtils;
import com.uwyn.rife.site.PropertyValidationRule;

/**
 * This abstract class extends the <code>PropertyValidationRule</code> class
 * to provide common functionality that is useful for all concrete CMF
 * validation rules.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class CmfPropertyValidationRule extends PropertyValidationRule
{
	private boolean	mFragment = false;

	/**
	 * Instantiates a new <code>CmfPropertyValidationRule</code> instance.
	 * 
	 * @param propertyName the name of the property
	 * @param fragment <code>true</code> if the property is a fragment; or
	 * <p><code>false</code> if it's a complete document
	 * @since 1.0
	 */
	public CmfPropertyValidationRule(String propertyName, boolean fragment)
	{
		super(propertyName);
		
		mFragment = fragment;
	}

	/**
	 * Indicates whether the property that is validated is a fragment.
	 * 
	 * @return <code>true</code> if the property is a fragment; or
	 * <p><code>false</code> if it's a complete document
	 * @since 1.0
	 */
	public boolean getFragment()
	{
		return mFragment;
	}

	/**
	 * Sets the cached loaded data to a {@link com.uwyn.rife.site.ConstrainedProperty
	 * ConstrainedProperty} if the content data has been successfully loaded during
	 * validation. This prevents the data of having to be loaded again
	 * elsewhere.
	 * <p>If the validation rule's bean is not {@link
	 * com.uwyn.rife.site.Constrained Constrained} or if it doesn't contain a
	 * corresponding <code>ConstrainedProperty</code>, this method does nothing.
	 * 
	 * @param data the loaded data
	 * @see com.uwyn.rife.site.ConstrainedProperty#setCachedLoadedData(Object)
	 * @since 1.0
	 */
	protected void setCachedLoadedData(Object data)
	{
		// if the bean is constrained and a CmfProperty exists that corresponds to
		// the property name that's being checked, store the loaded data
		// and prevent it from loading twice
		Constrained constrained = ConstrainedUtils.makeConstrainedInstance(getBean());
		if (constrained != null)
		{
			ConstrainedProperty property = constrained.getConstrainedProperty(getPropertyName());
			property.setCachedLoadedData(data);
		}
	}
}

