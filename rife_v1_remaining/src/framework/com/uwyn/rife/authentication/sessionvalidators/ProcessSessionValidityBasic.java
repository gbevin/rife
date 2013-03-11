/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ProcessSessionValidityBasic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.authentication.sessionvalidators;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcessSessionValidityBasic extends ProcessSessionValidity
{
	private int mValidity = DatabaseSessionValidator.SESSION_INVALID;
	
	public boolean processRow(ResultSet resultSet)
	throws SQLException
	{
		assert resultSet != null;
		
		mValidity = DatabaseSessionValidator.SESSION_VALID;
		
		return true;
	}

	public int getValidity()
	{
		return mValidity;
	}
}

