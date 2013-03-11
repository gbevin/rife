/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: CrudElement.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.elements.admin;

import com.uwyn.rife.cmf.ContentInfo;
import com.uwyn.rife.config.Config;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.crud.CrudPropertyNames;
import com.uwyn.rife.crud.CrudSiteProcessor;
import com.uwyn.rife.crud.CrudTemplateFactory;
import com.uwyn.rife.crud.dam.CrudContentQueryManager;
import com.uwyn.rife.crud.templates.AdminTemplateTransformer;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.site.*;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.Localization;
import com.uwyn.rife.tools.StringUtils;

import java.util.*;

public abstract class CrudElement extends Element
{
	private String					mImplementation = null;
	private Class					mBeanClass = null;
	private String					mBeanClassName = null;
	private Constrained				mBeanConstrainedInstance = null;
	private String					mShortClassName = null;
	private String					mIdentityVarName = null;
	private String					mSubmissionName = null;
	private String					mCrudPrefix = null;
	private Datasource				mDatasource = null;
	private CrudContentQueryManager	mContentQueryManager = null;
	private CrudTemplateFactory		mTemplateFactory = null;
	private Template				mTemplate = null;

	public abstract AdminTemplateTransformer getTransformer();

	public String getImplementation()
	{
		return mImplementation;
	}

	public Class getBeanClass()
	{
		return mBeanClass;
	}

	public String getBeanClassName()
	{
		return mBeanClassName;
	}

	public String getShortClassName()
	{
		return mShortClassName;
	}

	public Constrained getDefaultBeanConstrainedInstance()
	{
		if (null == mBeanConstrainedInstance)
		{
			mBeanConstrainedInstance = ConstrainedUtils.getConstrainedInstance(mBeanClass);
		}

		return mBeanConstrainedInstance;
	}

	public ConstrainedProperty getManytooneConstrainedProperty()
	{
		String manytoone_property_name = getPropertyString(CrudSiteProcessor.IDENTIFIER_MANYTOONE_PROPERTYNAME);
		if (manytoone_property_name != null)
		{
			return getDefaultBeanConstrainedInstance().getConstrainedProperty(manytoone_property_name);
		}

		return null;
	}

	public String getIdentityVarName()
	{
		return mIdentityVarName;
	}

	public String getSubmissionName()
	{
		return mSubmissionName;
	}

	public String getCrudPrefix()
	{
		return mCrudPrefix;
	}

	public Datasource getDatasource()
	{
		return mDatasource;
	}

	public CrudContentQueryManager getContentQueryManager()
	{
		return mContentQueryManager;
	}

	public CrudTemplateFactory getTemplateFactory()
	{
		return mTemplateFactory;
	}

	public Template getTemplate()
	{
		if (null == mTemplate)
		{
			Template template = mTemplateFactory.get(getTransformer());

			boolean detect_client_locale = Convert.toBoolean(getProperty(CrudPropertyNames.DETECT_CLIENT_LOCALE), false);
			if (detect_client_locale)
			{
				Enumeration locales = getRequestLocales();
				while (locales.hasMoreElements())
				{
					Locale locale = (Locale)locales.nextElement();
					template.addResourceBundle(Localization.getResourceBundle("l10n/crud/admin-"+StringUtils.encodeClassname(mBeanClassName), locale));
				}
			}
			String language = RifeConfig.Tools.getDefaultLanguage();
			template.addResourceBundle(Localization.getResourceBundle("l10n/crud/admin-"+StringUtils.encodeClassname(mBeanClassName), language));

			if (detect_client_locale)
			{
				Enumeration locales = getRequestLocales();
				while (locales.hasMoreElements())
				{
					Locale locale = (Locale)locales.nextElement();
					template.addResourceBundle(Localization.getResourceBundle("l10n/crud/admin", locale));
				}
			}
			template.addResourceBundle(Localization.getResourceBundle("l10n/crud/admin", language));

			mTemplate = template;
		}

		return mTemplate;
	}

	protected void generateManyToOneSelectFields(Template t, Constrained constrained)
	{
		if (null == constrained)
		{
			return;
		}

		// iterate over the constrained properties
		for (ConstrainedProperty property : (Collection<ConstrainedProperty>)constrained.getConstrainedProperties())
		{
			Map<String, String> list_names = generateManyToOneIdentifiers(property);
			if (list_names != null &&
				list_names.size() > 0)
			{
				// update the property constraints to define the list with accepted keys
				property.inList(list_names.keySet());

				// construct a resourcebundle from the key-value map and add it to the template
				t.addResourceBundle(new SelectResourceBundle(property.getPropertyName(), list_names));
			}
		}
	}

	protected Map<String, String> generateManyToOneIdentifiers(ConstrainedProperty property)
	{
		// look for properties that have many-to-one associations and that aren't the
		// one-to-may association of another class
		if (property.hasManyToOne() &&
			property.getManyToOne().getAssociatedClass() != null &&
			(!hasProperty(CrudSiteProcessor.IDENTIFIER_ASSOCIATED_CLASSNAME) ||
			!getPropertyString(CrudSiteProcessor.IDENTIFIER_ASSOCIATED_CLASSNAME).equals(property.getPropertyName())))
		{
			// restore all the possible beans for the many-to-one value
			CrudContentQueryManager manager = new CrudContentQueryManager(getDatasource(), property.getManyToOne().getAssociatedClass());
			List associations_beans = manager.restore();

			Map<String, String> list_names = new LinkedHashMap<String, String>(associations_beans.size());

			// offer an no selection 'empty' option if the porperty isn't mandatory
			if (!property.isNotNull())
			{
				list_names.put("", " ");
			}

			// construct a map with the bean's identifiers as the keys and
			// the textual identifier as the values
			String key;
			String value;
			Constrained constrained_association;
			ConstrainedBean constrained_bean_association;
			for (Object association : associations_beans)
			{
				key = String.valueOf(manager.getIdentifierValue(association));

				// by default, the value is the same as the key, which is the identifier value
				value = key;

				// try to get and alternative textual identifier as the value
				constrained_association = ConstrainedUtils.makeConstrainedInstance(association);
				if (constrained_association != null)
				{
					constrained_bean_association = constrained_association.getConstrainedBean();
					if (constrained_bean_association != null &&
						constrained_bean_association.hasTextualIdentifier())
					{
						TextualIdentifierGenerator identifier = constrained_bean_association.getTextualIdentifier();
						identifier.setBean(association);
						value = identifier.generateIdentifier();
					}
				}

				// store the entry
				list_names.put(key, value);
			}

			return list_names;
		}

		return null;
	}

	protected void displayImageProperty(Template template, ConstrainedProperty property, ContentInfo info, String valueIdPrefix)
	{
		if (null == valueIdPrefix)
		{
			valueIdPrefix = "";
		}

		String legend_id = valueIdPrefix + property.getPropertyName() + "-legend";
		if (info != null)
		{
			String width = info.getProperty("cmf:width");
			String height = info.getProperty("cmf:height");

			String src_id = valueIdPrefix + property.getPropertyName() + "-src";
			if (template.hasValueId(src_id))
			{
				template.setValue(src_id, template.getEncoder().encode(
					getExitQueryUrl(getCrudPrefix() + CrudSiteProcessor.SUFFIX_EXIT_SERVECONTENT,
												info.getPath()).toString()));
			}

			String width_id = valueIdPrefix + property.getPropertyName() + "-width";
			if (template.hasValueId(width_id))
			{
				template.setValue(width_id, width);
			}

			String height_id = valueIdPrefix + property.getPropertyName() + "-height";
			if (template.hasValueId(height_id))
			{
				template.setValue(height_id, height);
			}

			String size_id = valueIdPrefix + property.getPropertyName() + "-size";
			if (template.hasValueId(size_id))
			{
				template.setValue(size_id, info.getSize());
			}

			if (template.hasValueId(legend_id))
			{
				if (width != null &&
					height != null)
				{
					template.setValue(legend_id, width + "x" + height + " ");
				}
				template.appendValue(legend_id, info.getFormattedSize());
			}
		}
		else
		{
			if (template.hasValueId(legend_id))
			{
				template.setValue(legend_id, "");
			}
		}
	}

	protected void displayRawProperty(Template template, ConstrainedProperty property, String cmfPath, String valueIdPrefix)
	{
		if (null == valueIdPrefix)
		{
			valueIdPrefix = "";
		}

		ContentInfo info = getContentQueryManager().getContentManager().getContentInfo(cmfPath);
		String legend_id = valueIdPrefix + property.getPropertyName() + "-legend";
		if (info != null)
		{
			String url_id = valueIdPrefix + property.getPropertyName() + "-url";
			template.setValue(url_id, getExitQueryUrl(getCrudPrefix() + CrudSiteProcessor.SUFFIX_EXIT_SERVECONTENT, cmfPath));

			String name_id = valueIdPrefix + property.getPropertyName() + "-name";
			if (template.hasValueId(name_id))
			{
				template.setValue(name_id, template.getEncoder().encode(info.getName()));
			}

			String size_id = valueIdPrefix + property.getPropertyName() + "-size";
			if (template.hasValueId(size_id))
			{
				template.setValue(size_id, info.getSize());
			}

			if (template.hasValueId(legend_id))
			{
				template.setValue(legend_id, info.getFormattedSize());
			}
		}
		else
		{
			if (template.hasValueId(legend_id))
			{
				template.setValue(legend_id, "");
			}
		}
	}

	public void initialize()
	{
		String crud_template_type = getPropertyString(CrudPropertyNames.TEMPLATE_TYPE);
		if (crud_template_type != null)
		{
			mTemplateFactory = CrudTemplateFactory.getCrudFactory(crud_template_type);
		}
		else
		{
			// try to infer the template factory from the group name
			String crud_template_group = getPropertyString(CrudPropertyNames.TEMPLATE_GROUP);
			if (crud_template_group != null)
			{
				mTemplateFactory = CrudTemplateFactory.getCrudFactory("engine"+crud_template_group);
			}
		}

		// fall back to the html template factory
		if (null == mTemplateFactory)
		{
			mTemplateFactory = CrudTemplateFactory.CRUD_ENGINEHTML;
		}

		mDatasource = (Datasource)getProperty("datasource", Datasources.getRepInstance().getDatasource(Config.getRepInstance().getString("CRUD_DATASOURCE", Config.getRepInstance().getString("DATASOURCE"))));
		mImplementation = (String)getProperty(CrudSiteProcessor.IDENTIFIER_IMPLEMENTATION);
		mBeanClass = (Class)getProperty(CrudSiteProcessor.IDENTIFIER_CLASS);
		if (mBeanClass != null)
		{
			mBeanClassName = mBeanClass.getName();
			mShortClassName = mBeanClassName.substring(mBeanClassName.lastIndexOf(".")+1).toLowerCase();
			mIdentityVarName = mShortClassName+ConstrainedUtils.getIdentityProperty(getBeanClass());
			mSubmissionName = mShortClassName+"data";
			mCrudPrefix = CrudSiteProcessor.CRUD_PREFIX+mBeanClassName;
			mContentQueryManager = new CrudContentQueryManager(mDatasource, mBeanClass);

			ConstrainedProperty manytoone_property = getManytooneConstrainedProperty();
			if (manytoone_property != null)
			{
				ConstrainedProperty.ManyToOne manytoone_constraint = manytoone_property.getManyToOne();
				String input_name = manytoone_constraint.getDerivedTable()+manytoone_constraint.getColumn();
				if (!hasInputValue(input_name))
				{
					exit(CrudSiteProcessor.CRUD_PREFIX+getPropertyString(CrudSiteProcessor.IDENTIFIER_ASSOCIATED_CLASSNAME)+"-home");
				}
			}
		}
	}
}