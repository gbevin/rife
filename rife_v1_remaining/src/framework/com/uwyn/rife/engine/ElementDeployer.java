/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementDeployer.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;

/**
 * Classes that are responsible for deploying elements have to extend this
 * abstract class.
 * <p>After {@link ElementSupport#setDeploymentClass registering} the
 * <code>ElementDeployer</code> class with <code>ElementSupport</code>, an
 * instance of this class will be created when the element is deployed within
 * a site. The instance's {@link #deploy()} method will be called.
 * <p>Element deployers are handy if you need to setup element-specific
 * resources for all instances of the element's implementation.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see ElementSupport#setDeploymentClass
 * @since 1.0
 */
public abstract class ElementDeployer
{
	private ElementInfo mElementInfo = null;
	
	final void setElementInfo(ElementInfo elementInfo)
	{
		mElementInfo = elementInfo;
	}
	
	/**
	 * Retrieves the declaration information about the element that is being
	 * deployed.
	 * 
	 * @return the declaration information of the deployed element
	 * @since 1.0
	 */
	public final ElementInfo getElementInfo()
	{
		return mElementInfo;
	}
	
	/**
	 * This method is executed when the deployment should be performed.
	 * 
	 * @since 1.0
	 */
	public abstract void deploy() throws EngineException;
}
