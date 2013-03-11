/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractTextualIdentifierGenerator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

/**
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see TextualIdentifierGenerator
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class AbstractTextualIdentifierGenerator<T> implements TextualIdentifierGenerator<T>
{
	protected T	mBean = null;
	
	public void setBean(T bean)
	{
		mBean = bean;
	}
	
	public T getBean()
	{
		return mBean;
	}
	
	public abstract String generateIdentifier();
}

