/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InBean.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.uwyn.rife.site.MetaData;
import com.uwyn.rife.site.ValidatedConstrained;

/**
 * Declares an input bean.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see InBeanProperty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface InBean
{
	/**
	 * The class of the input bean.
	 * @since 1.5
	 */
	Class beanclass();

	/**
	 * The prefix that will be prepended to each property name of the input
	 * bean when corresponding inputs are automatically declared.
	 * @since 1.5
	 */
	String prefix() default "";

	/**
	 * The name of the output bean.
	 * @since 1.5
	 */
	String name() default "";

	/**
	 * The validation group that has been declared by the bean class.
	 * <p>This requires the bean class to implement the {@link ValidatedConstrained}
	 * interface, either directly, or by extending {@link MetaData}, or by using
	 * automated <a href="http://rifers.org/wiki/display/RIFE/Meta+data+merging">meta data merging</a>.
	 * <p>The group will indicate which bean properties should only be taken
	 * into account. Any properties outside the group will not be created as
	 * inputs.
	 * @since 1.5
	 */
	String group() default "";
}
