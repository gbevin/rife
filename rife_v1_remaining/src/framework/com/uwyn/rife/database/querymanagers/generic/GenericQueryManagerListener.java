/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: GenericQueryManagerListener.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

/**
 * The methods of a <code>GenericQueryManagerListener</code> will be executed
 * as the corresponding actions are successfully executed through the
 * <code>GenericQueryManager</code> that this listener is registered with.
 * <p>The difference with <code>Callbacks</code> is that listeners are
 * associated with a <code>GenericQueryManager</code> and
 * <code>Callbacks</code> are associated with your domain model. Listeners
 * are also only called as a notification mechanisms, they don't allow you to
 * intervene in the execution flow. Listeners are called before 'after'
 * callbacks.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see
 * com.uwyn.rife.database.querymanagers.generic.GenericQueryManager
 * @see com.uwyn.rife.database.querymanagers.generic.Callbacks
 * @since 1.5
 */
public interface GenericQueryManagerListener<BeanType>
{
	/**
	 * Executed when the database structure has been successfully installed.
	 *
	 * @since 1.5
	 */
	void installed();
	
	/**
	 * Executed when the database structure has been successfully removed.
	 *
	 * @since 1.5
	 */
	void removed();
	
	/**
	 * Executed when a bean was successfully inserted.
	 *
	 * @param bean the bean that was inserted
	 * @since 1.5
	 */
	void inserted(BeanType bean);
	
	/**
	 * Executed when a bean was successfully updated.
	 *
	 * @param bean the bean that was updated
	 * @since 1.5
	 */
	void updated(BeanType bean);
	
	/**
	 * Executed when a bean was successfully restored.
	 *
	 * @param bean the bean that was restored
	 * @since 1.5
	 */
	void restored(BeanType bean);

	/**
	 * Executed when a bean was successfully deleted.
	 *
	 * @param objectId the identifier of the bean that was deleted
	 * @since 1.5
	 */
	void deleted(int objectId);
}
