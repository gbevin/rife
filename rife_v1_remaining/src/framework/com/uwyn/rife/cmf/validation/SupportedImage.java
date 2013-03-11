/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SupportedImage.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.validation;

import com.uwyn.rife.cmf.loader.ImageContentLoader;
import com.uwyn.rife.site.ValidationError;
import com.uwyn.rife.tools.BeanUtils;
import com.uwyn.rife.tools.exceptions.BeanUtilsException;
import java.awt.Image;
import java.util.HashSet;
import java.util.Set;

/**
 * A validation rule that checks if the data in a property is able to be
 * loaded as a supported image format without errors.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class SupportedImage extends CmfPropertyValidationRule
{
	/**
	 * Creates a new <code>SupportedImage</code> instance.
	 * 
	 * @param propertyName the name of the property that has to be validated
	 * @since 1.0
	 */
	public SupportedImage(String propertyName)
	{
		super(propertyName, false);
	}
	
	public boolean validate()
	{
		Object value = null;
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
		
		// try to load the data in the property and if that's not possible, the data
		// is considered invalid
		Set<String> errors = new HashSet<String>();
		Image data = new ImageContentLoader().load(value, getFragment(), errors);
		if (errors.size() > 0)
		{
			setLoadingErrors(errors);
		}
		if (null == data)
		{
			return false;
		}
		
		setCachedLoadedData(data);
		
		return true;
	}

	public ValidationError getError()
	{
		return new ValidationError.INVALID(getSubject());
	}
}
