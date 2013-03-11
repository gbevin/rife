/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.resources;

/**
 * Helper class to avoid Double Check Locking and still have a thread-safe
 * singleton pattern without performance penalty.
 */
class ResourceFinderClasspathSingleton
{
    static final ResourceFinderClasspath INSTANCE = new ResourceFinderClasspath();
}

