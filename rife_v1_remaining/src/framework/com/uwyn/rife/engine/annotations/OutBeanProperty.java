/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OutBeanProperty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.*;

import com.uwyn.rife.engine.exceptions.OutbeanOutjectionException;
import com.uwyn.rife.site.MetaData;
import com.uwyn.rife.site.ValidatedConstrained;

/**
 * Declares that the bean property that corresponds to the annotated getter
 * will be used as an output bean. The name of the bean will be the name of
 * the property, and the bean class will be the property type.
 * <p>When an element is processed, the output bean instance will be
 * outjected from element through the getter as invididual output values that
 * correspond to the bean properties. An {@link OutbeanOutjectionException}
 * exception will be thrown if the outjection failed.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see OutBean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface OutBeanProperty
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
	 * The prefix that will be prepended to each property name of the output
	 * bean when corresponding output names are automatically declared.
	 * @since 1.5
	 */
	String prefix() default "";

	/**
	 * The validation group that has been declared by the bean class.
	 * <p>This requires the bean class to implement the {@link ValidatedConstrained}
	 * interface, either directly, or by extending {@link MetaData}, or by using
	 * automated <a href="http://rifers.org/wiki/display/RIFE/Meta+data+merging">meta data merging</a>.
	 * <p>The group will indicate which bean properties should only be taken
	 * into account. Any properties outside the group will not be created as
	 * outputs.
	 * @since 1.5
	 */
	String group() default "";
}
