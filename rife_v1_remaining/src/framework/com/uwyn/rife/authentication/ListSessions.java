/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ListSessions.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication;

/**
 * <p>This interface to be able to list all the active sessions in a {@link
 * SessionManager} without having to store them all in memory.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface ListSessions
{
	/**
	 * <p>This method is called when active authentication session was found.
	 * 
	 * @param userId the unique ID of the user
	 * @param hostIp the IP address of the host that initiated the session
	 * @param authId the unique identifier of this authentication session
	 * @return {@code true} when the next active session should be
	 * returned; or
	 * <p>{@code false} if the process should be interrupted
	 * @since 1.0
	 */
	public boolean foundSession(long userId, String hostIp, String authId);
}

