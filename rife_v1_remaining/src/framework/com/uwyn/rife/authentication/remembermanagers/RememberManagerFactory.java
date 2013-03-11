/*
 * Copyright 2001-2008 Steven Grimm <koreth[remove] at midwinter dot com> and
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id$
 */
package com.uwyn.rife.authentication.remembermanagers;

import com.uwyn.rife.authentication.RememberManager;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;

/**
 * Classes that generate {@link RememberManager} objects implement this interface.
 *
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: $
 * @see RememberManager
 * @since 1.6
 */
public interface RememberManagerFactory
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
	public RememberManager getRememberManager(HierarchicalProperties properties) throws PropertyValueException;
}
