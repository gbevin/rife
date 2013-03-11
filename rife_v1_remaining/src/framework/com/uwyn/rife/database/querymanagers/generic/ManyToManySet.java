/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com) and
 * JR Boyens <gnu-jrb[remove] at gmx dot net>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ManyToManySet.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database.querymanagers.generic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class ManyToManySet<E> extends AbstractManyToManyCollection<E> implements Set<E>
{
	private Set<E> mDelegate;
	
	ManyToManySet(AbstractGenericQueryManager manager, String columnName1, int objectId, ManyToManyDeclaration declaration)
	{
		super(manager, columnName1, objectId, declaration);
	}
	
	protected void ensurePopulatedDelegate()
	{
		if (null == mDelegate)
		{
			mDelegate = new HashSet<E>(restoreManyToManyMappings());
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
}
