/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep;

/**
 * An interface that can be implemented to receive notifications about the
 * progress of the initialization of a repository.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see Repository
 * @since 1.0
 */
public interface RepositoryListener
{
    /**
     * This method is called when an action has ended during the repository
     * initialization.
     * <p>Every supporting participant should call this when its own
     * initialization is finished, but also has the possibility to call it
     * explicitely at any moment. This allows them to detail their
     * initialization steps as much as is needed.
     * <p>Typically, the object that implements this method displays the
     * advancement of the repository initialization. Therefore, it's best to
     * first set up all participants. Then, the application is run for the
     * first time to determine exactly how many actions are performed and how
     * they are subdivided for each participant. Using this information, the
     * <code>RepositoryListener</code> can provide a correct visual
     * representation of the initialization process.
     *
     * @param participant The participant that triggered the action.
     * @since 1.0
     */
    public void initActionPerformed(Participant participant);

    /**
     * This method is called when the initialization of the repository has
     * completely finished.
     * <p>This can for instance be used by the <code>RepositoryListener</code>
     * to know when to display the actual application window.
     *
     * @since 1.0
     */
    public void initFinished();
}
