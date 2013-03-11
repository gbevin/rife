/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc.exceptions;

public class PropertyConstructionException extends RuntimeException
{
    private static final long serialVersionUID = -5470285155648841104L;
    private String entityType = null;
    private String declarationName = null;
    private String propertyName = null;

    public PropertyConstructionException(String entityType, String declarationName, String propertyName, Throwable e)
    {
        super("An error occured while constructing the property '" + propertyName + "' of " + entityType + " '" + declarationName + "'.", e);

        this.entityType = entityType;
        this.declarationName = declarationName;
        this.propertyName = propertyName;
    }

    public String getEntityType()
    {
        return entityType;
    }

    public String getDeclarationName()
    {
        return declarationName;
    }

    public String getPropertyName()
    {
        return propertyName;
    }
}
