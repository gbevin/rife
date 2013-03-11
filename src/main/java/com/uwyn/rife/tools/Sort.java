/*
 * Copyright 2001-2013 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.tools;

/**
 * Generic class that implements the quicksort algorithm. Extending classes
 * have to implement the abstract methods so that the sorting algorithm can
 * perform the appropriate modifications to the extending class's
 * datastructure.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @since 1.0
 */
public abstract class Sort
{
    /**
     * Starts the sorting of the provided datastructure.
     *
     * @param dataToSort          An <code>Object</code> instance that points to the
     *                            datastructure that has to be sorted. The extending class should know how
     *                            to manipulate this particular datastructure.
     * @param lastElementPosition An integer that specifies the position of the
     *                            last element in the provided datastructure.
     * @param ascending           true of the data has to be sorted in an ascending
     *                            fashion and false otherwise
     * @since 1.0
     */
    public final void sort(Object dataToSort, int lastElementPosition, boolean ascending)
    {
        if (null == dataToSort) throw new IllegalArgumentException("dataToSort can't be null");
        if (lastElementPosition < 0) throw new IllegalArgumentException("lastElementPosition has to be bigger than 0.");

        quickSort(dataToSort, 0, lastElementPosition, ascending);
    }

    /**
     * This method contains the actual sorting algorithm.
     *
     * @param dataToSort An <code>Object</code> instance that points to the
     *                   datastructure that has to be sorted.
     * @param lo0        An integer indicating the bottom boundary of the range that
     *                   will be sorted.
     * @param hi0        An integer indicating the upper boundary of the range that
     *                   will be sorted.
     * @param ascending  true of the data has to be sorted in an ascending
     *                   fashion and false otherwise
     * @since 1.0
     */
    protected final void quickSort(Object dataToSort, int lo0, int hi0, boolean ascending)
    {
        int lo = lo0;
        int hi = hi0;

        if (hi0 > lo0)
        {
            Object mid_element = elementAt(dataToSort, (lo0 + hi0) / 2);

            while (lo <= hi)
            {
                if (ascending)
                {
                    while ((lo < hi0) &&
                           compare(elementAt(dataToSort, lo), mid_element) < 0)
                    {
                        lo++;
                    }
                    while ((hi > lo0) &&
                           compare(elementAt(dataToSort, hi), mid_element) > 0)
                    {
                        hi--;
                    }
                }
                else
                {
                    while ((lo < hi0) &&
                           compare(elementAt(dataToSort, lo), mid_element) > 0)
                    {
                        lo++;
                    }
                    while ((hi > lo0) &&
                           compare(elementAt(dataToSort, hi), mid_element) < 0)
                    {
                        hi--;
                    }
                }

                if (lo <= hi)
                {
                    swap(dataToSort, lo, hi);
                    lo++;
                    hi--;
                }
            }

            if (lo0 < hi)
            {
                quickSort(dataToSort, lo0, hi, ascending);
            }

            if (lo < hi0)
            {
                quickSort(dataToSort, lo, hi0, ascending);
            }
        }
    }

    /**
     * Swaps the position of two entries in the provided datastructure. This is
     * an abstract method that needs to be implemented by every extending class.
     *
     * @param dataToSort An <code>Object</code> instance that points to the
     *                   datastructure in which the entries have to be swapped.
     * @param position1  An integer with the position of the first entry.
     * @param position2  An integer with the position of the second entry.
     * @since 1.0
     */
    protected abstract void swap(Object dataToSort, int position1, int position2);

    /**
     * Retrieves an entry from a certain position within the specified
     * datastructure.
     *
     * @param dataToSort An <code>Object</code> instance that points to the
     *                   datastructure from where the entry has to be retrieved.
     * @param position   An integer with the position of the entry that has to be
     *                   retrieved.
     * @return An <code>Object</code> instace containing the entry at the
     *         specified position.
     * @since 1.0
     */
    protected abstract Object elementAt(Object dataToSort, int position);

    /**
     * Compares the entries, determining which one comes before the other.
     *
     * @param element1 An <code>Object</code> instance containing the first
     *                 entry.
     * @param element2 An <code>Object</code> instance containing the second
     *                 entry.
     * @return <code>0</code> if the two entries are equals; or
     *         <p/>
     *         an integer <code>&lt;0</code> if the first entry comes before the second
     *         one; or
     *         <p/>
     *         an integer <code>&gt;0</code> if the first entry comes after the second
     *         one
     * @since 1.0
     */
    protected abstract int compare(Object element1, Object element2);
}


