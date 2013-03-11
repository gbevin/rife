/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
  * $Id: SiteProcessorFactory.java 3918 2008-04-14 17:35:35Z gbevin $
*/
package com.uwyn.rife.engine;

import com.uwyn.rife.datastructures.EnumClass;
import java.util.Collection;

public class SiteProcessorFactory extends EnumClass<String>
{
	public static final String MANUAL_IDENTIFIER = "manual";
	public static final String XML_IDENTIFIER = "xml";
	public static final String GROOVY_IDENTIFIER = "groovy";
	public static final String JANINO_IDENTIFIER = "janino";
	
	public static final SiteProcessorFactory	MANUAL = new SiteProcessorFactory(MANUAL_IDENTIFIER, null, null);
	public static final SiteProcessorFactory	XML = new SiteProcessorFactory(XML_IDENTIFIER, "xml", new Xml2Site());
	public static final SiteProcessorFactory	GROOVY = new SiteProcessorFactory(GROOVY_IDENTIFIER, "groovy", new Groovy2Site());
	public static final SiteProcessorFactory	JANINO = new SiteProcessorFactory(JANINO_IDENTIFIER, "janino", new Janino2Site());
	
	private String			mExtension = null;
	private SiteProcessor	mProcessor = null;
	
	public SiteProcessorFactory(String identifier, String extension, SiteProcessor processor)
	{
		super(identifier);
		
		mExtension = extension;
		mProcessor = processor;
	}
	
	public String getIdentifier()
	{
		return toString();
	}
	
	public String getExtension()
	{
		return mExtension;
	}
	
	public SiteProcessor getProcessor()
	{
		return mProcessor;
	}
	
	public static SiteProcessorFactory getSiteProcessorFactory(String identifier)
	{
		return getMember(SiteProcessorFactory.class, identifier);
	}
	
	public static Collection<SiteProcessorFactory> getSiteProcessorFactories()
	{
		return (Collection<SiteProcessorFactory>)getMembers(SiteProcessorFactory.class);
	}
}
