/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Priority.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a declaration priority for an element method.
 * <p>The main reason is to be able to group {@link SubmissionHandler} annotations
 * together with the related {@link FileProperty}, {@link ParamProperty} and {@link SubmissionBeanProperty} annotations.
 * <p>The priority is provided as an array of integers. Each array element is
 * compared in the natural order of the integer values, but a rightmost array
 * element is less important than a leftmost array element.
 * <p>For example:<br />
 * <pre>{1} < {1,1} < {1,2} < {2} < {3} < {3,1}</pre>
 * <p>This allows you to for example use the first array element to identify
 * the submissions, and the secone one to indicate which other methods belong
 * to those submissions.
 * <p>When two methods have the same priority, they will be ordered
 * alphabetically according to their method name.
 * <p>Methods without a priority are always processed before methods that have
 * one. This is important because it allows methods without a priority to be
 * added to a {@link Submission} annotation that has been provided for the class.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface Priority
{
	/**
	 * The priority of the method that is annotated.
	 * @since 1.5
	 */
	int[] value();
}
