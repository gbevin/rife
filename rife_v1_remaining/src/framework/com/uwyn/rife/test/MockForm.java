/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MockForm.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.test;

import java.util.*;

import com.uwyn.rife.engine.RequestMethod;
import com.uwyn.rife.tools.ArrayUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Corresponds to a form in a HTML document after it has been parsed with
 * {@link ParsedHtml#parse}.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.1
 */
public class MockForm
{
	private MockResponse                    mResponse;
	private Node                            mNode;
	private Map<String, String[]>           mParameters = new LinkedHashMap<String, String[]>();
	private Map<String, MockFileUpload[]>   mFiles = new LinkedHashMap<String, MockFileUpload[]>();
	
	MockForm(MockResponse response, Node node)
	{
		assert node != null;
		
		mResponse = response;
		mNode = node;

		Stack<NodeList> node_lists = new Stack<NodeList>();
		
		NodeList child_nodes = mNode.getChildNodes();
		if (child_nodes != null &&
			child_nodes.getLength() > 0)
		{
			node_lists.push(child_nodes);
		}
		
		while (node_lists.size() > 0)
		{
			child_nodes = node_lists.pop();

			for (int i = 0; i < child_nodes.getLength(); i++)
			{
				Node child_node = child_nodes.item(i);
				
				String node_name = child_node.getNodeName();
				String node_name_attribute = ParsedHtml.getNodeAttribute(child_node, "name", null);
				if (node_name_attribute != null)
				{
					if ("input".equals(node_name))
					{
						String input_type = ParsedHtml.getNodeAttribute(child_node, "type", null);
						if (input_type != null)
						{
							String input_value = ParsedHtml.getNodeAttribute(child_node, "value", null);
							if ("text".equals(input_type) ||
								"password".equals(input_type) ||
								"hidden".equals(input_type))
							{
								addParameterValue(node_name_attribute, input_value);
							}
							else
							{
								boolean input_checked = ParsedHtml.getNodeAttribute(child_node, "checked", null) != null;
								if (null == input_value)
								{
									input_value = "on";
								}
								
								if ("checkbox".equals(input_type))
								{
									if (input_checked)
									{
										addParameterValue(node_name_attribute, input_value);
									}
								}
								else if ("radio".equals(input_type))
								{
									if (input_checked)
									{
										setParameter(node_name_attribute, input_value);
									}
								}
							}
						}
					}
					else if ("textarea".equals(node_name))
					{
						String value = child_node.getTextContent();
						addParameterValue(node_name_attribute, value);
					}
					else if ("select".equals(node_name))
					{
						List<String> selected_options = new ArrayList<String>();
						
						boolean select_multiple = ParsedHtml.getNodeAttribute(child_node, "multiple", null) != null;

						String first_option_value = null;
						
						// go over all the select child nodes to find the options tags
						NodeList select_child_nodes = child_node.getChildNodes();
						for (int j = 0; j < select_child_nodes.getLength(); j++)
						{
							Node select_child_node = select_child_nodes.item(j);
							String select_node_name = select_child_node.getNodeName();
							
							// process each select option
							if ("option".equals(select_node_name))
							{
								// obtain the option value
								String option_value = ParsedHtml.getNodeAttribute(select_child_node, "value", null);
								if (null == option_value)
								{
									option_value = select_child_node.getTextContent();
								}
								
								// remember the first option value
								if (null == first_option_value)
								{
									first_option_value = option_value;
								}
								
								// select the indicated options
								boolean option_selected = ParsedHtml.getNodeAttribute(select_child_node, "selected", null) != null;
								if (option_selected)
								{
									if (!select_multiple)
									{
										selected_options.clear();
									}
									
									selected_options.add(option_value);
								}
							}
						}
						
						// if no options were selected and the select is not multiple,
						// the first option should be automatically selected
						if (0 == selected_options.size() &&
							!select_multiple &&
							select_child_nodes.getLength() > 0)
						{
							selected_options.add(first_option_value);
						}
						
						// add the selected values to the parameters
						for (String value : selected_options)
						{
							addParameterValue(node_name_attribute, value);
						}
					}
				}
				
				NodeList planned_child_nodes = child_node.getChildNodes();
				if (planned_child_nodes != null &&
					planned_child_nodes.getLength() > 0)
				{
					node_lists.push(planned_child_nodes);
				}                   
			}
		}
	}
	
	/**
	 * Retrieves the DOM XML node that this form corresponds to.
	 * 
	 * @return the corresponding DOM XML node
	 * @since 1.0
	 */
	public Node getNode()
	{
		return mNode;
	}
	
	/**
	 * Creates a new {@link MockRequest} that contains the method, the
	 * parameters and the files of this form.
	 * 
	 * @return the created <code>MockRequest</code>
	 * @since 1.0
	 */
	public MockRequest getRequest()
	{
		return new MockRequest()
			.method(RequestMethod.getMethod(getMethod()))
			.parameters(mParameters)
			.files(mFiles);
	}
	
	/**
	 * Submit this form with its current parameters and files; and returns the
	 * response.
	 * 
	 * @return the resulting {@link MockResponse}
	 * @since 1.0
	 */
	public MockResponse submit()
	{
		return mResponse.getMockConversation().doRequest(getAction(), getRequest());
	}
	
	private void addParameterValue(String name, String value)
	{
		if (null == value)
		{
			value = "";
		}
		
		String[] values = mParameters.get(name);
		if (null == values)
		{
			values = new String[] {value};
		}
		else
		{
			values = ArrayUtils.join(values, value);
		}
		
		mParameters.put(name, values);
	}
	
	/**
	 * Retrieves all the parameters of this form.
	 * 
	 * @return a <code>Map</code> of the parameters with the names as the keys
	 * and their value arrays as the values
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public Map<String, String[]> getParameters()
	{
		return mParameters;
	}
	
	/**
	 * Retrieves all the parameter names of this form.
	 * 
	 * @return a <code>Collection</code> of the parameter names
	 * @see #getParameters
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public Collection<String> getParameterNames()
	{
		return mParameters.keySet();
	}
	
	/**
	 * Checks whether a named parameter is present in this form.
	 * 
	 * @param name the name of the parameter to check
	 * @return <code>true</code> if the parameter is present; or
	 * <p><code>false</code> otherwise
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public boolean hasParameter(String name)
	{
		return mParameters.containsKey(name);
	}
	
	/**
	 * Retrieves the first value of a parameter in this form.
	 * 
	 * @param name the name of the parameter
	 * @return the first value of the parameter; or
	 * <p><code>null</code> if no such parameter could be found
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValues
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public String getParameterValue(String name)
	{
		String[] values = getParameterValues(name);
		if (null == values ||
			0 == values.length)
		{
			return null;
		}
		
		return values[0];
	}
	
	/**
	 * Retrieves the values of a parameter in this form.
	 * 
	 * @param name the name of the parameter
	 * @return the values of the parameter; or
	 * <p><code>null</code> if no such parameter could be found
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public String[] getParameterValues(String name)
	{
		return mParameters.get(name);
	}
	
	/**
	 * Sets a parameter in this form.
	 * 
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @see #setParameter(String, String[])
	 * @since 1.1
	 */
	public void setParameter(String name, String value)
	{
		if (null == name ||
			null == value)
		{
			return;
		}
		
		mParameters.put(name, new String[] {value});
	}
	
	/**
	 * Sets a parameter in this form.
	 * 
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @return this <code>MockForm</code> instance
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public MockForm parameter(String name, String value)
	{
		setParameter(name, value);
		
		return this;
	}
	
	/**
	 * Sets a parameter in this form.
	 * 
	 * @param name the name of the parameter
	 * @param values the value array of the parameter
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public void setParameter(String name, String[] values)
	{
		if (null == name)
		{
			return;
		}
		
		if (null == values)
		{
			mParameters.remove(name);
		}
		else
		{
			mParameters.put(name, values);
		}
	}
	
	/**
	 * Sets a parameter in this form.
	 * 
	 * @param name the name of the parameter
	 * @param values the value array of the parameter
	 * @return this <code>MockForm</code> instance
	 * @see #getParameters
	 * @see #getParameterNames
	 * @see #hasParameter
	 * @see #getParameterValue
	 * @see #getParameterValues
	 * @see #setParameter(String, String[])
	 * @see #setParameter(String, String)
	 * @since 1.1
	 */
	public MockForm parameter(String name, String[] values)
	{
		setParameter(name, values);
		
		return this;
	}
	
	/**
	 * Sets a file in this form.
	 * 
	 * @param name the parameter name of the file
	 * @param file the file specification that will be uploaded
	 * @see #setFiles(String, MockFileUpload[])
	 * @since 1.1
	 */
	public void setFile(String name, MockFileUpload file)
	{
		if (null == name ||
			null == file)
		{
			return;
		}
		
		mFiles.put(name, new MockFileUpload[] {file});
	}
	
	/**
	 * Sets a file in this form.
	 * 
	 * @param name the parameter name of the file
	 * @param file the file specification that will be uploaded
	 * @return this <code>MockForm</code> instance
	 * @see #setFiles(String, MockFileUpload[])
	 * @since 1.1
	 */
	public MockForm file(String name, MockFileUpload file)
	{
		setFile(name, file);
		
		return this;
	}
	
	/**
	 * Sets files in this request.
	 * 
	 * @param name the parameter name of the file
	 * @param files the file specifications that will be uploaded
	 * @see #setFile(String, MockFileUpload)
	 * @since 1.1
	 */
	public void setFiles(String name, MockFileUpload[] files)
	{
		if (null == name)
		{
			return;
		}
		
		if (null == files)
		{
			mFiles.remove(name);
		}
		else
		{
			mFiles.put(name, files);
		}
	}
	
	/**
	 * Sets files in this request.
	 * 
	 * @param name the parameter name of the file
	 * @param files the file specifications that will be uploaded
	 * @return this <code>MockForm</code> instance
	 * @see #setFile(String, MockFileUpload)
	 * @since 1.1
	 */
	public MockForm files(String name, MockFileUpload[] files)
	{
		setFiles(name, files);
		
		return this;
	}
	
	/**
	 * Retrieves the content of this form's <code>id</code> attribute.
	 * 
	 * @return the content of the <code>id</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getId()
	{
		return getAttribute("id");
	}
	
	/**
	 * Retrieves the content of this form's <code>class</code> attribute.
	 * 
	 * @return the content of the <code>class</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getClassName()
	{
		return getAttribute("class");
	}
	
	/**
	 * Retrieves the content of this form's <code>title</code> attribute.
	 * 
	 * @return the content of the <code>title</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getTitle()
	{
		return getAttribute("title");
	}
	
	/**
	 * Retrieves the content of this form's <code>action</code> attribute.
	 * 
	 * @return the content of the <code>action</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getAction()
	{
		return getAttribute("action");
	}
	
	/**
	 * Retrieves the content of this form's <code>method</code> attribute.
	 * 
	 * @return the content of the <code>method</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getMethod()
	{
		return getAttribute("method");
	}
	
	/**
	 * Retrieves the content of this form's <code>name</code> attribute.
	 * 
	 * @return the content of the <code>name</code> attribute; or
	 * <p>null if no such attribute could be found
	 * @since 1.0
	 */
	public String getName()
	{
		return getAttribute("name");
	}
	
	private String getAttribute(String attributeName)
	{
		return ParsedHtml.getNodeAttribute(mNode, attributeName, null);
	}
}

