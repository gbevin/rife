/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: VirtualParametersHandler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

/**
 * Internal interface that defines the methods that a
 * <code>VirtualParameters</code> handler has to support.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface VirtualParametersHandler
{
	/**
	 * To whatever is needed according to the virtual parameters that have
	 * been defined in a prepared statement before execution.
	 * 
	 * @param statement the prepared statement that has all the virtual
	 * parameters defined.
	 * @since 1.0
	 */
	public void handleValues(DbPreparedStatement statement);
}
