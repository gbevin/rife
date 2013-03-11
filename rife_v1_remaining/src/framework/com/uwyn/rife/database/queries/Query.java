/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Query.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.queries;

import com.uwyn.rife.database.capabilities.Capabilities;
import com.uwyn.rife.database.exceptions.DbQueryException;

public interface Query
{
	public void clear();
	public String getSql() throws DbQueryException;
	public QueryParameters getParameters();
	public Capabilities getCapabilities();
	public void setExcludeUnsupportedCapabilities(boolean flag);
}
