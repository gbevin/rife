/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AutolinkExitField.java 3961 2008-07-11 11:35:59Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.*;

/**
 * Declares an auto link for the element. This annotation may only be used on
 * final String fields. The value of the field is used as the source exit name.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3961 $
 * @since 1.6.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface AutolinkExitField
{
	/**
	 * The ID of the destination element for this flow link.
	 * <p>If <code>destClass</code> is provided, it will override the
	 * <code>destId</code> value.
	 * @see #destClass
	 * @since 1.6.2
	 */
	public abstract String destId() default "";

	/**
	 * The Java class of the destination element for this data link. This
	 * class should at least have an {@link com.uwyn.rife.engine.annotations.Elem} annotation.
	 * <p>If <code>destClass</code> is provided, it will override the
	 * <code>destId</code> value.
	 * <p>The ID will be evaluated locally to the current subsite. If you
	 * have to refer to an ID in another subsite, you have to use the
	 * {@link #destClassIdPrefix}.
	 * @see #destId
	 * @see #destClassIdPrefix
	 * @since 1.6.2
	 */
	public abstract Class destClass() default void.class;

	/**
	 * The prefix that will be added to the <code>destClass</code> ID.
	 * <p>This makes it possible to refer to an ID in another subsite.
	 * Note that this prefix is not validated individually, it is merely added
	 * as a string to build the final ID that will be used.
	 * @see #destClass
	 * @since 1.6.2
	 */
	public abstract String destClassIdPrefix() default "";

	/**
	 * Indicates whether this flow link will redirect to a dedicated URL
	 * instead of executing the destination element directly.
	 * @since 1.6.2
	 */
	public abstract boolean redirect() default false;

	/**
	 * Indicates whether the behavioral inheritance (3D flow) should be
	 * preserved or cancelled when this flow link is followed.
	 * @since 1.6.2
	 */
	public abstract Inheritance inheritance() default Inheritance.PRESERVE;

	/**
	 * Indicates whether element embedded should be preserved or cancelled
	 * when this flow link is followed.
	 * @since 1.6.2
	 */
	public abstract Embedding embedding() default Embedding.PRESERVE;

	/**
	 * Indicates whether the continuations should be
	 * preserved or cancelled when this submission is sent.
	 * @since 1.6.2
	 */
	public abstract Continuations continuations() default Continuations.PRESERVE;

	public enum Inheritance { PRESERVE, CANCEL }
	public enum Embedding { PRESERVE, CANCEL }
	public enum Continuations { PRESERVE, CANCEL }
}