/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationError.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.logging.Logger;

import com.uwyn.rife.tools.ExceptionUtils;

/**
 * Instances of this class detail subjects that were found invalid during
 * validation.
 * <p>Each <code>ValidationError</code> is tied to a specific subject and
 * provides more information through an explicative textual identifier.
 * <p>A collection of commonly used identifiers and implementations are
 * provided as static member variables and static inner classes.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see Validated
 * @since 1.0
 */
public abstract class ValidationError implements Cloneable
{
	public final static String  IDENTIFIER_MANDATORY = "MANDATORY";
	public final static String  IDENTIFIER_UNICITY = "UNICITY";
	public final static String  IDENTIFIER_WRONGLENGTH = "WRONGLENGTH";
	public final static String  IDENTIFIER_WRONGFORMAT = "WRONGFORMAT";
	public final static String  IDENTIFIER_NOTNUMERIC = "NOTNUMERIC";
	public final static String  IDENTIFIER_UNEXPECTED = "UNEXPECTED";
	public final static String  IDENTIFIER_INCOMPLETE = "INCOMPLETE";
	public final static String  IDENTIFIER_INVALID = "INVALID";
	public final static String  IDENTIFIER_NOTSAME = "NOTSAME";
	
	private String	mIdentifier = null;
	private String	mSubject = null;
	private Object	mErroneousValue = null;
	private boolean	mOverridable = false;
	
	/**
	 * Creates a new <code>ValidationError</code> instance for the specified
	 * identifier and subject.
	 * <p>The error will not be automatic overridable.
	 *
	 * @param identifier a non-<code>null</code> <code>String</code> with the
	 * textual error identifier
	 * @param subject a non-<code>null</code> <code>String</code> with the
	 * name of the erroneous subject
	 * @since 1.0
	 */
	public ValidationError(String identifier, String subject)
	{
		if (null == identifier) throw new IllegalArgumentException("identifier can't be null");
		if (null == subject)    throw new IllegalArgumentException("subject can't be null");

		mIdentifier = identifier;
		mSubject = subject;
	}
	
	/**
	 * Creates a new <code>ValidationError</code> instance for the specified
	 * identifier and subject.
	 *
	 * @param identifier a non-<code>null</code> <code>String</code> with the
	 * textual error identifier
	 * @param subject a non-<code>null</code> <code>String</code> with the
	 * name of the erroneous subject
	 * @param overridable <code>true</code> to make any other error for the same
	 * subject override this error, <code>false</code> if this error should
	 * always be shown
	 * @since 1.5
	 */
	public ValidationError(String identifier, String subject, boolean overridable)
	{
		this(identifier, subject);
		
		mOverridable = overridable;
	}
	
	/**
	 * Returns the textual identifier that categorizes this validation error.
	 *
	 * @since 1.0
	 */
	public final String getIdentifier()
	{
		return mIdentifier;
	}

	/**
	 * Returns the erroneous subject name of this validation error.
	 *
	 * @since 1.0
	 */
	public final String getSubject()
	{
		return mSubject;
	}
	
	/**
	 * Returns wether this error is overridable for the same subject.
	 *
	 * @since 1.5
	 */
	public final boolean isOverridable()
	{
		return mOverridable;
	}
	
	/**
	 * Stores the erroneous value that caused the validation error.
	 * This is optional and should only be done when the erroneous value
	 * gives more information from the context in which the validation
	 * error occurred.
	 *
	 * @since 1.0
	 */
	public void setErroneousValue(Object erroneousValue)
	{
		mErroneousValue = erroneousValue;
	}
	
	/**
	 * Chainable setter to make validation error construction easier
	 *
	 * @see #setErroneousValue
	 * @since 1.0
	 */
	public ValidationError erroneousValue(Object erroneousValue)
	{
		setErroneousValue(erroneousValue);
		
		return this;
	}
	
	/**
	 * Returns the erroneous value that caused the validation error, if it's present.
	 *
	 * @since 1.0
	 */
	public Object getErroneousValue()
	{
		return mErroneousValue;
	}
	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.site").severe(ExceptionUtils.getExceptionStackTrace(e));
			return null;
			///CLOVER:ON
		}
	}
	
	public int hashCode()
	{
		return mIdentifier.hashCode()*mSubject.hashCode();
	}
	
	public boolean equals(Object object)
	{
		if (null == object)
		{
			return false;
		}
		
		if (object instanceof ValidationError)
		{
			ValidationError other_error = (ValidationError)object;
			return other_error.mIdentifier.equals(mIdentifier) &&
					other_error.mSubject.equals(mSubject);
		}
		
		return false;
	}

	public static class MANDATORY extends ValidationError
	{
		public MANDATORY(String subject)
		{
			super(IDENTIFIER_MANDATORY, subject, true);
		}
	}

	public static class UNICITY extends ValidationError
	{
		public UNICITY(String subject)
		{
			super(IDENTIFIER_UNICITY, subject);
		}
	}

	public static class WRONGLENGTH extends ValidationError
	{
		public WRONGLENGTH(String subject)
		{
			super(IDENTIFIER_WRONGLENGTH, subject);
		}
	}

	public static class WRONGFORMAT extends ValidationError
	{
		public WRONGFORMAT(String subject)
		{
			super(IDENTIFIER_WRONGFORMAT, subject);
		}
	}

	public static class NOTNUMERIC extends ValidationError
	{
		public NOTNUMERIC(String subject)
		{
			super(IDENTIFIER_NOTNUMERIC, subject);
		}
	}

	public static class UNEXPECTED extends ValidationError
	{
		public UNEXPECTED(String subject)
		{
			super(IDENTIFIER_UNEXPECTED, subject);
		}
	}

	public static class INCOMPLETE extends ValidationError
	{
		public INCOMPLETE(String subject)
		{
			super(IDENTIFIER_INCOMPLETE, subject);
		}
	}

	public static class INVALID extends ValidationError
	{
		public INVALID(String subject)
		{
			super(IDENTIFIER_INVALID, subject);
		}
	}

	public static class NOTSAMEAS extends ValidationError
	{
		public NOTSAMEAS(String subject)
		{
			super(IDENTIFIER_NOTSAME, subject);
		}
	}
}
