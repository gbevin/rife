/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutputProperty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that the bean property that corresponds to the annotated getter
 * will be used as an output. The name of the output will be the name of
 * the property.
 * <p>When an element is processed, the output will be
 * outjected from element through the getter. An {@link com.uwyn.rife.engine.exceptions.OutputOutjectionException}
 * exception will be thrown if the outjection failed.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see Output
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface OutputProperty
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
	 * The default values of the output.
	 * @since 1.5
	 */
	String[] defaultValues() default {};
}
