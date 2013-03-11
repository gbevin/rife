/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementIdAlreadyRegisteredToElementException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class ElementIdAlreadyRegisteredToElementException extends EngineException
{
	private static final long serialVersionUID = 1918932252060861075L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;
	private String	mExistingElementDeclarationName = null;
	private String	mConflictingElementDeclarationName = null;
	
	public ElementIdAlreadyRegisteredToElementException(String siteDeclarationName, String elementId, String existingElementDeclarationName, String conflictingElementDeclarationName)
	{
		super("The element ID '"+elementId+"' in site '"+siteDeclarationName+"' has already been registered to the element '"+existingElementDeclarationName+"', it's not possible to register it again for element '"+conflictingElementDeclarationName+"'.");
		
		mSiteDeclarationName = siteDeclarationName;
		mElementId = elementId;
		mExistingElementDeclarationName = existingElementDeclarationName;
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
	
	public String getExistingElementDeclarationName()
	{
		return mExistingElementDeclarationName;
	}
	
	public String getConflictingElementDeclarationName()
	{
		return mConflictingElementDeclarationName;
	}
}
