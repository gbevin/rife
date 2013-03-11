/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JDialog.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class JDialog extends javax.swing.JDialog
{
    private static final long serialVersionUID = 3351745796334519740L;

	public JDialog(JFrame owner, String title, boolean modal)
	{
		super(owner, title, modal);
	}

	public void nonModalShow()
	{
		SwingUtilities.invokeLater(new DialogShowThread(this));
	}

	public class DialogShowThread extends Thread
	{
		JDialog mDialog = null;

		public DialogShowThread(JDialog dialog)
		{
			mDialog = dialog;
		}

		public void run()
		{
			mDialog.setVisible(true);
		}
	}
}

