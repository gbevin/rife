/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileRegexp.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a file upload regular expression during a submission.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 * @see File
 * @see FileProperty
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@Documented
public @interface FileRegexp
{
	/**
	 * The regular expression that needs to be respected for the name of the
	 * parameter that will contain the file data.
	 * @since 1.5
	 */
	String value();
}
