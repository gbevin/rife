/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JKeySelectableList.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.ListModel;
	
public class JKeySelectableList extends JList implements KeyListener
{
	private static final long serialVersionUID = -9040186688203231054L;

	public JKeySelectableList()
	{
		super();
		addKeyListener(this);
	}
	
	public JKeySelectableList(Vector items)
	{
		super(items);
		addKeyListener(this);
	}
	
	public void keyReleased(KeyEvent event)
	{
	}
	
	public void keyPressed(KeyEvent event)
	{
	}
	
	public void keyTyped(KeyEvent event)
	{
		selectWithKeyChar(event.getKeyChar());
	}
	
	public boolean selectWithKeyChar(char keyChar)
	{
		int index = -1;

		index = selectionForKey(keyChar, getModel());
		
		if (-1 != index)
		{
			setSelectedIndex(index);
			ensureIndexIsVisible(index);
			
			return true;
		}
		else
		{
			return false;
		}
	}

	public int selectionForKey(char key, ListModel listModel)
	{
		int i = 0;
		int size = 0;
		int current_selection = -1;

		Object selected_item = listModel.getElementAt(getSelectedIndex());
		String value = null;
		String pattern = null;
		
		size = listModel.getSize();
		
		if (null != selected_item)
		{
			selected_item = selected_item.toString();
		
			for (i = 0; i < size; i++)
			{
				if (selected_item.equals(listModel.getElementAt(i).toString()))
				{
					current_selection  =  i;
					break;
				}
			}
		}
		
		pattern = ("" + key).toLowerCase();
		key = pattern.charAt(0);
		
		for (i = ++current_selection; i < size; i++)
		{
			value = listModel.getElementAt(i).toString().toLowerCase();
			if (value.length() > 0 &&
                key == value.charAt(0))
			{
				return i;
			}
		}
		
		for (i = 0; i < current_selection; i++)
		{
			value = listModel.getElementAt(i).toString().toLowerCase();
			if (value.length() > 0 &&
                key == value.charAt(0))
			{
				return i;
			}
		}
		
		return -1;
	}
}
