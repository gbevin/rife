/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubsiteIdAlreadyRegisteredToElementException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubsiteIdAlreadyRegisteredToElementException extends EngineException
{
	private static final long serialVersionUID = 4883095137974676123L;
	
	private String	mSiteDeclarationName = null;
	private String	mSubsiteId = null;
	private String	mExistingElementDeclarationName = null;
	private String	mConflictingSubsiteDeclarationName = null;
	
	public SubsiteIdAlreadyRegisteredToElementException(String siteDeclarationName, String subsiteId, String existingElementDeclarationName, String conflictingSubsiteDeclarationName)
	{
		super("The subsite ID '"+subsiteId+"' in site '"+siteDeclarationName+"' has already been registered to the element '"+existingElementDeclarationName+"', it's not possible to register it again for element '"+conflictingSubsiteDeclarationName+"'.");
		
		mSiteDeclarationName = siteDeclarationName;
		mSubsiteId = subsiteId;
		mExistingElementDeclarationName = existingElementDeclarationName;
		mConflictingSubsiteDeclarationName = conflictingSubsiteDeclarationName;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getSubsiteId()
	{
		return mSubsiteId;
	}
	
	public String getExistingElementDeclarationName()
	{
		return mExistingElementDeclarationName;
	}
	
	public String getConflictingSubsiteDeclarationName()
	{
		return mConflictingSubsiteDeclarationName;
	}
}
