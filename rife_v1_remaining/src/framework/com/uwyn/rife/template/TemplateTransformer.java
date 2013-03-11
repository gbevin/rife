/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateTransformer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.template.exceptions.TemplateException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;

public interface TemplateTransformer
{
	public Collection<URL> transform(String templateName, URL resource, OutputStream result, String encoding) throws TemplateException;
	public ResourceFinder getResourceFinder();
	public void setResourceFinder(ResourceFinder resourceFinder);
	public String getEncoding();
	public String getState();
}

