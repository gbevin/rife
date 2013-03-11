/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Distributed under the terms of the GNU General Public License, v2 or later
 * $Id: HomeRegular.groovy 3951 2008-05-05 13:35:02Z gbevin $
 */
import com.uwyn.rife.config.RifeConfig
import com.uwyn.rife.engine.Element
import com.uwyn.rife.tools.Localization

class HomeRegular extends Element
{
	void processElement()
	{
		def t = getHtmlTemplate("custom.html.home_custom")
		def language = RifeConfig.Tools.getDefaultLanguage();
		t.addResourceBundle(Localization.getResourceBundle("l10n/crud/admin"))

		print(t);
	}
}
