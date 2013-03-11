/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public interface BeanPropertyProcessor
{
    public boolean gotProperty(String name, PropertyDescriptor descriptor) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
}

