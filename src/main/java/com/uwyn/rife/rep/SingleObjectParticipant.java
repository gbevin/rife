/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep;

/**
 * Convenience abstract base class to make it easy to implement participants
 * that only provide a single data object.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see Participant
 * @since 1.0
 */
public abstract class SingleObjectParticipant implements Participant
{
    public abstract Object getObject();

    public Object getObject(Object key)
    {
        return getObject();
    }

    public boolean isFinished()
    {
        return true;
    }
}
