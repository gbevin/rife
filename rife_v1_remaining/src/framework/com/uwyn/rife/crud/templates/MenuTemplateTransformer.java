/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: MenuTemplateTransformer.java 3953 2008-05-08 01:04:08Z gbevin $
 */
package com.uwyn.rife.crud.templates;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.crud.CrudPropertyNames;
import com.uwyn.rife.crud.CrudSiteProcessor;
import com.uwyn.rife.crud.elements.admin.CrudElement;
import com.uwyn.rife.engine.ElementInfo;
import com.uwyn.rife.engine.FlowLink;
import com.uwyn.rife.engine.GlobalExit;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.template.Template;
import com.uwyn.rife.template.exceptions.TemplateException;
import com.uwyn.rife.tools.Localization;
import com.uwyn.rife.tools.StringUtils;

import java.util.*;

public class MenuTemplateTransformer extends AdminTemplateTransformer
{
	protected ElementInfo	mEmbeddingElementInfo = null;
	protected int			mActiveMenuGroupId = 0;
	protected String		mActiveMenuItem = null;
	protected String		mTopElementId = null;
	protected int			mTopExitGroupId = Integer.MAX_VALUE;
	
	public MenuTemplateTransformer(CrudElement element)
	{
		super(element);
		
		mEmbeddingElementInfo = mElement.getEmbeddingElement().getElementInfo();
		
		// auto-select the browse element when the arrival is active
		if (mCrudPrefix != null &&
			mEmbeddingElementInfo.getId().endsWith("."))
		{
			String exit_home = mCrudPrefix+CrudSiteProcessor.SUFFIX_EXIT_BROWSE;
			if (mEmbeddingElementInfo.containsGlobalExit(exit_home))
			{
				mEmbeddingElementInfo = mEmbeddingElementInfo.getGlobalExitInfo(exit_home).getTarget();
			}
		}
	}
	
	public String getSupportedTemplateName()
	{
		return mElement.getPropertyString(CrudPropertyNames.TEMPLATE_NAME_MENU, buildGroupedTemplateName("menu"));
	}

	public String getTemplateNameDifferentiator()
	{
		FlowLink flow_link = mElement.getEmbeddingElement().getElementInfo().getFlowLink(mCrudPrefix+"-home");
		if (null == flow_link)
		{
			return "";
		}

		String home_element_id = flow_link.getTarget().getId();
		String id_prefix = home_element_id.substring(0, home_element_id.lastIndexOf("."));
		String embedding_id = mElement.getEmbeddingElement().getElementInfo().getId();
		if (!embedding_id.startsWith(id_prefix))
		{
			return "";
		}
		
		return "_"+embedding_id.substring(id_prefix.length()+1);
	}
	
	public void transformTemplate(Template t)
	{
		detectTopAndActiveMenuItem();
		
		Map<Integer, List<String>>	menu_levels = getMenuLevels();
		List<String>				highlighted_menu_items = getHighlightedMenuItems();
		Map<ElementInfo, String>	top_menu_items = getTopMenuItems();

		renderTopMenuItems(t, top_menu_items, highlighted_menu_items);
		renderMenuLevels(t, menu_levels, highlighted_menu_items);
	}
	
	protected void detectTopAndActiveMenuItem()
	{
		// determine the group ids of the top menu item and the active
		// menu item, these will be used later to build the menu
		String		menu_item;
		ElementInfo	target;
		int			exit_groupid;
		int			target_groupid;
		for (Map.Entry<String, GlobalExit> globalexit_entry : mEmbeddingElementInfo.getGlobalExitEntries())
		{
			menu_item = globalexit_entry.getKey();
			
			// only consider the CMF admin global exits
			if (menu_item.startsWith(CrudSiteProcessor.CRUD_PREFIX))
			{
				// get the information about this exit
				target = globalexit_entry.getValue().getTarget();
				exit_groupid = globalexit_entry.getValue().getGroupId();
				target_groupid = target.getGroupId();
				
				// adapt the top exit group id if the group id is smaller
				// than the last one that was consider the top groupid
				if (exit_groupid < mTopExitGroupId)
				{
					mTopExitGroupId = exit_groupid;
					mTopElementId = target.getId();
				}
				
				// if the active menu groupid hasn't been set yet and
				// the exit's target id corresponds to the element that
				// is rendering the template, set the group id as the
				// active menu group id
				if (0 == mActiveMenuGroupId &&
					target.getId().equals(mEmbeddingElementInfo.getId()))
				{
					mActiveMenuGroupId = target_groupid;
				}
			}
		}
	}
	
	protected Map<Integer, List<String>> getMenuLevels()
	{
		Map<Integer, List<String>>	primary_menu_levels = new TreeMap<Integer, List<String>>();
		Map<Integer, List<String>>	secondary_menu_levels = new TreeMap<Integer, List<String>>();
		
		String		menu_item;
		ElementInfo	target;
		int			exit_groupid;
		int			target_groupid;
		
		// obtain each menu level and its items
		int				combined_groupid;
		GlobalExit		global_exit;
		List<String>	menu_level = null;
		boolean			is_secondary = false;
		for (Map.Entry<String, GlobalExit> globalexit_entry : mEmbeddingElementInfo.getGlobalExitEntries())
		{
			is_secondary = false;
			menu_item = globalexit_entry.getKey();
			
			// only consider the CMF admin global exits
			if (menu_item.startsWith(CrudSiteProcessor.CRUD_PREFIX) &&
				!menu_item.endsWith(CrudSiteProcessor.SUFFIX_EXIT_SERVECONTENT))
			{
				// obtain the global exit
				global_exit = globalexit_entry.getValue();
				
				// get the target of this exit
				target = global_exit.getTarget();
				
				// get the group ids related to this exit
				exit_groupid = global_exit.getGroupId();
				target_groupid = target.getGroupId();
				
				// handle the association home exits
				if (target.containsProperty(CrudSiteProcessor.IDENTIFIER_ASSOCIATED_CLASSNAME) &&
					target.getId().endsWith("."))
				{
					GlobalExit associated_edit_exit = mEmbeddingElementInfo.getGlobalExitInfo(CrudSiteProcessor.CRUD_PREFIX + target.getPropertyString(CrudSiteProcessor.IDENTIFIER_ASSOCIATED_CLASSNAME) + CrudSiteProcessor.SUFFIX_EXIT_EDIT);
					if (associated_edit_exit != null)
					{
						exit_groupid = associated_edit_exit.getGroupId();
						target_groupid = associated_edit_exit.getTarget().getGroupId();
						is_secondary = true;
					}
				}
				
				// combine the exit's group id and the top menu group id
				// to get a unique combination for this menu creation
				if (mTopElementId.equals(globalexit_entry.getValue().getTarget().getId()))
				{
					combined_groupid = mTopExitGroupId;
				}
				else
				{
					combined_groupid = exit_groupid + target_groupid;
				}
				
				// get the list of menu items, which is the menu level
				// construct it if it doesn't exist yet
				if (is_secondary)
				{
					menu_level = secondary_menu_levels.get(combined_groupid);
				}
				else
				{
					menu_level = primary_menu_levels.get(combined_groupid);
				}
				if (null == menu_level)
				{
					menu_level = new ArrayList<String>();
				}
				
				// strip away the crud-specific exit prefix
				menu_item = menu_item.substring(CrudSiteProcessor.CRUD_PREFIX.length());
				
				// if the target id of the exit corresponds to the element
				// that is rendering the template, use the element's id
				// as the active menu item
				if (target.getId().equals(mEmbeddingElementInfo.getId()))
				{
					mActiveMenuItem = menu_item;
				}
				
				// add the exit to the menu level if it's not the top level
				// and the target group id is lower that the active group id
				// this ensures that no lower-level menus are rendered
				if (!mTopElementId.equals(globalexit_entry.getValue().getTarget().getId()) &&
					target_groupid <= mActiveMenuGroupId)
				{
					if (is_secondary)
					{
						secondary_menu_levels.put(combined_groupid, menu_level);
					}
					else
					{
						primary_menu_levels.put(combined_groupid, menu_level);
					}
					menu_level.add(menu_item);
				}
			}
		}
		
		// merge the primary and secondary menu levels
		for (Map.Entry<Integer, List<String>> secondary_entry : secondary_menu_levels.entrySet())
		{
			if (primary_menu_levels.containsKey(secondary_entry.getKey()))
			{
				primary_menu_levels.get(secondary_entry.getKey()).addAll(secondary_entry.getValue());
			}
			else
			{
				primary_menu_levels.put(secondary_entry.getKey(), secondary_entry.getValue());
			}
		}
		
		return primary_menu_levels;
	}
	
	protected List<String> getHighlightedMenuItems()
	{
		List<String> highlighted_menu_items = new ArrayList<String>();
		
		String			highlight_property = mEmbeddingElementInfo.getPropertyString(CrudSiteProcessor.IDENTIFIER_HIGHLIGHT);
		
		// collect the highlighted menu items
		String			highlight_property_unprefixed = null;
		FlowLink		highlight_flowlink = null;
		ElementInfo		highlight_target = mEmbeddingElementInfo;
		while (highlight_property != null)
		{
			highlight_property_unprefixed = highlight_property.substring(CrudSiteProcessor.CRUD_PREFIX.length());
			
			// prevent circular references
			if (highlighted_menu_items.contains(highlight_property_unprefixed))
			{
				break;
			}
			
			highlighted_menu_items.add(highlight_property_unprefixed);
			
			// handle associations
			if (highlight_property.endsWith(((Class)highlight_target.getProperty(CrudSiteProcessor.IDENTIFIER_CLASS)).getName()) &&
				highlight_target.containsProperty(CrudSiteProcessor.IDENTIFIER_ASSOCIATED_CLASSNAME))
			{
				highlight_property = CrudSiteProcessor.CRUD_PREFIX+highlight_target.getProperty(CrudSiteProcessor.IDENTIFIER_ASSOCIATED_CLASSNAME)+CrudSiteProcessor.SUFFIX_EXIT_BROWSE;
			}
			else
			{
				// get the target element of the last highlight
				highlight_flowlink = highlight_target.getFlowLink(highlight_property);
				if (null == highlight_flowlink)
				{
					break;
				}
				
				highlight_target = highlight_flowlink.getTarget();
				highlight_property = highlight_target.getPropertyString(CrudSiteProcessor.IDENTIFIER_HIGHLIGHT);
			}
		}
		
		return highlighted_menu_items;
	}
	
	protected LinkedHashMap<ElementInfo, String> getTopMenuItems()
	throws EngineException
	{
		LinkedHashMap<ElementInfo, String> top_menu_items = new LinkedHashMap<ElementInfo, String>();
		
		String crud_site_prefix = null;
		if (mTopElementId != null)
		{
			// top_element_id has the format ".SAMPLES.ACCOUNT."
			// and we need ".SAMPLES.", so we need to go two dots backwards to
			// construct crud_site_prefix
			int first_dot_index = mTopElementId.lastIndexOf(".");
			int second_dot_index = mTopElementId.lastIndexOf(".", first_dot_index - 1);
			crud_site_prefix = mTopElementId.substring(0, second_dot_index + 1);
		}
		// if there's no top element id, this is template is printed from an element that sits outside
		// the automatically generated crud site structure, it then assumes that it's part of the site
		// that contains all the crud admin interfaces and uses that site id as the crud site prefix
		else
		{
			String element_id = mElement.getElementInfo().getId();
			int first_dot_index = element_id.lastIndexOf(".");
			crud_site_prefix = element_id.substring(0, first_dot_index + 1);
		}
		
		// we go over all the element ids in the site and get the urls of the
		// home templates these are then used to construct the top menu
		int first_dot_index = -1;
		for (String id : mEmbeddingElementInfo.getSite().getIds())
		{
			if (id.startsWith(crud_site_prefix))
			{
				first_dot_index = id.indexOf(".", crud_site_prefix.length());
				if (-1 == first_dot_index)
				{
					continue;
				}
				
				// only work with the home element
				String element_id = id.substring(first_dot_index);
				if (!element_id.equals("."))
				{
					continue;
				}
				
				// obtain the element info that corresponds to the id
				ElementInfo retrieved_element_info = mEmbeddingElementInfo.getSite().resolveId(id);
				
				// get the class property and construct the home exit name
				Object klass_property = retrieved_element_info.getProperty(CrudSiteProcessor.IDENTIFIER_CLASS);
				if (null == klass_property)
				{
					continue;
				}
				String root_exit_name = ((Class)klass_property).getName();
				
				top_menu_items.put(retrieved_element_info, root_exit_name);
			}
		}
		
		return top_menu_items;
	}
	
	protected void renderTopMenuItems(Template template, Map<ElementInfo, String> topMenuItems, List<String> highlightedMenuItems)
	throws TemplateException
	{
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
		String language = RifeConfig.Tools.getDefaultLanguage();
		if (mBeanClassNameEncoded != null)
		{
			bundles.add(Localization.getResourceBundle("l10n/crud/admin-" + mBeanClassNameEncoded, language));
		}
		bundles.add(Localization.getResourceBundle("l10n/crud/admin", language));
		
		// prepend the top menu if this has been provided
		for (ResourceBundle bundle : bundles)
		{
			if (null == bundle)
			{
				continue;
			}
			
			try
			{
				String prepend = bundle.getString("CRUD_ROOTMENU_PREPEND");
				if (prepend.trim().length() > 0)
				{
					template.appendValue("menu_items", prepend);
					break;
				}
			}
			catch (MissingResourceException e)
			{
				continue;
			}
		}
		
		for (Map.Entry<ElementInfo, String> top_menu_item : topMenuItems.entrySet())
		{
			// indicate when a menu item is active
			if (mEmbeddingElementInfo.getId().equals(top_menu_item.getKey().getId()))
			{
				template.setBlock("menu_item_class", "menu_item_class-active");
			}
			else if (highlightedMenuItems.contains(top_menu_item.getValue()))
			{
				template.setBlock("menu_item_class", "menu_item_class-highlight");
			}
			
			// sets the menu item label
			String url = top_menu_item.getKey().getUrl();
			if (url.startsWith("/"))
			{
				url = url.substring(1);
			}
			template.setValue("url", url);
			template.setValue("short-classname", getShortClassname(top_menu_item.getValue()));
			template.setValue("exit_name", top_menu_item.getValue());
			template.appendBlock("menu_items", "menu_item_top");
			
			// remove the menu item decoration
			template.removeValue("menu_item_class");
		}
		
		// append the top menu if this has been provided
		for (ResourceBundle bundle : bundles)
		{
			if (null == bundle)
			{
				continue;
			}
			
			try
			{
				String append = bundle.getString("CRUD_ROOTMENU_APPEND");
				if (append.trim().length() > 0)
				{
					template.appendValue("menu_items", append);
					break;
				}
			}
			catch (MissingResourceException e)
			{
				continue;
			}
		}
	}
	
	protected String getShortClassname(String menuItem)
	{
		String[] parts = StringUtils.splitToArray(menuItem, "-");
		String classname = parts[0];

		int last_dollarindex = classname.lastIndexOf("$");
		if (last_dollarindex != -1)
		{
			return classname.substring(last_dollarindex+1);
		}

		int last_dotindex = classname.lastIndexOf(".");
		if (last_dotindex != -1)
		{
			return classname.substring(last_dotindex+1);
		}
		
		return classname;
	}
	
	protected void renderMenuLevels(Template t, Map<Integer, List<String>> menuLevels, List<String> highlightedMenuItems)
	throws TemplateException
	{
		// process each menu level seperately and each exit in each seperate
		// level as a menu item
		for (List<String> menu_level : menuLevels.values())
		{
			// display the menu level seperator after the first level
			t.appendBlock("menu_items", "menu_seperator");
			
			// process this level's menu items
			for (String menu_item : menu_level)
			{
				// indicate when a menu item is active
				if (mActiveMenuItem != null &&
					mActiveMenuItem.equals(menu_item))
				{
					t.setBlock("menu_item_class", "menu_item_class-active");
				}
				else if (highlightedMenuItems.contains(menu_item))
				{
					t.setBlock("menu_item_class", "menu_item_class-highlight");
				}
				
				// sets the menu item label
				t.setValue("exit_name", menu_item);
				String[] parts = StringUtils.splitToArray(menu_item, "-");
				if (parts.length <= 1)
				{
					t.setValue("short-exit_name", getShortClassname(menu_item));
				}
				else
				{
					t.setValue("short-exit_name", parts[1]);
				}
				t.appendBlock("menu_items", "menu_item");
				
				// remove the menu item decoration
				t.removeValue("menu_item_class");
			}
		}
	}
}

