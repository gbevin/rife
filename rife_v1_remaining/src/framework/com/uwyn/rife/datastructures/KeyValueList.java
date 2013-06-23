/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.datastructures;

import com.uwyn.rife.tools.Sort;
import com.uwyn.rife.tools.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class KeyValueList implements Collection, Cloneable
{
    ArrayList<KeyValue> elements = null;

    public KeyValueList()
    {
        elements = new ArrayList<>();
    }

    synchronized public void swap(int position1, int position2)
    {
        KeyValue element1 = this.get(position1);
        KeyValue element2 = this.get(position2);

        this.add(position1 + 1, element2);
        this.remove(position1);
        this.add(position2 + 1, element1);
        this.remove(position2);
    }

    synchronized public void sortKeys()
    {
        (new SortKeys()).sort(this, this.size() - 1, true);
    }

    synchronized public void sortValues()
    {
        (new SortValues()).sort(this, this.size() - 1, true);
    }

    public String getValue(String key)
    {
        KeyValue element = get(key);

        if (null == element)
        {
            return null;
        }
        else
        {
            return element.getValue();
        }
    }

    public synchronized KeyValue getPrevious(int elementIndex)
    {
        if (-1 != elementIndex &&
            elementIndex > 0)
        {
            return get(elementIndex - 1);
        }
        else
        {
            return null;
        }
    }

    public KeyValue getPrevious(String key)
    {
        return getPrevious(indexOf(key));
    }

    public KeyValue getPrevious(KeyValue element)
    {
        return getPrevious(indexOf(element));
    }

    public KeyValue getNext(int elementIndex)
    {
        if (-1 != elementIndex &&
            elementIndex < size() - 1)
        {
            return get(elementIndex + 1);
        }
        else
        {
            return null;
        }
    }

    public KeyValue getNext(String key)
    {
        return getNext(indexOf(key));
    }

    public KeyValue getNext(KeyValue element)
    {
        return getNext(indexOf(element));
    }

    public synchronized Collection<KeyValue> getAll(String key)
    {
        ArrayList<KeyValue> matched_elements = new ArrayList<>();
        KeyValue[] element_array = new KeyValue[size()];
        toArray(element_array);
        int index = 0;
        do
        {
            index = indexOf(index, key);
            if (-1 != index)
            {
                matched_elements.add(element_array[index]);
            }
        }
        while (-1 != index);

        if (matched_elements.isEmpty())
        {
            return null;
        }
        else
        {
            return matched_elements;
        }
    }

    public boolean contains(String key)
    {
        if (!elements.isEmpty())
        {
            for (KeyValue element : elements)
            {
                if (element.getKey().equals(key))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean contains(Object keyvalue)
    {
        return null != keyvalue && keyvalue instanceof KeyValue && elements.contains(keyvalue);
    }

    public boolean contains(String key, String value)
    {
        return elements.contains(new KeyValue(key, value));
    }

    public boolean contains(KeyValue element)
    {
        return elements.contains(element);
    }

    public boolean containsAll(Collection collection)
    {
        return elements.containsAll(collection);
    }

    public int indexOf(String key, String value)
    {
        return elements.indexOf(new KeyValue(key, value));
    }

    public int indexOf(KeyValue element)
    {
        return elements.indexOf(element);
    }

    public synchronized int indexOf(int startIndex, KeyValue element)
    {
        if (!elements.isEmpty())
        {
            for (int i = startIndex; i < elements.size(); i++)
            {
                if (elements.get(i).equals(element))
                {
                    return i;
                }
            }
        }

        return -1;
    }

    public synchronized int indexOf(String key)
    {
        return indexOf(0, key);
    }

    public synchronized int indexOf(int startIndex, String key)
    {
        if (!elements.isEmpty())
        {
            for (int i = startIndex; i < elements.size(); i++)
            {
                if (elements.get(i).getKey().equals(key))
                {
                    return i;
                }
            }
        }

        return -1;
    }

    public synchronized boolean add(Object element)
    {
        return element instanceof KeyValue && add((KeyValue)element);
    }

    public synchronized boolean add(String key, String value)
    {
        return elements.add(new KeyValue(key, value));
    }

    public synchronized boolean add(KeyValue element)
    {
        return elements.add(element);
    }

    public synchronized boolean add(KeyValueList source)
    {
        boolean result = true;

        for (Object element : source)
        {
            if (!add(((KeyValue)element).clone()))
            {
                result = false;
            }
        }

        return result;
    }

    public synchronized void add(int index, String key, String value)
    {
        elements.add(index, new KeyValue(key, value));
    }

    public synchronized void add(int index, KeyValue element)
    {
        elements.add(index, element);
    }

    public boolean addAll(Collection collection)
    {
        boolean result = true;

        for (Object element : collection)
        {
            if (!add(element))
            {
                result = false;
            }
        }

        return result;
    }

    public synchronized void addAfter(KeyValue existingElement, String key, String value)
    {
        elements.add(indexOf(existingElement) + 1, new KeyValue(key, value));
    }

    public synchronized void addAfter(KeyValue existingElement, KeyValue newElement)
    {
        elements.add(indexOf(existingElement) + 1, newElement);
    }

    public synchronized void addBefore(KeyValue existingElement, String key, String value)
    {
        elements.add(indexOf(existingElement), new KeyValue(key, value));
    }

    public synchronized void addBefore(KeyValue existingElement, KeyValue newElement)
    {
        elements.add(indexOf(existingElement), newElement);
    }

    public synchronized boolean remove(Object element)
    {
        return element instanceof KeyValue && remove((KeyValue)element);
    }

    public synchronized boolean remove(String key, String value)
    {
        return elements.remove(new KeyValue(key, value));
    }

    public synchronized boolean remove(KeyValue element)
    {
        return elements.remove(element);
    }

    public synchronized void remove(int index)
    {
        elements.remove(index);
    }

    public boolean removeAll(Collection collection)
    {
        return elements.removeAll(collection);
    }

    public boolean retainAll(Collection collection)
    {
        return elements.retainAll(collection);
    }

    public synchronized boolean remove(String key)
    {
        if (!elements.isEmpty())
        {
            for (KeyValue element : elements)
            {
                if (element.getKey().equals(key))
                {
                    return elements.remove(element);
                }
            }
        }

        return false;
    }

    public void clear()
    {
        elements.clear();
    }

    public synchronized Object[] toArray()
    {
        return elements.toArray();
    }

    public synchronized Object[] toArray(Object anArray[])
    {
        elements.toArray(anArray);

        return anArray;
    }

    public synchronized String[] getKeysArray()
    {
        String[] keys = new String[size()];

        int i = 0;
        for (Object key : keys())
        {
            keys[i] = (String)key;
            i++;
        }

        return keys;
    }

    public synchronized void trimToSize()
    {
        elements.trimToSize();
    }

    public synchronized void ensureCapacity(int minCapacity)
    {
        elements.ensureCapacity(minCapacity);
    }

    public int size()
    {
        return elements.size();
    }

    public synchronized void setSize(int newSize)
    {
        if (size() > newSize)
        {
            ArrayList<KeyValue> new_elements = new ArrayList<>(newSize);
            for (int i = 0; i < newSize; i++)
            {
                new_elements.set(i, elements.get(i));
            }
            elements = new_elements;
        }
    }

    public boolean isEmpty()
    {
        return elements.isEmpty();
    }

    public synchronized Iterator<KeyValue> iterator()
    {
        return elements.iterator();
    }

    public synchronized Collection<String> keys()
    {
        ArrayList<String> keys = new ArrayList<>();

        for (KeyValue element : elements)
        {
            keys.add(element.getKey());
        }

        return keys;
    }

    public synchronized Collection<String> values()
    {
        ArrayList<String> values = new ArrayList<>();

        for (KeyValue element : elements)
        {
            values.add(element.getValue());
        }

        return values;
    }

    public synchronized KeyValue get(int index)
    {
        return elements.get(index);
    }

    public synchronized KeyValue get(String key)
    {
        int index = indexOf(key);

        if (-1 == index)
        {
            return null;
        }
        else
        {
            return get(index);
        }
    }

    public synchronized KeyValue first()
    {
        return elements.get(0);
    }

    public synchronized KeyValue last()
    {
        return elements.get(size() - 1);
    }

    public synchronized void set(int index, String key, String value)
    {
        elements.set(index, new KeyValue(key, value));
    }

    public synchronized void set(int index, KeyValue element)
    {
        elements.set(index, element);
    }

    public synchronized KeyValueList clone()
    {
        KeyValueList new_object;
        try
        {
            new_object = (KeyValueList)super.clone();

            if (null != new_object.elements)
            {
                new_object.elements = (ArrayList<KeyValue>)elements.clone();

                for (int i = 0; i < elements.size(); i++)
                {
                    new_object.elements.set(i, elements.get(i).clone());
                }
            }
        }
        catch (CloneNotSupportedException e)
        {
            new_object = null;
        }

        return new_object;
    }

    public String toHtml()
    {
        StringBuilder html = new StringBuilder();

        if (!elements.isEmpty())
        {
            for (KeyValue element : elements)
            {
                html.append("<b>").append(StringUtils.encodeHtml(element.getKey())).append("</b> = &quot;").append(StringUtils.encodeHtml(element.getValue())).append("&quot;<br/>\n");
            }
        }

        return html.toString();
    }

    public String toString()
    {
        StringBuilder output = new StringBuilder();

        if (!elements.isEmpty())
        {
            for (KeyValue element : elements)
            {
                output.append(element.getValue()).append(" ");
            }
        }

        return output.toString();
    }

    public String toStringVerbose()
    {
        StringBuilder html = new StringBuilder();

        if (!elements.isEmpty())
        {
            for (KeyValue element : elements)
            {
                html.append(element.getKey()).append(" = \"").append(element.getValue()).append("\"\n");
            }
        }

        return html.toString();
    }

    private class SortKeys extends Sort
    {
        public void swap(Object dataToSort, int position1, int position2)
        {
            ((KeyValueList)dataToSort).swap(position1, position2);
        }

        public Object elementAt(Object dataToSort, int position)
        {
            return ((KeyValueList)dataToSort).get(position).getKey();
        }

        public int compare(Object element1, Object element2)
        {
            return ((String)element1).compareTo((String)element2);
        }
    }

    private class SortValues extends Sort
    {
        public void swap(Object dataToSort, int position1, int position2)
        {
            ((KeyValueList)dataToSort).swap(position1, position2);
        }

        public Object elementAt(Object dataToSort, int position)
        {
            return ((KeyValueList)dataToSort).get(position).getValue();
        }

        public int compare(Object element1, Object element2)
        {
            return ((String)element1).compareTo((String)element2);
        }
    }
}
