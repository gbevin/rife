/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionHandler.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that the annotated method will be used as a submission handler.
 * The method should be in the format "<code>doSubmissionName</code>". The
 * "<code>do</code>" prefix indicates that it's a submission handler and
 * the rest of the method name will be used as the name of the submission
 * (with a lower-cased initial character).
 * <p>When the element is processed and a submission was received with the
 * same name as the handler, the handler method will be executed instead of
 * the "<code>processElement</code>" method.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see Submission
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface SubmissionHandler
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
