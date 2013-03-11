/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementScripted.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;

/**
 * The <code>ElementScripted</code> class provides a bridge between
 * scripting engines and the element backend. You should never have to deal
 * with this class directly.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class ElementScripted extends Element
{
	private ScriptedEngine	mEngine = null;

	/**
	 * Creates a new <code>ElementScripted</code> instance for a particular
	 * scripting engine.
	 *
	 * @param engine the scripting engine that this element has to be linked to
	 *
	 * @exception EngineException when an unexpected error occurred while linking
	 * the scripted element to the engine
	 */
	public ElementScripted(ScriptedEngine engine)
	throws EngineException
	{
		super();
		
		mEngine = engine;
		engine.setElement(this);
	}
	
	public void processElement()
	{
		mEngine.processElement();
	}
	
	public boolean childTriggered(String name, String[] values)
	{
		return mEngine.childTriggered(name, values);
	}
}
