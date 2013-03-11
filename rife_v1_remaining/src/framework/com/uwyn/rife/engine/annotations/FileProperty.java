/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileProperty.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.*;

import com.uwyn.rife.engine.UploadedFile;

/**
 * Declares that the bean property that corresponds to the annotated setter
 * will be used to inject an uploaded submission file. The property type
 * should be {@link UploadedFile}, and the file will be added to the previous
 * submission that has been declared.
 * <p>If no submission has been declared beforehand, either through {@link Submission}
 * or {@link SubmissionHandler}, an exception will be thrown when the
 * annotations of this element are evaluated.
 * <p>When the element is processed and the file was uploaded through its
 * submission, the corresponding instance of {@link UploadedFile} will be injected
 * into the element through the setter.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see File
 * @see FileRegexp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface FileProperty
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
}
