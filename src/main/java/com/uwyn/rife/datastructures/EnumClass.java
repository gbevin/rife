/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.datastructures;

import java.util.Collection;
import java.util.HashMap;

/**
 * The purpose of this abstract base class is to allow the creation of
 * type-safe enumerations.
 * <p>Only the derived class is allowed to create instances and should do so
 * as <code>public static final</code> objects.
 * <p>Each instance of a <code>EnumClass</code> class needs an identifier to
 * its constructor. This identifier is used to uniquely differentiate
 * enumeration members amongst each-other.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @since 1.0
 */
public abstract class EnumClass<IdentifierType>
{
    private static HashMap<String, HashMap<Object, EnumClass>> types = new HashMap<>();
    protected IdentifierType identifier = null;

    protected EnumClass(IdentifierType identifier)
    {
        registerType(this.getClass(), identifier);
    }

    protected EnumClass(Class klass, IdentifierType identifier)
    {
        registerType(klass, identifier);
    }

    protected static Collection<?> getIdentifiers(Class<? extends EnumClass> type)
    {
        return types.get(type.getName()).keySet();
    }

    protected static Collection<? extends EnumClass> getMembers(Class<? extends EnumClass> type)
    {
        return types.get(type.getName()).values();
    }

    protected static <MemberType extends EnumClass> MemberType getMember(Class<MemberType> type, Object identifier)
    {
        return (MemberType)types.get(type.getName()).get(identifier);
    }

    protected final void registerType(Class klass, IdentifierType identifier)
    {
        assert klass != null;
        assert identifier != null;

        String class_name = klass.getName();
        HashMap<Object, EnumClass> instances;

        if (!types.containsKey(class_name))
        {
            instances = new HashMap<>();
            types.put(class_name, instances);
        }
        else
        {
            instances = types.get(class_name);
        }
        this.identifier = identifier;
        instances.put(this.identifier, this);
    }

    public IdentifierType getIdentifier()
    {
        return identifier;
    }

    public String toString()
    {
        return identifier.toString();
    }

    public int hashCode()
    {
        return identifier.hashCode();
    }

    public boolean equals(Object object)
    {
        if (null == object)
        {
            return false;
        }

        if (object instanceof EnumClass)
        {
            EnumClass other_enumclass = (EnumClass)object;
            return other_enumclass.identifier.equals(identifier);
        }

        return object.equals(identifier);
    }
}
