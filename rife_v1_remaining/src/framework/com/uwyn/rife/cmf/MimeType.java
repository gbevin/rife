/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MimeType.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.cmf;

import com.uwyn.rife.cmf.format.Formatter;
import com.uwyn.rife.cmf.format.ImageFormatter;
import com.uwyn.rife.cmf.format.PlainTextFormatter;
import com.uwyn.rife.cmf.format.RawFormatter;
import com.uwyn.rife.cmf.format.XhtmlFormatter;
import com.uwyn.rife.cmf.validation.CmfPropertyValidationRule;
import com.uwyn.rife.cmf.validation.SupportedImage;
import com.uwyn.rife.cmf.validation.SupportedXhtml;
import com.uwyn.rife.datastructures.EnumClass;
import com.uwyn.rife.site.ConstrainedProperty;

/**
 * This is a typed enumeration of all the mime types that the content
 * management framework specifically knows about.
 * <p>The types that are defined here can be validated and transformed.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.0
 */
public abstract class MimeType extends EnumClass<String>
{
	/**
	 * The <code>application/xhtml+xml</code> mime type.
	 */
	public static final MimeType    APPLICATION_XHTML = new MimeType("application/xhtml+xml") {
			public CmfPropertyValidationRule getValidationRule(ConstrainedProperty constrainedProperty)
			{
				return new SupportedXhtml(constrainedProperty.getPropertyName(), constrainedProperty.isFragment());
			}
			
			public Formatter getFormatter()
			{
				return new XhtmlFormatter();
			}
		};
	/**
	 * The <code>image/gif</code> mime type.
	 */
	public static final MimeType    IMAGE_GIF = new MimeType("image/gif") {
			public CmfPropertyValidationRule getValidationRule(ConstrainedProperty constrainedProperty)
			{
				return new SupportedImage(constrainedProperty.getPropertyName());
			}
			
			public Formatter getFormatter()
			{
				return new ImageFormatter();
			}
		};
	/**
	 * The <code>image/jpeg</code> mime type.
	 */
	public static final MimeType    IMAGE_JPEG = new MimeType("image/jpeg") {
			public CmfPropertyValidationRule getValidationRule(ConstrainedProperty constrainedProperty)
			{
				return new SupportedImage(constrainedProperty.getPropertyName());
			}
			
			public Formatter getFormatter()
			{
				return new ImageFormatter();
			}
		};
	/**
	 * The <code>image/png</code> mime type.
	 */
	public static final MimeType    IMAGE_PNG = new MimeType("image/png") {
			public CmfPropertyValidationRule getValidationRule(ConstrainedProperty constrainedProperty)
			{
				return new SupportedImage(constrainedProperty.getPropertyName());
			}
			
			public Formatter getFormatter()
			{
				return new ImageFormatter();
			}
		};
	/**
	 * The <code>text/plain</code> mime type.
	 */
	public static final MimeType    TEXT_PLAIN = new MimeType("text/plain") {
			public CmfPropertyValidationRule getValidationRule(ConstrainedProperty constrainedProperty)
			{
				return null;
			}
			
			public Formatter getFormatter()
			{
				return new PlainTextFormatter();
			}
		};
	/**
	 * The <code>text/plain</code> mime type.
	 */
	public static final MimeType    TEXT_XML = new MimeType("text/xml") {
			public CmfPropertyValidationRule getValidationRule(ConstrainedProperty constrainedProperty)
			{
				return null;
			}
			
			public Formatter getFormatter()
			{
				return new PlainTextFormatter();
			}
		};
	/**
	 * A generic mime type indicating that the content should be stored as raw
	 * data without any mime-type related processing.
	 */
	public static final MimeType    RAW = new MimeType("raw") {
			public CmfPropertyValidationRule getValidationRule(ConstrainedProperty constrainedProperty)
			{
				return null;
			}
			
			public Formatter getFormatter()
			{
				return new RawFormatter();
			}
		};
	
	/**
	 * Constructs and returns a CMF-specific validation rule that is able to
	 * validate data for this mime type.
	 *
	 * @param constrainedProperty an instance of the property for which the
	 * validation rule has to be built
	 * @return an instance of the validation rule
	 * @since 1.0
	 */
	public abstract CmfPropertyValidationRule getValidationRule(ConstrainedProperty constrainedProperty);

	/**
	 * Returns an instance of the formatter for this mime type.
	 *
	 * @return an instance of the formatter
	 * @since 1.0
	 */
	public abstract Formatter getFormatter();
		
	/**
	 * Returns the <code>MimeType</code> instance that corresponds to a given
	 * textual identifier.
	 *
	 * @param identifier the identifier of the mime type that has to be
	 * retrieved
	 * @return the requested <code>MimeType</code>; or
	 * <p><code>null</code> if the <code>MimeType</code> is not supported
	 * @since 1.0
	 */
	public static MimeType getMimeType(String identifier)
	{
		return getMember(MimeType.class, identifier);
	}

	MimeType(String identifier)
	{
		super(MimeType.class, identifier);
	}
}
