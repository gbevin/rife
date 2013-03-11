/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SessionManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication;

import com.uwyn.rife.authentication.exceptions.SessionManagerException;

/**
 * This interface defines the methods that classes with
 * {@code SessionManager} functionalities have to implement.
 * <p>A {@code SessionManager} is reponsible for handling all tasks
 * related to the lifetime of a session in which a user is successfully
 * authenticated.
 * <p>This kind of session doesn't provide any state persistance across
 * requests and doesn't store any additional business data on the server-side.
 * It merely provides a unique authentication id which can be used to identify
 * a successfully authenticated user.
 * <p>For safety's sake, sessions time out after a certain period of
 * inactivity and their validity is bound only to a unique user id and a host
 * ip. No assumptions are being made about the actual meaning or structure of
 * a 'user'. A unique numeric identifier is all that's required.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.authentication.SessionValidator
 * @since 1.0
 */
public interface SessionManager
{
	/**
	 * Obtains the maximum time that a user can stay inactive before an active
	 * session becomes invalid.
	 *
	 * @return The maximum time of inactivity in milliseconds.
	 * @since 1.0
	 */
	public long getSessionDuration();

	/**
	 * Sets the maximum time that a user can stay inactive before an active
	 * session becomes invalid.
	 *
	 * @param milliseconds The maximum time of inactivity in milliseconds.
	 * @since 1.0
	 */
	public void setSessionDuration(long milliseconds);
	/**
	 * Obtains the restriction policy of the authentication ID with regards to the
	 * user's host IP.
	 * <p>The default is {@code false}, or no restriction.
	 *
	 * @return {@code true} if the authentication is restricted to one host IP; or
	 * <p>{@code false} if the authentication ID can be used with any host IP
	 * @since 1.0
	 */
	public boolean getRestrictHostIp();
	
	/**
	 * Sets the restriction policy of the authentication ID with regards to the
	 * user's host IP.
	 * <p>The default is {@code false}, or no restriction.
	 *
	 * @param flag {@code true} to activate the host IP restriction; or
	 * <p>{@code false} otherwise
	 * @since 1.0
	 */
	public void setRestrictHostIp(boolean flag);
	
	/**
	 * Starts a new session.
	 *
	 * @param userId The id that uniquely identifies the user that is allowed
	 * to use this session.
	 * @param hostIp The ip address of the host from which the user accesses
	 * the application.
	 * @param remembered Indicates whether the session is started through
	 * remember me or from scratch.
	 * @return A {@code String} that uniquely identifies the
	 * authentication session that was just started.
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when a session is started. They are
	 * all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public String startSession(long userId, String hostIp, boolean remembered) throws SessionManagerException;

	/**
	 * Verifies if a session is valid and still active.
	 *
	 * @param authId The unique id of the authentication session that needs to
	 * be verified.
	 * @param hostIp The ip address of the host from which the user accesses
	 * the application.
	 * @return {@code true} if a valid active session was found; or
	 * <p>{@code false} if this was not possible.
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when a session is verified. They
	 * are all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public boolean isSessionValid(String authId, String hostIp) throws SessionManagerException;

	/**
	 * Continues an already active session. This means that the inactivity
	 * time-out is reset to the maximal value. This is typically called each
	 * time a user accesses an application.
	 *
	 * @param authId The unique id of the authentication session that needs to
	 * be continued.
	 * @return {@code true} if the session was successfully continued; or
	 * <p>{@code false} if this was not possible (ie. the session
	 * couldn't be found).
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when a session is continued. They
	 * are all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public boolean continueSession(String authId) throws SessionManagerException;

	/**
	 * Removes all traces of an authentication session. This makes the session
	 * instantly inactive and invalid.
	 *
	 * @param authId The unique id of the authentication session that needs to
	 * be erased.
	 * @return {@code true} if the session was successfully erased; or
	 * <p>{@code false} if this was not possible (ie. the session
	 * couldn't be found).
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when a session is erased. They are
	 * all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public boolean eraseSession(String authId) throws SessionManagerException;

	/**
	 * Checks if a session was previously automatically created from remembered
	 * data.
	 *
	 * @param authId The unique id of the authentication session that needs to
	 * be erased.
	 * @return {@code true} if the session was created automatically from
	 * remembered data; or
	 * <p>{@code false} if it was created from full credentials or if the
	 * session couldn't be found.
	 *
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when a session is erased. They are
	 * all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public boolean wasRemembered(String authId) throws SessionManagerException;
	
	/**
	 * Removes all traces of all authentication sessions for a particular
	 * user. This makes all sessions of this user instantly inactive and
	 * invalid.
	 *
	 * @param userId The id that uniquely identifies the user whose sessions
	 * are to be erased.
	 * @return {@code true} if the sessions were successfully erased; or
	 * <p>{@code false} if this was not possible (ie. no sessions
	 * couldn't be found).
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when a session is erased. They are
	 * all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public boolean eraseUserSessions(long userId) throws SessionManagerException;

	/**
	 * Removes all available sessions. This makes all sessions instantly
	 * invalid and inactive for all users.
	 *
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when a session is erased. They are
	 * all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public void eraseAllSessions() throws SessionManagerException;

	/**
	 * Retrieves the id of a user that has access to a particular session.
	 *
	 * @param authId The unique id of the authentication session for which the
	 * user needs to be looked up.
	 * @return A long {@code &gt;= 0} that corresponds to the user id
	 * that has access to the session; or
	 * <p>{@code -1} if the session couldn't be found.
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when user id of a session is
	 * retrieved. They are all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public long getSessionUserId(String authId) throws SessionManagerException;

	/**
	 * Removes all sessions that are inactive. This means that all sessions
	 * where the inactivity time has been exceeded, will be removed.
	 *
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when a session is purged. They are
	 * all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public void purgeSessions() throws SessionManagerException;

	/**
	 * Counts the number of active sessions.
	 *
	 * @return The number of active sessions.
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when a session is counted. They are
	 * all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public long countSessions() throws SessionManagerException;
	
	/**
	 * Lists the active sessions.
	 *
	 * @param processor The row processor that will be used to list the active
	 * sessions.
	 * @return {@code true} if active sessions were found; or
	 * <p>{@code false} if no session was active.
	 * @exception SessionManagerException An undefined number of exceptional
	 * cases or error situations can occur when session are listed. They are
	 * all indicated by throwing an instance of
	 * {@code SessionManagerException}. It's up to the implementations of
	 * this interface to give more specific meanings to these exceptions.
	 * @since 1.0
	 */
	public boolean listSessions(ListSessions processor) throws SessionManagerException;
}


