/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: OpenBeanshellAction.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

import com.uwyn.rife.gui.ui.commands.OpenBeanshell;
import com.uwyn.rife.swing.JAction;
import com.uwyn.rife.tools.Localization;

public class OpenBeanshellAction extends JAction
{
	public OpenBeanshellAction()
	{
		super(new OpenBeanshell(),
            Localization.getString("rife.menu.tools.beanshell.opendesktop"),
			Localization.getChar("rife.menu.tools.beanshell.opendesktop.mnemonic"),
			KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK, false));
	}
}


