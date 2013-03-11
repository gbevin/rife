/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ValidationBuilder.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.Collection;

import com.uwyn.rife.site.exceptions.ValidationBuilderException;
import com.uwyn.rife.template.Template;

public interface ValidationBuilder extends Cloneable
{
	public static final String	PREFIX_ERROR = "ERROR:";
	public static final String	PREFIX_ERRORMESSAGE = "ERRORMESSAGE:";
	public static final String	PREFIX_ERRORS = "ERRORS:";
	public static final String	PREFIX_MARK = "MARK:";
	public static final String	PREFIX_MARK_ERROR = "MARK:ERROR";

	public static final String	ID_ERROR_WILDCARD = "ERROR:*";
	public static final String	ID_ERRORMESSAGE = "ERRORMESSAGE";
	public static final String	ID_ERRORMESSAGE_WILDCARD = "ERRORMESSAGE:*";
	public static final String	ID_ERRORS = "ERRORS";
	public static final String	ID_ERRORS_FALLBACK = "ERRORS:";
	public static final String	ID_ERRORS_WILDCARD = "ERRORS:*";

	public static final String	TAG_ERRORS = "(?=(?<=^"+PREFIX_ERRORS+")|\\G(?<!^))\\s*(\\w+)\\s*,?(?=[\\w,]+$|$)";
	public static final String	TAG_ERRORMESSAGE = "(?=(?<=^"+PREFIX_ERRORMESSAGE+")|\\G(?<!^))\\s*(\\w+)\\s*,?(?=[\\w,]+$|$)";
	public static final String	TAG_MARK = "(?:^"+PREFIX_MARK+"(?:(\\w+):)?|\\G(?<!^))\\s*(\\w+)\\s*,?(?=[\\w,]+$|$)";

	public void setFallbackErrorArea(Template template, String message);
	public Collection<String> generateValidationErrors(Template template, Collection<ValidationError> errors, Collection<String> onlySubjectsToClear, String prefix);
	public Collection<String> generateErrorMarkings(Template template, Collection<ValidationError> errors, Collection<String> onlySubjectsToClear, String prefix) throws ValidationBuilderException;
	public void removeValidationErrors(Template template, Collection<String> subjects, String prefix);
	public void removeErrorMarkings(Template template, Collection<String> subjects, String prefix);
	public Object clone();
}
