/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Validation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import java.util.*;

import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.ObjectUtils;
import java.util.logging.Logger;

public class Validation<B extends ConstrainedBean, P extends ConstrainedProperty> implements ValidatedConstrained<P>, Cloneable, Constrained<B, P>, ConstrainedPropertyListener
{
	private boolean	mActivated = false;
	
	private Validated							mValidatedBean = null;
	private List<ValidationRule>				mValidationRules = null;
	private List<String>						mValidatedSubjects = null;
	private Map<String, P>						mConstrainedProperties = null;
	private Set<String>							mActivePropertyConstraints = null;
	private Map<String, ValidationGroup<P>>		mValidationGroups = null;
	private B									mConstrainedBean = null;

	private Set<ValidationError>				mValidationErrors = null;
	private List<String>						mErrorLimitedSubjects = null;
	
	
	public Validation()
	{
	}
	
	/**
	 * This method is called at least once and maximum once when any method
	 * related to Validated rules, subjects and group or Constrained
	 * properties are used.
	 * <p>By overriding this method, you can thus isolate all the validation
	 * setup code code and don't enforce a performance penalty at each object
	 * construction when doing it in the default constructor.
	 *
	 * @since 1.0
	 */
	protected void activateValidation()
	{
	}
	
	public void provideValidatedBean(Validated bean)
	{
		if (mValidationRules != null)
		{
			for (ValidationRule rule : mValidationRules)
			{
				if (rule.getBean() == mValidatedBean)
				{
					rule.setBean(bean);
				}
			}
		}

		mValidatedBean = bean;
	}
	
	public Validated retrieveValidatedBean()
	{
		if (null == mValidatedBean)
		{
			return this;
		}
		
		return mValidatedBean;
	}
	
	private void ensureActivatedValidation()
	{
		if (mActivated)
		{
			return;
		}
		mActivated = true;
		
		activateValidation();
	}

	public ValidationGroup<P> addGroup(String name)
	{
		ensureActivatedValidation();
		
		if (null == mValidationGroups)
		{
			mValidationGroups = new HashMap<String, ValidationGroup<P>>();
		}
		
		ValidationGroup<P> group = new ValidationGroup<P>(name, this);
		mValidationGroups.put(name, group);
		return group;
	}
	
	public void focusGroup(String name)
	{
		ensureActivatedValidation();
		
		if (null == mValidationGroups ||
			null == mValidationErrors ||
			null == name)
		{
			return;
		}
		
		ValidationGroup<P> group = mValidationGroups.get(name);
		if (null == group)
		{
			return;
		}
		
		Set<ValidationError>	retained_errors = new LinkedHashSet<ValidationError>();
		List<String>			retained_subjects = group.getSubjects();
		for (ValidationError error : mValidationErrors)
		{
			if (retained_subjects.contains(error.getSubject()))
			{
				retained_errors.add(error);
			}
		}
		mValidationErrors = retained_errors;
	}
	
	public void resetGroup(String name)
	{
		ensureActivatedValidation();
		
		if (null == mValidationGroups ||
			null == mValidationErrors ||
			null == name)
		{
			return;
		}
		
		ValidationGroup<P> group = mValidationGroups.get(name);
		if (null == group)
		{
			return;
		}
		
		Set<ValidationError>	retained_errors = new LinkedHashSet<ValidationError>();
		List<String>			group_subjects = group.getSubjects();
		for (ValidationError error : mValidationErrors)
		{
			if (!group_subjects.contains(error.getSubject()))
			{
				retained_errors.add(error);
			}
		}
		mValidationErrors = retained_errors;
	}

	public void addRule(ValidationRule rule)
	{
		ensureActivatedValidation();
		
		if (null == rule)
		{
			return;
		}
		
		if (null == mValidationRules)
		{
			mValidationRules = new ArrayList<ValidationRule>();
		}
		if (null == mValidatedSubjects)
		{
			mValidatedSubjects = new ArrayList<String>();
		}
		
		if (null == rule.getBean())
		{
			rule.setBean(retrieveValidatedBean());
		}
		
		mValidationRules.add(rule);
		String subject = rule.getSubject();
		if (!mValidatedSubjects.contains(subject))
		{
			mValidatedSubjects.add(subject);
		}
	}
	
	private ValidationRule addConstrainedPropertyRule(P constrainedProperty, PropertyValidationRule rule)
	{
		rule.setConstrainedProperty(constrainedProperty);
		rule.setSubject(constrainedProperty.getSubjectName());
		addRule(rule);
		
		return rule;
	}
	
	public List<PropertyValidationRule> generateConstrainedPropertyRules(P constrainedProperty)
	{
		List<PropertyValidationRule>   rules = new ArrayList<PropertyValidationRule>();
		if (constrainedProperty.isNotNull())
		{
			rules.add(new ValidationRuleNotNull(constrainedProperty.getPropertyName()));
		}
		if (constrainedProperty.isNotEmpty())
		{
			rules.add(new ValidationRuleNotEmpty(constrainedProperty.getPropertyName()));
		}
		if (constrainedProperty.isNotEqual())
		{
			rules.add(new ValidationRuleNotEqual(constrainedProperty.getPropertyName(), constrainedProperty.getNotEqual()));
		}
		if (constrainedProperty.hasLimitedLength())
		{
			rules.add(new ValidationRuleLimitedLength(constrainedProperty.getPropertyName(), constrainedProperty.getMinLength(), constrainedProperty.getMaxLength()));
		}
		if (constrainedProperty.isEmail())
		{
			rules.add(new ValidationRuleEmail(constrainedProperty.getPropertyName()));
		}
		if (constrainedProperty.isUrl())
		{
			rules.add(new ValidationRuleUrl(constrainedProperty.getPropertyName()));
		}
		if (constrainedProperty.matchesRegexp())
		{
			rules.add(new ValidationRuleRegexp(constrainedProperty.getPropertyName(), constrainedProperty.getRegexp()));
		}
		if (constrainedProperty.isLimitedDate())
		{
			rules.add(new ValidationRuleLimitedDate(constrainedProperty.getPropertyName(), constrainedProperty.getMinDate(), constrainedProperty.getMaxDate()));
		}
		if (constrainedProperty.isInList())
		{
			rules.add(new ValidationRuleInList(constrainedProperty.getPropertyName(), constrainedProperty.getInList()));
		}
		if (constrainedProperty.isRange())
		{
			rules.add(new ValidationRuleRange(constrainedProperty.getPropertyName(), constrainedProperty.getRangeBegin(), constrainedProperty.getRangeEnd()));
		}
		if (constrainedProperty.isSameAs())
		{
			rules.add(new ValidationRuleSameAs(constrainedProperty.getPropertyName(), constrainedProperty.getSameAs()));
		}
		if (constrainedProperty.isFormatted())
		{
			rules.add(new ValidationRuleFormat(constrainedProperty.getPropertyName(), constrainedProperty.getFormat()));
		}
		if (constrainedProperty.hasMimeType())
		{
			PropertyValidationRule rule = constrainedProperty.getMimeType().getValidationRule(constrainedProperty);
			if (rule != null)
			{
				rules.add(rule);
			}
		}
		
		return rules;
	}

	public List<PropertyValidationRule> addConstrainedPropertyRules(P constrainedProperty)
	{
		ensureActivatedValidation();
		
		if (null == constrainedProperty)
		{
			return null;
		}
		
		if (null == mConstrainedProperties)
		{
			mConstrainedProperties = new LinkedHashMap<String, P>();
		}
		
		// store the constrained property and obtain the old one if it exists
		P old_constrained_property = mConstrainedProperties.put(constrainedProperty.getPropertyName(), constrainedProperty);
		if (old_constrained_property != null &&
			mValidationRules != null)
		{
			// obtain all validation rules that were generated by the old constrained property
			ArrayList<ValidationRule> rules_to_remove = new ArrayList<ValidationRule>();
			for (ValidationRule rule : mValidationRules)
			{
				if (rule instanceof PropertyValidationRule)
				{
					if (old_constrained_property == ((PropertyValidationRule)rule).getConstrainedProperty())
					{
						rules_to_remove.add(rule);
					}
				}
			}
			
			// remove all validation rules that were generated by the old constrained property
			mValidationRules.removeAll(rules_to_remove);
			
			// merge constraints
			Map<String, Object> merged_constraints = new HashMap<String, Object>(old_constrained_property.getConstraints());
			merged_constraints.putAll(constrainedProperty.getConstraints());
			constrainedProperty.getConstraints().putAll(merged_constraints);
		}
		
		// add the validation rules of the new constrained property
		List<PropertyValidationRule> rules = generateConstrainedPropertyRules(constrainedProperty);
		for (PropertyValidationRule rule : rules)
		{
			addConstrainedPropertyRule(constrainedProperty, rule);
		}
		
		// register which constraint names are active
		if (null == mActivePropertyConstraints)
		{
			mActivePropertyConstraints = new HashSet<String>();
		}
		synchronized (mActivePropertyConstraints)
		{
			mActivePropertyConstraints.addAll(constrainedProperty.getConstraints().keySet());
		}
		
		// register this validation object as the listener of future constraint additions to the property
		constrainedProperty.addListener(this);
		
		// unregister this bean from the old constrained property
		if (old_constrained_property != null)
		{
			old_constrained_property.removeListener(this);
		}
		
		return rules;
	}

	public void addConstraint(P constrainedProperty)
	{
		addConstrainedPropertyRules(constrainedProperty);
	}
	
	public void addConstraint(B constrainedBean)
	{
		if (null == constrainedBean)
		{
			return;
		}
		
		if (mConstrainedBean != null)
		{
			HashMap<String, Object> merged_constraints = mConstrainedBean.getConstraints();
			merged_constraints.putAll(constrainedBean.getConstraints());
			mConstrainedBean.getConstraints().putAll(merged_constraints);
		}
		
		mConstrainedBean = constrainedBean;
	}

	public void addValidationError(ValidationError newError)
	{
		if (null == newError)
		{
			return;
		}
		
		if (null == mValidationErrors)
		{
			mValidationErrors = new LinkedHashSet<ValidationError>();
		}
		
		if (mErrorLimitedSubjects != null &&
			mErrorLimitedSubjects.contains(newError.getSubject()) &&
			!isSubjectValid(newError.getSubject()))
		{
			return;
		}
		
		// Handle the overridable errors.
		ValidationError error_to_remove = null;
		for (ValidationError error : mValidationErrors)
		{
			if (error.getSubject().equals(newError.getSubject()))
			{
				// If the new error is overridable, don't add it since there's already
				// and error present for the same subject.
				if (newError.isOverridable())
				{
					return;
				}
				// If an error is present that is overridable, remember it so that it
				// can be removed when the new error is added.
				else if (error.isOverridable())
				{
					error_to_remove = error;
					break;
				}
			}
		}
		
		if (error_to_remove != null)
		{
			mValidationErrors.remove(error_to_remove);
		}
		
		mValidationErrors.add(newError);
	}
	
	public List<ValidationRule> getRules()
	{
		ensureActivatedValidation();
		
		if (null == mValidationRules)
		{
			mValidationRules = new ArrayList<ValidationRule>();
		}
		
		return mValidationRules;
	}
	
	public Collection<P> getConstrainedProperties()
	{
		ensureActivatedValidation();
		
		if (null == mConstrainedProperties)
		{
			mConstrainedProperties = new LinkedHashMap<String, P>();
		}
		
		return mConstrainedProperties.values();
	}
	
	public boolean hasPropertyConstraint(String name)
	{
		if (null == mActivePropertyConstraints)
		{
			return false;
		}
		
		return mActivePropertyConstraints.contains(name);
	}
	
	public P getConstrainedProperty(String propertyName)
	{
		ensureActivatedValidation();
		
		if (null == propertyName ||
			0 == propertyName.length() ||
			null == mConstrainedProperties)
		{
			return null;
		}
		
		return mConstrainedProperties.get(propertyName);
	}
	
	public Collection<ValidationGroup<P>> getGroups()
	{
		ensureActivatedValidation();
		
		if (null == mValidationGroups)
		{
			mValidationGroups = new HashMap<String, ValidationGroup<P>>();
		}
		
		return mValidationGroups.values();
	}
	
	public ValidationGroup<P> getGroup(String name)
	{
		ensureActivatedValidation();
		
		if (null == name ||
			0 == name.length() ||
			null == mValidationGroups)
		{
			return null;
		}
		
		return mValidationGroups.get(name);
	}
	
	public B getConstrainedBean()
	{
		ensureActivatedValidation();
		
		return mConstrainedBean;
	}
	
	private boolean validateSubjects(List<String> subjects)
	{
		ensureActivatedValidation();
		
		if (mValidationRules != null &&
			mValidationRules.size() > 0)
		{
			for (ValidationRule rule : mValidationRules)
			{
				if (subjects != null &&
					!subjects.contains(rule.getSubject()))
				{
					continue;
				}
				
				if (!rule.validate())
				{
					addValidationError(rule.getError());
				}
			}
		}

		return 0 == countValidationErrors();
	}
	
	public boolean validate()
	{
		return validateSubjects(null);
	}
	
	public boolean validate(ValidationContext context)
	{
		if (context != null)
		{
			context.validate(retrieveValidatedBean());
		}
		
		validateSubjects(null);
		
		return 0 == countValidationErrors();
	}
	
	public boolean validateGroup(String name)
	{
		return validateGroup(name, null);
	}
	
	public boolean validateGroup(String name, ValidationContext context)
	{
		ensureActivatedValidation();
		
		if (null == name ||
			null == mValidationGroups)
		{
			return true;
		}
		
		List<String> subjects = null;
		ValidationGroup<P> group = mValidationGroups.get(name);
		if (group != null)
		{
			subjects = group.getSubjects();
		}
		
		if (null == subjects)
		{
			return true;
		}
		
		if (context != null)
		{
			context.validate(retrieveValidatedBean());
		}

		return validateSubjects(subjects);
	}
	
	public int countValidationErrors()
	{
		if (null == mValidationErrors)
		{
			return 0;
		}
		
		return mValidationErrors.size();
	}

	public void resetValidation()
	{
		if (mValidationErrors != null)
		{
			mValidationErrors = new LinkedHashSet<ValidationError>();
		}
	}
	
	public Set<ValidationError> getValidationErrors()
	{
		if (null == mValidationErrors)
		{
			mValidationErrors = new LinkedHashSet<ValidationError>();
		}
		
		return mValidationErrors;
	}
	
	public void replaceValidationErrors(Set<ValidationError> errors)
	{
		mValidationErrors = errors;
	}

	public void limitSubjectErrors(String subject)
	{
		if (null == subject)
		{
			return;
		}
		
		if (null == mErrorLimitedSubjects)
		{
			mErrorLimitedSubjects = new ArrayList<String>();
		}
		
		if (!mErrorLimitedSubjects.contains(subject))
		{
			mErrorLimitedSubjects.add(subject);
		}
	}

	public void unlimitSubjectErrors(String subject)
	{
		if (null == subject)
		{
			return;
		}
		
		if (null == mErrorLimitedSubjects)
		{
			return;
		}
		
		mErrorLimitedSubjects.remove(subject);
	}
	
	public boolean isSubjectValid(String subject)
	{
		if (null == subject)
		{
			return true;
		}
		
		if (null == mValidationErrors)
		{
			return true;
		}
		
		boolean valid = true;
		
		for (ValidationError error : mValidationErrors)
		{
			if (error.getSubject().equals(subject))
			{
				valid = false;
				break;
			}
		}
		
		return valid;
	}

	public void makeSubjectValid(String subject)
	{
		if (null == subject)
		{
			return;
		}
		
		if (null == mValidationErrors)
		{
			return;
		}
		
		ArrayList<ValidationError>  errors_to_remove = new ArrayList<ValidationError>();
		
		for (ValidationError error : mValidationErrors)
		{
			if (error.getSubject().equals(subject))
			{
				errors_to_remove.add(error);
			}
		}

		for (ValidationError error_to_remove : errors_to_remove)
		{
			mValidationErrors.remove(error_to_remove);
		}
	}

	public void makeErrorValid(String identifier, String subject)
	{
		if (null == subject)
		{
			return;
		}
		
		if (null == identifier)
		{
			return;
		}
		
		if (null == mValidationErrors)
		{
			return;
		}
		
		ArrayList<ValidationError>  errors_to_remove = new ArrayList<ValidationError>();
		
		for (ValidationError error : mValidationErrors)
		{
			if (error.getSubject().equals(subject) &&
				error.getIdentifier().equals(identifier))
			{
				errors_to_remove.add(error);
			}
		}

		for (ValidationError error_to_remove : errors_to_remove)
		{
			mValidationErrors.remove(error_to_remove);
		}
	}
	
	public List<String> getValidatedSubjects()
	{
		ensureActivatedValidation();
		
		if (null == mValidatedSubjects)
		{
			mValidatedSubjects = new ArrayList<String>();
		}
		
		return mValidatedSubjects;
	}
	
	public static String getErrorIndication(Validated validated, String subject, String valid, String error)
	{
		if (null != validated &&
			!validated.isSubjectValid(subject))
		{
			return error;
		}
		else
		{
			return valid;
		}
	}
	
	public Collection<String> getLoadingErrors(String propertyName)
	{
		if (null == propertyName)
		{
			return null;
		}
		
		for (ValidationRule rule : getRules())
		{
			if (rule instanceof PropertyValidationRule)
			{
				PropertyValidationRule property_rule = (PropertyValidationRule)rule;
				if (propertyName.equals(property_rule.getPropertyName()) &&
					property_rule.getLoadingErrors() != null &&
					property_rule.getLoadingErrors().size() > 0)
				{
					return property_rule.getLoadingErrors();
				}
			}
		}
		
		return null;
	}
	
	public void constraintSet(ConstrainedProperty property, String name, Object constraintData)
	{
		if (null == mActivePropertyConstraints)
		{
			mActivePropertyConstraints = new HashSet<String>();
		}
		mActivePropertyConstraints.add(name);
	}
	
	public Object clone()
	throws CloneNotSupportedException
	{
		Validation new_validation = null;
		try
		{
			new_validation = (Validation)super.clone();

			if (mValidationRules != null)
			{
				new_validation.mValidationRules = ObjectUtils.deepClone(mValidationRules);
				for (ValidationRule rule : (ArrayList<ValidationRule>)new_validation.mValidationRules)
				{
					if (this == rule.getBean())
					{
						rule.setBean(new_validation);
					}
				}
			}

			if (this == new_validation.mValidatedBean)
			{
				new_validation.mValidatedBean = new_validation;
			}

			if (mValidationErrors != null)
			{
				new_validation.mValidationErrors = new LinkedHashSet<ValidationError>(mValidationErrors);
			}

			if (mConstrainedProperties != null)
			{
				new_validation.mConstrainedProperties = new LinkedHashMap<String, P>();
				for (Map.Entry<String, P> entry_property : mConstrainedProperties.entrySet())
				{
					new_validation.mConstrainedProperties.put(entry_property.getKey(), entry_property.getValue());
					new_validation.addConstraint(entry_property.getValue().clone());
				}
			}

			if (mActivePropertyConstraints != null)
			{
				new_validation.mActivePropertyConstraints = new HashSet<String>(mActivePropertyConstraints);
			}
			
			if (mErrorLimitedSubjects != null)
			{
				new_validation.mErrorLimitedSubjects = new ArrayList<String>(mErrorLimitedSubjects);
			}

			if (mValidationGroups != null)
			{
				new_validation.mValidationGroups = new HashMap<String, ValidationGroup<P>>();
				ValidationGroup<P> new_group;
				for (ValidationGroup<P> group : mValidationGroups.values())
				{
					new_group = group.clone();
					new_group.setValidation(new_validation);
					new_validation.mValidationGroups.put(new_group.getName(), new_group);
				}
			}
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.site").severe(ExceptionUtils.getExceptionStackTrace(e));
			///CLOVER:ON
		}

		return new_validation;
	}
}
