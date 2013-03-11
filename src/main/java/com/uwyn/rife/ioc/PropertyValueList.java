/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc;

import com.uwyn.rife.ioc.exceptions.PropertyValueException;

import java.util.ArrayList;

/**
 * An ordered list of property values.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @since 1.0
 */
public class PropertyValueList extends ArrayList<PropertyValue>
{
    private static final long serialVersionUID = -7791482346118685259L;

    /**
     * Interpretes the list of property values and make one new property value
     * out of it.
     *
     * @return the new <code>PropertyValue</code> instance
     * @since 1.0
     */
    public PropertyValue makePropertyValue()
            throws PropertyValueException
    {
        // evaluate the current property values series and check if this should be
        // interpreted as a text result or as a participant value
        PropertyValue result = null;

        PropertyValue non_neglectablepropval = null;
        for (PropertyValue propval : this)
        {
            if (!propval.isNeglectable())
            {
                if (non_neglectablepropval != null)
                {
                    non_neglectablepropval = null;
                    break;
                }

                non_neglectablepropval = propval;
            }
        }

        if (non_neglectablepropval != null)
        {
            if (non_neglectablepropval.isStatic())
            {
                result = new PropertyValueObject(non_neglectablepropval.getValueString().trim());
            }
            else
            {
                result = non_neglectablepropval;
            }
        }

        if (null == result)
        {
            StringBuilder key_text = new StringBuilder();
            for (PropertyValue propval : this)
            {
                key_text.append(propval.getValueString());
            }
            result = new PropertyValueObject(key_text.toString().trim());
        }

        return result;
    }
}
