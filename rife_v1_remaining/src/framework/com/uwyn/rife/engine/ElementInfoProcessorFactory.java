/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
  * $Id: ElementInfoProcessorFactory.java 3918 2008-04-14 17:35:35Z gbevin $
*/
package com.uwyn.rife.engine;

import com.uwyn.rife.datastructures.EnumClass;

import java.util.Collection;

public class ElementInfoProcessorFactory extends EnumClass<String>
{
	public static final String MANUAL_IDENTIFIER = "manual";
	public static final String XML_IDENTIFIER = "xml";
	public static final String GROOVY_IDENTIFIER = "groovy";
	public static final String JANINO_IDENTIFIER = "janino";
	public static final String ANNOTATIONS_IDENTIFIER = "annotations";

	public static final ElementInfoProcessorFactory	MANUAL = new ElementInfoProcessorFactory(MANUAL_IDENTIFIER, null, null, true, true);
	public static final ElementInfoProcessorFactory	XML = new ElementInfoProcessorFactory(XML_IDENTIFIER, "xml", new Xml2ElementInfo(), false, false);
	public static final ElementInfoProcessorFactory	GROOVY = new ElementInfoProcessorFactory(GROOVY_IDENTIFIER, "groovy", new Groovy2ElementInfo(), true, true);
	public static final ElementInfoProcessorFactory	JANINO = new ElementInfoProcessorFactory(JANINO_IDENTIFIER, "janino", new Janino2ElementInfo(), true, true);
	public static final ElementInfoProcessorFactory	ANNOTATIONS = new ElementInfoProcessorFactory(ANNOTATIONS_IDENTIFIER, null, new Annotations2ElementInfo(), true, true);

	private String					mExtension = null;
	private ElementInfoProcessor	mProcessor = null;

	private boolean				 	mExitsFromFlowlinks = false;
	private boolean				 	mOutputsFromDatalinks = false;

	public ElementInfoProcessorFactory(String identifier, String extension, ElementInfoProcessor processor, boolean exitsFromFlowlinks, boolean outputsFromDatalinks)
	{
		super(identifier);
		
		mExtension = extension;
		mProcessor = processor;
		mExitsFromFlowlinks = exitsFromFlowlinks;
		mOutputsFromDatalinks = outputsFromDatalinks;
	}
	
	public String getExtension()
	{
		return mExtension;
	}
	
	public String getIdentifier()
	{
		return identifier;
	}
	
	public ElementInfoProcessor getProcessor()
	{
		return mProcessor;
	}

	public boolean generateOutputsFromDatalinks()
	{
		return mOutputsFromDatalinks;
	}

	public boolean generateExitsFromFlowlinks()
	{
		return mExitsFromFlowlinks;
	}

	public static ElementInfoProcessorFactory getElementInfoProcessorFactory(String identifier)
	{
		return getMember(ElementInfoProcessorFactory.class, identifier);
	}
	
	public static Collection<ElementInfoProcessorFactory> getElementInfoProcessorFactories()
	{
		return (Collection<ElementInfoProcessorFactory>)getMembers(ElementInfoProcessorFactory.class);
	}
}
