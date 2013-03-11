/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.ioc;

import com.uwyn.rife.ioc.exceptions.IncompatiblePropertyValueTypeException;
import com.uwyn.rife.ioc.exceptions.PropertyValueException;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;

/**
 * This class allows the creation of a hierarchical tree of named {@link
 * PropertyValue} instances.
 * <p>When a property is looked up in a child
 * <code>HierarchicalProperties</code> instance, the lookup will be propagated
 * to its parent when it couldn't be found in the child. A single hierarchical
 * line is thus considered to be one collection that groups all involved
 * <code>HierarchicalProperties</code> instances. Retrieving the names and the
 * size will recursively take all the properties of the parents into account
 * and return the consolidated result. To offer these features, intelligent
 * caching has been implemented to ensure optimal performance.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @since 1.1
 */
public class HierarchicalProperties
{
    private LinkedHashMap<String, PropertyValue> properties;
    private HierarchicalProperties parent;
    private LinkedHashSet<HierarchicalProperties> children;
    private Set<String> cachedNames;
    private Set<String> cachedInjectableNames;

    public HierarchicalProperties()
    {
    }

    private HierarchicalProperties(HierarchicalProperties shadow)
    {
        properties = shadow.properties;
    }

    /**
     * Creates a copy of this <code>HierarchicalProperties</code> hierarchy
     * until a certain instance is reached.
     * <p/>
     * Each copied instance will shared the datastructure in which the
     * properties are stored with the original. Creating a shadow is for
     * changing the hierarchical structure but maintaining a centralized
     * management of the properties.
     *
     * @param limit the <code>HierarchicalProperties</code> instance that will
     *              not be part of the shadow copy and interrupt the copying process; or
     *              <code>null</code> if the entire hierachy should be copied.
     * @return the shadow copy of this <code>HierarchicalProperties</code>
     *         hierarchy
     *         hierarchy
     * @since 1.1
     */
    public HierarchicalProperties createShadow(HierarchicalProperties limit)
    {
        HierarchicalProperties result = new HierarchicalProperties(this);

        HierarchicalProperties original = this;
        HierarchicalProperties shadow = result;
        while (original.getParent() != null &&
               original.getParent() != limit)
        {
            shadow.setParent(new HierarchicalProperties(original.getParent()));
            original = original.getParent();
            shadow = shadow.getParent();
        }
        return result;
    }

    /**
     * Retrieves the first parent of this <code>HierarchicalProperties</code>
     * hierarchy.
     *
     * @return the root of this <code>HierarchicalProperties</code>
     *         hierarchy
     * @since 1.1
     */
    public HierarchicalProperties getRoot()
    {
        HierarchicalProperties root = this;
        while (root.getParent() != null)
        {
            root = root.getParent();
        }

        return root;
    }

    /**
     * Retrieves the <code>Map</code> with only the properties that are
     * locally present in this <code>HierarchicalProperties</code> instance.
     *
     * @return the local <code>Map</code> of this
     *         <code>HierarchicalProperties</code> instance
     * @since 1.1
     */
    public Map<String, PropertyValue> getLocalMap()
    {
        if (null == properties)
        {
            return Collections.EMPTY_MAP;
        }

        return properties;
    }

    /**
     * Sets the parent of this <code>HierarchicalProperties</code> instance.
     *
     * @param parent the parent of this instance; or <code>null</code> if this
     *               instance should be isolated
     * @return this <code>HierarchicalProperties</code> instance
     * @see #getParent
     * @since 1.1
     */
    public HierarchicalProperties parent(HierarchicalProperties parent)
    {
        setParent(parent);

        return this;
    }

    /**
     * Retrieves the parent of this <code>HierarchicalProperties</code>
     * instance.
     *
     * @return the parent of this <code>HierarchicalProperties</code>
     *         instance; or
     *         <p><code>null</code> if this instance is isolated
     * @see #parent
     * @since 1.1
     */
    public HierarchicalProperties getParent()
    {
        return parent;
    }

    /**
     * Sets the parent of this <code>HierarchicalProperties</code> instance.
     *
     * @param parent the parent of this instance; or <code>null</code> if this
     *               instance should be isolated
     * @see #getParent
     * @since 1.1
     */
    public void setParent(HierarchicalProperties parent)
    {
        clearCaches();

        if (this.parent != null)
        {
            this.parent.removeChild(this);
        }

        this.parent = parent;

        if (this.parent != null)
        {
            this.parent.addChild(this);
        }
    }

    /**
     * Associates the specified value with the specified name in this
     * <code>HierarchicalProperties</code> instance. If it previously
     * contained a mapping for this name, the old value is replaced by the
     * specified value.
     *
     * @param name  the name that will be associated with the property
     * @param value the property value that will be associated with the
     *              specified name
     * @return this <code>HierarchicalProperties</code> instance
     * @see #put(String, Object)
     * @see #putAll
     * @since 1.1
     */
    public HierarchicalProperties put(String name, PropertyValue value)
    {
        clearCaches();

        if (null == properties)
        {
            properties = new LinkedHashMap<>();
        }

        properties.put(name, value);

        return this;
    }

    /**
     * Associates the specified fixed object value with the specified name
     * in this <code>HierarchicalProperties</code> instance. If it previously
     * contained a mapping for this name, the old value is replaced by the
     * specified value.
     *
     * @param name  the name that will be associated with the property
     * @param value the property value that will be associated with the
     *              specified name, note that this method will create a {@link PropertyValueObject}
     *              instance that will contain the value in a fixed manner
     * @return this <code>HierarchicalProperties</code> instance
     * @see #put(String, PropertyValue)
     * @see #putAll
     * @since 1.6
     */
    public HierarchicalProperties put(String name, Object value)
    {
        put(name, new PropertyValueObject(value));

        return this;
    }

    /**
     * Removes the mapping for this name from this
     * <code>HierarchicalProperties</code> instance, if it is present.
     *
     * @param name the name that will be removed
     * @return the previously associated value; or
     *         <p><code>null</code> if the name wasn't found in this
     *         <code>HierarchicalProperties</code> instance
     * @since 1.1
     */
    public PropertyValue remove(String name)
    {
        if (null == properties)
        {
            return null;
        }

        clearCaches();

        return properties.remove(name);
    }

    /**
     * Copies all of the named properties from the specified
     * <code>HierarchicalProperties</code> instance to this
     * <code>HierarchicalProperties</code> instance. The effect of this call
     * is equivalent to that of calling {@link #put} on this
     * <code>HierarchicalProperties</code> once for each mapping from the
     * specified <code>HierarchicalProperties</code> instance.
     *
     * @param source the properties that will be stored in this
     *               <code>HierarchicalProperties</code> instance
     * @return this <code>HierarchicalProperties</code> instance
     * @see #put
     * @since 1.1
     */
    public HierarchicalProperties putAll(HierarchicalProperties source)
    {
        clearCaches();

        if (source.properties != null)
        {
            if (null == properties)
            {
                properties = new LinkedHashMap<>();
            }

            properties.putAll(source.properties);
        }

        return this;
    }

    /**
     * Copies all of the named properties from the specified
     * <code>HierarchicalProperties</code> instance to this
     * <code>HierarchicalProperties</code> instance, without replacing existing
     * properties. The effect of this call
     * is equivalent to that of calling {@link #put} on this
     * <code>HierarchicalProperties</code> once for each mapping from the
     * specified <code>HierarchicalProperties</code> instance that doesn't
     * have a key in this instance yet.
     *
     * @param source the properties that will be stored in this
     *               <code>HierarchicalProperties</code> instance
     * @return this <code>HierarchicalProperties</code> instance
     * @see #put
     * @since 1.6.2
     */
    public HierarchicalProperties putAllWithoutReplacing(HierarchicalProperties source)
    {
        clearCaches();

        if (source.properties != null)
        {
            if (null == properties)
            {
                properties = new LinkedHashMap<>();
            }

            for (Map.Entry<String, PropertyValue> entry : source.properties.entrySet())
            {
                if (!properties.containsKey(entry.getKey()))
                {
                    properties.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return this;
    }

    /**
     * Copies all of the entries for a <code>Map</code> instance to this
     * <code>HierarchicalProperties</code> instance.
     *
     * @param source the map entries that will be stored in this
     *               <code>HierarchicalProperties</code> instance
     * @return this <code>HierarchicalProperties</code> instance
     * @since 1.5
     */
    public HierarchicalProperties putAll(Map source)
    {
        if (null == source)
        {
            return this;
        }

        clearCaches();

        if (null == properties)
        {
            properties = new LinkedHashMap<>();
        }

        for (Map.Entry entry : ((Set<Map.Entry>)source.entrySet()))
        {
            properties.put(String.valueOf(entry.getKey()), new PropertyValueObject(entry.getValue()));
        }

        return this;
    }

    /**
     * Checks the <code>HierarchicalProperties</code> hierarchy for the
     * presence of the specified name.
     *
     * @param name the name whose presence will be checked
     * @return <code>true</code> if the name was found; or
     *         <p><code>false</code> otherwise
     * @see #get
     * @since 1.1
     */
    public boolean contains(String name)
    {
        HierarchicalProperties current = this;

        LinkedHashMap<String, PropertyValue> properties;
        while (true)
        {
            properties = current.properties;

            if (properties != null)
            {
                if (properties.containsKey(name))
                {
                    return true;
                }
            }

            if (null == current.parent)
            {
                break;
            }

            current = current.parent;
        }

        return false;
    }

    /**
     * Retrieves the <code>PropertyValue</code> for a specific name from the
     * <code>HierarchicalProperties</code> hierarchy.
     *
     * @param name the name whose associated value will be returned
     * @return the associated <code>PropertyValue</code>; or
     *         <p><code>null</code> if the name could not be found
     * @see #contains
     * @since 1.1
     */
    public PropertyValue get(String name)
    {
        HierarchicalProperties current = this;
        PropertyValue result;

        LinkedHashMap<String, PropertyValue> properties;
        while (true)
        {
            properties = current.properties;

            if (properties != null)
            {
                result = properties.get(name);
                if (result != null)
                {
                    return result;
                }
            }

            if (null == current.parent)
            {
                break;
            }

            current = current.parent;
        }

        return null;
    }

    /**
     * Retrieves the value of <code>PropertyValue</code> for a specific name from
     * the <code>HierarchicalProperties</code> hierarchy.
     *
     * @param name the name whose associated value will be returned
     * @return the associated <code>PropertyValue</code>; or
     *         <p><code>null</code> if the name could not be found
     * @throws PropertyValueException when an error occurred while retrieving the
     *                                property value
     * @see #get
     * @see #getValue(String, Object)
     * @since 1.1
     */
    public Object getValue(String name)
            throws PropertyValueException
    {
        return getValue(name, null);
    }

    /**
     * Retrieves the value of <code>PropertyValue</code> for a specific name from
     * the <code>HierarchicalProperties</code> hierarchy. If the property couldn't
     * be found or if the value was <code>null</code>, the default value will be
     * returned.
     *
     * @param name         the name whose associated value will be returned
     * @param defaultValue the value that should be used as a fallback
     * @return the associated <code>PropertyValue</code>; or
     *         <p>the <code>defaultValue</code> if the property couldn't be found or if
     *         the value was <code>null</code>
     * @throws PropertyValueException when an error occurred while retrieving the
     *                                property value
     * @see #get
     * @see #getValue(String)
     * @since 1.1
     */
    public Object getValue(String name, Object defaultValue)
            throws PropertyValueException
    {
        Object result = null;

        PropertyValue property = get(name);
        if (property != null)
        {
            result = property.getValue();
        }

        if (null == result)
        {
            return defaultValue;
        }

        return result;
    }

    /**
     * Retrieves the string value of <code>PropertyValue</code> for a specific name from
     * the <code>HierarchicalProperties</code> hierarchy.
     *
     * @param name the name whose associated value will be returned
     * @return the string value of the retrieved <code>PropertyValue</code>; or
     *         <p><code>null</code> if the name could not be found
     * @throws PropertyValueException when an error occurred while retrieving the
     *                                property value
     * @see #get
     * @see #getValueString(String, String)
     * @see #getValueTyped
     * @since 1.1
     */
    public String getValueString(String name)
            throws PropertyValueException
    {
        return getValueString(name, null);
    }

    /**
     * Retrieves the string value of <code>PropertyValue</code> for a specific name from
     * the <code>HierarchicalProperties</code> hierarchy. If the property couldn't
     * be found, if the value was <code>null</code> or if the value was empty, the
     * default value will be returned.
     *
     * @param name         the name whose associated value will be returned
     * @param defaultValue the value that should be used as a fallback
     * @return the string value of the retrieved <code>PropertyValue</code>; or
     *         <p>the <code>defaultValue</code> if the property couldn't be found or if
     *         the value was <code>null</code> or an empty string
     * @throws PropertyValueException when an error occurred while retrieving the
     *                                property value
     * @see #get
     * @see #getValueString(String)
     * @see #getValueTyped
     * @since 1.1
     */
    public String getValueString(String name, String defaultValue)
            throws PropertyValueException
    {
        String result = null;

        PropertyValue property = get(name);
        if (property != null)
        {
            result = property.getValueString();
        }

        if (null == result ||
            0 == result.length())
        {
            return defaultValue;
        }

        return result;
    }

    /**
     * Retrieves the typed value of <code>PropertyValue</code> for a specific name from
     * the <code>HierarchicalProperties</code> hierarchy.
     * <p/>
     * Note that no conversion will occurr, the value is simple verified to be
     * assignable to the requested type and then casted to it.
     *
     * @param name the name whose associated value will be returned
     * @param type the class that the value has to be retrieved as
     * @return the associated <code>PropertyValue</code> as an instance of the
     *         provided type;  or
     *         <p><code>null</code> if the name could not be found
     * @throws IncompatiblePropertyValueTypeException
     *                                when the type of the property
     *                                value wasn't compatible with the requested type
     * @throws PropertyValueException when an error occurred while retrieving the
     *                                property value
     * @see #get
     * @see #getValueString
     * @see #getValueTyped(String, Class)
     * @since 1.6
     */
    public <T> T getValueTyped(String name, Class<T> type)
            throws PropertyValueException
    {
        return getValueTyped(name, type, null);
    }

    /**
     * Retrieves the typed value of <code>PropertyValue</code> for a specific name from
     * the <code>HierarchicalProperties</code> hierarchy.
     * <p/>
     * Note that no conversion will occurr, the value is simple verified to be
     * assignable to the requested type and then casted to it.
     *
     * @param name         the name whose associated value will be returned
     * @param type         the class that the value has to be retrieved as
     * @param defaultValue the value that should be used as a fallback
     * @return the associated <code>PropertyValue</code> as an instance of the
     *         provided type;  or
     *         <p>the <code>defaultValue</code> if the property couldn't be found or if
     *         the value was <code>null</code>
     * @throws IncompatiblePropertyValueTypeException
     *                                when the type of the property
     *                                value wasn't compatible with the requested type
     * @throws PropertyValueException when an error occurred while retrieving the
     *                                property value
     * @see #get
     * @see #getValueString
     * @see #getValueTyped(String, Class)
     * @since 1.6
     */
    public <T> T getValueTyped(String name, Class<T> type, T defaultValue)
            throws PropertyValueException
    {
        if (null == name ||
            null == type ||
            0 == name.length())
        {
            return defaultValue;
        }

        Object result = null;

        PropertyValue property = get(name);
        if (property != null)
        {
            result = property.getValue();
        }

        if (null == result)
        {
            return defaultValue;
        }

        if (!type.isAssignableFrom(result.getClass()))
        {
            throw new IncompatiblePropertyValueTypeException(name, type, result.getClass(), null);
        }

        return (T)result;
    }

    /**
     * Retrieves the number of unique names in the
     * <code>HierarchicalProperties</code> hierarchy.
     *
     * @return the amount of unique names
     * @since 1.1
     */
    public int size()
    {
        return getNames().size();
    }

    /**
     * Retrieves a <code>Set</code> with the unique names that are present in
     * the <code>HierarchicalProperties</code> hierarchy.
     *
     * @return a collection with the unique names
     * @see #getInjectableNames
     * @since 1.1
     */
    public Collection<String> getNames()
    {
        if (cachedNames != null)
        {
            return cachedNames;
        }

        HierarchicalProperties current = this;
        Set<String> names = new LinkedHashSet<>();

        LinkedHashMap<String, PropertyValue> properties;
        while (true)
        {
            properties = current.properties;

            if (properties != null)
            {
                names.addAll(properties.keySet());
            }

            if (null == current.parent)
            {
                break;
            }

            current = current.parent;
        }

        cachedNames = names;

        return names;
    }

    /**
     * Retrieves a <code>Set</code> with the unique names that are present in
     * the <code>HierarchicalProperties</code> hierarchy and that conform to
     * the <a
     * href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.8">Java
     * rules for valid identifiers</a>. The names in this set are thus usable
     * for injection through bean setters.
     *
     * @return a <code>Set</code> with the unique injectable names
     * @see #getNames
     * @since 1.1
     */
    public Collection<String> getInjectableNames()
    {
        if (cachedInjectableNames != null)
        {
            return cachedInjectableNames;
        }

        Set<String> injectable_names = new LinkedHashSet<>();

        Collection<String> names = getNames();
        for (String name : names)
        {
            boolean injectable = true;
            CharacterIterator it = new StringCharacterIterator(name);
            for (char c = it.first(); c != CharacterIterator.DONE; c = it.next())
            {
                if (!Character.isJavaIdentifierPart(c))
                {
                    injectable = false;
                    break;
                }
            }

            if (injectable)
            {
                injectable_names.add(name);
            }
        }

        cachedInjectableNames = injectable_names;

        return injectable_names;
    }

    private void clearCaches()
    {
        cachedNames = null;
        cachedInjectableNames = null;

        if (null == children)
        {
            return;
        }

        for (HierarchicalProperties child : children)
        {
            child.clearCaches();
        }
    }

    private void addChild(HierarchicalProperties child)
    {
        if (null == children)
        {
            children = new LinkedHashSet<>();
        }
        children.add(child);
    }

    private void removeChild(HierarchicalProperties child)
    {
        if (null == children)
        {
            return;
        }
        children.remove(child);
    }
}
