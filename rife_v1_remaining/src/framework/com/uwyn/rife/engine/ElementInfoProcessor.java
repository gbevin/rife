/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
  * $Id: ElementInfoProcessor.java 3918 2008-04-14 17:35:35Z gbevin $
*/
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.resources.ResourceFinder;

public interface ElementInfoProcessor
{
	public final static String	DEFAULT_ELEMENTS_PATH = "elements/";
	
	public void processElementInfo(ElementInfoBuilder builder, String declarationName, ResourceFinder resourceFinder) throws EngineException;
}
