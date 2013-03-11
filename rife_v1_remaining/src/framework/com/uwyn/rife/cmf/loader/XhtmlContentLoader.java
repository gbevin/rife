/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: XhtmlContentLoader.java 3939 2008-04-26 20:09:07Z gbevin $
 */
package com.uwyn.rife.cmf.loader;

import com.uwyn.rife.cmf.loader.xhtml.Jdk14Loader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads raw content as xhtml data. The internal type to which everything will
 * be converted is <code>java.lang.String</code>.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3939 $
 * @since 1.0
 * @see com.uwyn.rife.cmf.loader.ContentLoader
 */
public class XhtmlContentLoader extends ContentLoader<String>
{
	private static final List<ContentLoaderBackend<String>> sBackends;
	
	static
	{
		sBackends = new ArrayList<ContentLoaderBackend<String>>();
		sBackends.add(new Jdk14Loader());
	}

	public List<ContentLoaderBackend<String>> getBackends()
	{
		return sBackends;
	}
}
