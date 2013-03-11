/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementInitializer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

/**
 * This interface contains all the methods that a class must implement to be
 * able to handle the initialization of an element.
 * <p>The initialization of an element is performed for each element instance
 * as the first action in a fully setup context. This is handy to perform
 * initialization logic that's common for the {@link
 * ElementAware#processElement} method and the dynamic
 * <code>doSubmissionName</code> methods. More information can be found about
 * this in the description of the {@link ElementAware} class.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see ElementSupport#setInitializer
 * @see ElementAware
 * @since 1.0
 */
public interface ElementInitializer
{
	/**
	 * This method will be called to perform the actual initialization.
	 * 
	 * @since 1.0
	 */
	public void initialize();
}

