/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CancelEmbeddingTriggeredException.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.exceptions;

import com.uwyn.rife.tools.exceptions.ControlFlowRuntimeException;
import java.util.ArrayList;

public class CancelEmbeddingTriggeredException extends EngineException implements ControlFlowRuntimeException
{
	private static final long serialVersionUID = -6691750389014518326L;

	private ArrayList<CharSequence>	mEmbeddingContent = null;

	public CancelEmbeddingTriggeredException(ArrayList<CharSequence> embeddingContent)
	{
		super();
		
		mEmbeddingContent = embeddingContent;
	}
	
	public ArrayList<CharSequence> getEmbeddingContent()
	{
		return mEmbeddingContent;
	}
}
