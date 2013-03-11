/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: EmbeddingContext.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.template.Template;

class EmbeddingContext
{
	private ElementContext	mEmbeddingContext = null;
	private ElementSupport	mEmbeddingElement = null;
	private Template		mTemplate = null;
	private String			mValue = null;
	private String			mDifferentiator = null;
	private Object			mData = null;
	private boolean			mCancelEmbedding = false;
	
	EmbeddingContext(ElementContext embeddingContext, ElementSupport embeddingElement, Template template, String value, String differentiator, Object data)
	throws EngineException
	{
		assert embeddingContext != null;
		assert embeddingElement != null;
		assert template != null;
		
		mEmbeddingContext = embeddingContext;
		mEmbeddingElement = embeddingElement;
		mTemplate = template;
		mValue = value;
		mDifferentiator = differentiator;
		mData = data;
	}
	
	ElementContext getElementContext()
	{
		return mEmbeddingContext;
	}
	
	ElementSupport getEmbeddingElement()
	{
		return mEmbeddingElement;
	}
	
	Template getTemplate()
	{
		return mTemplate;
	}
	
	String getDifferentiator()
	{
		return mDifferentiator;
	}
	
	String getValue()
	{
		return mValue;
	}

	Object getData()
	{
		return mData;
	}

	Properties getEmbedProperties()
	throws IOException
	{
		if (null == mValue)
		{
			return null;
		}

		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(mValue.getBytes("ISO-8859-1")));

		return properties;
	}
	
	void setCancelEmbedding(boolean cancelEmbedding)
	{
		mCancelEmbedding = cancelEmbedding;
	}
	
	boolean getCancelEmbedding()
	{
		return mCancelEmbedding;
	}
}

