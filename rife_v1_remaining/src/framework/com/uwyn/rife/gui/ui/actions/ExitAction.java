/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ExitAction.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui.actions;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

import com.uwyn.rife.gui.ui.commands.Exit;
import com.uwyn.rife.swing.JAction;
import com.uwyn.rife.tools.Localization;

public class ExitAction extends JAction
{
	public ExitAction()
	{
		super(new Exit(),
			Localization.getString("rife.menu.file.exit"),
			Localization.getChar("rife.menu.file.exit.mnemonic"),
			KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK, false));
	}
}


