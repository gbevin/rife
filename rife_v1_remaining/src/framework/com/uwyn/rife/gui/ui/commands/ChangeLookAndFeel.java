/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ChangeLookAndFeel.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.ui.commands;

import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.swing.Command;

public class ChangeLookAndFeel implements Command
{
	String mLookAndFeelClassname = null;

    public ChangeLookAndFeel(String lookAndFeelClassname)
    {
		mLookAndFeelClassname = lookAndFeelClassname;
    }

	public void execute()
	{
		Rife.setLookAndFeel(mLookAndFeelClassname);
	}
}



