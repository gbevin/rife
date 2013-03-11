/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.sessionvalidators;

import com.uwyn.rife.authentication.SessionValidator;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;

/**
 * Classes that can create {@link SessionValidator} objects implement
 * this interface.
 * 
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: $
 * @see SessionValidator
 * @since 1.6
 */
public interface SessionValidatorFactory
{
	/**
	 * Returns the manager specified by properties.
	 * <p>
	 * The specific properties that are used are determined by the
	 * implementation class.
	 *
	 * @param properties the properties that will setup the manager
	 * @throws PropertyValueException when an error occurred during the retrieval
	 * of the property values
	 * @since 1.6
	 */
	public SessionValidator getValidator(HierarchicalProperties properties) throws PropertyValueException;
}
