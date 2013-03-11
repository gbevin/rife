/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EngineContinuationConfigRuntime.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.continuations.ContinuationConfigRuntime;
import com.uwyn.rife.continuations.ContinuationManager;

public class EngineContinuationConfigRuntime extends ContinuationConfigRuntime<ElementSupport>
{
	EngineContinuationConfigRuntime()
	{
		// just make sure that the default constructor can only be called
		// by the EngineContinuationConfigSingleton class
	}
	
	public ElementSupport getAssociatedContinuableObject(Object executingInstance)
	{
		return ElementContext.getActiveElementSupport();
	}
	
	public ContinuationManager getContinuationManager(ElementSupport executingContinuable)
	{
		return ((ElementSupport)executingContinuable).getSite().getContinuationManager();
	}
	
	public boolean cloneContinuations(ElementSupport executingContinuable)
	{
		return executingContinuable.cloneContinuations();
	}
	
    public long getContinuationDuration()
	{
        return RifeConfig.Engine.getContinuationDuration();
    }
	
    public int getContinuationPurgeFrequency()
	{
        return RifeConfig.Engine.getContinuationPurgeFrequency();
    }
	
    public int getContinuationPurgeScale()
	{
        return RifeConfig.Engine.getContinuationPurgeScale();
    }
}
