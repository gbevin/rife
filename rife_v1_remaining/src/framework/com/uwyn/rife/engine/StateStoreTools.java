/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: StateStoreTools.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.exceptions.EngineException;

public abstract class StateStoreTools
{
	private final static String HTML_PART1 = "<input name=\"";
	private final static String HTML_PART2 = "\" type=\"hidden\" value=\"";
	private final static String HTML_PART3 = "\" />";
	private final static String JAVASCRIPT_PART1 = appendJavascriptAsciiArray(new StringBuilder(), HTML_PART1).toString();
	private final static String JAVASCRIPT_PART2 = appendJavascriptAsciiArray(new StringBuilder(), HTML_PART2).toString();
	private final static String JAVASCRIPT_PART3 = appendJavascriptAsciiArray(new StringBuilder(), HTML_PART3).toString();
	
	public static void appendJavascriptHeader(StringBuilder state)
	throws EngineException
	{
		state.append("<script type=\"text/javascript\">\n");
		state.append("{\n");
		state.append("var part1 = ");
		state.append(JAVASCRIPT_PART1);
		state.append("; ");
		state.append("var part2 = ");
		state.append(JAVASCRIPT_PART2);
		state.append("; ");
		state.append("var part3 = ");
		state.append(JAVASCRIPT_PART3);
		state.append("; ");
		state.append("var state = ''; ");
		state.append("var intArrayToString = function(intArray) { ");
		state.append("    var result = ''; ");
		state.append("    for (var i = 0; i < intArray.length; i++) result += String.fromCharCode(intArray[i]); ");
		state.append("    return result; ");
		state.append("}; ");
	}
	
	public static void appendHtmlHiddenParam(StringBuilder state, CharSequenceDeferred deferred, String name, String value)
	throws EngineException
	{
		state.append(HTML_PART1);
		state.append(deferred.encode(name));
		state.append(HTML_PART2);
		state.append(deferred.encode(value));
		state.append(HTML_PART3);
	}
	
	public static StringBuilder appendJavascriptAsciiArray(StringBuilder state, String value)
	{
		if (null == value)
		{
			state.append("[]");
		}
		
		char seperator = ',';
		int current_index = 0;
		state.append("[");
		while (current_index < value.length() - 1)
		{
			state.append((int)value.charAt(current_index));
			state.append(seperator);
			current_index++;
		}

		state.append((int)value.charAt(current_index));
		state.append("]");
		
		return state;
	}
	
	public static void appendJavascriptHiddenParam(StringBuilder state, String name, String value)
	throws EngineException
	{
		state.append("state += intArrayToString(part1); ");
		state.append("state += intArrayToString(");
		appendJavascriptAsciiArray(state, name);
		state.append("); ");
		state.append("state += intArrayToString(part2); ");
		state.append("state += intArrayToString(");
		appendJavascriptAsciiArray(state, value);
		state.append("); ");
		state.append("state += intArrayToString(part3); ");
	}
	
	public static void appendJavascriptFooter(StringBuilder state)
	throws EngineException
	{
		state.append("document.write(state);\n");
		state.append("}\n");
		state.append("</script>");
	}
}
