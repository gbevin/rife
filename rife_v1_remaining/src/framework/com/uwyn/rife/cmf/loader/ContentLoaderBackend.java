/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ContentLoaderBackend.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf.loader;

import java.util.Set;

/**
 * This is an interface that should be implemented by all content loader
 * back-ends.
 * <p>All content loader back-ends that are fronted by the same {@link
 * ContentLoader} should handle the same <code>InternalType</code>, which is
 * returned by the {@link #load(Object, boolean, Set) load} method.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface ContentLoaderBackend<InternalType>
{
	/**
	 * Indicates whether the back-end is present.
	 * <p>This can be important for optional libraries that should only
	 * actually try to load the data when the required classes are available
	 * in the classpath.
	 * 
	 * @return <code>true</code> if the back-end is present; or
	 * <p><code>false</code> if this is not the case
	 * @since 1.0
	 */
	public boolean isBackendPresent();

	/**
	 * Loads any kind of raw data and tries to accommodate as much as possible
	 * to return an instance of <code>InternalType</code> after successful
	 * loading and handling.
	 * <p>Should any errors occur, then they will be added as text messages to
	 * the <code>errors</code> collection.
	 * 
	 * @param data the raw data that has to be loaded
	 * @param fragment <code>true</code> if the raw data is a fragment; or
	 * <p><code>false</code> if the raw data is a complete document or file
	 * @param errors a set to which possible error messages will be added
	 * @return an instance of the <code>InternalType</code>; or
	 * <p><code>null</code> if the raw data couldn't be loaded
	 * @since 1.0
	 */
	public InternalType load(Object data, boolean fragment, Set<String> errors);
}
