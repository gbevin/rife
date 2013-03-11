/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: generic.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.mail.dam.databasedrivers;

import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.mail.dam.DatabaseMailQueue;

public class generic extends DatabaseMailQueue
{
    public generic(Datasource datasource)
    {
		super(datasource);
	}
}
