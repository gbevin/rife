/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Distributed under the terms of the GNU General Public License, v2 or later
 * $Id: Blog.groovy 3951 2008-05-05 13:35:02Z gbevin $
 */
import com.uwyn.rife.cmf.MimeType;
import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.site.ConstrainedBean;
import com.uwyn.rife.site.ConstrainedProperty;
import com.uwyn.rife.site.MetaData;

class Blog extends MetaData
{
	int		id = -1;
	String	title = null;
	Date	moment = new Date();
	byte[]	image = null;
	String	body = null;
	boolean	draft = false;

	void activateMetaData()
	{
		addConstraint(new ConstrainedBean()
			.defaultOrder("moment", ConstrainedBean.DESC)
			.defaultOrder("title"));
		
		addConstraint(new ConstrainedProperty("id")
			.editable(false)
			.identifier(true));
		addConstraint(new ConstrainedProperty("moment")
			.listed(true)
			.notNull(true)
			.format(RifeConfig.Tools.getDefaultInputDateFormat()));
		addConstraint(new ConstrainedProperty("image")
			.file(true)
			.listed(true)
			.mimeType(MimeType.IMAGE_PNG)
			.contentAttribute("width", 150));
		addConstraint(new ConstrainedProperty("title")
			.notNull(true)
			.notEmpty(true)
			.maxLength(100)
			.listed(true));
		addConstraint(new ConstrainedProperty("body")
			.notNull(true)
			.notEmpty(true)
			.mimeType(MimeType.APPLICATION_XHTML)
			.autoRetrieved(true)
			.fragment(true));
		addConstraint(new ConstrainedProperty("draft")
			.notNull(true)
			.defaultValue(false));
	}
}
