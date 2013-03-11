/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.rep.exceptions.InitializationErrorException;

/**
 * This class implements a wrapper blocking participant that is able to delegate
 * all the logic to another participant, while still being usable in a blocking
 * repository.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see Rep
 * @since 1.0
 */
class BlockingParticipantDelegate extends BlockingParticipant
{
    private Class<Participant> delegateClass = null;
    private Participant delegate = null;

    BlockingParticipantDelegate(Class<Participant> delegateClass)
    {
        this.delegateClass = delegateClass;
    }

    protected void initialize()
    {
        try
        {
            delegate = delegateClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new InitializationErrorException(e);
        }
    }

    protected Object _getObject()
    {
        return delegate.getObject();
    }

    protected Object _getObject(Object key)
    {
        return delegate.getObject(key);
    }
}

