/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ServletUtils.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.engine.Response;
import javax.servlet.http.HttpServletRequest;

public abstract class ServletUtils
{
	public static String getServletDir(HttpServletRequest request)
	{
		String servletpath = request.getServletPath();
		if (null != servletpath)
		{
			int lastindex = servletpath.lastIndexOf("/");
	
			if (-1 != lastindex)
			{
				return servletpath.substring(0, lastindex+1);
			}
		}
		return "";
	}

	public static void preventCaching(Response response)
	{
		response.addHeader("Cache-Control","no-cache");			// HTTP/1.1
		response.addHeader("Cache-Control","no-store");			// HTTP/1.1
		response.addHeader("Cache-Control","must-revalidate");	// HTTP/1.1
		response.addHeader("Pragma","no-cache");             	// HTTP 1.0
		response.addHeader("Expires", "1");
	}
}

