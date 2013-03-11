/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc;

import com.uwyn.rife.ioc.exceptions.ParticipantUnknownException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;
import com.uwyn.rife.rep.Participant;
import com.uwyn.rife.rep.Rep;

/**
 * Retrieves a property value as an object from a participant.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @since 1.0
 */
public class PropertyValueParticipant implements PropertyValue
{
    private String name = null;
    private PropertyValue key = null;

    /**
     * The constructor that stores the retrieval parameters.
     *
     * @param name the participant's name
     * @param key  the key that will be used to look up the participant's
     *             object
     * @since 1.0
     */
    public PropertyValueParticipant(String name, PropertyValue key)
    {
        this.name = name;
        this.key = key;
    }

    public Object getValue()
    throws PropertyValueException
    {
        Participant participant = Rep.getParticipant(name);
        if (null == participant)
        {
            throw new ParticipantUnknownException(name);
        }

        Object key = null;
        if (this.key != null)
        {
            key = this.key.getValue();
        }
        return participant.getObject(key);
    }

    public String getValueString()
    throws PropertyValueException
    {
        return String.valueOf(getValue());
    }

    public String toString()
    {
        return getValueString();
    }

    public boolean isNeglectable()
    {
        return false;
    }

    public boolean isStatic()
    {
        return false;
    }
}
