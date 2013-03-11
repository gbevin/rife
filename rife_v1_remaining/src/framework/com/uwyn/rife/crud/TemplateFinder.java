/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateFinder.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud;

import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.template.Parser;

import java.net.URL;

public class TemplateFinder extends ResourceFinderClasspath implements ResourceFinder
{
	public URL getResource(String name)
	{
		if (null == name)
		{
			return null;
		}
		
		// retrieve the index of the implementation seperator
		int implementation_index = name.indexOf(CrudTemplateFactory.CRUD_SEPARATOR);
		if (-1 == implementation_index)
		{
			return super.getResource(name);
		}
		
		// compensate for the possibility of a default templates path
		boolean default_path_prefixed = false;
		if (name.startsWith(Parser.DEFAULT_TEMPLATES_PATH))
		{
			default_path_prefixed = true;
		}
		
		name = name.substring(implementation_index+CrudTemplateFactory.CRUD_SEPARATOR.length());
		if (default_path_prefixed)
		{
			name = Parser.DEFAULT_TEMPLATES_PATH+name;
		}
		
		// try to obtain a resource for the generic template
		return super.getResource(name);
	}
}

