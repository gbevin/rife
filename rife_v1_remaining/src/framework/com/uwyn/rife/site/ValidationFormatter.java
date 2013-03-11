/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationFormatter.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.Collection;

import com.uwyn.rife.template.Template;

/**
 * Since this entire class has been deprecated it will not even be
 * tested anymore, use {@link ValidationBuilder} now instead.
 *
 * @deprecated
 */
///CLOVER:OFF
public abstract class ValidationFormatter
{
	/**
	 * @deprecated
	 */
	public static final String	DEFAULT_ERROR_MESSAGE_ID = "error_message";
	/**
	 * @deprecated
	 */
	public static final String	DEFAULT_ERROR_LINE_ID = "error_line";
	/**
	 * @deprecated
	 */
	public static final String	DEFAULT_ERROR_CONTENT_ID = "error_content";
	/**
	 * @deprecated
	 */
	public static final String	DEFAULT_ERROR_AREA_ID = "error_area";

	/**
	 * @deprecated
	 */
	public static void setValidationErrors(Template template, Collection<ValidationError> errors)
	{
		setValidationErrors(template, errors, DEFAULT_ERROR_MESSAGE_ID, DEFAULT_ERROR_LINE_ID, DEFAULT_ERROR_CONTENT_ID, DEFAULT_ERROR_AREA_ID);
	}
	
	/**
	 * @deprecated
	 */
	public static void setValidationErrors(Template template, Collection<ValidationError> errors, String errorMessageId, String errorLineId, String errorContentId, String errorAreaId)
	{
		if (null == template ||
			null == errors)
		{
			return;
		}
		
		if (null == errorMessageId)	throw new IllegalArgumentException("errorMessageId can't be null.");
		if (null == errorLineId)	throw new IllegalArgumentException("errorLineId can't be null.");
		if (null == errorContentId)	throw new IllegalArgumentException("errorContentId can't be null.");
		if (null == errorAreaId)	throw new IllegalArgumentException("errorAreaId can't be null.");
		
		if (!template.hasValueId(errorMessageId))	throw new IllegalArgumentException("Missing template value '"+errorMessageId+"'.");
		if (!template.hasValueId(errorContentId))	throw new IllegalArgumentException("Missing template value '"+errorContentId+"'.");
		if (!template.hasValueId(errorAreaId))		throw new IllegalArgumentException("Missing template value '"+errorAreaId+"'.");
		if (!template.hasBlock(errorLineId))		throw new IllegalArgumentException("Missing template block '"+errorLineId+"'.");
		if (!template.hasBlock(errorAreaId))		throw new IllegalArgumentException("Missing template block '"+errorAreaId+"'.");
		
		String	error_block_name;
		for (ValidationError error : errors)
		{
			error_block_name = error.getIdentifier()+":"+error.getSubject();
			if (template.hasBlock(error_block_name))
			{
				template.setBlock(errorMessageId, error_block_name);
			}
			else
			{
				template.setValue(errorMessageId, error_block_name);
			}
			template.appendBlock(errorContentId, errorLineId);
		}
		
		template.setBlock(errorAreaId, errorAreaId);
	}
	
	/**
	 * @deprecated
	 */
	public static void setErrorArea(Template template, String content)
	{
		setErrorArea(template, content, DEFAULT_ERROR_MESSAGE_ID, DEFAULT_ERROR_LINE_ID, DEFAULT_ERROR_CONTENT_ID, DEFAULT_ERROR_AREA_ID);
	}
	
	/**
	 * @deprecated
	 */
	public static void setErrorArea(Template template, String content, String errorMessageId, String errorLineId, String errorContentId, String errorAreaId)
	{
		if (null == template ||
			null == content)
		{
			return;
		}
		
		if (null == errorMessageId)	throw new IllegalArgumentException("errorMessageId can't be null.");
		if (null == errorLineId)	throw new IllegalArgumentException("errorLineId can't be null.");
		if (null == errorContentId)	throw new IllegalArgumentException("errorContentId can't be null.");
		if (null == errorAreaId)	throw new IllegalArgumentException("errorAreaId can't be null.");
		
		if (!template.hasValueId(errorMessageId))	throw new IllegalArgumentException("Missing template value '"+errorMessageId+"'.");
		if (!template.hasValueId(errorContentId))	throw new IllegalArgumentException("Missing template value '"+errorContentId+"'.");
		if (!template.hasValueId(errorAreaId))		throw new IllegalArgumentException("Missing template value '"+errorAreaId+"'.");
		if (!template.hasBlock(errorLineId))		throw new IllegalArgumentException("Missing template block '"+errorLineId+"'.");
		if (!template.hasBlock(errorAreaId))		throw new IllegalArgumentException("Missing template block '"+errorAreaId+"'.");

		template.setValue(errorMessageId, content);
		template.setBlock(errorContentId, errorLineId);
		template.setBlock(errorAreaId, errorAreaId);
	}
	
	/**
	 * @deprecated
	 */
	public static void highlightInvalidSubjects(Template template, Collection<ValidationError> errors, String highlightValuePrefix, String highlightBlockId)
	{
		if (null == template ||
			null == errors)
		{
			return;
		}
		
		if (null == highlightValuePrefix)	throw new IllegalArgumentException("highlightValuePrefix can't be null.");
		if (null == highlightBlockId)	throw new IllegalArgumentException("highlightBlockId can't be null.");

		// highlight the validation erors
		String value_id;
		for (ValidationError validationerror : errors)
		{
			value_id = highlightValuePrefix+validationerror.getSubject();
			if (template.hasValueId(value_id))
			{
				template.setBlock(value_id, highlightBlockId);
			}
		}
	}
}
///CLOVER:ON
