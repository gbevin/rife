/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep;

/**
 * A <code>Participant</code> is basically a service that needs to be
 * initialized before it can return objects that correspond to specified
 * identification keys.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see Rep
 * @since 1.0
 */
public interface Participant
{
    /**
     * Returns the default object for this participant.
     *
     * @return <code>an <code>Object</code> instance containing the default
     *         object</code>; or
     *         <p><code>null</code> if no default object exists
     * @since 1.0
     */
    public Object getObject();

    /**
     * Retrieves the object from the participant that corresponds to a
     * particular key.
     *
     * @param key An <code>Object</code> instance that used as the key to look
     *            up a corresponding object from the participant.
     * @return the requested <code>Object</code> instance; or
     *         <p><code>null</code> if no object could be found that corresponds to
     *         the provided key
     * @since 1.0
     */
    public Object getObject(Object key);

    /**
     * Checks if the initialization of this participant is finished.
     *
     * @return <code>true</code> if the initialization is finished; or
     *         <p><code>false</code> if the initialization is in progress
     * @since 1.0
     */
    public boolean isFinished();
}


