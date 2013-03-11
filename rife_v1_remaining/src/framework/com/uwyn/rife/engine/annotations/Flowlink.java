/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Flowlink.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a flow link for the element.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface Flowlink
{
	/**
	 * The name of the exit in the source element that this flow link will
	 * be connected to.
	 * <p>If no such exit exists, it will be created automatically.
	 * @since 1.5
	 */
	String srcExit();

	/**
	 * The ID of the destination element for this flow link.
	 * <p>If <code>destClass</code> is provided, it will override the
	 * <code>destId</code> value.
	 * @see #destClass
	 * @since 1.5
	 */
	String destId() default "";
	
	/**
	 * The Java class of the destination element for this data link. This
	 * class should at least have an {@link Elem} annotation.
	 * <p>If <code>destClass</code> is provided, it will override the
	 * <code>destId</code> value.
	 * <p>The ID will be evaluated locally to the current subsite. If you
	 * have to refer to an ID in another subsite, you have to use the 
	 * {@link #destClassIdPrefix}.
	 * @see #destId
	 * @see #destClassIdPrefix
	 * @since 1.5
	 */
	Class destClass() default void.class;
	
	/**
	 * The prefix that will be added to the <code>destClass</code> ID.
	 * <p>This makes it possible to refer to an ID in another subsite.
	 * Note that this prefix is not validated individually, it is merely added
	 * as a string to build the final ID that will be used.
	 * @see #destClass
	 * @since 1.5
	 */
	String destClassIdPrefix() default "";
	
	/**
	 * Indicates whether this flow link is a snapback.
	 * @since 1.5
	 */
	boolean snapback() default false;

	/**
	 * Indicates whether this flow link will redirect to a dedicated URL
	 * instead of executing the destination element directly.
	 * @since 1.5
	 */
	boolean redirect() default false;

	/**
	 * Indicates whether the behavioral inheritance (3D flow) should be
	 * preserved or cancelled when this flow link is followed.
	 * @since 1.5
	 */
	Inheritance inheritance() default Inheritance.PRESERVE;

	/**
	 * Indicates whether element embedded should be preserved or cancelled
	 * when this flow link is followed.
	 * @since 1.5
	 */
	Embedding embedding() default Embedding.PRESERVE;
	
	/**
	 * Indicates whether the continuations should be
	 * preserved or cancelled when this submission is sent.
	 * @since 1.6
	 */
	Continuations continuations() default Continuations.PRESERVE;	
	
	/**
	 * This flow link's data links.
	 * @since 1.5
	 */
    Datalink[] datalinks() default {};

	public enum Inheritance { PRESERVE, CANCEL }
	public enum Embedding { PRESERVE, CANCEL }
	public enum Continuations { PRESERVE, CANCEL }
}
