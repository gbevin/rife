/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: InternalValue.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.template.exceptions.BlockUnknownException;
import com.uwyn.rife.template.exceptions.CircularContructionException;
import com.uwyn.rife.template.exceptions.TemplateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.text.NumberFormat;

/**
 * An anonymous value inside a template, which is not referenced anywhere in
 * the template, but can be used to produce intermediate strings using the
 * template engine. To obtain an <code>InternalValue</code>, you should use
 * {@link Template#createInternalValue()}.
 *
 * @author Keith Lea (keith[remove] at cs dot oswego dot edu)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public class InternalValue
{
    private AbstractTemplate		mTemplate = null;
    private ArrayList<CharSequence>	mConstruction = new ArrayList<CharSequence>();
    private ArrayList<CharSequence>	mValueIds = new ArrayList<CharSequence>();
    private ArrayList<CharSequence>	mValueTags = new ArrayList<CharSequence>();
	
    /**
     * Appends the content of a block to this value. The values used by the
     * block will be captured when this method is called, so any future
     * changes to template values will not affect text which was appended when
     * this method is called.
     *
     * @param blockId the ID of the block whose value should be appended to
     * the specified value
     * @exception TemplateException if the specified block does not exist in
     * the corresponding template
     * @since 1.0
     */
    public void appendBlock(String blockId)
    throws TemplateException
    {
		if (null == blockId)            throw new IllegalArgumentException("blockId can't be null.");
		if (0 == blockId.length())      throw new IllegalArgumentException("blockId can't be empty.");
		
		if (!mTemplate.appendBlockInternalForm(blockId, this))
		{
			throw new BlockUnknownException(blockId);
		}
    }
	
    /**
     * Appends to this value the value of the given internal value.
     *
     * @param value an internal value
     * @since 1.0
     */
    public void appendValue(InternalValue value)
    {
		if (null == value)      throw new IllegalArgumentException("value can't be null.");
		
		appendConstructedValue(value);
    }
	
    /**
     * Appends the result of calling {@link String#valueOf(Object)
     * String.valueOf} on the given <code>value</code> to this value in this
     * template.
     *
     * @param value an object
     * @since 1.0
     */
    public void appendValue(Object value)
    {
		appendValue(String.valueOf(value));
    }
	
    /**
     * Appends <code>"true"</code> or <code>"false"</code> to this value,
     * depending on the given <code>value</code>.
     *
     * @param value a boolean value
     * @since 1.0
     */
    public void appendValue(boolean value)
    {
		appendValue(String.valueOf(value));
    }
	
    /**
     * Appends the single specified character to this value.
     *
     * @param value a character
     * @since 1.0
     */
    public void appendValue(char value)
    {
		appendValue(String.valueOf(value));
    }
	
    /**
     * Appends the given characters to this value.
     *
     * @param value a string of characters
     * @since 1.0
     */
    public void appendValue(char[] value)
    {
		appendValue(String.valueOf(value));
    }
	
    /**
     * Appends the specified range of the given character string to this
     * value. The specified number of bytes from <code>value</code> will be
     * used, starting at the character specified by <code>offset</code>.
     *
     * @param value a character string
     * @param offset the index in <code>value</code> of the first character to
     * use
     * @param count the number of characters to use
     * @since 1.0
     */
    public void appendValue(char[] value, int offset, int count)
    {
		appendValue(String.valueOf(value, offset, count));
    }
	
    /**
     * Appends the given double precision floating point value to this value.
     * This method uses the {@linkplain String#valueOf(double) String.valueOf}
     * method to print the given value, which probably prints more digits than
     * you like. You probably want {@link String#format String.format} or
     * {@link NumberFormat} instead.
     *
     * @param value a floating point value
     * @since 1.0
     */
    public void appendValue(double value)
    {
		appendValue(String.valueOf(value));
    }
	
    /**
     * Appends the given floating point value to this value. This method uses
     * the {@linkplain String#valueOf(float) String.valueOf} method to print
     * the given value, which probably prints more digits than you like. You
     * probably want {@link String#format String.format} or {@link
     * NumberFormat} instead.
     *
     * @param value a floating point value
     * @since 1.0
     */
    public void appendValue(float value)
    {
		appendValue(String.valueOf(value));
    }
	
    /**
     * Appends the given integer to this value.
     *
     * @param value an integer
     * @since 1.0
     */
    public void appendValue(int value)
    {
		appendValue(String.valueOf(value));
    }
	
    /**
     * Appends the given long to this value.
     *
     * @param value a long
     * @since 1.0
     */
    public void appendValue(long value)
    {
		appendValue(String.valueOf(value));
    }
	
    /**
     * Appends the given string to this value. The given string cannot be
     * null.
     *
     * @param value a string
     * @since 1.0
     */
    public void appendValue(String value)
    {
		if (null == value)      throw new IllegalArgumentException("value can't be null.");
		
		appendText(value);
    }
	
    /**
     * Appends the given character sequence to this value. The given
	 * character sequence cannot be null.
     *
     * @param value a character sequence
     * @since 1.5
     */
    public void appendValue(CharSequence value)
    {
		if (null == value)      throw new IllegalArgumentException("value can't be null.");
		
		appendText(value);
    }
	
    InternalValue(AbstractTemplate template)
    {
		super();
		
		mTemplate = template;
    }
	
    InternalValue(AbstractTemplate template, List<CharSequence> deferredContent)
    {
		super();
		
		mTemplate = template;
		if (deferredContent != null)
		{
			mConstruction.addAll(deferredContent);
		}
    }
	
    void increasePartsCapacity(int size)
    {
		mConstruction.ensureCapacity(size + mConstruction.size());
    }
	
    void increaseValuesCapacity(int size)
    {
		mValueIds.ensureCapacity(size + mValueIds.size());
		mValueTags.ensureCapacity(size + mValueTags.size());
    }
	
    int partsSize()
    {
		return mConstruction.size();
    }
	
    int valuesSize()
    {
		return mValueIds.size();
    }
	
    void appendExternalForm(ExternalValue result)
    {
		String	value_id = null;
		String	value_tag = null;
		int		value_count = 0;
		
		for (CharSequence part : mConstruction)
		{
			// part is a value
			if (null == part)
			{
				value_id = mValueIds.get(value_count).toString();
				value_tag = mValueTags.get(value_count).toString();
				value_count++;
				
				// check if the template contains content for the value
				mTemplate.appendValueExternalForm(value_id, value_tag, result);
			}
			// part is just text
			else
			{
				result.add(part);
			}
		}
    }
	
    void appendText(CharSequence text)
    {
		mConstruction.add(text);
    }
	
    void appendValueId(String id, String tag)
    {
		mConstruction.add(null);
		mValueIds.add(id);
		mValueTags.add(tag);
    }
	
    void appendConstructedValue(InternalValue constructedValue)
    {
		// prevent concurrent modification errors
		if (this == constructedValue ||
			mValueIds == constructedValue.mValueIds)
		{
			throw new CircularContructionException();
		}
		
		increasePartsCapacity(constructedValue.partsSize());
		increaseValuesCapacity(constructedValue.valuesSize());
		
		for (CharSequence charsequence : constructedValue.mConstruction)
		{
			mConstruction.add(charsequence);
		}
		
		for (CharSequence charsequence : constructedValue.mValueIds)
		{
			mValueIds.add(charsequence);
		}
		
		for (CharSequence charsequence : constructedValue.mValueTags)
		{
			mValueTags.add(charsequence);
		}
    }
	
    /**
     * Returns whether this value contains no cnotent. This method will return
     * <code>false</code> for newly created values as well as values for whom
     * {@link #clear} has just been called.
     *
     * @return whether this value has no contents
     * @since 1.0
     */
    public boolean isEmpty()
    {
		return 0 == mConstruction.size();
    }
	
    /**
     * Removes all content from this value.
     */
    public void clear()
    {
		mConstruction = new ArrayList<CharSequence>();
		mValueIds = new ArrayList<CharSequence>();
		mValueTags = new ArrayList<CharSequence>();
    }
	
    public boolean equals(Object object)
    {
		if (null == object)
		{
			return false;
		}
		
		if (object == this)
		{
			return true;
		}
		
		if (object.getClass() != this.getClass())
		{
			return false;
		}
		
		InternalValue other = (InternalValue)object;
		if (this.mConstruction.size() != other.mConstruction.size())
		{
			return false;
		}
		
		Iterator<CharSequence>  this_it = null;
		Iterator<CharSequence>  other_it = null;
		
		CharSequence    this_value = null;
		CharSequence    other_value = null;
		this_it = this.mConstruction.iterator();
		other_it = other.mConstruction.iterator();
		while (this_it.hasNext())
		{
			if (!other_it.hasNext())
			{
				return false;
			}
			this_value = this_it.next();
			other_value = other_it.next();
			if (null == this_value && null == other_value)
			{
				continue;
			}
			if (null == this_value || null == other_value)
			{
				return false;
			}
			if (!this_value.equals(other_value))
			{
				return false;
			}
		}
		
		CharSequence    this_valueid = null;
		CharSequence    other_valueid = null;
		this_it = this.mValueIds.iterator();
		other_it = other.mValueIds.iterator();
		while (this_it.hasNext())
		{
			if (!other_it.hasNext())
			{
				return false;
			}
			this_valueid = this_it.next();
			other_valueid = other_it.next();
			if (null == this_valueid && null == other_valueid)
			{
				continue;
			}
			if (null == this_valueid || null == other_valueid)
			{
				return false;
			}
			if (!this_valueid.equals(other_valueid))
			{
				return false;
			}
		}
		
		CharSequence    this_valuetag = null;
		CharSequence    other_valuetag = null;
		this_it = this.mValueTags.iterator();
		other_it = other.mValueTags.iterator();
		while (this_it.hasNext())
		{
			if (!other_it.hasNext())
			{
				return false;
			}
			this_valuetag = this_it.next();
			other_valuetag = other_it.next();
			if (null == this_valuetag && null == other_valuetag)
			{
				continue;
			}
			if (null == this_valuetag || null == other_valuetag)
			{
				return false;
			}
			if (!this_valuetag.equals(other_valuetag))
			{
				return false;
			}
		}
		
		return true;
    }
}


