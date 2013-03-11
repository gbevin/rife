// OBSOLETE
// Just to keep to old rife gui code running

/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: DynamicMenuBuilder.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import javax.swing.*;

import java.awt.MenuContainer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DynamicMenuBuilder implements ActionListener
{
	public JMenu addMenu(MenuContainer parentMenu, String menuLabel)
	{
		return addMenu(parentMenu, menuLabel, (char)0);
	}

	public JMenu addMenu(MenuContainer parentMenu, String menuLabel, Character mnemonic)
	{
		return addMenu(parentMenu, menuLabel, mnemonic.charValue());
	}

	public JMenu addMenu(MenuContainer parentMenu, String menuLabel, char mnemonic)
	{
		JMenu menu = new JMenu(menuLabel);
		if (0 != mnemonic)
		{
			menu.setMnemonic(mnemonic);
		}
		if (null != parentMenu)
		{
			((JComponent)parentMenu).add(menu);
		}

		return menu;
	}

	public JMenuItem addMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action)
	{
		return addMenuItem(parentMenu, menuLabel, action, (char)0, null);
	}

	public JMenuItem addMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, Character mnemonic)
	{
		return addMenuItem(parentMenu, menuLabel, action, mnemonic.charValue(), null);
	}

	public JMenuItem addMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, Character mnemonic, KeyStroke accelerator)
	{
		return addMenuItem(parentMenu, menuLabel, action, mnemonic.charValue(), accelerator);
	}

	public JMenuItem addMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, char mnemonic)
	{
		return addMenuItem(parentMenu, menuLabel, action, mnemonic, null);
	}

	public JMenuItem addMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, char mnemonic, KeyStroke accelerator)
	{
		return processMenuItem(new JMenuItem(menuLabel), parentMenu, action, mnemonic, accelerator);
	}

	public JCheckBoxMenuItem addCheckBoxMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action)
	{
		return addCheckBoxMenuItem(parentMenu, menuLabel, action, (char)0, null);
	}

	public JCheckBoxMenuItem addCheckBoxMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, Character mnemonic)
	{
		return addCheckBoxMenuItem(parentMenu, menuLabel, action, mnemonic.charValue(), null);
	}

	public JCheckBoxMenuItem addCheckBoxMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, Character mnemonic, KeyStroke accelerator)
	{
		return addCheckBoxMenuItem(parentMenu, menuLabel, action, mnemonic.charValue(), accelerator);
	}

	public JCheckBoxMenuItem addCheckBoxMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, char mnemonic)
	{
		return addCheckBoxMenuItem(parentMenu, menuLabel, action, mnemonic, null);
	}

	public JCheckBoxMenuItem addCheckBoxMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, char mnemonic, KeyStroke accelerator)
	{
		return (JCheckBoxMenuItem)processMenuItem(new JCheckBoxMenuItem(menuLabel), parentMenu, action, mnemonic, accelerator);
	}

	public JRadioButtonMenuItem addRadioButtonMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action)
	{
		return addRadioButtonMenuItem(parentMenu, menuLabel, action, (char)0, null);
	}

	public JRadioButtonMenuItem addRadioButtonMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, Character mnemonic)
	{
		return addRadioButtonMenuItem(parentMenu, menuLabel, action, mnemonic.charValue(), null);
	}

	public JRadioButtonMenuItem addRadioButtonMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, Character mnemonic, KeyStroke accelerator)
	{
		return addRadioButtonMenuItem(parentMenu, menuLabel, action, mnemonic.charValue(), accelerator);
	}

	public JRadioButtonMenuItem addRadioButtonMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, char mnemonic)
	{
		return addRadioButtonMenuItem(parentMenu, menuLabel, action, mnemonic, null);
	}

	public JRadioButtonMenuItem addRadioButtonMenuItem(MenuContainer parentMenu, String menuLabel, DynamicMenuAction action, char mnemonic, KeyStroke accelerator)
	{
		return (JRadioButtonMenuItem)processMenuItem(new JRadioButtonMenuItem(menuLabel), parentMenu, action, mnemonic, accelerator);
	}

	private JMenuItem processMenuItem(JMenuItem menuItem, MenuContainer parentMenu, DynamicMenuAction action, char mnemonic, KeyStroke accelerator)
	{
		menuItem.addActionListener(this);
		menuItem.putClientProperty("DYNAMICMENUACTION", action);
		if (0 != mnemonic)
		{
			menuItem.setMnemonic(mnemonic);
		}
		if (null != accelerator)
		{
			menuItem.setAccelerator(accelerator);
		}
		if (null != parentMenu)
		{
			((JComponent)parentMenu).add(menuItem);
		}

		return menuItem;
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		if (source instanceof JMenuItem)
		{
			DynamicMenuAction action = (DynamicMenuAction)((JMenuItem)source).getClientProperty("DYNAMICMENUACTION");
			if (null != action)
			{
				action.execute((JMenuItem)source);
			}
		}
	}
}

