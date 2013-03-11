/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import java.util.Date;

public abstract class ClassUtils
{
    public static boolean isNumeric(Class klass)
    {
        if (Number.class.isAssignableFrom(klass) ||
            byte.class == klass ||
            short.class == klass ||
            int.class == klass ||
            long.class == klass ||
            float.class == klass ||
            double.class == klass)
        {
            return true;
        }

        return false;
    }

    public static boolean isText(Class klass)
    {
        if (CharSequence.class.isAssignableFrom(klass) ||
            Character.class == klass ||
            char.class == klass)
        {
            return true;
        }

        return false;
    }

    public static boolean isBasic(Class klass)
    {
        if (null == klass)
        {
            return false;
        }

        if (isNumeric(klass) ||
            boolean.class == klass ||
            Boolean.class == klass ||
            Date.class.isAssignableFrom(klass) ||
            klass.isEnum() ||
            isText(klass))
        {
            return true;
        }

        return false;
    }

    public static String simpleClassName(Class klass)
    {
        String class_name = klass.getName();
        if (klass.getPackage() != null)
        {
            class_name = class_name.substring(klass.getPackage().getName().length() + 1);
        }

        return class_name;
    }

    public static String shortenClassName(Class klass)
    {
        return simpleClassName(klass).replace('$', '_').toLowerCase();
    }

    public static String[] getEnumClassValues(Class klass)
    {
        if (JavaSpecificationUtils.isAtLeastJdk15() &&
            klass.isEnum())
        {
            Object[] values = klass.getEnumConstants();
            return ArrayUtils.createStringArray(values);
        }

        return null;
    }
}