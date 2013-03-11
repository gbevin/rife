/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SubsiteIdAlreadyRegisteredToSubsiteException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class SubsiteIdAlreadyRegisteredToSubsiteException extends EngineException
{
	private static final long serialVersionUID = 8961821651544292196L;
	
	private String	mSiteDeclarationName = null;
	private String	mSubsiteId = null;
	private String	mExistingSubsiteDeclarationName = null;
	private String	mConflictingSubsiteDeclarationName = null;
	
	public SubsiteIdAlreadyRegisteredToSubsiteException(String siteDeclarationName, String subsiteId, String existingSubsiteDeclarationName, String conflictingSubsiteDeclarationName)
	{
		super("The subsite ID '"+subsiteId+"' in site '"+siteDeclarationName+"' has already been registered to the subsite '"+existingSubsiteDeclarationName+"', it's not possible to register it again for element '"+conflictingSubsiteDeclarationName+"'.");
		
		mSiteDeclarationName = siteDeclarationName;
		mSubsiteId = subsiteId;
		mExistingSubsiteDeclarationName = existingSubsiteDeclarationName;
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
	
	public String getExistingSubsiteDeclarationName()
	{
		return mExistingSubsiteDeclarationName;
	}
	
	public String getConflictingSubsiteDeclarationName()
	{
		return mConflictingSubsiteDeclarationName;
	}
}
