/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ActivateCrudSiteProcessorParticipant.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.rep.participants;

import com.uwyn.rife.crud.CrudSiteProcessor;
import com.uwyn.rife.engine.SiteProcessorFactory;
import com.uwyn.rife.rep.BlockingParticipant;

public class ActivateCrudSiteProcessorParticipant extends BlockingParticipant
{
	public static final String CRUD_IDENTIFIER = "crud";
	
	{
		new SiteProcessorFactory(CRUD_IDENTIFIER, null, new CrudSiteProcessor());
	}
	
	protected void initialize()
	{
	}
}
