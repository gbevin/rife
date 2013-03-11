/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Mapping.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the input mapping of a pathinfo declaration.
 * <p>The format of the mapping is explained in the
 * <a href="http://rifers.org/wiki/display/RIFE/Pathinfo+mapping+for+element+inputs">cook book</a>.
 * <p>Any inputs that are referenced in the mapping definition are
 * automatically declared if they don't exist yet.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface Mapping
{
	/**
	 * The mapping definition.
	 * @since 1.5
	 */
	String value();
}
