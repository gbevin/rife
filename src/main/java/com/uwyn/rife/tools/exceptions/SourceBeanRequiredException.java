/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools.exceptions;

public class SourceBeanRequiredException extends RuntimeException
{
    private static final long serialVersionUID = -8331911446024868930L;
    private Class beanClass = null;

    public SourceBeanRequiredException(Class beanClass)
    {
        super("It's required to set the source bean before accessing property change support functionalities in class '" + beanClass.getName() + "'.");
        this.beanClass = beanClass;
    }

    public Class getBeanClass()
    {
        return beanClass;
    }
}
