/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Pathinfo.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the URL pathinfo for an element.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface Pathinfo
{
	/**
	 * The possible pathinfo mapping policies. A <code>LOOSE</code> policy
	 * executes the element even if none of the mappings match. A
	 * <code>STRICT</code> policy only executes the element if at least one
	 * mapping matches.
	 * @since 1.5
	 */
    public enum MappingPolicy { LOOSE, STRICT }

	/**
	 * The collection of pathinfo mappings for this pathinfo.
	 * @since 1.5
	 */
	Mapping[] mappings();

	/**
	 * Indicates the pathinfo mapping policy.
	 * @since 1.5
	 */
	MappingPolicy policy() default MappingPolicy.LOOSE;
}
