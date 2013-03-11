/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CmfProperty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import com.uwyn.rife.site.ConstrainedProperty;

/**
 * This class extends <code>ConstrainedProperty</code> to provide additional
 * constraints that are specific for the content management framework (CMF).
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 * @deprecated As of RIFE 1.3.2, all the methods moved to the base class
 *              {@link ConstrainedProperty}
 */
public class CmfProperty extends ConstrainedProperty<CmfProperty>
{
	/**
	 * Instantiates a new <code>CmfProperty</code> instance.
	 * 
	 * @param propertyName the name of the property
	 * @since 1.0
	 * @deprecated As of RIFE 1.3.2, all the methods moved to the base class
	 *              {@link ConstrainedProperty}
	 */
	public CmfProperty(String propertyName)
	{
		super(propertyName);
	}
}
