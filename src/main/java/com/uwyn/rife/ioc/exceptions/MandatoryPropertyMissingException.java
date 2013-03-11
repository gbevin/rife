/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc.exceptions;

public class MandatoryPropertyMissingException extends PropertyValueException
{
    private static final long serialVersionUID = -2438475921094369181L;
    private String propertyName = null;

    public MandatoryPropertyMissingException(String propertyName)
    {
        super("The mandatory property '" + propertyName + "' is missing.");

        this.propertyName = propertyName;
    }

    public String getPropertyName()
    {
        return propertyName;
    }
}
