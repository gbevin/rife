/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InputProperty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.*;

import com.uwyn.rife.engine.exceptions.InputInjectionException;

/**
 * Declares that the bean property that corresponds to the annotated setter
 * will be used as an input. The name of the input will be the name of
 * the property.
 * <p>When the element is processed, the value of the input will be injected
 * into the element through the setter and RIFE's type conversion will try to
 * convert the input's string value into the property type. An
 * {@link InputInjectionException} exception will be thrown if the
 * conversion failed.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see Input
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface InputProperty
{
	/**
	 * The expected name of the property.
	 * <p>
	 * This hasn't got any influence on the actual name that is being used
	 * for the property, but is used instead to ensure that the property name
	 * is the same as the one specified here. This is typically used to create
	 * a single point of declaration for the property name that can be
	 * referenced elsewhere and that is ensured to be correct.
	 * 
	 * @since 1.6
	 */
	String name() default "";

	/**
	 * The default value of the input.
	 * @since 1.5
	 */
	String[] defaultValues() default {};
}
