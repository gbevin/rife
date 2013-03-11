/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RoleUsersManager.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.credentialsmanagers;

import com.uwyn.rife.authentication.exceptions.CredentialsManagerException;

public interface RoleUsersManager extends IdentifiableUsersManager
{
	public RoleUsersManager addRole(String role) throws CredentialsManagerException;
	public boolean containsRole(String role) throws CredentialsManagerException;
	public long countRoles() throws CredentialsManagerException;
	public boolean listRoles(ListRoles processor) throws CredentialsManagerException;
	public RoleUsersManager addUser(String login, RoleUserAttributes attributes) throws CredentialsManagerException;
	public boolean containsUser(String login) throws CredentialsManagerException;
	public long countUsers() throws CredentialsManagerException;
	public long getUserId(String login) throws CredentialsManagerException;
	public boolean listUsers(ListUsers processor) throws CredentialsManagerException;
	public boolean listUsers(ListUsers processor, int limit, int offset) throws CredentialsManagerException;
	public boolean isUserInRole(long userId, String role) throws CredentialsManagerException;
	public boolean listUsersInRole(ListUsers processor, String role) throws CredentialsManagerException;
	public boolean updateUser(String login, RoleUserAttributes attributes) throws CredentialsManagerException;
	public boolean removeUser(String login) throws CredentialsManagerException;
	public boolean removeUser(long userId) throws CredentialsManagerException;
	public boolean removeRole(String name) throws CredentialsManagerException;
	public void clearUsers() throws CredentialsManagerException;
	public boolean listUserRoles(String login, ListRoles processor) throws CredentialsManagerException;
}
