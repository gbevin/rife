/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

public abstract class TerracottaUtils
{
    private static transient Boolean IS_TC_PRESENT = null;

    public static boolean isTcPresent()
    {
        if (null == IS_TC_PRESENT)
        {
            IS_TC_PRESENT = Boolean.getBoolean("tc.active");
        }

        return IS_TC_PRESENT;
    }
}

