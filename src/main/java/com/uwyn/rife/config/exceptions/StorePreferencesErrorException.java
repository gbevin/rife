/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.config.exceptions;

import java.util.prefs.Preferences;

public class StorePreferencesErrorException extends ConfigErrorException
{
    private static final long serialVersionUID = -1781853604693398578L;
    private Preferences preferences = null;

    public StorePreferencesErrorException(Preferences preferences, Throwable cause)
    {
        super("An error occurred while storing the data to the preferences user node '" + preferences.absolutePath() + "'.", cause);

        this.preferences = preferences;
    }

    public Preferences getPreferences()
    {
        return preferences;
    }
}
