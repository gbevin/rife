/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Datalink.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a data link for the element.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface Datalink
{
	/**
	 * The name of this element's source output.
	 * @since 1.5
	 */
	String srcOutput() default "";

	/**
	 * The name of this element's outbean.
	 * @since 1.5
	 */
	String srcOutbean() default "";

	/**
	 * The ID of the destination element for this data link.
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
	 * Indicates whether this data link is a snapback.
	 * @since 1.5
	 */
	boolean snapback() default false;

	/**
	 * The name of the target element's destination input.
	 * @since 1.5
	 */
	String destInput() default "";

	/**
	 * The name of the target element's destination inbean.
	 * @since 1.5
	 */
	String destInbean() default "";
}
