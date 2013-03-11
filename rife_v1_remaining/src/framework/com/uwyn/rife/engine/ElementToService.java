/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementToService.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

/**
 * Contains the information required to service an element request.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6
 */
public class ElementToService
{
	private ElementInfo	mElementInfo;
	private String		mPathInfo;
	
	/**
	 * Creates a new <code>ElementToService</code> instance.
	 *
	 * @param elementInfo the <code>ElementInfo</code> of the element that needs
	 * to be serviced
	 * @param pathInfo the pathinfo string that needs to be used while servicing
	 * the element
	 * @since 1.6
	 */
	public ElementToService(ElementInfo elementInfo, String pathInfo)
	{
		if (null == elementInfo)	throw new IllegalArgumentException("elementInfo can't be null");

		if (null == pathInfo)
		{
			pathInfo = "";
		}

		mElementInfo = elementInfo;
		mPathInfo = pathInfo;
	}
	
	/**
	 * Retrieves the <code>ElementInfo</code> to service an element request.
	 *
	 * @return the element's <code>ElementInfo</code>, which can never be <code>null</code>
	 * @since 1.6
	 */
	public ElementInfo getElementInfo()
	{
		return mElementInfo;
	}
	
	/**
	 * Retrieves the pathinfo that will be used while servicing the element.
	 *
	 * @return the pathinfo, which can never be <code>null</code>
	 * @since 1.6
	 */
	public String getPathInfo()
	{
		return mPathInfo;
	}
}
