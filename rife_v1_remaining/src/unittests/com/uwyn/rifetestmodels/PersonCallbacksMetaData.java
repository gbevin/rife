/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PersonCallbacksMetaData.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rifetestmodels;

import com.uwyn.rife.database.querymanagers.generic.AbstractCallbacks;
import com.uwyn.rife.database.querymanagers.generic.Callbacks;
import com.uwyn.rife.database.querymanagers.generic.CallbacksProvider;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

public class PersonCallbacksMetaData extends MetaData implements CallbacksProvider
{
	public Callbacks getCallbacks()
	{
		return new AbstractCallbacks<PersonCallbacks>() {
			public boolean beforeSave(PersonCallbacks object)
			{
				object.setFirstname("beforeSave");
				return true;
			}
		};
	}
	
	public void activateMetaData()
	{
		addConstraint(new ConstrainedProperty("firstname")
					  .maxLength(10)
					  .notNull(true));
		addConstraint(new ConstrainedProperty("lastname")
					  .inList("Smith", "Jones", "Ronda"));
	}
}
