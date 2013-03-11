/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DataLinkIncompatibleInputOutputException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

public class DataLinkIncompatibleInputOutputException extends EngineException
{
	private static final long serialVersionUID = 3044391130298285543L;

	private String	mSiteDeclarationName = null;
	private String	mElementId = null;
	private String	mDestinationId = null;
	private boolean	mSnapback = false;

	public DataLinkIncompatibleInputOutputException(String siteDeclarationName, String elementId, String destinationId, boolean snapback)
	{
		super("The site '"+siteDeclarationName+"' has an incompatible input and output defined for the "+(!snapback ? "" : "snapback ")+"datalink that originates at the element '"+elementId+"'"+(null == destinationId ? "" : " towards the element '"+destinationId+"'")+", do not connect an output to an inbean or an outbean to an input.");
		
		mSiteDeclarationName = siteDeclarationName;
		mElementId = elementId;
		mDestinationId = destinationId;
		mSnapback = snapback;
	}
	
	public String getSiteDeclarationName()
	{
		return mSiteDeclarationName;
	}
	
	public String getElementId()
	{
		return mElementId;
	}
	
	public String getDestinationId()
	{
		return mDestinationId;
	}
	
	public boolean getSnapback()
	{
		return mSnapback;
	}
}
