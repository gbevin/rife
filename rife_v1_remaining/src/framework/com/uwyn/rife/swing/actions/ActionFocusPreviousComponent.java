/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ActionFocusPreviousComponent.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.FocusManager;
import javax.swing.JComponent;
	
public class ActionFocusPreviousComponent extends AbstractAction
{
	private static final long serialVersionUID = 3855517051766995837L;

	public ActionFocusPreviousComponent()
	{
		super();
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if (null != event)
		{
			JComponent source_component = (JComponent)event.getSource();
			if (null != source_component)
			{
				FocusManager.getCurrentManager().focusPreviousComponent(source_component);
			}
		}
	}
}
