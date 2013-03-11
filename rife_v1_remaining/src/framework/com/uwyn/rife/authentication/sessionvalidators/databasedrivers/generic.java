/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators.databasedrivers;

import com.uwyn.rife.authentication.SessionAttributes;
import com.uwyn.rife.authentication.exceptions.SessionValidatorException;
import com.uwyn.rife.authentication.sessionvalidators.DatabaseSessionValidator;
import com.uwyn.rife.authentication.sessionvalidators.ProcessSessionValidityBasic;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.Select;

public class generic extends DatabaseSessionValidator
{
	protected Select	mCheckValidityNoRole = null;
	protected Select	mCheckValidityNoRoleRestrictHostIp = null;
	protected Select	mCheckValidityRole = null;
	protected Select	mCheckValidityRoleRestrictHostIp = null;
	
	public generic(Datasource datasource)
	{
		super(datasource);

		mCheckValidityNoRole = new Select(getDatasource())
			.from(RifeConfig.Authentication.getTableAuthentication())
			.field(RifeConfig.Authentication.getTableAuthentication()+".userId")
			.whereParameter(RifeConfig.Authentication.getTableAuthentication()+".authId", "=")
			.whereParameterAnd(RifeConfig.Authentication.getTableAuthentication()+".sessStart", ">");

		mCheckValidityNoRoleRestrictHostIp = mCheckValidityNoRole.clone()
			.whereParameterAnd(RifeConfig.Authentication.getTableAuthentication()+".hostIp", "=");
		
		mCheckValidityRole = new Select(getDatasource())
			.from(RifeConfig.Authentication.getTableAuthentication())
			.join(RifeConfig.Authentication.getTableRoleLink())
			.join(RifeConfig.Authentication.getTableRole())
			.field(RifeConfig.Authentication.getTableAuthentication()+".userId")
			.whereParameter(RifeConfig.Authentication.getTableAuthentication()+".authId", "=")
			.whereParameterAnd(RifeConfig.Authentication.getTableAuthentication()+".sessStart", ">")
			.whereAnd(RifeConfig.Authentication.getTableAuthentication()+".userId = "+RifeConfig.Authentication.getTableRoleLink()+".userId")
			.whereParameterAnd(RifeConfig.Authentication.getTableRole()+".name", "role", "=")
			.whereAnd(RifeConfig.Authentication.getTableRole()+".roleId = "+RifeConfig.Authentication.getTableRoleLink()+".roleId");
		
		mCheckValidityRoleRestrictHostIp = mCheckValidityRole.clone()
			.whereParameterAnd(RifeConfig.Authentication.getTableAuthentication()+".hostIp", "=");
	}
	
	public int validateSession(String authId, String hostIp, SessionAttributes attributes)
	throws SessionValidatorException
	{
		return _validateSession(mCheckValidityNoRole, mCheckValidityNoRoleRestrictHostIp, mCheckValidityRole, mCheckValidityRoleRestrictHostIp, new ProcessSessionValidityBasic(), authId, hostIp, attributes);
	}
}


