/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools.exceptions;

public class BeanUtilsException extends Exception
{
    private static final long serialVersionUID = -6557891499066342431L;
    private Class beanClass = null;

    public BeanUtilsException(String message, Class beanClass)
    {
        super(message);
        this.beanClass = beanClass;
    }

    public BeanUtilsException(String message, Class beanClass, Throwable cause)
    {
        super(message, cause);
        this.beanClass = beanClass;
    }

    public Class getBeanClass()
    {
        return beanClass;
    }
}
