/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ChildRequestEncoder.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine;

import com.uwyn.rife.engine.RequestMethod;
import com.uwyn.rife.tools.ArrayUtils;
import com.uwyn.rife.tools.Base64;
import java.util.Map;

class ChildRequestEncoder
{
	private static final String		SEP_DECLARATION_NAME = "x\000";
	private static final int		SEP_DECLARATION_NAME_LENGTH = SEP_DECLARATION_NAME.length();
	private static final byte[]		SEP_DECLARATION_NAME_BYTES = SEP_DECLARATION_NAME.getBytes();
	private static final String		SEP_METHOD = "m\000";
	private static final int		SEP_METHOD_LENGTH = SEP_METHOD.length();
	private static final byte[]		SEP_METHOD_BYTES = SEP_METHOD.getBytes();
	private static final String		SEP_PATHINFO = "i\000";
	private static final int		SEP_PATHINFO_LENGTH = SEP_PATHINFO.length();
	private static final byte[]		SEP_PATHINFO_BYTES = SEP_PATHINFO.getBytes();

	static String encode(ElementInfo elementInfo, RequestState state)
	{
		byte[]	childrequest_bytes = new byte[0];
		
		if (state != null)
		{
			// serialize the declaration name
			String declaration_name = elementInfo.getDeclarationName();
			childrequest_bytes = declaration_name.getBytes();
			childrequest_bytes = ArrayUtils.join(childrequest_bytes, SEP_DECLARATION_NAME_BYTES);

			// serialize the method
			RequestMethod method = state.getElementState().getMethod();
			if (method != null)
			{
				childrequest_bytes = ArrayUtils.join(childrequest_bytes, method.toString().getBytes());
				childrequest_bytes = ArrayUtils.join(childrequest_bytes, SEP_METHOD_BYTES);
			}

			// serialize the pathinfo
			String path_info = state.getElementState().getPathInfo();
			if (path_info != null &&
				path_info.length() > 0)
			{
				childrequest_bytes = ArrayUtils.join(childrequest_bytes, path_info.getBytes());
				childrequest_bytes = ArrayUtils.join(childrequest_bytes, SEP_PATHINFO_BYTES);
			}

			// serialize the parameters and their values
			childrequest_bytes = ArrayUtils.join(childrequest_bytes, ParameterMapEncoder.encodeToBytes(state.getElementState().getRequestParameters()));
		}
		
		return Base64.encodeToString(childrequest_bytes, false);
	}
	
	static void decode(ElementInfo elementInfo, RequestState state)
	{
		if (state != null)
		{
			String	encoded_childrequest = state.getElementState().getRequestParameter(ReservedParameters.CHILDREQUEST);

			String					declaration_name = null;
			String					method = "";
			String					path_info = "";
			Map<String, String[]>	parameters = null;
			if (encoded_childrequest != null &
				encoded_childrequest.length() > 0)
			{
				String	decoded_childrequest = new String(Base64.decode(encoded_childrequest));
				
				// deserialize the declaration name
				int declaration_name_index = decoded_childrequest.indexOf(SEP_DECLARATION_NAME);
				if (declaration_name_index != -1)
				{
					declaration_name = decoded_childrequest.substring(0, declaration_name_index);
				}
				// online use this childrequest if it's element declaration name
				// matches the one that was targeted when the child request was
				// encoded
				if (null == declaration_name ||
				    !declaration_name.equals(elementInfo.getDeclarationName()))
				{
					return;
				}
				decoded_childrequest = decoded_childrequest.substring(declaration_name_index+SEP_DECLARATION_NAME_LENGTH);
				
				// deserialize the method
				int method_index = decoded_childrequest.indexOf(SEP_METHOD);
				if (method_index != -1)
				{
					method = decoded_childrequest.substring(0, method_index);
					decoded_childrequest = decoded_childrequest.substring(method_index+SEP_METHOD_LENGTH);
				}
	
				// deserialize the pathinfo
				int path_info_index = decoded_childrequest.indexOf(SEP_PATHINFO);
				if (path_info_index != -1)
				{
					path_info = decoded_childrequest.substring(0, path_info_index);
					decoded_childrequest = decoded_childrequest.substring(path_info_index+SEP_PATHINFO_LENGTH);
				}
	
				// deserialize the parameters and their values
				parameters = ParameterMapEncoder.decodeFromString(decoded_childrequest);
			}
			
			state.getElementState().setMethod(RequestMethod.getMethod(method));
			state.getElementState().setPathInfo(path_info);
			state.getElementState().setRequestParameters(parameters);
		}
	}
}
