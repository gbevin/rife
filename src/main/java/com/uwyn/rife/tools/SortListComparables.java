/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

import java.util.List;

/**
 * Extends the <code>Sort</code> class to implement the features that are
 * needed to sort an <code>List</code> datastructure containing only
 * objects that implement the <code>Comparable</code> interface.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see Sort
 * @since 1.0
 */
public class SortListComparables extends Sort
{
    /**
     * Starts the sorting of the entire ArrayList.
     *
     * @param dataToSort An <code>ArrayList</code> instance that has to be
     *                   sorted.
     * @since 1.0
     */
    public final void sort(List<? extends Comparable> dataToSort)
    {
        if (null == dataToSort) throw new IllegalArgumentException("dataToSort can't be null");

        if (dataToSort.size() > 0)
        {
            quickSort(dataToSort, 0, dataToSort.size() - 1, true);
        }
    }

    /**
     * Starts the sorting of the entire ArrayList.
     *
     * @param dataToSort An <code>ArrayList</code> instance that has to be
     *                   sorted.
     * @param ascending  true of the data has to be sorted in an ascending
     *                   fashion and false otherwise
     * @since 1.0
     */
    public final void sort(List<? extends Comparable> dataToSort, boolean ascending)
    {
        if (null == dataToSort) throw new IllegalArgumentException("dataToSort can't be null");

        if (dataToSort.size() > 0)
        {
            quickSort(dataToSort, 0, dataToSort.size() - 1, ascending);
        }
    }

    protected void swap(Object dataToSort, int position1, int position2)
    {
        List data_to_sort = (List)dataToSort;
        Object element1 = data_to_sort.get(position1);
        Object element2 = data_to_sort.get(position2);

        data_to_sort.add(position1 + 1, element2);
        data_to_sort.remove(position1);
        data_to_sort.add(position2 + 1, element1);
        data_to_sort.remove(position2);
    }

    protected Object elementAt(Object dataToSort, int position)
    {
        return ((List)dataToSort).get(position);
    }

    protected int compare(Object element1, Object element2)
    {
        return ((Comparable)element1).compareTo(element2);
    }
}


