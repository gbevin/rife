/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CallbacksProvider.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

/**
 * Callbacks can either be implemented directly by implementing the {@link
 * com.uwyn.rife.database.querymanagers.generic.Callbacks} interface, or they
 * can be provided by implementing this interface.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.database.querymanagers.generic.Callbacks
 * @since 1.0
 */
public interface CallbacksProvider<BeanType>
{
	/**
	 * Returns an implementation of the {@link
	 * com.uwyn.rife.database.querymanagers.generic.Callbacks} interface.
	 * 
	 * @return a callbacks instance
	 * @since 1.0
	 */
	public Callbacks<BeanType> getCallbacks();
}
