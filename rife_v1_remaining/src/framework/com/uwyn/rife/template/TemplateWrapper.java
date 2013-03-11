/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TemplateWrapper.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.template.exceptions.TemplateException;

import java.io.OutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Map;
import java.net.URL;

/**
 * Wraps around an existing {@link Template} instance and delegates all
 * methods to that instance.
 * <p>This class is handy when you need to customize the behavior of certain
 * method calls. Extending this class and just overridding the methods in
 * question, with or without delegation to the super method at the appropriate
 * time, allows you to do this.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @since 1.6.2
 */public class TemplateWrapper implements Template
{
	private final Template mDelegate;

	public TemplateWrapper(final Template delegate)
	{
		mDelegate = delegate;
	}

	public void appendBlock(String valueId, String blockId) throws TemplateException
	{
		mDelegate.appendBlock(valueId, blockId);
	}

	public void setBlock(String valueId, String blockId) throws TemplateException
	{
		mDelegate.setBlock(valueId, blockId);
	}

	public String getBlock(String id) throws TemplateException
	{
		return mDelegate.getBlock(id);
	}

	public String getContent() throws TemplateException
	{
		return mDelegate.getContent();
	}

	public void writeBlock(String id, OutputStream out) throws IOException, TemplateException
	{
		mDelegate.writeBlock(id, out);
	}

	public void writeContent(OutputStream out) throws IOException, TemplateException
	{
		mDelegate.writeContent(out);
	}

	public void writeContent(OutputStream out, String charsetName) throws IOException, TemplateException
	{
		mDelegate.writeContent(out, charsetName);
	}

	public void write(OutputStream out) throws IOException, TemplateException
	{
		mDelegate.write(out);
	}

	public List<CharSequence> getDeferredBlock(String id) throws TemplateException
	{
		return mDelegate.getDeferredBlock(id);
	}

	public List<CharSequence> getDeferredContent() throws TemplateException
	{
		return mDelegate.getDeferredContent();
	}

	public InternalValue createInternalValue()
	{
		return mDelegate.createInternalValue();
	}

	public void setValue(String id, List<CharSequence> deferredContent) throws TemplateException
	{
		mDelegate.setValue(id, deferredContent);
	}

	public void setValue(String id, InternalValue internalValue) throws TemplateException
	{
		mDelegate.setValue(id, internalValue);
	}

	public void setValue(String id, Object value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, boolean value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, char value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, char[] value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, char[] value, int offset, int count) throws TemplateException
	{
		mDelegate.setValue(id, value, offset, count);
	}

	public void setValue(String id, double value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, float value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, int value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, long value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, String value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, CharSequence value) throws TemplateException
	{
		mDelegate.setValue(id, value);
	}

	public void setValue(String id, Template template) throws TemplateException
	{
		mDelegate.setValue(id, template);
	}

	public void setBean(Object bean) throws TemplateException
	{
		mDelegate.setBean(bean);
	}

	public void setBean(Object bean, String prefix) throws TemplateException
	{
		mDelegate.setBean(bean, prefix);
	}

	public void setBean(Object bean, String prefix, boolean encode) throws TemplateException
	{
		mDelegate.setBean(bean, prefix, encode);
	}

	public void removeBean(Object bean) throws TemplateException
	{
		mDelegate.removeBean(bean);
	}

	public void removeBean(Object bean, String prefix) throws TemplateException
	{
		mDelegate.removeBean(bean, prefix);
	}

	public void appendValue(String id, Object value) throws TemplateException
	{
		mDelegate.appendValue(id, value);
	}

	public void appendValue(String id, boolean value) throws TemplateException
	{
		mDelegate.appendValue(id, value);
	}

	public void appendValue(String id, char value) throws TemplateException
	{
		mDelegate.appendValue(id, value);
	}

	public void appendValue(String id, char[] value) throws TemplateException
	{
		mDelegate.appendValue(id, value);
	}

	public void appendValue(String id, char[] value, int offset, int count) throws TemplateException
	{
		mDelegate.appendValue(id, value, offset, count);
	}

	public void appendValue(String id, double value) throws TemplateException
	{
		mDelegate.appendValue(id, value);
	}

	public void appendValue(String id, float value) throws TemplateException
	{
		mDelegate.appendValue(id, value);
	}

	public void appendValue(String id, int value) throws TemplateException
	{
		mDelegate.appendValue(id, value);
	}

	public void appendValue(String id, long value) throws TemplateException
	{
		mDelegate.appendValue(id, value);
	}

	public void appendValue(String id, String value) throws TemplateException
	{
		mDelegate.appendValue(id, value);
	}

	public String getValue(String id) throws TemplateException
	{
		return mDelegate.getValue(id);
	}

	public String getDefaultValue(String id)
	{
		return mDelegate.getDefaultValue(id);
	}

	public boolean hasDefaultValue(String id)
	{
		return mDelegate.hasDefaultValue(id);
	}

	public List<String[]> getFilteredBlocks(String filter)
	{
		return mDelegate.getFilteredBlocks(filter);
	}

	public boolean hasFilteredBlocks(String filter)
	{
		return mDelegate.hasFilteredBlocks(filter);
	}

	public List<String[]> getFilteredValues(String filter)
	{
		return mDelegate.getFilteredValues(filter);
	}

	public boolean hasFilteredValues(String filter)
	{
		return mDelegate.hasFilteredValues(filter);
	}

	public List<String> evaluateL10nTags()
	{
		return mDelegate.evaluateL10nTags();
	}

	public List<String> evaluateConfigTags()
	{
		return mDelegate.evaluateConfigTags();
	}

	public List<String> evaluateLangTags(String id)
	{
		return mDelegate.evaluateLangTags(id);
	}

	public List<String> evaluateExpressionTags(String id)
	{
		return mDelegate.evaluateExpressionTags(id);
	}

	public List<String> evaluateExpressionConfigTags(String id)
	{
		return mDelegate.evaluateExpressionConfigTags(id);
	}

	public List<String> evaluateRenderTags() throws TemplateException
	{
		return mDelegate.evaluateRenderTags();
	}

	public boolean hasBlock(String id)
	{
		return mDelegate.hasBlock(id);
	}

	public boolean isValueSet(String id)
	{
		return mDelegate.isValueSet(id);
	}

	public int countValues()
	{
		return mDelegate.countValues();
	}

	public void removeValue(String id)
	{
		mDelegate.removeValue(id);
	}

	public void removeValues(List<String> ids)
	{
		mDelegate.removeValues(ids);
	}

	public void blankValue(String id)
	{
		mDelegate.blankValue(id);
	}

	public void clear()
	{
		mDelegate.clear();
	}

	public String[] getAvailableValueIds()
	{
		return mDelegate.getAvailableValueIds();
	}

	public Collection<String> getUnsetValueIds()
	{
		return mDelegate.getUnsetValueIds();
	}

	public boolean hasValueId(String id)
	{
		return mDelegate.hasValueId(id);
	}

	public long getModificationTime()
	{
		return mDelegate.getModificationTime();
	}

	public BeanHandler getBeanHandler()
	{
		return mDelegate.getBeanHandler();
	}

	public TemplateEncoder getEncoder()
	{
		return mDelegate.getEncoder();
	}

	public void addResourceBundle(ResourceBundle resourceBundle)
	{
		mDelegate.addResourceBundle(resourceBundle);
	}

	public Collection<ResourceBundle> getResourceBundles()
	{
		return mDelegate.getResourceBundles();
	}

	public boolean hasResourceBundles()
	{
		return mDelegate.hasResourceBundles();
	}

	public void setLanguage(String lang)
	{
		mDelegate.setLanguage(lang);
	}

	public String getLanguage()
	{
		return mDelegate.getLanguage();
	}

	public void setExpressionVar(String name, Object value)
	{
		mDelegate.setExpressionVar(name, value);
	}

	public void setExpressionVars(Map<String, Object> map)
	{
		mDelegate.setExpressionVars(map);
	}

	public Map<String, Object> getExpressionVars()
	{
		return mDelegate.getExpressionVars();
	}

	public void cacheObject(String key, Object value)
	{
		mDelegate.cacheObject(key, value);
	}

	public Object getCacheObject(String key)
	{
		return mDelegate.getCacheObject(key);
	}

	public Map<URL, Long> getDependencies()
	{
		return mDelegate.getDependencies();
	}

	public String getName()
	{
		return mDelegate.getName();
	}

	public String getFullName()
	{
		return mDelegate.getFullName();
	}

	public String getDefaultContentType()
	{
		return mDelegate.getDefaultContentType();
	}

	public Object clone()
	{
		return mDelegate.clone();
	}
}