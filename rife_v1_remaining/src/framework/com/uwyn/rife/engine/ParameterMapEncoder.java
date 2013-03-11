/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ParameterMapEncoder.java 3957 2008-05-26 07:57:51Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.tools.ArrayUtils;
import com.uwyn.rife.tools.Base64;
import com.uwyn.rife.tools.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

class ParameterMapEncoder
{
	private static final String		SEP_CONTEXT;
	private static final String		SEP_PARAMETER;
	private static final String		SEP_PARAMETER_NAME;
	private static final String		SEP_VALUE;
	private static final byte[]		SEP_CONTEXT_BYTES;
	private static final byte[]		SEP_PARAMETER_BYTES;
	private static final byte[]		SEP_PARAMETER_NAME_BYTES;
	private static final byte[]		SEP_VALUE_BYTES;
	
	static
	{
		SEP_CONTEXT = "c\000";
		SEP_PARAMETER = "p\000";
		SEP_PARAMETER_NAME = "n\000";
		SEP_VALUE = "v\000";
		byte[] context_bytes = null;
		byte[] parameter_bytes = null;
		byte[] parameter_name_bytes = null;
		byte[] value_bytes = null;
		try
		{
			context_bytes = SEP_CONTEXT.getBytes("UTF-8");
			parameter_bytes = SEP_PARAMETER.getBytes("UTF-8");
			parameter_name_bytes = SEP_PARAMETER_NAME.getBytes("UTF-8");
			value_bytes = SEP_VALUE.getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// should never happen
		}
		SEP_CONTEXT_BYTES = context_bytes;
		SEP_PARAMETER_BYTES = parameter_bytes;
		SEP_PARAMETER_NAME_BYTES = parameter_name_bytes;
		SEP_VALUE_BYTES = value_bytes;
	}

	static String encodeToBase64String(Map<String, String[]> map)
	{
		return Base64.encodeToString(encodeToBytes(map, null), false);
	}

	static String encodeToBase64String(Map<String, String[]> map, String context)
	{
		return Base64.encodeToString(encodeToBytes(map, context), false);
	}
	
	static byte[] encodeToBytes(Map<String, String[]> map)
	{
		return encodeToBytes(map, null);
	}
	
	static byte[] encodeToBytes(Map<String, String[]> map, String context)
	{
		byte[]	map_bytes = new byte[0];
		
		if (map != null)
		{
			Set<Map.Entry<String, String[]>>		parameter_entries = map.entrySet();
			if (parameter_entries.size() > 0)
			{
				try
				{
					// handle the context
					if (context != null)
					{
						map_bytes = context.getBytes("UTF-8");
						map_bytes = ArrayUtils.join(map_bytes, SEP_CONTEXT_BYTES);
					}
					
					// handle the parameters
					Iterator<Map.Entry<String, String[]>>	parameter_entries_it = parameter_entries.iterator();
					Map.Entry<String, String[]>				parameter_entry = null;
					String[]								parameter_values = null;
					while (parameter_entries_it.hasNext())
					{
						parameter_entry = parameter_entries_it.next();
						
						// add the parameter name
						map_bytes = ArrayUtils.join(map_bytes, parameter_entry.getKey().getBytes("UTF-8"));
						map_bytes = ArrayUtils.join(map_bytes, SEP_PARAMETER_NAME_BYTES);
						
						// add the values of the parameter
						parameter_values = parameter_entry.getValue();
						if (parameter_values != null)
						{
							for (int i = 0; i < parameter_values.length; i++)
							{
								map_bytes = ArrayUtils.join(map_bytes, parameter_values[i].getBytes("UTF-8"));
								if (i < parameter_values.length-1)
								{
									map_bytes = ArrayUtils.join(map_bytes, SEP_VALUE_BYTES);
								}
							}
						}
						
						// add the parameter seperator
						if (parameter_entries_it.hasNext())
						{
							map_bytes = ArrayUtils.join(map_bytes, SEP_PARAMETER_BYTES);
						}
					}
				}
				catch (UnsupportedEncodingException e)
				{
					// should never happen
				}
			}
		}
		
		return map_bytes;
	}
	
	static Map<String, String[]> decodeFromBase64String(String base64Encoded)
	{
		if (null == base64Encoded)
		{
			return Collections.EMPTY_MAP;
		}
		
		try
		{
			byte[] decoded_bytes = Base64.decode(base64Encoded);
			if (null == decoded_bytes)
			{
				return Collections.EMPTY_MAP;
			}
			
			String decoded = new String(decoded_bytes, "UTF-8");
			
			return decodeFromString(decoded);
		}
		catch (UnsupportedEncodingException e)
		{
			// should never happen
			return null;
		}
	}
	
	static String[] seperateBase64ContextString(String base64Encoded)
	{
		if (null == base64Encoded)
		{
			return null;
		}
		
		try
		{
			byte[] decoded_bytes = Base64.decode(base64Encoded);
			if (null == decoded_bytes)
			{
				return null;
			}
			
			String decoded = new String(decoded_bytes, "UTF-8");
			List<String> context_parts = StringUtils.split(decoded, SEP_CONTEXT);
			if (2 == context_parts.size())
			{
				return new String[] {context_parts.get(0), context_parts.get(1)};
			}
			else if (1 == context_parts.size())
			{
				return new String[] {"", context_parts.get(0)};
			}
		}
		catch (UnsupportedEncodingException e)
		{
			// should never happen
		}
		
		return null;
	}
	
	static Map<String, String[]> decodeFromString(String encoded)
	{
		HashMap<String, String[]>	parameters = new HashMap<String, String[]>();
		if (encoded != null &&
			encoded.length() > 0)
		{
			if (encoded.length() > 0)
			{
				if (encoded.indexOf(SEP_CONTEXT) != -1)
				{
					return parameters;
				}
				
				// deserialize the parameters and their values
				List<String>	parameter_entries = null;
				
				// get the parameters
				parameter_entries = StringUtils.split(encoded, SEP_PARAMETER);
				
				// iterate over the parameters
				List<String>	parameter_parts = null;
				String[]		parameter_values = null;
				for (String parameter_entry : parameter_entries)
				{
					// get the parameter name and its values
					parameter_parts = StringUtils.split(parameter_entry, SEP_PARAMETER_NAME);
					parameter_values = StringUtils.splitToArray(parameter_parts.get(1), SEP_VALUE);
					
					parameters.put(parameter_parts.get(0), parameter_values);
				}
			}
		}
		
		return parameters;
	}
}
