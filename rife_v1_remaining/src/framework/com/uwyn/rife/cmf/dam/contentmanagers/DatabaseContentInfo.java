/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DatabaseContentInfo.java 3918 2008-04-14 17:35:35Z gbevin $
 */

package com.uwyn.rife.cmf.dam.contentmanagers;

import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.site.ConstrainedProperty;

/**
 * This class adds additional properties to the <code>ContentInfo</code> class
 * to be able to store the data in a datab.ase
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class DatabaseContentInfo extends ContentInfo
{
	private int	mContentId = -1;
	
	/**
	 * Instantiates a new <code>DatabaseContentInfo</code> instance.
	 */
	public DatabaseContentInfo()
	{
	}
	
	public void activateValidation()
	{
		super.activateValidation();
		
		addConstraint(new ConstrainedProperty("contentId")
						  .notNull(true)
						  .rangeBegin(0)
						  .identifier(true));
	}
	
	/**
	 * Sets the ID of the stored <code>Content</code> instance.
	 * <p>This ID will not be used to refer to the Content instance from
	 * outside the backend. The path and the version should be used for this
	 * instead.
	 *
	 * @param contentId the ID of the <code>Content</code> instance
	 * @see #getContentId()
	 */
	public void setContentId(int contentId)
	{
		mContentId = contentId;
	}
	
	/**
	 * Retrieves the ID of the stored <code>Content</code> instance.
	 *
	 * @return the <code>Content</code>'s ID
	 * @see #setContentId(int)
	 */
	public int getContentId()
	{
		return mContentId;
	}
}
