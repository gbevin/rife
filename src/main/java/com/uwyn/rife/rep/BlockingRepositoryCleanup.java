/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep;

/**
 * The <code>BlockingRepositoryCleanup</code> class is simply a thread that
 * calls the <code>BlockingRepository</code>'s {@link
 * BlockingRepository#cleanup() cleanup} method.
 * <p>It's typically used by the repository to register its own cleanup as a
 * mandatory shutdown hook.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see BlockingRepository#cleanup()
 * @since 1.0
 */
public class BlockingRepositoryCleanup extends Thread
{
    private BlockingRepository repository = null;

    public BlockingRepositoryCleanup(BlockingRepository repository)
    {
        this.repository = repository;
    }

    public void run()
    {
        repository.cleanup();
    }
}
