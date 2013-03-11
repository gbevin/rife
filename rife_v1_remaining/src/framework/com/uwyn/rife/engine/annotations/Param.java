/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Param.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a submission parameter.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see ParamProperty
 * @see ParamRegexp
 * @see Submission
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface Param
{
	/**
	 * The name of the parameter.
	 * @since 1.5
	 */
	String name();

	/**
	 * The default values of the parameter.
	 * @since 1.5
	 */
	String[] defaultValues() default {};
}
