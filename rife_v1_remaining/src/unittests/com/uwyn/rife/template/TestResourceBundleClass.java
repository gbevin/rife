package com.uwyn.rife.template;

import java.util.ListResourceBundle;

public class TestResourceBundleClass extends ListResourceBundle
{
	public Object[][] getContents()
	{
		return new Object[][] {{"THE_CLASS_KEY", "list key class"}};
	}
}
