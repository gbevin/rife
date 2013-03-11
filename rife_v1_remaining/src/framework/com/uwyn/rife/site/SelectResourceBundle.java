/*
 * Copyright 2005 Keith Lea <keith[remove] at cs dot oswego dot edu>,
 * Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: SelectResourceBundle.java 3884 2007-08-22 08:52:24Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.ListResourceBundle;
import java.util.Map;

/**
 * A <code>ResourceBundle</code> implementation which facilitates the use of select
 * boxes in forms for properties with {@link ConstrainedProperty#inList inList}
 * constraints.
 * 
 * Example use:
 * <pre>Template t = ...
 * List&lt;Club&gt; clubs = ...;
 * ClubSelectionBean bean = ...;
 * 
 * Map&lt;String,String&gt; names = new HashMap&lt;String,String&gt;(clubs.size());
 * for (Club club : clubs) {
 * &nbsp;&nbsp;&nbsp;&nbsp;names.put(club.getUniqueName(), club.getFullName());
 * }
 *  
 * bean.getConstrainedProperty("clubUniqueName")
 * &nbsp;&nbsp;&nbsp;&nbsp;.setInList(names.keySet());
 * t.addResourceBundle(new SelectResourceBundle("clubUniqueName", names));
 *  
 * generateForm(t, bean);
 * print(t);</pre>
 *
 * @author Keith Lea (keith[remove] at cs dot oswego dot edu)
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3884 $
 * @since 1.0
 */
public class SelectResourceBundle extends ListResourceBundle
{
    /** The list of keys and associated values. */
    private final Object[][] mObjectNames;
	
	/**
     * Creates a new select tag resource bundle with the given input form
     * property and the given map from values to displayed strings.
     *
     * @param property the property whose possible values are described in
     * <code>map</code>
     * @param map a map from possible property values with their corresponding
     * descriptions
     */
    public SelectResourceBundle(String property, Map<? extends CharSequence,? extends CharSequence> map)
	throws IllegalArgumentException
	{
        if (null == property)	throw new IllegalArgumentException("property can't be null");
        if (null == map)		throw new IllegalArgumentException("map can't be null");
        
		Object[][] processed = new Object[map.size()][2];
        int i = 0;
        for (Map.Entry<? extends CharSequence, ?> entry : map.entrySet())
		{
            if (null == entry.getKey())
			{
                continue;
            }
            
			Object[] objects = processed[i];
            objects[0] = property + ":" + entry.getKey();
            objects[1] = entry.getValue();
            i++;
        }
		
        mObjectNames = processed;
    }
	
	protected Object[][] getContents()
	{
		return mObjectNames;
	}
}
