/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FlowlinkExitField.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Documented;

/**
 * Declares a flow link for the element. This annotation may only be used on
 * final String fields. The value of the field is used as the source exit name.
 *
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see Flowlink
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface FlowlinkExitField
{
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
	 * This flow link's data links.
	 * @since 1.5
	 */
    Datalink[] datalinks() default {};

	public enum Inheritance { PRESERVE, CANCEL }
	public enum Embedding { PRESERVE, CANCEL }
}
