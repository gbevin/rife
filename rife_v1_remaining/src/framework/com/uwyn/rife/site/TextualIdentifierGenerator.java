/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TextualIdentifierGenerator.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

/**
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see ConstrainedBean
 * @version $Revision: 3918 $
 * @since 1.0
 */
public interface TextualIdentifierGenerator<T>
{
	public void setBean(T bean);
	public String generateIdentifier();
}

