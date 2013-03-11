/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Submission.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a submission.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see SubmissionHandler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface Submission
{
	/**
	 * A <code>LOCAL</code> submission scope means that only the submission
	 * with the same name in the same element will be able to receive the
	 * data that was submitted.
	 * <p>A <code>GLOBAL</code> submission scope means that any submission
	 * with the same name (even if it belongs to another element) will be
	 * able to receive the data that was submitted.
	 * @since 1.5
	 */
	public enum Scope { LOCAL, GLOBAL }

	/**
	 * The submission's name.
	 * @since 1.5
	 */
	String name();

	/**
	 * The submission's scope.
	 * @since 1.5
	 */
 	Scope scope() default Scope.LOCAL;

	/**
	 * The submission's parameters.
	 * @since 1.5
	 */
	Param[] params() default {};

	/**
	 * The submission's regular expression parameters.
	 * @since 1.5
	 */
	ParamRegexp[] paramRegexps() default {};

	/**
	 * The submission's beans.
	 * @since 1.5
	 */
	SubmissionBean[] beans() default {};

	/**
	 * The submission's uploaded files.
	 * @since 1.5
	 */
	File[] files() default {};

	/**
	 * The submission's regular expression file uploads.
	 * @since 1.5
	 */
	FileRegexp[] fileRegexps() default {};
	
	/**
	 * Indicates whether the continuations should be
	 * preserved or cancelled when this submission is sent.
	 * @since 1.6
	 */
	Continuations continuations() default Continuations.PRESERVE;
	
	public enum Continuations { PRESERVE, CANCEL }
}
