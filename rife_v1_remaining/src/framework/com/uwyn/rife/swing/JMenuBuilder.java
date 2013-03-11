/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JMenuBuilder.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import javax.swing.*;

import java.awt.MenuContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class JMenuBuilder
{
	private static final ItemActionEventBridge ITEM_ACTION_EVENT_BRIDGE = new ItemActionEventBridge();
	
	private static class ItemActionEventBridge implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			if (ItemEvent.SELECTED == event.getStateChange())
			{
				JMenuItem menuitem = (JMenuItem)event.getSource();
				Action action = menuitem.getAction();
				if (action != null)
				{
					action.actionPerformed(new ActionEvent(event.getSource(), ActionEvent.ACTION_PERFORMED, menuitem.getActionCommand()));
				}
			}
		}
	}
	
	public static JMenu addMenu(MenuContainer parentMenu, String menuLabel)
	{
		return addMenu(parentMenu, menuLabel, (char)0);
	}

	public static JMenu addMenu(MenuContainer parentMenu, String menuLabel, char mnemonic)
	{
		if (null == parentMenu)			throw new IllegalArgumentException("parentMenu can't be null.");
		if (null == menuLabel)			throw new IllegalArgumentException("menuLabel can't be null.");
		if (0 == menuLabel.length())	throw new IllegalArgumentException("menuLabel can't be empty.");
		
		JMenu menu = new JMenu(menuLabel);
		if (0 != mnemonic)
		{
			menu.setMnemonic(mnemonic);
		}
		
		((JComponent)parentMenu).add(menu);

		return menu;
	}

	public static JMenuItem addMenuItem(MenuContainer parentMenu, Action action)
	{
		if (null == action)	throw new IllegalArgumentException("action can't be null.");
		
		return processMenuItem(new JMenuItem(action), parentMenu, null);
	}

	public static JCheckBoxMenuItem addCheckBoxMenuItem(MenuContainer parentMenu, Action action)
	{
		if (null == action)	throw new IllegalArgumentException("action can't be null.");
		
		return (JCheckBoxMenuItem)processMenuItem(new JCheckBoxMenuItem(action), parentMenu, null);
	}

	public static JCheckBoxMenuItem addCheckBoxMenuItem(MenuContainer parentMenu, ButtonGroup group, Action action)
	{
		if (null == action)	throw new IllegalArgumentException("action can't be null.");
		
		return (JCheckBoxMenuItem)processMenuItem(new JCheckBoxMenuItem(action), parentMenu, null);
	}

	public static JRadioButtonMenuItem addRadioButtonMenuItem(MenuContainer parentMenu, Action action)
	{
		if (null == action)	throw new IllegalArgumentException("action can't be null.");
		
		return (JRadioButtonMenuItem)processMenuItem(new JRadioButtonMenuItem(action), parentMenu, null);
	}

	public static JRadioButtonMenuItem addRadioButtonMenuItem(MenuContainer parentMenu, ButtonGroup group, Action action)
	{
		if (null == action)	throw new IllegalArgumentException("action can't be null.");
		
		return (JRadioButtonMenuItem)processMenuItem(new JRadioButtonMenuItem(action), parentMenu, group);
	}

	private static JMenuItem processMenuItem(JMenuItem menuItem, MenuContainer parentMenu, ButtonGroup group)
	{
		if (null == menuItem)	throw new IllegalArgumentException("menuItem can't be null.");
		if (null == parentMenu)	throw new IllegalArgumentException("parentMenu can't be null.");
		
		((JComponent)parentMenu).add(menuItem);
		menuItem.addItemListener(ITEM_ACTION_EVENT_BRIDGE);

		if (group != null)
		{
			group.add(menuItem);
		}
		
		return menuItem;
	}
}

