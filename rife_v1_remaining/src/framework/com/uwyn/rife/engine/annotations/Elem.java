/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Elem.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.annotations;

import java.lang.annotation.*;

import com.uwyn.rife.engine.ElementSupport;

/**
 * Declares that a Java class is a RIFE element.
 * <p>If {@link #id} isn't specified, the short class name (without the
 * package) will be used as the element's ID. The ID will be interpreted
 * relative to the ID of the sub-site in which the element is defined.
 * <p>For example, a site with ID <code>MY.SITE</code>, and an element with
 * class <code>mypackage.FooStuff</code>, will result in the following
 * absolute ID for the element: <code>MY.SITE.FooStuff</code>.
 * <p>If {@link #url} isn't specified, the lower-case short class name will
 * be used. For example the class <code>mypackage.FooStuff</code>, will result
 * in the <code>foostuff</code> URL.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Elem
{
	final static String DEFAULT_CONTENT_TYPE = "[default content type]";
	final static String DEFAULT_URL = "[default url]";

	/**
	 * The ID of this element.
	 * <p>If the ID isn't specified, a default will be generated based on the
	 * short class name. See the class documentation for more information.
	 * @since 1.5
	 */
    String id() default "";

	/**
	 * The URL of this element.
	 * <p>If the URL isn't specified, a default will be generated based on the
	 * lower-cased short class name. See the class documentation for more
	 * information.
	 * <p>To explicit specify that no URL should be used, provide an empty string.
	 * @since 1.5
	 */
	String url() default DEFAULT_URL;

	/**
	 * The ID of the element whose behavior should be inherited.
	 * <p>If <code>inheritsClass</code> is provided, it will override the
	 * <code>inheritsId</code> value.
	 * @see #inheritsClass
	 * @since 1.5
	 */
	String inheritsId() default "";

	/**
	 * The Java class of the element whose behavior should be inherited. This
	 * class should at least have an {@link Elem} annotation.
	 * <p>If <code>inheritsClass</code> is provided, it will override the
	 * <code>inheritsId</code> value.
	 * <p>The ID will be evaluated locally to the current subsite. If you
	 * have to refer to an ID in another subsite, you have to use the 
	 * {@link #inheritsClassIdPrefix}.
	 * @see #inheritsId
	 * @see #inheritsClassIdPrefix
	 * @since 1.5
	 */
	Class inheritsClass() default void.class;
	
	/**
	 * The prefix that will be added to the <code>inheritsClass</code> ID.
	 * <p>This makes it possible to refer to an ID in another subsite.
	 * Note that this prefix is not validated individually, it is merely added
	 * as a string to build the final ID that will be used.
	 * @see #inheritsClass
	 * @since 1.5
	 */
	String inheritsClassIdPrefix() default "";
	
	/**
	 * The ID of the element that should precede this element.
	 * <p>If <code>preClass</code> is provided, it will override the
	 * <code>preId</code> value.
	 * @see #preClass
	 * @since 1.5
	 */
	String preId() default "";

	/**
	 * The Java class of the element that should precede this element. This
	 * class should at least have an {@link Elem} annotation.
	 * <p>If <code>preClass</code> is provided, it will override the
	 * <code>preId</code> value.
	 * <p>The ID will be evaluated locally to the current subsite. If you
	 * have to refer to an ID in another subsite, you have to use the 
	 * {@link #preClassIdPrefix}.
	 * @see #preId
	 * @see #preClassIdPrefix
	 * @since 1.5
	 */
	Class preClass() default void.class;
	
	/**
	 * The prefix that will be added to the <code>preClass</code> ID.
	 * <p>This makes it possible to refer to an ID in another subsite.
	 * Note that this prefix is not validated individually, it is merely added
	 * as a string to build the final ID that will be used.
	 * @see #preClass
	 * @since 1.5
	 */
	String preClassIdPrefix() default "";
	

	/**
	 * The content type of this element's output. By default this will be
	 * whatever is specified by {@link com.uwyn.rife.config.RifeConfig.Engine#getDefaultContentType()}
	 * <p>If you want to use a dynamic content type, set it to an empty string
	 * (<code>""</code>) here, and use {@link ElementSupport#setContentType(String)}
	 * during the element logic.
	 * @since 1.5
	 */
	String contentType() default DEFAULT_CONTENT_TYPE;

	/**
	 * This element's inputs.
	 * @since 1.5
	 */
    Input[] inputs() default {};

	/**
	 * This element's input beans.
	 * @since 1.5
	 */
    InBean[] inbeans() default {};

	/**
	 * This element's incookies.
	 *
	 * @since 1.5
	 */
    InCookie[] incookies() default {};

	/**
	 * This element's outputs.
	 * @since 1.5
	 */
    Output[] outputs() default {};

	/**
	 * This element's output beans.
	 * @since 1.5
	 */
	OutBean[] outbeans() default {};

	/**
	 * This element's outcookies.
	 * @since 1.5
	 */
    OutCookie[] outcookies() default {};

	/**
	 * This element's submissions.
	 * @since 1.5
	 */
    Submission[] submissions() default {};

	/**
	 * This element's exits.
	 * @since 1.5
	 */
    Exit[] exits() default {};

	/**
	 * This element's child triggers.
	 * @since 1.5
	 */
    ChildTrigger[] childTriggers() default {};

	/**
	 * This element's pathinfo specifications.
	 * @since 1.5
	 */
    Pathinfo pathinfo() default @Pathinfo(mappings = {});

	/**
	 * This element's flow links.
	 * @since 1.5
	 */
    Flowlink[] flowlinks() default {};

	/**
	 * This element's data links.
	 * @since 1.5
	 */
    Datalink[] datalinks() default {};
	
	/**
	 * This element's auto links.
	 * @since 1.5.1
	 */
    Autolink[] autolinks() default {};
}
