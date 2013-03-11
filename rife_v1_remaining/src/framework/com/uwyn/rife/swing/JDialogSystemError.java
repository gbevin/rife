/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: JDialogSystemError.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.swing;

import com.uwyn.rife.tools.Localization;
import javax.swing.JFrame;

public class JDialogSystemError extends JDialogError
{
	private static final long serialVersionUID = 6710056639278804474L;

	public JDialogSystemError(JFrame frame, String errorString)
	{
		super(frame, Localization.getString("rife.dialog.systemerror.title"), new JMultiLabel(errorString, null == frame ? 0 : (frame.getSize().width/3)*2));
	}
}

