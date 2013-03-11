/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RememberMe.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentials;

/**
 * <p>This interface needs to be implemented by all credentials classes that
 * are allowed to be remembered.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface RememberMe
{
	/**
	 * Indicates whether the submitted credentials should be remembered.
	 * 
	 * @return {@code true} if the submitted credentials should be
	 * remembered; or
	 * <p>{@code false} otherwise
	 * @since 1.0
	 */
	public boolean getRemember();
	/**
	 * Sets whether the submitted credentials should be remembered.
	 * 
	 * @param rememberMe {@code true} if the submitted credentials should
	 * be remembered; or {@code false} otherwise
	 * @since 1.0
	 */
	public void setRemember(boolean rememberMe);
}
