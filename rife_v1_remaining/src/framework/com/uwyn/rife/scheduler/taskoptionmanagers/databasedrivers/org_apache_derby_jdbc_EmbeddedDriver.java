/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: org_apache_derby_jdbc_EmbeddedDriver.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.scheduler.taskoptionmanagers.databasedrivers;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.queries.CreateTable;

public class org_apache_derby_jdbc_EmbeddedDriver extends generic
{
	public org_apache_derby_jdbc_EmbeddedDriver(Datasource datasource)
	{
		super(datasource);

		mCreateTableTaskoption = new CreateTable(getDatasource())
			.table(RifeConfig.Scheduler.getTableTaskoption())
			.column("task_id", Integer.class, CreateTable.NOTNULL)
			.column("name", String.class, RifeConfig.Scheduler.getTaskoptionNameMaximumLength(), CreateTable.NOTNULL)
			.column("value", String.class, RifeConfig.Scheduler.getTaskoptionValueMaximumLength(), CreateTable.NOTNULL)
			.primaryKey(RifeConfig.Scheduler.getTableTaskoption().toUpperCase()+"_PK", new String[] {"task_id", "name"})
			.foreignKey(RifeConfig.Scheduler.getTableTaskoption().toUpperCase()+"_FK", RifeConfig.Scheduler.getTableTask(), "task_id", "id", null, CreateTable.CASCADE);
	}
}
