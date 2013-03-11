/*
 * Copyright 2001-2013 Steven Grimm <koreth[remove] at midwinter dot com>
 * and Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import com.uwyn.rife.ioc.HierarchicalProperties;
import com.uwyn.rife.ioc.exceptions.MandatoryPropertyMissingException;

import java.util.HashMap;
import java.util.Map;

/**
 * Maintains a map of singletons of various classes, instantiating new
 * ones as needed.
 *
 * @author Steven Grimm (koreth[remove] at midwinter dot com)
 * @since 1.6
 */
public class SingletonFactory<T>
{
    private Class<T> klass;
    private Map<String, T> singletons = new HashMap<>();

    public SingletonFactory(Class<T> klass)
    {
        this.klass = klass;
    }

    /**
     * Returns a singleton with a particular identifier, or creates one with
     * a particular class if none exists.
     *
     * @param className  the name of the class a singleton has to be obtained
     *                   for
     * @param identifier an identifier to differentiate several singletons for
     *                   the same class
     * @return the requested singleton instance
     * @since 1.6
     */
    public synchronized T getInstance(String className, String identifier)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        if (singletons.containsKey(identifier))
        {
            return singletons.get(identifier);
        }

        Class<T> klass = (Class)Class.forName(className);
        if (!this.klass.isAssignableFrom(klass))
        {
            throw new ClassCastException("Can't cast " + className + " to " + this.klass.getName());
        }

        T obj = klass.newInstance();
        singletons.put(identifier, obj);
        return obj;
    }

    /**
     * Returns a singleton instance of a class.
     *
     * @param className the name of the class a singleton has to be obtained
     *                  for
     * @return the requested singleton instance
     * @since 1.6
     */
    public T getInstance(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        return getInstance(className, className);
    }

    /**
     * Returns an instance of a class based on a required property name from a
     * properties collection.
     *
     * @param properties   The properties where the class name has to be obtained from
     * @param propertyName Which property contains the class name
     * @param relativeTo   Class whose package should be used if no package is
     *                     specified in the property
     * @return the requested singleton instance
     * @since 1.6
     */
    public T getInstance(HierarchicalProperties properties, String propertyName, Class relativeTo)
            throws MandatoryPropertyMissingException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        String className = properties.getValueString(propertyName);
        if (null == className)
        {
            throw new MandatoryPropertyMissingException(propertyName);
        }

        if (!className.contains("."))
        {
            className = relativeTo.getPackage().getName() + "." + className;
        }

        return getInstance(className);
    }
}
