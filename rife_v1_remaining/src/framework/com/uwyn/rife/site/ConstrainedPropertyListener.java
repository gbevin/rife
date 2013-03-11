/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ConstrainedPropertyListener.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

/**
 * Listeners that implement this interface will be notified when changes
 * occur to the {@code ConstrainedProperty} instances that it has been
 * added to.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see ConstrainedProperty#addListener
 * @see ConstrainedProperty#removeListener
 * @version $Revision: 3918 $
 * @since 1.6
 */
public interface ConstrainedPropertyListener
{
	/**
	 * This method is called on a registered listener when a constraint has been
	 * set to a particular value.
	 *
	 * @param property the {@code ConstrainedProperty} instance where the
	 * constraint has been set
	 * @param name the name of the constraint
	 * @param constraintData the data that the constraint has been set to
	 * @since 1.6
	 */
	public void constraintSet(ConstrainedProperty property, String name, Object constraintData);
}
