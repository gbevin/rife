/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementIdAlreadyRegisteredToSubsiteException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementIdAlreadyRegisteredToSubsiteException extends EngineException
{
	private static final long serialVersionUID = -4206716475746929153L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;
	private String	mExistingSubsiteDeclarationName = null;
	private String	mConflictingElementDeclarationName = null;
	
	public ElementIdAlreadyRegisteredToSubsiteException(String siteDeclarationName, String elementId, String existingSubsiteDeclarationName, String conflictingElementDeclarationName)
	{
		super("The element ID '"+elementId+"' in site '"+siteDeclarationName+"' has already been registered to the subsite '"+existingSubsiteDeclarationName+"', it's not possible to register it again for element '"+conflictingElementDeclarationName+"'.");
		
		mSiteDeclarationName = siteDeclarationName;
		mElementId = elementId;
		mExistingSubsiteDeclarationName = existingSubsiteDeclarationName;
		mConflictingElementDeclarationName = conflictingElementDeclarationName;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getElementId()
	{
		return mElementId;
	}
	
	public String getExistingSubsiteDeclarationName()
	{
		return mExistingSubsiteDeclarationName;
	}
	
	public String getConflictingElementDeclarationName()
	{
		return mConflictingElementDeclarationName;
	}
}
