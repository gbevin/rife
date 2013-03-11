/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanHandlerPlainSingleton.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

/**
 * Helper class to avoid Double Check Locking
 * and still have a thread-safe singleton pattern
 */
class BeanHandlerPlainSingleton
{
	static final BeanHandlerPlain	INSTANCE = new BeanHandlerPlain();
}

