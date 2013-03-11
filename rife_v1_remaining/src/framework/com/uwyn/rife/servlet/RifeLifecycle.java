/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RifeLifecycle.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.servlet;

import com.uwyn.rife.engine.Gate;
import com.uwyn.rife.engine.InitConfig;
import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.rep.BlockingRepository;
import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.rep.Repository;
import java.util.Enumeration;
import javax.servlet.ServletContext;

public class RifeLifecycle
{
	public Gate init(InitConfig config)
	{
		// instantiate the repository
		Repository rep = Rep.getDefaultRepository();
		boolean		initialize_repository = false;
		if (null == rep)
		{
			initialize_repository = true;
			rep = new BlockingRepository(config.getServletContext());
			Rep.setDefaultRepository(rep);
		}
		
		// setup the properties
		HierarchicalProperties	properties = rep.getProperties();
		
		Enumeration	names = null;
		String		name = null;
		
		ServletContext context = config.getServletContext();
		names = context.getInitParameterNames();
		name = null;
		while (names.hasMoreElements())
		{
			name = (String)names.nextElement();
			properties.put(name, context.getInitParameter(name));
		}
		
		names = config.getInitParameterNames();
		name = null;
		while (names.hasMoreElements())
		{
			name = (String)names.nextElement();
			properties.put(name, config.getInitParameter(name));
		}

		// initialize the repository
		if (initialize_repository)
		{
			((BlockingRepository)rep).initialize(config.getInitParameter("rep.path"), null);
		}
		
		// initialize the gate
		Gate gate = new Gate();
		gate.init(config);
		
		return gate;
	}

	public void destroy()
	{
		Rep.cleanup();
	}
}
