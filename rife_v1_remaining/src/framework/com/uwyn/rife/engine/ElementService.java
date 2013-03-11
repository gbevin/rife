/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementService.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

/**
 * This interface allows web service objects to gain access to the element
 * context during their invocation.
 * <p>All web service back-ends that are integrated in RIFE will detect the
 * implementation of this interface for their service objects. They will call
 * the {@link #setRequestElement} method and provide the {@link ElementSupport}
 * instance that they are using before invoking the service. This is handy
 * when you have to retrieve authentication information from within the web
 * service, for instance.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface ElementService
{
	/**
	 * This method will be called by the web service back-end integration
	 * element before invoking the actual web service.
	 * 
	 * @param elementSupport the current <code>ElementSupport</code> instance
	 * @since 1.0
	 */
	public void setRequestElement(ElementSupport elementSupport);
}

