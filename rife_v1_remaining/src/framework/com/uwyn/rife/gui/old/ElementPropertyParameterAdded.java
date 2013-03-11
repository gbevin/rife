/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: ElementPropertyParameterAdded.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.gui.old;

import com.uwyn.rife.gui.Rife;
import com.uwyn.rife.swing.JDialogError;
import com.uwyn.rife.tools.Localization;

class ElementPropertyParameterAdded extends ElementPropertyParameter
{
	public ElementPropertyParameterAdded(Element element, String name)
	{
		super(element, name);
	}

	public JDialogError getUnicityErrorDialog()
	{
		return new JDialogError(Rife.getMainFrame(), "rife.dialog.addedparameterexists.title", Localization.getString("rife.dialog.addedparameterexists.message"));
	}
}
