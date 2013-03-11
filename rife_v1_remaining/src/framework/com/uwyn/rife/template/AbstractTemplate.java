/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: AbstractTemplate.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.template.exceptions.*;
import com.uwyn.rife.tools.Localization;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

public abstract class AbstractTemplate implements Template
{
	protected TemplateInitializer			mInitializer = null;
	protected Map<String, InternalString>	mFixedValues = new HashMap<String, InternalString>();
	protected Map<String, InternalValue>	mConstructedValues = new HashMap<String, InternalValue>();
	protected BeanHandler					mBeanHandler = null;
	protected TemplateEncoder				mEncoder = EncoderDummy.getInstance();
	protected List<ResourceBundle>			mDefaultResourceBundles = null;
	protected List<ResourceBundle>			mResourceBundles = null;
	protected Map<String, Object>			mExpressionVars = null;
	protected String						mLanguage = null;
	protected Map<String, Object>			mCache = null;
	protected String						mDefaultContentType = null;

	public final void appendBlock(String valueId, String blockId)
	throws TemplateException
	{
		if (null == valueId ||
			0 == valueId.length() ||
			!hasValueId(valueId))
		{
			throw new ValueUnknownException(valueId);
		}
		if (null == blockId ||
			0 == blockId.length())
		{
			throw new BlockUnknownException(blockId);
		}

		if (mFixedValues.containsKey(valueId))
		{
			InternalValue constructed_value = new InternalValue(this);
			constructed_value.appendText(mFixedValues.get(valueId));
			if (!appendBlockInternalForm(blockId, constructed_value))
			{
				throw new BlockUnknownException(blockId);
			}
			mConstructedValues.put(valueId, constructed_value);
			mFixedValues.remove(valueId);
		}
		else if(mConstructedValues.containsKey(valueId))
		{
			if (!appendBlockInternalForm(blockId, mConstructedValues.get(valueId)))
			{
				throw new BlockUnknownException(blockId);
			}
		}
		else
		{
			InternalValue constructed_value = new InternalValue(this);
			if (!appendBlockInternalForm(blockId, constructed_value))
			{
				throw new BlockUnknownException(blockId);
			}
			mConstructedValues.put(valueId, constructed_value);
		}
	}

	public final void setBlock(String valueId, String blockId)
	throws TemplateException
	{
		if (null == valueId ||
			0 == valueId.length() ||
			!hasValueId(valueId))
		{
			throw new ValueUnknownException(valueId);
		}
		if (null == blockId ||
			0 == blockId.length())
		{
			throw new BlockUnknownException(blockId);
		}

		if (mFixedValues.containsKey(valueId))
		{
			mFixedValues.remove(valueId);
		}

		InternalValue constructed_value = new InternalValue(this);
		if (!appendBlockInternalForm(blockId, constructed_value))
		{
			throw new BlockUnknownException(blockId);
		}
		mConstructedValues.put(valueId, constructed_value);
	}

	public String getBlock(String id)
	throws TemplateException
	{
		if (null == id ||
			0 == id.length())
		{
			throw new BlockUnknownException(id);
		}

		ExternalValue result = new ExternalValue();

		if (!appendBlockExternalForm(id, result))
		{
			throw new BlockUnknownException(id);
		}
		return result.toString();
	}

	public final String getContent()
	throws TemplateException
	{
		List<String> set_values = processLateTags();

		ExternalValue result = new ExternalValue();

		if (!appendBlockExternalForm("", result))
		{
			throw new BlockUnknownException("");
		}
		String content = result.toString();
		removeValues(set_values);
		return content;
	}

	public void writeBlock(String id, OutputStream out)
	throws IOException, TemplateException
	{
		writeBlock(id, out, null);
	}

	public void writeBlock(String id, OutputStream out, String charsetName)
	throws IOException, TemplateException
	{
		if (null == out)
		{
			return;
		}
		if (null == id ||
			0 == id.length())
		{
			throw new BlockUnknownException(id);
		}

		ExternalValue result = new ExternalValue();

		if (!appendBlockExternalForm(id, result))
		{
			throw new BlockUnknownException(id);
		}

		result.write(out, charsetName);
	}

	public final void writeContent(OutputStream out)
	throws IOException, TemplateException
	{
		writeContent(out, null);
	}

	public final void writeContent(OutputStream out, String charsetName)
	throws IOException, TemplateException
	{
		if (null == out)
		{
			return;
		}

		ExternalValue result = new ExternalValue();

		if (!appendBlockExternalForm("", result))
		{
			throw new BlockUnknownException("");
		}

		result.write(out, charsetName);
	}

	public final void write(OutputStream out)
	throws IOException, TemplateException
	{
		writeContent(out);
	}

	public final List<CharSequence> getDeferredBlock(String id)
	throws TemplateException
	{
		if (null == id ||
			0 == id.length())
		{
			throw new BlockUnknownException(id);
		}

		ExternalValue result = new ExternalValue();

		if (!appendBlockExternalForm(id, result))
		{
			throw new BlockUnknownException(id);
		}

		return result;
	}

	public final List<CharSequence> getDeferredContent()
	throws TemplateException
	{
		List<String> set_values = processLateTags();

		ExternalValue result = new ExternalValue();

		if (!appendBlockExternalForm("", result))
		{
			throw new BlockUnknownException("");
		}

		removeValues(set_values);
		
		return result;
	}

	private List<String> processLateTags()
	{
		List<String> set_values = new ArrayList<String>();
		
		_evaluateL10nTags(set_values);
		_evaluateRenderTags(set_values);
		_evaluateLangTags(set_values, null);
		_evaluateOgnlTags(set_values, null);
		_evaluateOgnlConfigTags(set_values, null);
		_evaluateMvelTags(set_values, null);
		_evaluateMvelConfigTags(set_values, null);
		_evaluateGroovyTags(set_values, null);
		_evaluateGroovyConfigTags(set_values, null);
		_evaluateJaninoTags(set_values, null);
		_evaluateJaninoConfigTags(set_values, null);
		
		return set_values;
	}

	public List<String> evaluateRenderTags()
	throws TemplateException
	{
		List<String> set_values = new ArrayList<String>();
		_evaluateRenderTags(set_values);
		return set_values;
	}

	private void _evaluateRenderTags(List<String> setValues)
	throws TemplateException
	{
		if (hasFilteredValues(TemplateFactory.TAG_RENDER))
		{
			List<String[]>	render_tags = getFilteredValues(TemplateFactory.TAG_RENDER);
			for (String[] captured_groups : render_tags)
			{
				// only execute the renderer if the value hasn't been set in the
				// template yet
				if (!isValueSet(captured_groups[0]))
				{
					String classname = captured_groups[1];
					try
					{
						Class klass = Class.forName(classname);
						if (!ValueRenderer.class.isAssignableFrom(klass))
						{
							throw new RendererWrongTypeException(this, classname);
						}

						ValueRenderer renderer = null;
						try
						{
							renderer = (ValueRenderer)klass.newInstance();
						}
						catch (Exception e)
						{
							throw new RendererInstantiationException(this, classname, e);
						}

						setValue(captured_groups[0], renderer.render(this, captured_groups[0], captured_groups[2]));
						if (setValues != null)
						{
							setValues.add(captured_groups[0]);
						}
					}
					catch (ClassNotFoundException e)
					{
						throw new RendererNotFoundException(this, classname, e);
					}
				}
			}
		}
	}

	public List<String> evaluateConfigTags()
	{
		List<String> set_values = new ArrayList<String>();
		_evaluateConfigTags(set_values);
		return set_values;
	}

	private void _evaluateConfigTags(List<String> setValues)
	{
		// process the config tags
		List<String[]>	config_tags = getFilteredValues(TemplateFactory.TAG_CONFIG);
		if (config_tags != null &&
			Config.hasRepInstance())
		{
			String	config_key = null;
			String	config_value = null;

			for (String[] captured_groups : config_tags)
			{
				// only set the config value if the value hasn't been set in the
				// template yet
				if (!isValueSet(captured_groups[0]))
				{
					config_key = captured_groups[1];

					// obtain the configuration value
					config_value = Config.getRepInstance().getString(config_key);

					// don't continue if the config parameter doesn't exist
					if (config_value != null)
					{
						// set the config value in the template
						setValue(captured_groups[0], getEncoder().encode(config_value));
						if (setValues != null)
						{
							setValues.add(captured_groups[0]);
						}
					}
				}
			}
		}
	}

	public List<String> evaluateL10nTags()
	{
		List<String> set_values = new ArrayList<String>();
		_evaluateL10nTags(set_values);
		return set_values;
	}

	private void _evaluateL10nTags(List<String> setValues)
	{
		// process the localization keys
		List<String[]>	l10n_tags = getFilteredValues(TemplateFactory.TAG_L10N);
		if (l10n_tags != null && l10n_tags.size() > 0)
		{
			String	l10n_key = null;
			String	l10n_value = null;
			String	l10n_bundle = null;

			for (String[] captured_groups : l10n_tags)
			{
				// only set the config value if the value hasn't been set in the
				// template yet
				if (!isValueSet(captured_groups[0]))
				{
					l10n_value = null;

					// check if an explicit bundle name was provided
					// if not go through all the bundles that have been registered
					// for this template instance
					if (null == captured_groups[2])
					{
						if (hasResourceBundles())
						{
							l10n_key = captured_groups[1];

							for (ResourceBundle bundle : mResourceBundles)
							{
								// obtain the configuration value
								try
								{
									l10n_value = bundle.getString(l10n_key);
									break;
								}
								catch (MissingResourceException e)
								{
									// no-op, go to the next resource bundle
								}
							}
						}
					}
					else
					{
						l10n_bundle = captured_groups[1];
						l10n_key = captured_groups[2];

						ResourceBundle bundle = Localization.getResourceBundle(l10n_bundle);
						if (bundle != null)
						{
							l10n_value = bundle.getString(l10n_key);
						}
						else
						{
							throw new ResourceBundleNotFoundException(getName(), captured_groups[0], l10n_bundle);
						}
					}

					// don't continue if the config parameter doesn't exist
					if (l10n_value != null)
					{
						// set the config value in the template
						setValue(captured_groups[0], getEncoder().encodeDefensive(l10n_value));
						if (setValues != null)
						{
							setValues.add(captured_groups[0]);
						}
					}
				}
			}
		}
	}

	public List<String> evaluateLangTags(String id)
	{
		if (null == id)	throw new IllegalArgumentException("id can't be null.");

		List<String> set_values = new ArrayList<String>();
		_evaluateLangTags(set_values, TemplateFactory.PREFIX_LANG+id);
		return set_values;
	}

	private void _evaluateLangTags(List<String> setValues, String id)
	{
		// process the lang keys
		List<String[]>	lang_blocks = getFilteredBlocks(TemplateFactory.TAG_LANG);
		String			language = getLanguage();
		if (lang_blocks != null &&
			language != null)
		{

			for (String[] lang_block : lang_blocks)
			{
				if (id != null &&
					!id.equals(lang_block[1]))
				{
					continue;
				}

				if (null == id &&
					isValueSet(lang_block[1]))
				{
					continue;
				}

				String	block_lang = lang_block[lang_block.length-1];
				if (block_lang.equals(language))
				{
					setBlock(lang_block[1], lang_block[0]);
					if (setValues != null)
					{
						setValues.add(lang_block[1]);
					}
				}
			}
		}
	}

	public List<String> evaluateExpressionTags(String id)
	{
		if (null == id)	throw new IllegalArgumentException("id can't be null.");

		List<String> set_values = new ArrayList<String>();
		_evaluateOgnlTags(set_values, TemplateFactory.PREFIX_OGNL+id);
		_evaluateMvelTags(set_values, TemplateFactory.PREFIX_MVEL+id);
		_evaluateGroovyTags(set_values, TemplateFactory.PREFIX_GROOVY+id);
		_evaluateJaninoTags(set_values, TemplateFactory.PREFIX_JANINO+id);
		return set_values;
	}

	public List<String> evaluateExpressionConfigTags(String id)
	{
		if (null == id)	throw new IllegalArgumentException("id can't be null.");

		List<String> set_values = new ArrayList<String>();
		_evaluateOgnlConfigTags(set_values, TemplateFactory.PREFIX_OGNL_CONFIG+id);
		_evaluateMvelConfigTags(set_values, TemplateFactory.PREFIX_MVEL_CONFIG+id);
		_evaluateGroovyConfigTags(set_values, TemplateFactory.PREFIX_GROOVY_CONFIG+id);
		_evaluateJaninoConfigTags(set_values, TemplateFactory.PREFIX_JANINO_CONFIG+id);
		return set_values;
	}

	private void _evaluateOgnlTags(List<String> setValues, String id)
	{
		if (hasFilteredBlocks(TemplateFactory.TAG_OGNL))
		{
			FilteredTagProcessorOgnl.getInstance().processTags(setValues, this, getFilteredBlocks(TemplateFactory.TAG_OGNL), id, Template.class, "template", this, null);
		}
	}

	private void _evaluateOgnlConfigTags(List<String> setValues, String id)
	{
		if (hasFilteredBlocks(TemplateFactory.TAG_OGNL_CONFIG))
		{
			FilteredTagProcessorOgnl.getInstance().processTags(setValues, this, getFilteredBlocks(TemplateFactory.TAG_OGNL_CONFIG), id, Config.class, "config", Config.getRepInstance(), null);
		}
	}

	private void _evaluateMvelTags(List<String> setValues, String id)
	{
		if (hasFilteredBlocks(TemplateFactory.TAG_MVEL))
		{
			FilteredTagProcessorMvel.getInstance().processTags(setValues, this, getFilteredBlocks(TemplateFactory.TAG_MVEL), id, Template.class, "template", this, null);
		}
	}

	private void _evaluateMvelConfigTags(List<String> setValues, String id)
	{
		if (hasFilteredBlocks(TemplateFactory.TAG_MVEL_CONFIG))
		{
			FilteredTagProcessorMvel.getInstance().processTags(setValues, this, getFilteredBlocks(TemplateFactory.TAG_MVEL_CONFIG), id, Config.class, "config", Config.getRepInstance(), null);
		}
	}

	private void _evaluateGroovyTags(List<String> setValues, String id)
	{
		if (hasFilteredBlocks(TemplateFactory.TAG_GROOVY))
		{
			FilteredTagProcessorGroovy.getInstance().processTags(setValues, this, getFilteredBlocks(TemplateFactory.TAG_GROOVY), id, Template.class, "template", this, null);
		}
	}

	private void _evaluateGroovyConfigTags(List<String> setValues, String id)
	{
		if (hasFilteredBlocks(TemplateFactory.TAG_GROOVY_CONFIG))
		{
			FilteredTagProcessorGroovy.getInstance().processTags(setValues, this, getFilteredBlocks(TemplateFactory.TAG_GROOVY_CONFIG), id, Config.class, "config", Config.getRepInstance(), null);
		}
	}

	private void _evaluateJaninoTags(List<String> setValues, String id)
	{
		if (hasFilteredBlocks(TemplateFactory.TAG_JANINO))
		{
			FilteredTagProcessorJanino.getInstance().processTags(setValues, this, getFilteredBlocks(TemplateFactory.TAG_JANINO), id, Template.class, "template", this, null);
		}
	}

	private void _evaluateJaninoConfigTags(List<String> setValues, String id)
	{
		if (hasFilteredBlocks(TemplateFactory.TAG_JANINO_CONFIG))
		{
			FilteredTagProcessorJanino.getInstance().processTags(setValues, this, getFilteredBlocks(TemplateFactory.TAG_JANINO_CONFIG), id, Config.class, "config", Config.getRepInstance(), null);
		}
	}

	public final InternalValue createInternalValue()
	{
		return new InternalValue(this);
	}

	public final void setValue(String id, List<CharSequence> deferredContent)
	throws TemplateException
	{
		if (null == id ||
			0 == id.length() ||
			!hasValueId(id))
		{
			throw new ValueUnknownException(id);
		}

		if (mFixedValues.containsKey(id))
		{
			mFixedValues.remove(id);
		}

		mConstructedValues.put(id, new InternalValue(this, deferredContent));
	}

	public final void setValue(String id, InternalValue internalValue)
	throws TemplateException
	{
		if (null == id ||
			0 == id.length() ||
			!hasValueId(id))
		{
			throw new ValueUnknownException(id);
		}
		if (null == internalValue)
		{
			internalValue = createInternalValue();
		}

		if (mFixedValues.containsKey(id))
		{
			mFixedValues.remove(id);
		}

		mConstructedValues.put(id, internalValue);
	}

	public final void setValue(String id, Template template)
	throws TemplateException
	{
		if (null == template)
		{
			setValue(id, "");
		}

		setValue(id, template.getContent());
	}

	public final void setValue(String id, Object value)
	throws TemplateException
	{
		setValue(id, String.valueOf(value));
	}

	public final void setValue(String id, boolean value)
	throws TemplateException
	{
		setValue(id, String.valueOf(value));
	}

	public final void setValue(String id, char value)
	throws TemplateException
	{
		setValue(id, String.valueOf(value));
	}

	public final void setValue(String id, char[] value)
	throws TemplateException
	{
		setValue(id, String.valueOf(value));
	}

	public final void setValue(String id, char[] value, int offset, int count)
	throws TemplateException
	{
		setValue(id, String.valueOf(value, offset, count));
	}

	public final void setValue(String id, double value)
	throws TemplateException
	{
		setValue(id, String.valueOf(value));
	}

	public final void setValue(String id, float value)
	throws TemplateException
	{
		setValue(id, String.valueOf(value));
	}

	public final void setValue(String id, int value)
	throws TemplateException
	{
		setValue(id, String.valueOf(value));
	}

	public final void setValue(String id, long value)
	throws TemplateException
	{
		setValue(id, String.valueOf(value));
	}

	public final void setValue(String id, String value)
	throws TemplateException
	{
		setValue(id, (CharSequence)value);
	}

	public final void setValue(String id, CharSequence value)
	throws TemplateException
	{
		if (null == id ||
			0 == id.length() ||
			!hasValueId(id))
		{
			throw new ValueUnknownException(id);
		}
		if (null == value)
		{
			value = "";
		}

		mFixedValues.remove(id);
		mConstructedValues.remove(id);
		mFixedValues.put(id, new InternalString(value));
	}

	public void setBean(Object bean)
	throws TemplateException
	{
		setBean(bean, null, true);
	}

	public void setBean(Object bean, String prefix)
	throws TemplateException
	{
		setBean(bean, prefix, true);
	}

	public void setBean(Object bean, String prefix, boolean encode)
	throws TemplateException
	{
		if (null == mBeanHandler)
		{
			throw new BeanHandlerUnsupportedException(this, bean);
		}

		mBeanHandler.setBean(this, bean, prefix, encode);
	}

	public void removeBean(Object bean)
	throws TemplateException
	{
		removeBean(bean, null);
	}

	public void removeBean(Object bean, String prefix)
	throws TemplateException
	{
		if (null == mBeanHandler)
		{
			throw new BeanHandlerUnsupportedException(this, bean);
		}

		mBeanHandler.removeBean(this, bean, prefix);
	}

	public final void appendValue(String id, Object value)
	throws TemplateException
	{
		appendValue(id, String.valueOf(value));
	}

	public final void appendValue(String id, boolean value)
	throws TemplateException
	{
		appendValue(id, String.valueOf(value));
	}

	public final void appendValue(String id, char value)
	throws TemplateException
	{
		appendValue(id, String.valueOf(value));
	}

	public final void appendValue(String id, char[] value)
	throws TemplateException
	{
		appendValue(id, String.valueOf(value));
	}

	public final void appendValue(String id, char[] value, int offset, int count)
	throws TemplateException
	{
		appendValue(id, String.valueOf(value, offset, count));
	}

	public final void appendValue(String id, double value)
	throws TemplateException
	{
		appendValue(id, String.valueOf(value));
	}

	public final void appendValue(String id, float value)
	throws TemplateException
	{
		appendValue(id, String.valueOf(value));
	}

	public final void appendValue(String id, int value)
	throws TemplateException
	{
		appendValue(id, String.valueOf(value));
	}

	public final void appendValue(String id, long value)
	throws TemplateException
	{
		appendValue(id, String.valueOf(value));
	}

	public final void appendValue(String id, String value)
	throws TemplateException
	{
		if (null == id ||
			0 == id.length() ||
			!hasValueId(id))
		{
			throw new ValueUnknownException(id);
		}
		if (null == value)
		{
			return;
		}

		if (mFixedValues.containsKey(id))
		{
			mFixedValues.get(id).append(value);
		}
		else if (mConstructedValues.containsKey(id))
		{
			mConstructedValues.get(id).appendText(value);
		}
		else
		{
			mFixedValues.put(id, new InternalString(value));
		}
	}

	public final String getValue(String id)
	throws TemplateException
	{
		if (null == id ||
			0 == id.length() ||
			!hasValueId(id))
		{
			throw new ValueUnknownException(id);
		}

		if (mFixedValues.containsKey(id))
		{
			return mFixedValues.get(id).toString();
		}
		if (mConstructedValues.containsKey(id))
		{
			ExternalValue result = new ExternalValue();
			mConstructedValues.get(id).appendExternalForm(result);
			return result.toString();
		}

		return getDefaultValue(id);
	}

	public abstract String getDefaultValue(String id);

	public boolean hasDefaultValue(String id)
	{
		if (null == getDefaultValue(id))
		{
			return false;
		}

		return true;
	}

	abstract public List<String[]> getFilteredBlocks(String filter);
	abstract public boolean hasFilteredBlocks(String filter);

	abstract public List<String[]> getFilteredValues(String filter);
	abstract public boolean hasFilteredValues(String filter);

	public final boolean hasBlock(String id)
	{
		if (null == id ||
			0 == id.length())
		{
			return false;
		}

		ExternalValue	temp_value = new ExternalValue();

		return appendBlockExternalForm(id, temp_value);
	}

	public final boolean isValueSet(String id)
	{
		if (null == id ||
			0 == id.length())
		{
			return false;
		}

		return mFixedValues.containsKey(id) || mConstructedValues.containsKey(id);
	}

	public final int countValues()
	{
		return mFixedValues.size()+mConstructedValues.size();
	}

	public final void removeValue(String id)
	{
		if (null == id ||
			0 == id.length() ||
			!hasValueId(id))
		{
			throw new ValueUnknownException(id);
		}

		mFixedValues.remove(id);
		mConstructedValues.remove(id);
	}

	public final void removeValues(List<String> ids)
	{
		if (null == ids ||
			0 == ids.size())
		{
			return;
		}

		for (String id : ids)
		{
			removeValue(id);
		}
	}
	
	public final void blankValue(String id)
	{
		setValue(id, "");
	}
	
	public final void clear()
	{
		mFixedValues = new HashMap<String, InternalString>();
		mConstructedValues = new HashMap<String, InternalValue>();
		mResourceBundles = null;
		if (mDefaultResourceBundles != null)
		{
			mResourceBundles = new ArrayList<ResourceBundle>(mDefaultResourceBundles);
		}
		initialize();
	}

	public abstract String[] getAvailableValueIds();
	public abstract Collection<String> getUnsetValueIds();
	public abstract boolean hasValueId(String id);

	public abstract long getModificationTime();

	// make the template only instantiateable from within this package or from derived classes
	protected AbstractTemplate()
	{
	}

	protected void appendTextInternal(InternalValue value, CharSequence text)
	{
		value.appendText(text);
	}

	protected void increasePartsCapacityInternal(InternalValue value, int size)
	{
		value.increasePartsCapacity(size);
	}

	protected void increaseValuesCapacityInternal(InternalValue value, int size)
	{
		value.increaseValuesCapacity(size);
	}

	protected abstract boolean appendBlockExternalForm(String id, ExternalValue result);
	protected abstract boolean appendBlockInternalForm(String id, InternalValue result);

	protected final void appendValueExternalForm(String id, String tag, ExternalValue result)
	{
		assert id != null;
		assert id.length() != 0;

		CharSequence fixed_value = mFixedValues.get(id);
		if (fixed_value != null)
		{
			result.add(fixed_value);
			return;
		}

		InternalValue constructed_value = mConstructedValues.get(id);
		if (constructed_value != null)
		{
			constructed_value.appendExternalForm(result);
			return;
		}

		if (!appendDefaultValueExternalForm(id, result))
		{
			result.add(tag);
		}
	}

	protected abstract boolean appendDefaultValueExternalForm(String id, ExternalValue result);

	protected final void appendValueInternalForm(String id, String tag, InternalValue result)
	{
		CharSequence fixed_value = mFixedValues.get(id);
		if (fixed_value != null)
		{
			result.appendText(fixed_value);
			return;
		}

		InternalValue constructed_value = mConstructedValues.get(id);
		if (constructed_value != null)
		{
			result.appendConstructedValue(constructed_value);
			return;
		}

		if (!appendDefaultValueInternalForm(id, result))
		{
			result.appendValueId(id, tag);
		}
	}

	protected abstract boolean appendDefaultValueInternalForm(String id, InternalValue result);

	public final BeanHandler getBeanHandler()
	{
		return mBeanHandler;
	}

	final void setBeanHandler(BeanHandler beanHandler)
	{
		mBeanHandler = beanHandler;
	}

	public final TemplateEncoder getEncoder()
	{
		return mEncoder;
	}

	final void setEncoder(TemplateEncoder encoder)
	{
		if (null == encoder)
		{
			mEncoder = EncoderDummy.getInstance();
		}
		else
		{
			mEncoder = encoder;
		}
	}

	void setDefaultResourceBundles(ArrayList<ResourceBundle> bundles)
	{
		mDefaultResourceBundles = bundles;
		if (bundles != null)
		{
			mResourceBundles = new ArrayList<ResourceBundle>(bundles);
		}
	}

	public final void addResourceBundle(ResourceBundle resourceBundle)
	{
		if (null == resourceBundle)
		{
			return;
		}

		if (null == mResourceBundles)
		{
			mResourceBundles = new ArrayList<ResourceBundle>();
		}

		mResourceBundles.add(resourceBundle);
	}

	public final Collection<ResourceBundle> getResourceBundles()
	{
		if (null == mResourceBundles)
		{
			mResourceBundles = new ArrayList<ResourceBundle>();
		}

		return mResourceBundles;
	}

	public final boolean hasResourceBundles()
	{
		return mResourceBundles != null && mResourceBundles.size() > 0;
	}

	public void setLanguage(String lang)
	{
		mLanguage = lang;
	}

	public String getLanguage()
	{
		if (null == mLanguage)
		{
			return RifeConfig.Tools.getDefaultLanguage();
		}

		return mLanguage;
	}

	public void setExpressionVar(String name, Object value)
	{
		if (null == mExpressionVars)
		{
			mExpressionVars = new HashMap<String, Object>();
		}

		mExpressionVars.put(name, value);
	}

	public void setExpressionVars(Map<String, Object> map)
	{
		mExpressionVars = map;
	}

	public Map<String, Object> getExpressionVars()
	{
		return mExpressionVars;
	}

	final void initialize()
	throws TemplateException
	{
		_evaluateConfigTags(null);
		_evaluateL10nTags(null);

		if (null == mInitializer)
		{
			return;
		}

		mInitializer.initialize(this);
	}

	final void setInitializer(TemplateInitializer initializer)
	{
		mInitializer = initializer;
	}

	public void cacheObject(String key, Object value)
	{
		if (null == key)
		{
			return;
		}

		if (null == mCache)
		{
			mCache = new HashMap<String, Object>();
		}

		mCache.put(key, value);
	}

	public Object getCacheObject(String key)
	{
		if (null == mCache)
		{
			return null;
		}

		return mCache.get(key);
	}

	public String getDefaultContentType()
	{
		return mDefaultContentType;
	}

	public void setDefaultContentType(String defaultContentType)
	{
		mDefaultContentType = defaultContentType;
	}

	protected static boolean isTemplateClassModified(URL templateResource, long templateModificationTime,
													 Map templateDependencies, String templateModificationState,
													 ResourceFinder resourceFinder, String modificationState)
	{
        try
        {
            if (Parser.getModificationTime(resourceFinder, templateResource) > templateModificationTime)
            {
                return true;
            }

            if (templateDependencies.size() > 0)
            {
                Iterator    url_it = templateDependencies.keySet().iterator();
                URL         dependency_resource = null;
                while (url_it.hasNext())
                {
                    dependency_resource = (URL)url_it.next();
                    if (Parser.getModificationTime(resourceFinder, dependency_resource) > ((Long)templateDependencies.get(dependency_resource)).longValue())
                    {
                        return true;
                    }
                }
            }

            if (templateModificationState != null || modificationState != null)
            {
                if (null == templateModificationState || null == modificationState)
                {
                    return true;
                }

                if (!templateModificationState.equals(modificationState))
                {
                    return true;
                }
            }

        }
        catch (TemplateException e)
        {
            return false;
        }

        return false;
    }

	public Template clone()
	{
		AbstractTemplate new_template = null;
		try
		{
			new_template = (AbstractTemplate)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			new_template = null;
		}

		new_template.mBeanHandler = mBeanHandler;
		new_template.mInitializer = mInitializer;
		new_template.mEncoder = mEncoder;
		new_template.mLanguage = mLanguage;
		new_template.mDefaultContentType = mDefaultContentType;

		new_template.mFixedValues = new HashMap<String, InternalString>();

		for (String value_id : mFixedValues.keySet())
		{
			new_template.mFixedValues.put(value_id, mFixedValues.get(value_id));
		}

		new_template.mConstructedValues = new HashMap<String, InternalValue>();

		for (String constructed_value_id : mConstructedValues.keySet())
		{
			new_template.mConstructedValues.put(constructed_value_id, mConstructedValues.get(constructed_value_id));
		}

		if (mExpressionVars != null)
		{
			new_template.mExpressionVars = new HashMap<String, Object>(mExpressionVars);
		}

		return new_template;
	}
}
