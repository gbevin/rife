/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementChildTrigger.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

/**
 * This interface contains all the methods that a class must implement to be
 * able to handle child trigger events.
 * <p>Child triggers are setup in the site structure to drill down an element
 * inheritance hierarchy according to value changes in inputs or cookies. This
 * will happen both when values are sent by a client and when values are set
 * by an element.
 * <p>By registering an instance of a <code>ElementChildTrigger</code> class,
 * it's possible to only allow the child to be activated according to certain
 * conditions. This can for instance be used to validate an authentication
 * session identifier, and only allow the child activation if the identifier
 * is valid and not expired.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see ElementSupport#setChildTrigger
 * @since 1.0
 */
public interface ElementChildTrigger
{
	/**
	 * This method will be called when a child trigger instance is registered
	 * and a value is provided for an input or cookie that is setup as a child
	 * trigger.
	 * <p>You are not allowed to access any information of the element context
	 * state besides the name and values of the child trigger variable. These
	 * are provided to you as method arguments. If you do try to access other
	 * state information, the engine will throw a {@link
	 * com.uwyn.rife.engine.exceptions.RequestAccessDeniedException}
	 * exception. This is needed to be able to record all the child triggers
	 * that occurred. The engine uses this information to replay child
	 * triggers automatically during subsequent requests and access a child
	 * element directly if all child triggers are still successful.
	 * 
	 * @param name the name of the child trigger variable
	 * @param values the values of the child trigger variable
	 * @return <code>true</code> if the child element can be executed; or
	 * <p><code>false</code> if the logic should continue in the element that
	 * activated the child trigger
	 * @since 1.0
	 */
	public boolean childTriggered(String name, String[] values);
}

