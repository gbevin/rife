/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import junit.framework.TestCase;

public class TestLocalizationUtils extends TestCase
{
    public TestLocalizationUtils(String name)
    {
        super(name);
    }

    public void testExtractLocalizedUrl()
    {
        assertEquals("/root", Localization.extractLocalizedUrl("en:/root,nl:/wortel"));
        assertEquals("/logout", Localization.extractLocalizedUrl("nl:/afmelden,/logout,fr:/deconnection"));
        assertNull(Localization.extractLocalizedUrl(null));
        assertEquals("/root", Localization.extractLocalizedUrl("/root"));
        assertEquals(null, Localization.extractLocalizedUrl("nl:/afmelden,fr:/deconnection"));
        assertEquals("http://www.uwyn.com", Localization.extractLocalizedUrl("http://www.uwyn.com"));
        assertEquals("http://www.uwyn.com", Localization.extractLocalizedUrl("fr:/racine,en:http://www.uwyn.com,nl:/wortel"));
    }
}
