/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.tools.exceptions.SourceBeanRequiredException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

public abstract class AbstractPropertyChangeSupport implements Cloneable
{
    private PropertyChangeSupport propertyChangeSupport = null;

    protected AbstractPropertyChangeSupport()
    {
    }

    protected void setSource(Object sourceBean)
    {
        propertyChangeSupport = new PropertyChangeSupport(sourceBean);
    }

    private void ensureSourceBean()
    {
        if (null == propertyChangeSupport)
        {
            throw new SourceBeanRequiredException(getClass());
        }
    }

    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        ensureSourceBean();
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        ensureSourceBean();
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * @see PropertyChangeSupport#firePropertyChange(String, boolean, boolean)
     */
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
    {
        ensureSourceBean();
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @see PropertyChangeSupport#firePropertyChange(String, int, int)
     */
    public void firePropertyChange(String propertyName, int oldValue, int newValue)
    {
        ensureSourceBean();
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @see PropertyChangeSupport#firePropertyChange(PropertyChangeEvent)
     */
    public void firePropertyChange(PropertyChangeEvent evt)
    {
        ensureSourceBean();
        propertyChangeSupport.firePropertyChange(evt);
    }

    /**
     * @see PropertyChangeSupport#firePropertyChange(String, Object, Object)
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        ensureSourceBean();
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * @see PropertyChangeSupport#getPropertyChangeListeners(String)
     */
    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName)
    {
        ensureSourceBean();
        return propertyChangeSupport.getPropertyChangeListeners(propertyName);
    }

    /**
     * @see PropertyChangeSupport#getPropertyChangeListeners()
     */
    public PropertyChangeListener[] getPropertyChangeListeners()
    {
        ensureSourceBean();
        return propertyChangeSupport.getPropertyChangeListeners();
    }

    /**
     * @see PropertyChangeSupport#hasListeners(String)
     */
    public boolean hasListeners(String propertyName)
    {
        ensureSourceBean();
        return propertyChangeSupport.hasListeners(propertyName);
    }

    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        ensureSourceBean();
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        ensureSourceBean();
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public AbstractPropertyChangeSupport clone()
    {
        AbstractPropertyChangeSupport new_object = null;
        try
        {
            new_object = (AbstractPropertyChangeSupport)super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            ///CLOVER:OFF
            // should never happen
            Logger.getLogger("com.uwyn.rife.tools").severe(ExceptionUtils.getExceptionStackTrace(e));
            ///CLOVER:ON
        }

        new_object.propertyChangeSupport = new PropertyChangeSupport(new_object);

        return new_object;
    }
}

