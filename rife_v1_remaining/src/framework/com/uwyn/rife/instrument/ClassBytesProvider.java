/*
 * Copyright 2001-2005 Patrick Lightbody and
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ClassBytesProvider.java 3877 2007-08-03 19:48:10Z gbevin $
 */
package com.uwyn.rife.instrument;

public interface ClassBytesProvider
{
    public byte[] getClassBytes(String className, boolean reloadAutomatically) throws ClassNotFoundException;
}

