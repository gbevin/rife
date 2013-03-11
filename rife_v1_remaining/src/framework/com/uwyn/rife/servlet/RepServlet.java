/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: RepServlet.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.servlet;

import com.uwyn.rife.rep.Rep;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class RepServlet extends HttpServlet
{
	private static final long serialVersionUID = -7674168531946742291L;

	public void init()
	throws ServletException
	{
		Rep.initialize(getInitParameter("rep.path"), ResourceFinderClasspath.getInstance(), getServletContext());
	}

    public void destroy()
	{
		super.destroy();
	}
}

