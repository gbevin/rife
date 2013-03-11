/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.rep;

import com.uwyn.rife.resources.ResourceFinder;

/**
 * A repository participant is basically a service that needs to be
 * initialized before it can return objects that correspond to specified
 * identification keys.
 * <p>Each participant is launched in a separate thread which is started to
 * perform the initialization. This thread can run in parallel with the
 * initializations of other participants. Whether this is the case is
 * determined by the repository through the <code>blocking</code> parameter
 * that has to be provided during the registration of the participant with the
 * repository.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see Rep
 * @since 1.0
 */
public abstract class BlockingParticipant implements Participant, Runnable
{
    /**
     * Object that is used for thread synchronization of the initialization.
     */
    private final Object initThreadMonitor = new Object();
    /**
     * The repository this participqnt belongs to.
     */
    private BlockingRepository repository = null;
    /**
     * Text message is that retrieved by the repository to describe the
     * function of a particular participant during the initialization.
     */
    private String initializationMessage = null;
    /**
     * Text message is that retrieved by the repository to describe the
     * function of a particular participant during the cleanup.
     */
    private String cleanupMessage = null;
    /**
     * Instance of the <code>ResourceFinder</code> class that is used by the
     * repository when it initializes its participants. This resource finder
     * is only accessible by sub classes through the
     * <code>getResourceFinder()</code> method.
     */
    private ResourceFinder resourcefinder = null;
    /**
     * Indicates whether the initialization of this participant has finished
     * or not.
     */
    private volatile boolean initializationFinished = false;
    /**
     * Indicates whether the initialization of this participant throw an error
     */
    private volatile boolean initializationError = false;
    /**
     * The parameter that has been specified in the configuration file.
     */
    private String parameter = null;
    /**
     * The name of the thread in which the participant will run.
     */
    private String threadName = null;
    /**
     * The thread instance in which the participant will run.
     */
    private Thread thread = null;

    /**
     * Retrieves the name of the thread.
     *
     * @since 1.0
     */
    public String getName()
    {
        if (thread != null)
        {
            return thread.getName();
        }

        return threadName;
    }

    /**
     * Sets the name of the thread.
     *
     * @param name The name of the thread.
     * @since 1.0
     */
    void setName(String name)
    {
        threadName = name;

        if (thread != null)
        {
            thread.setName(threadName);
        }
    }

    /**
     * Performs the actual initialization actions for the participant. This is
     * an abstract method that needs to be implemented by every participant.
     *
     * @since 1.0
     */
    protected abstract void initialize();

    /**
     * Does the actual retrieval of an object from the participant according
     * to a specified key. This method needs to be implemented by every
     * participant that provides access to data, by default it just returns
     * <code>null</code>.
     *
     * @param key An <code>Object</code> instance that is used as the key to
     *            obtain a corresponding object from the participant with.
     * @return <code>null</code> if no object could be found that corresponds
     *         to the provided key; or
     *         <p>an <code>Object</code> instance that corresponds to the provided key
     * @see #getObject()
     * @see #getObject(Object)
     * @since 1.0
     */
    protected Object _getObject(Object key)
    {
        return null;
    }

    /**
     * Performs the actual cleanup actions for the participant. This is method
     * can be overridden when a participant needs to customize the cleanup..
     *
     * @since 1.0
     */
    protected void cleanup()
    {
    }

    /**
     * Retrieves the repository that this participant belongs to.
     *
     * @since 1.0
     */
    public BlockingRepository getRepository()
    {
        return repository;
    }

    /**
     * Sets the repository that this participant belongs to.
     *
     * @param repository an instance of <code>BlockingRepository</code>
     * @see BlockingRepository
     * @since 1.0
     */
    void setRepository(BlockingRepository repository)
    {
        this.repository = repository;
    }

    /**
     * Retrieves the optional parameter.
     *
     * @return <code>null</code> if no parameter was provided; or
     *         <p>the requested parameter <code>String</code> instance otherwise
     * @since 1.0
     */
    public String getParameter()
    {
        return parameter;
    }

    /**
     * Sets the optional parameter.
     *
     * @param parameter A <code>String</code> containing the optional
     *                  parameter for this participant.
     * @since 1.0
     */
    public void setParameter(String parameter)
    {
        this.parameter = parameter;
    }

    /**
     * Retrieves the resource finder that is used during the initialization.
     *
     * @return <code>null</code> if no resource finder was provided or if the
     *         method was called after the initialization; or
     *         <p>the requested <code>ResourceFinder</code> instance otherwise
     * @see #setResourceFinder(ResourceFinder)
     * @since 1.0
     */
    public ResourceFinder getResourceFinder()
    {
        return resourcefinder;
    }

    /**
     * Sets the resource finder that can be used during the
     * <code>initialize()</code> method.
     *
     * @param resourceFinder A <code>ResourceFinder</code> instance containing
     *                       the resource finder that is used during the initialization of the
     *                       repository and its participants.
     * @see #getResourceFinder()
     * @since 1.0
     */
    public void setResourceFinder(ResourceFinder resourceFinder)
    {
        resourcefinder = resourceFinder;
    }

    /**
     * Starts the initialization.
     *
     * @since 1.0
     */
    public final void run()
    {
        // Only initialize once.
        if (!isFinished())
        {
            try
            {
                initialize();
            }
            catch (Throwable e)
            {
                initializationError = true;
                getThread().getThreadGroup().uncaughtException(thread, e);
            }
            finally
            {
                // Obtain a lock on the synchronization monitor of this particular
                // participant to make it possible to notify all waiting threads.
                synchronized (initThreadMonitor)
                {
                    initializationFinished = true;
                    initThreadMonitor.notifyAll();
                    repository.fireInitActionPerformed(this);
                }
            }
        }
    }

    /**
     * Checks if the initialization of this participant is finished.
     *
     * @return <code>true</code> if the initialization is finished; or
     *         <p><code>false</code> if the initialization is in progress
     * @since 1.0
     */
    public final boolean isFinished()
    {
        return initializationFinished;
    }

    /**
     * Checks if the initialization of this participant threw an error.
     *
     * @return <code>true</code> if the initialization threw an error; or
     *         <p><code>false</code> if the initialization was successful
     * @since 1.0
     */
    public boolean hadInitializationError()
    {
        synchronized (initThreadMonitor)
        {
            return initializationError;
        }
    }

    /**
     * Makes the calling thread wait until the initialization of this
     * participant has finished.
     *
     * @since 1.0
     */
    public final void waitUntilFinished()
    {
        // Obtain a lock on the synchronization monitor of this particular
        // participant to make it possible to wait for notifications on this
        // monitor.
        synchronized (initThreadMonitor)
        {
            // Only make the calling thread wait if the initialization is still
            // busy.
            while (!isFinished())
            {
                try
                {
                    initThreadMonitor.wait();
                }
                catch (InterruptedException ignored)
                {
                }
            }
        }
    }

    /**
     * Returns a message that is supposed to describe the initialization of
     * this participant. If no message has been set with <code>setInitializationMessage(String
     * message)</code>, a default message is generated.
     *
     * @return A <code>String</code> containing the message that describes the
     *         initialization of this participant.
     * @see #setInitializationMessage(String)
     * @since 1.0
     */
    public String getInitializationMessage()
    {
        if (null == initializationMessage)
        {
            return "Initializing '" + this.getClass().getName() + "' ...";
        }
        else
        {
            return initializationMessage;
        }
    }

    /**
     * Overrides the default message that describes the initialization of this
     * participant.
     *
     * @param message A <code>String</code> containing the message.
     * @see #getInitializationMessage()
     * @since 1.0
     */
    public void setInitializationMessage(String message)
    {
        initializationMessage = message;
    }

    /**
     * Returns a message that is supposed to describe the cleanup of this
     * participant. If no message has been set with <code>setCleanupMessage(String
     * message)</code>, a default message is generated.
     *
     * @return A <code>String</code> containing the message that describes the
     *         cleanup of this participant.
     * @see #setCleanupMessage(String)
     * @since 1.0
     */
    public String getCleanupMessage()
    {
        if (null == cleanupMessage)
        {
            return "Cleaning up '" + this.getClass().getName() + "' ...";
        }
        else
        {
            return cleanupMessage;
        }
    }

    /**
     * Overrides the default message that describes the cleanup of this
     * participant.
     *
     * @param message A <code>String</code> containing the message.
     * @see #getCleanupMessage()
     * @since 1.0
     */
    public void setCleanupMessage(String message)
    {
        cleanupMessage = message;
    }

    /**
     * Returns the default object for this participant.
     * <p>If the initialization of the participant hasn't finished yet, the
     * thread that executes this method will be suspended and woken up when
     * the initialization finishes.
     *
     * @return <code>null</code> if no default object exists; or
     *         <p>an <code>Object</code> instance containing the default object
     * @see #getObject(Object)
     * @see #_getObject(Object)
     * @since 1.0
     */
    public final Object getObject()
    {
        // if the participant is finished, return the object directly
        if (isFinished())
        {
            return _getObject();
        }

        return getObjectAndWait();
    }

    private Object getObjectAndWait()
    {
        // Obtain a lock on the synchronization monitor of this particular
        // participant to make it possible to wait for notifications on this
        // monitor.
        synchronized (initThreadMonitor)
        {
            // If the initialization hasn't finished, suspend the executing
            // thread.
            while (!isFinished())
            {
                try
                {
                    initThreadMonitor.wait();
                }
                catch (InterruptedException e)
                {
                    // do nothing
                }
            }
            return _getObject();
        }
    }

    protected Object _getObject()
    {
        return getObject(null);
    }

    /**
     * Retrieves the object from the participant that corresponds to a
     * particular key.
     * <p>If the initialization of the participant hasn't finished yet, the
     * thread that executes this method will be suspended and woken up when
     * the initialization finishes.
     *
     * @param key An <code>Object</code> instance that used as the key to
     *            obtain a corresponding object from the participant with.
     * @return <code>null</code> if no object could be found that corresponds
     *         to the provided key; or
     *         <p>the requested <code>Object</code> instance
     * @see #getObject()
     * @see #_getObject(Object)
     * @since 1.0
     */
    public final Object getObject(Object key)
    {
        // if the participant is finished, return the object directly
        if (isFinished())
        {
            return _getObject(key);
        }

        return getObjectAndWait(key);
    }

    private Object getObjectAndWait(Object key)
    {
        // Obtain a lock on the synchronization monitor of this particular
        // participant to make it possible to wait for notifications on this
        // monitor.
        synchronized (initThreadMonitor)
        {
            // If the initialization hasn't finished, suspend the executing
            // thread.
            while (!isFinished())
            {
                try
                {
                    initThreadMonitor.wait();
                }
                catch (InterruptedException e)
                {
                    // do nothing
                }
            }
            return _getObject(key);
        }
    }

    Thread getThread()
    {
        return thread;
    }

    void setThread(Thread thread)
    {
        this.thread = thread;

        if (threadName != null)
        {
            this.thread.setName(threadName);
        }
    }
}

