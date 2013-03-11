/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementPropertyParameterUsed.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.swing.JDialogError;
import com.uwyn.rife.tools.Localization;

class ElementPropertyParameterUsed extends ElementPropertyParameter
{
	public ElementPropertyParameterUsed(Element element, String name)
	{
		super(element, name);
	}

	public JDialogError getUnicityErrorDialog()
	{
		return new JDialogError(Rife.getMainFrame(), "rife.dialog.usedparameterexists.title", Localization.getString("rife.dialog.usedparameterexists.message"));
	}
}
