/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SiteListener.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

/**
 * An interface that can be implemented to receive notifications about the
 * events related to a site in the web engine
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see Site
 * @since 1.5
 */
public interface SiteListener
{
	/**
	 * This method is called when any of the resources that were used during
	 * the construction of the site has been detected as being modified.
	 * <p>This will only be detected if the <code>SITE_AUTO_RELOAD</code>
	 * configuration parameter is set to <code>true</code>.
	 * <p>If you manually create a new site, you have to call
	 * {@link Site#populateFromOther} on the outdated site instance, so that the
	 * rest of this request and subsequent requests will be processed against the
	 * up-to-date version.
	 * 
	 * @param outdatedSite the instance of the site where a modified resource
	 * has been detected
	 * @since 1.5
	 */
	public void modified(Site outdatedSite);
}
