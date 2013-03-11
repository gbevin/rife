/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ManyToManyList.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class ManyToManyList<E> extends AbstractManyToManyCollection<E> implements List<E>
{
	private List<E> mDelegate;
	
	ManyToManyList(AbstractGenericQueryManager manager, String columnName1, int objectId, ManyToManyDeclaration declaration)
	{
		super(manager, columnName1, objectId, declaration);
	}
	
	protected void ensurePopulatedDelegate()
	{
		if (null == mDelegate)
		{
			mDelegate = restoreManyToManyMappings();
		}
	}
	
	public int size()
	{
		ensurePopulatedDelegate();
		return mDelegate.size();
	}
	
	public boolean isEmpty()
	{
		ensurePopulatedDelegate();
		return mDelegate.isEmpty();
	}
	
	public boolean contains(Object o)
	{
		ensurePopulatedDelegate();
		return mDelegate.contains(o);
	}
	
	public Iterator<E> iterator()
	{
		ensurePopulatedDelegate();
		return mDelegate.iterator();
	}
	
	public Object[] toArray()
	{
		ensurePopulatedDelegate();
		return mDelegate.toArray();
	}
	
	public <T extends Object> T[] toArray(T[] a)
	{
		ensurePopulatedDelegate();
		return mDelegate.toArray(a);
	}
	
	public boolean add(E o)
	{
		ensurePopulatedDelegate();
		return mDelegate.add(o);
	}
	
	public boolean remove(Object o)
	{
		ensurePopulatedDelegate();
		return mDelegate.remove(o);
	}
	
	public boolean containsAll(Collection<?> c)
	{
		ensurePopulatedDelegate();
		return mDelegate.containsAll(c);
	}
	
	public boolean addAll(Collection<? extends E> c)
	{
		ensurePopulatedDelegate();
		return mDelegate.addAll(c);
	}
	
	public boolean addAll(int index, Collection<? extends E> c)
	{
		ensurePopulatedDelegate();
		return mDelegate.addAll(index, c);
	}
	
	public boolean removeAll(Collection<?> c)
	{
		ensurePopulatedDelegate();
		return mDelegate.removeAll(c);
	}
	
	public boolean retainAll(Collection<?> c)
	{
		ensurePopulatedDelegate();
		return mDelegate.retainAll(c);
	}
	
	public void clear()
	{
		ensurePopulatedDelegate();
		mDelegate.clear();
	}
	
	public E get(int index)
	{
		ensurePopulatedDelegate();
		return mDelegate.get(index);
	}
	
	public E set(int index, E element)
	{
		ensurePopulatedDelegate();
		return mDelegate.set(index, element);
	}
	
	public void add(int index, E element)
	{
		ensurePopulatedDelegate();
		mDelegate.add(index, element);
	}
	
	public E remove(int index)
	{
		ensurePopulatedDelegate();
		return mDelegate.remove(index);
	}
	
	public int indexOf(Object o)
	{
		ensurePopulatedDelegate();
		return mDelegate.indexOf(o);
	}
	
	public int lastIndexOf(Object o)
	{
		ensurePopulatedDelegate();
		return mDelegate.lastIndexOf(o);
	}
	
	public ListIterator<E> listIterator()
	{
		ensurePopulatedDelegate();
		return mDelegate.listIterator();
	}
	
	public ListIterator<E> listIterator(int index)
	{
		ensurePopulatedDelegate();
		return mDelegate.listIterator(index);
	}
	
	public List<E> subList(int fromIndex, int toIndex)
	{
		ensurePopulatedDelegate();
		return mDelegate.subList(fromIndex, toIndex);
	}
}
