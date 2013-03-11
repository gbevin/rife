/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TriggerListEncoder.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import java.util.*;

import com.uwyn.rife.tools.ArrayUtils;
import com.uwyn.rife.tools.Base64;
import com.uwyn.rife.tools.IntegerUtils;
import com.uwyn.rife.tools.StringUtils;

class TriggerListEncoder
{
	private static final String	SEP_DATAPART = "d\000";
	private static final byte[]	SEP_DATAPART_BYTES = SEP_DATAPART.getBytes();
	private static final String	SEP_DECLARATION_NAME = "x\000";
	private static final byte[]	SEP_DECLARATION_NAME_BYTES = SEP_DECLARATION_NAME.getBytes();
	private static final String	SEP_TRIGGER_NAME = "t\000";
	private static final byte[]	SEP_TRIGGER_NAME_BYTES = SEP_TRIGGER_NAME.getBytes();
	private static final String	SEP_PARAMETER_NAME = "n\000";
	private static final byte[]	SEP_PARAMETER_NAME_BYTES = SEP_PARAMETER_NAME.getBytes();
	private static final String	SEP_PARAMETER_NAMES = "s\000";
	private static final byte[]	SEP_PARAMETER_NAMES_BYTES = SEP_PARAMETER_NAMES.getBytes();
	private static final String	SEP_VALUE = "v\000";
	private static final byte[]	SEP_VALUE_BYTES = SEP_VALUE.getBytes();
	private static final String	SEP_VALUEARRAY = "a\000";
	private static final byte[]	SEP_VALUEARRAY_BYTES = SEP_VALUEARRAY.getBytes();
	private static final String	SEP_VALUEARRAYS = "y\000";
	private static final byte[]	SEP_VALUEARRAYS_BYTES = SEP_VALUEARRAYS.getBytes();

	static String encode(List<TriggerContext> triggerList)
	{
		assert triggerList != null;
			
		if (0 == triggerList.size())
		{
			return "";
		}
		else
		{
			byte[]						trigger_list = new byte[4+triggerList.size()*4];
	
			Iterator<TriggerContext>	trigger_it = null;
			TriggerContext				trigger_context = null;
			byte[]						hashcode_bytes = new byte[4];
			int							counter = 0;
	
			// store the number of elements that activated a child
			hashcode_bytes = IntegerUtils.intToBytes(triggerList.size());
			trigger_list[counter++] = hashcode_bytes[0];
			trigger_list[counter++] = hashcode_bytes[1];
			trigger_list[counter++] = hashcode_bytes[2];
			trigger_list[counter++] = hashcode_bytes[3];
			
			// store the trigger type ids
			trigger_it = triggerList.iterator();
			while (trigger_it.hasNext())
			{
				trigger_context = trigger_it.next();
				hashcode_bytes = IntegerUtils.intToBytes(trigger_context.getType());
				trigger_list[counter++] = hashcode_bytes[0];
				trigger_list[counter++] = hashcode_bytes[1];
				trigger_list[counter++] = hashcode_bytes[2];
				trigger_list[counter++] = hashcode_bytes[3];
			}

			// store the element info xml filenames
			byte[]	xmlfile_names_encoded = new byte[0];
			trigger_it = triggerList.iterator();
			while (trigger_it.hasNext())
			{
				trigger_context = trigger_it.next();
				xmlfile_names_encoded = ArrayUtils.join(xmlfile_names_encoded, (trigger_context.getDeclarationName()).getBytes());
				// add seperator between element info xml filenames
				if (trigger_it.hasNext())
				{
					xmlfile_names_encoded = ArrayUtils.join(xmlfile_names_encoded, SEP_DECLARATION_NAME_BYTES);
				}
			}
	
			// store the trigger names that were activated
			byte[]	trigger_names_encoded = new byte[0];
			trigger_it = triggerList.iterator();
			while (trigger_it.hasNext())
			{
				trigger_context = trigger_it.next();
				trigger_names_encoded = ArrayUtils.join(trigger_names_encoded, (trigger_context.getTriggerName()).getBytes());
				// add seperator between trigger names
				if (trigger_it.hasNext())
				{
					trigger_names_encoded = ArrayUtils.join(trigger_names_encoded, SEP_TRIGGER_NAME_BYTES);
				}
			}
			
			// store the trigger values
			String[]	trigger_values = null;
			byte[]		trigger_values_encoded = new byte[0];
			trigger_it = triggerList.iterator();
			while (trigger_it.hasNext())
			{
				trigger_context = trigger_it.next();
				trigger_values = trigger_context.getTriggerValues();
				
				// store the parameter values
				for (int i = 0; i < trigger_values.length; i++)
				{
					trigger_values_encoded = ArrayUtils.join(trigger_values_encoded, (trigger_values[i]).getBytes());
					// add seperator between different values
					if (i < trigger_values.length-1)
					{
						trigger_list = ArrayUtils.join(trigger_list, SEP_VALUE_BYTES);
					}
				}
				
				// add seperator between different triggers
				if (trigger_it.hasNext())
				{
					trigger_values_encoded = ArrayUtils.join(trigger_values_encoded, SEP_VALUEARRAY_BYTES);
				}
			}
			
			// process the parameter entries
			Set<Map.Entry<String, String[]>>		parameter_entries = null;
			Iterator<Map.Entry<String, String[]>>	parameter_entries_it = null;
			Map.Entry<String, String[]>				parameter_entry = null;
			String[]								parameter_values = null;
			byte[]									parameter_names_encoded = new byte[0];
			byte[]									parameter_values_encoded = new byte[0];

			trigger_it = triggerList.iterator();
			while (trigger_it.hasNext())
			{
				trigger_context = trigger_it.next();
				parameter_entries = trigger_context.getParameters().entrySet();
				parameter_entries_it = parameter_entries.iterator();
				while (parameter_entries_it.hasNext())
				{
					parameter_entry = parameter_entries_it.next();
					
					// store the parameter name
					parameter_names_encoded = ArrayUtils.join(parameter_names_encoded, parameter_entry.getKey().getBytes());
					parameter_values = parameter_entry.getValue();
					// store the parameter values
					for (int i = 0; i < parameter_values.length; i++)
					{
						parameter_values_encoded = ArrayUtils.join(parameter_values_encoded, (parameter_values[i]).getBytes());
						// add seperator between different values
						if (i < parameter_values.length-1)
						{
							trigger_list = ArrayUtils.join(trigger_list, SEP_VALUE_BYTES);
						}
					}
					
					// add seperator between different names and values
					if (parameter_entries_it.hasNext())
					{
						parameter_names_encoded = ArrayUtils.join(parameter_names_encoded, SEP_PARAMETER_NAME_BYTES);
						parameter_values_encoded = ArrayUtils.join(parameter_values_encoded, SEP_VALUEARRAY_BYTES);
					}
				}
				
				// add seperator between different triggers
				if (trigger_it.hasNext())
				{
					parameter_names_encoded = ArrayUtils.join(parameter_names_encoded, SEP_PARAMETER_NAMES_BYTES);
					parameter_values_encoded = ArrayUtils.join(parameter_values_encoded, SEP_VALUEARRAYS_BYTES);
				}
			}
			
			trigger_list = ArrayUtils.join(trigger_list, xmlfile_names_encoded);
			trigger_list = ArrayUtils.join(trigger_list, SEP_DATAPART_BYTES);
			trigger_list = ArrayUtils.join(trigger_list, trigger_names_encoded);
			trigger_list = ArrayUtils.join(trigger_list, SEP_DATAPART_BYTES);
			trigger_list = ArrayUtils.join(trigger_list, trigger_values_encoded);
			trigger_list = ArrayUtils.join(trigger_list, SEP_DATAPART_BYTES);
			trigger_list = ArrayUtils.join(trigger_list, parameter_names_encoded);
			trigger_list = ArrayUtils.join(trigger_list, SEP_DATAPART_BYTES);
			trigger_list = ArrayUtils.join(trigger_list, parameter_values_encoded);
			
			return Base64.encodeToString(trigger_list, false);
		}
	}

	static List<TriggerContext> decode(String[] triggerListValues)
	{
		if (triggerListValues != null &&
			triggerListValues.length > 0 &&
			triggerListValues[0] != null &&
			triggerListValues[0].length() > 0)
		{
			TriggerContext[]	trigger_list = null;
			byte[]				hashcode_bytes = new byte[4];
			byte[]				decoded_bytes = null;
			
			decoded_bytes = Base64.decode(triggerListValues[0]);
			if (null == decoded_bytes)
			{
				return new ArrayList<TriggerContext>();
			}
			
			// obtain the number of triggers
			System.arraycopy(decoded_bytes, 0, hashcode_bytes, 0, 4);
			int	number_of_triggers = IntegerUtils.bytesToInt(hashcode_bytes);
			trigger_list = new TriggerContext[number_of_triggers];
			
			// obtain the trigger type ids
			int		triggers_types_offset = 4;
			for (int i = 0; i < number_of_triggers; i++)
			{
				System.arraycopy(decoded_bytes, triggers_types_offset+i*4, hashcode_bytes, 0, 4);
				trigger_list[i] = new TriggerContext();
				trigger_list[i].setType(IntegerUtils.bytesToInt(hashcode_bytes));
			}
			
			// obtain the data section
			int		triggers_names_offset = 4+number_of_triggers*4;
			String	triggers_data = new String(decoded_bytes, triggers_names_offset, decoded_bytes.length-triggers_names_offset);
			
			// split the data into five parts, a section with the element info xml filenames,
			// a section with the trigger names, a section with the trigger values,
			// a section with the parameter values and another section with the associated values
			List<String> triggers_data_parts = StringUtils.split(triggers_data, SEP_DATAPART);
			
			// obtain the element info xml filenames
			List<String>	elementinfo_names = StringUtils.split(triggers_data_parts.get(0), SEP_DECLARATION_NAME);
			for (int i = 0; i < number_of_triggers; i++)
			{
				trigger_list[i].setDeclarationName(elementinfo_names.get(i));
			}

			// obtain the trigger names
			List<String>	triggers_names = StringUtils.split(triggers_data_parts.get(1), SEP_TRIGGER_NAME);
			for (int i = 0; i < number_of_triggers; i++)
			{
				trigger_list[i].setTriggerName(triggers_names.get(i));
			}
			
			// obtains the trigger values
			List<String>	trigger_values_list = StringUtils.split(triggers_data_parts.get(2), SEP_VALUEARRAY);
			List<String>	trigger_values = null;
			String[]			trigger_values_array = null;
			for (int i = 0; i < number_of_triggers; i++)
			{
				trigger_values = StringUtils.split(trigger_values_list.get(i), SEP_VALUE);
				trigger_values_array = new String[trigger_values.size()];
				trigger_values_array = trigger_values.toArray(trigger_values_array);
				trigger_list[i].setTriggerValues(trigger_values_array);
			}
			
			// obtain the parameter names and their values
			List<String>			parameter_names_list = StringUtils.split(triggers_data_parts.get(3), SEP_PARAMETER_NAMES);
			List<String>			parameter_values_list = StringUtils.split(triggers_data_parts.get(4), SEP_VALUEARRAYS);
			List<String>			parameter_names = null;
			List<String>			parameter_valuearrays = null;
			List<String>			parameter_values = null;
			String[]				parameter_values_array = null;
			Map<String, String[]>	parameters = null;
			for (int i = 0; i < number_of_triggers; i++)
			{
				parameters = new HashMap<String, String[]>();
				
				if (parameter_names_list.get(i).length() > 0)
				{
					parameter_names = StringUtils.split(parameter_names_list.get(i), SEP_PARAMETER_NAME);
					parameter_valuearrays = StringUtils.split(parameter_values_list.get(i), SEP_VALUEARRAY);
					for (int j = 0; j < parameter_names.size(); j++)
					{
						parameter_values = StringUtils.split(parameter_valuearrays.get(j), SEP_VALUE);
						parameter_values_array = new String[parameter_values.size()];
						parameter_values_array = parameter_values.toArray(parameter_values_array);
						parameters.put(parameter_names.get(j), parameter_values_array);
					}
				}
				
				trigger_list[i].setParameters(parameters);
			}
		
			return new ArrayList<TriggerContext>(Arrays.asList(trigger_list));
		}
		else
		{
			return new ArrayList<TriggerContext>();
		}
	}
}
