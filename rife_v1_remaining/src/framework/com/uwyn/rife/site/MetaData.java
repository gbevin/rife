/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MetaData.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;
import com.uwyn.rife.tools.ExceptionUtils;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This abstract base class can be conveniently used to added {@link
 * Constrained} and {@link ValidatedConstrained} meta data to a POJO.
 * <p>Besides implementing all the required interfaces for you, it also sets
 * up the underlying data structures in a lazy fashion. This allows you to
 * benefit from a rich API without the memory overhead when the meta data
 * isn't used.
 * 
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @see MetaDataMerged
 * @see MetaDataBeanAware
 * @version $Revision: 3918 $
 * @since 1.4
 */
public abstract class MetaData<B extends ConstrainedBean, P extends ConstrainedProperty> implements ValidatedConstrained<P>, Constrained<B, P>, MetaDataMerged, MetaDataBeanAware, Cloneable
{
	private Validated           mMetaDataBean = this;
	private Validation<B, P>    mValidation;	
	
	/**
	 * This method is called at least once and maximum once when any meta-data
	 * introspection logic is executed.
	 * <p>You need to implement this method since it will be called after the
	 * underlying validation context has been initialized. Incidentally, by
	 * doing all your meta data setup here, you don't enforce a performance
	 * penalty at each object construction like when you do this in the
	 * default constructor.
	 * 
	 * @since 1.4
	 */
	public void activateMetaData()
	{
	}
	
	public final void setMetaDataBean(Object bean)
	{
		mMetaDataBean = (Validated)bean;
		
		if (mValidation != null)
		{
			mValidation.provideValidatedBean(mMetaDataBean);
		}
	}

	public Object retrieveMetaDataBean()
	{
		return mMetaDataBean;
	}
	
	private void ensureActivatedMetaData()
	{
		if (null == mValidation)
		{
			mValidation = new Validation<B, P>();
			mValidation.provideValidatedBean(mMetaDataBean);
			activateMetaData();
		}
	}
	
	public void provideValidatedBean(Validated bean)
	{
		ensureActivatedMetaData();
		mValidation.provideValidatedBean(bean);
	}
	
	public final Validated retrieveValidatedBean()
	{
		ensureActivatedMetaData();
		return mValidation.retrieveValidatedBean();
	}
	
	public void addConstraint(B constrainedBean)
	{
		ensureActivatedMetaData();
		mValidation.addConstraint(constrainedBean);
	}
	
	public void addConstraint(P constrainedProperty)
	{
		ensureActivatedMetaData();
		mValidation.addConstraint(constrainedProperty);
	}
	
	public B getConstrainedBean()
	{
		ensureActivatedMetaData();
		return mValidation.getConstrainedBean();
	}
	
	public Collection<P> getConstrainedProperties()
	{
		ensureActivatedMetaData();
		return mValidation.getConstrainedProperties();
	}

	public boolean hasPropertyConstraint(String name)
	{
		ensureActivatedMetaData();
		return mValidation.hasPropertyConstraint(name);
	}
	
	public P getConstrainedProperty(String propertyName)
	{
		ensureActivatedMetaData();
		return mValidation.getConstrainedProperty(propertyName);
	}
	
	public boolean validate()
	{
		ensureActivatedMetaData();
		return mValidation.validate();
	}
	
	public boolean validate(ValidationContext context)
	{
		ensureActivatedMetaData();
		return mValidation.validate(context);
	}
	
	public void resetValidation()
	{
		ensureActivatedMetaData();
		mValidation.resetValidation();
	}
	
	public void addValidationError(ValidationError error)
	{
		ensureActivatedMetaData();
		mValidation.addValidationError(error);
	}
	
	public Set<ValidationError> getValidationErrors()
	{
		ensureActivatedMetaData();
		return mValidation.getValidationErrors();
	}
	
	public int countValidationErrors()
	{
		ensureActivatedMetaData();
		return mValidation.countValidationErrors();
	}
	
	public void replaceValidationErrors(Set<ValidationError> errors)
	{
		ensureActivatedMetaData();
		mValidation.replaceValidationErrors(errors);
	}
	
	public void limitSubjectErrors(String subject)
	{
		ensureActivatedMetaData();
		mValidation.limitSubjectErrors(subject);
	}
	
	public void unlimitSubjectErrors(String subject)
	{
		ensureActivatedMetaData();
		mValidation.unlimitSubjectErrors(subject);
	}
	
	public List<String> getValidatedSubjects()
	{
		ensureActivatedMetaData();
		return mValidation.getValidatedSubjects();
	}
	
	public boolean isSubjectValid(String subject)
	{
		ensureActivatedMetaData();
		return mValidation.isSubjectValid(subject);
	}
	
	public void makeErrorValid(String identifier, String subject)
	{
		ensureActivatedMetaData();
		mValidation.makeErrorValid(identifier, subject);
	}
	
	public void makeSubjectValid(String subject)
	{
		ensureActivatedMetaData();
		mValidation.makeSubjectValid(subject);
	}
	
	public ValidationGroup<P> addGroup(String name)
	{
		ensureActivatedMetaData();
		return mValidation.addGroup(name);
	}
	
	public void focusGroup(String name)
	{
		ensureActivatedMetaData();
		mValidation.focusGroup(name);
	}
	
	public void resetGroup(String name)
	{
		ensureActivatedMetaData();
		mValidation.resetGroup(name);
	}
	
	public void addRule(ValidationRule rule)
	{
		ensureActivatedMetaData();
		mValidation.addRule(rule);
	}
	
	public List<PropertyValidationRule> addConstrainedPropertyRules(P constrainedProperty)
	{
		ensureActivatedMetaData();
		return mValidation.addConstrainedPropertyRules(constrainedProperty);
	}
	
	public List<PropertyValidationRule> generateConstrainedPropertyRules(P constrainedProperty)
	{
		ensureActivatedMetaData();
		return mValidation.generateConstrainedPropertyRules(constrainedProperty);
	}
	
	public List<ValidationRule> getRules()
	{
		ensureActivatedMetaData();
		return mValidation.getRules();
	}
	
	public Collection<ValidationGroup<P>> getGroups()
	{
		ensureActivatedMetaData();
		return mValidation.getGroups();
	}
	
	public ValidationGroup<P> getGroup(String name)
	{
		ensureActivatedMetaData();
		return mValidation.getGroup(name);
	}
	
	public boolean validateGroup(String name)
	{
		ensureActivatedMetaData();
		return mValidation.validateGroup(name);
	}
	
	public boolean validateGroup(String name, ValidationContext context)
	{
		ensureActivatedMetaData();
		return mValidation.validateGroup(name, context);
	}
	
	public Collection<String> getLoadingErrors(String propertyName)
	{
		ensureActivatedMetaData();
		return mValidation.getLoadingErrors(propertyName);
	}
	
	public Object clone()
	throws CloneNotSupportedException
	{
		MetaData new_metadata = null;
		try
		{
			new_metadata = (MetaData)super.clone();

			if (mValidation != null)
			{
				new_metadata.mValidation = (Validation)mValidation.clone();
			}

			if (this == new_metadata.mMetaDataBean)
			{
				if (mValidation != null)
				{
					new_metadata.mValidation.provideValidatedBean(new_metadata);
				}
				new_metadata.mMetaDataBean = new_metadata;
			}
		}
		catch (CloneNotSupportedException e)
		{
			///CLOVER:OFF
			// this should never happen
			Logger.getLogger("com.uwyn.rife.site").severe(ExceptionUtils.getExceptionStackTrace(e));
			///CLOVER:ON
		}

		return new_metadata;
	}
}
