/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubmissionBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a submission bean.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see SubmissionBeanProperty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface SubmissionBean
{
	/**
	 * The class of the submission bean.
	 * @since 1.5
	 */
	Class beanclass();

	/**
	 * The prefix that will be prepended to each property name of the
	 * submission bean when corresponding parameters are automatically
	 * declared.
	 * @since 1.5
	 */
	String prefix() default "";

	/**
	 * The name of the submission bean.
	 * @since 1.5
	 */
	String name() default "";

	/**
	 * The validation group that has been declared by the bean class.
	 * <p>This requires the bean class to implement the {@link com.uwyn.rife.site.ValidatedConstrained}
	 * interface, either directly, or by extending {@link com.uwyn.rife.site.MetaData}, or by using
	 * automated <a href="http://rifers.org/wiki/display/RIFE/Meta+data+merging">meta data merging</a>.
	 * <p>The group will indicate which bean properties should only be taken
	 * into account. Any properties outside the group will not be created as
	 * submission parameters.
	 * @since 1.5
	 */
	String group() default "";
}
