/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: PagedNavigation.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.site;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

import com.uwyn.rife.engine.ElementSupport;
import com.uwyn.rife.template.Template;

/**
 * This class provides utility methods to generate navigation for paged lists.
 * <p>The generation of the navigation depends on a collection of block and
 * value IDs that should be defined in a template. Following is a table of all
 * the IDs and their purpose:
 * <table border="1" cellpadding="3">
 * <tr valign="top">
 * <th>ID
 * <th>Description
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:FIRSTRANGE'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used to jump to the first range. This
 * block has to contain an EXIT:QUERY value that will be replaced with the
 * actual URL that will trigger the paging behaviour.
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:FIRSTRANGE:DISABLED'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used when jumping to the first range
 * is not appropriate, for instance when the first range is already the
 * current offset.
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:PREVIOUSRANGE'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used to jump to the previous range
 * according to the current offset. This block has to contain an EXIT:QUERY
 * value that will be replaced with the actual URL that will trigger the
 * paging behaviour.
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:PREVIOUSRANGE:DISABLED'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used when jumping to the previous
 * range is not appropriate, for instance when the first range is the current
 * offset.
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:ABSOLUTERANGE'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used to jump directly to each
 * individual range. This block has to contain an EXIT:QUERY value that will
 * be replaced with the actual URL that will trigger the paging behaviour.
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:ABSOLUTERANGE:DISABLED'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used when jumping directly to a
 * specific individual range is not appropriate, for instance when that range
 * corresponds to the current offset.
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:NEXTRANGE'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used to jump to the next range
 * according to the current offset. This block has to contain an EXIT:QUERY
 * value that will be replaced with the actual URL that will trigger the
 * paging behaviour.
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:NEXTRANGE:DISABLED'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used when jumping to the next range
 * is not appropriate, for instance when the last range is the current offset.
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:LASTRANGE'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used to the last range. This block
 * has to contain an EXIT:QUERY value that will be replaced with the actual
 * URL that will trigger the paging behaviour.
 * <tr valign="top">
 * <td><code>&lt;!--B&nbsp;'NAV:LASTRANGE:DISABLED'--&gt;&lt;!--/B--&gt;</code>
 * <td>Provides the content that will be used when jumping to the last range
 * is not appropriate, for instance when the last range is already the current
 * offset.
 * <tr valign="top">
 * <td><code>&lt;!--V&nbsp;'NAV:RANGECOUNT'/--&gt;</code>
 * <td>Will contain the number of ranges that are needed to display all the
 * information that is paged. This value is optional.
 * <tr valign="top">
 * <td><code>&lt;!--V&nbsp;'NAV:FIRSTRANGE'/--&gt;</code>
 * <td>Will contain the content that allows to jump to the first range. This
 * corresponds to the beginning of the paged data.
 * <tr valign="top">
 * <td><code>&lt;!--V&nbsp;'NAV:PREVIOUSRANGE'/--&gt;</code>
 * <td>Will contain the content that allows to jump to the previous range
 * according to the current offset.
 * <tr valign="top">
 * <td><code>&lt;!--V&nbsp;'NAV:ABSOLUTERANGES'/--&gt;</code>
 * <td>Will contain the content that allows to jump directly to each
 * individual range that is available.
 * <tr valign="top">
 * <td><code>&lt;!--V&nbsp;'NAV:NEXTRANGE'/--&gt;</code>
 * <td>Will contain the content that allows to jump to the next range
 * according to the current offset.
 * <tr valign="top">
 * <td><code>&lt;!--V&nbsp;'NAV:LASTRANGE'/--&gt;</code>
 * <td>Will contain the content that allows to jump to the last range. This
 * corresponds to the end of the paged data.
 * </table>
 * <p>Besides these template conventions, you also have to provide one exit
 * and one output that will be used to create the links that will perform the
 * actual paging behaviour of the navigation. By default, the
 * <code>change_offset</code> exit and the offset <code>output</code> will be
 * used. It's up to you to create the datalink and flowlink and to correctly
 * handle the offset value when it changes.
 * <p>A very basic paged navigation could for example be defined like this:
 * <pre>&lt;!--B&nbsp;'NAV:FIRSTRANGE'--&gt;&lt;a href="[!V 'EXIT:QUERY:change_offset'/]"&gt;&amp;lt;&amp;lt;&lt;/a&gt;&lt;!--/B--&gt;
 *&lt;!--B&nbsp;'NAV:FIRSTRANGE:DISABLED'--&gt;&amp;lt;&amp;lt;&lt;!--/B--&gt;
 *&lt;!--B&nbsp;'NAV:PREVIOUSRANGE'--&gt;&lt;a href="[!V 'EXIT:QUERY:change_offset'/]"&gt;&amp;lt;&lt;/a&gt;&lt;!--/B--&gt;
 *&lt;!--B&nbsp;'NAV:PREVIOUSRANGE:DISABLED'--&gt;&amp;lt;&lt;!--/B--&gt;
 *&lt;!--B&nbsp;'NAV:ABSOLUTERANGE'--&gt;&amp;nbsp;&lt;a href="[!V 'EXIT:QUERY:change_offset'/]"&gt;&lt;!--V&nbsp;'ABSOLUTERANGE_TEXT'/--&gt;&lt;/a&gt;&amp;nbsp;&lt;!--/B--&gt;
 *&lt;!--B&nbsp;'NAV:ABSOLUTERANGE:DISABLED'--&gt;&amp;nbsp;&lt;!--V&nbsp;'ABSOLUTERANGE_TEXT'/--&gt;&amp;nbsp;&lt;!--/B--&gt;
 *&lt;!--B&nbsp;'NAV:NEXTRANGE'--&gt;&lt;a href="[!V 'EXIT:QUERY:change_offset'/]"&gt;&amp;gt;&lt;/a&gt;&lt;!--/B--&gt;
 *&lt;!--B&nbsp;'NAV:NEXTRANGE:DISABLED'--&gt;&amp;gt;&lt;!--/B--&gt;
 *&lt;!--B&nbsp;'NAV:LASTRANGE'--&gt;&lt;a href="[!V 'EXIT:QUERY:change_offset'/]"&gt;&amp;gt;&amp;gt;&lt;/a&gt;&lt;!--/B--&gt;
 *&lt;!--B&nbsp;'NAV:LASTRANGE:DISABLED'--&gt;&amp;gt;&amp;gt;&lt;!--/B--&gt;
 *
 *Pages: &lt;!--V&nbsp;'NAV:RANGECOUNT'/--&gt; ( &lt;!--V&nbsp;'NAV:FIRSTRANGE'/--&gt; &lt;!--V&nbsp;'NAV:PREVIOUSRANGE'/--&gt; &lt;!--V&nbsp;'NAV:NEXTRANGE'/--&gt; &lt;!--V&nbsp;'NAV:LASTRANGE'/--&gt; | &lt;!--V&nbsp;'NAV:ABSOLUTERANGES'/--&gt; )</pre>
 * <p>Which could result in the following output where all the underlined
 * parts are clickable and will trigger the <code>change_offset</code> exit
 * and provide a new corresponding value for the offset <code>output</code>:
 * <p><code>Pages: 9 ( &lt;&lt; &lt; <u>&gt;</u> <u>&gt;&gt;</u> | 1 <u>2</u>
 * <u>3</u> <u>4</u> <u>5</u> <u>6</u> <u>7</u> <u>8</u> <u>9</u> )</code>
 * <p>The element that displays the list and calls the navigation generation
 * method could for example be like this:
 * <pre>public class List extends Element
 *{
 *    public final static int LIMIT = 10;
 *    public final static int SPAN = 5;
 *
 *    public void processElement()
 *    {
 *        Template t = getHtmlTemplate("article.list");
 *        DatabaseArticles manager = DatabaseArticlesFactory.getInstance();
 *
 *        int count = manager.countArticles();
 *        if (0 == count) t.setBlock("content", "noarticles");
 *        else
 *        {
 *            int offset = getInputInt("offset", 0);
 *
 *            PagedNavigation.generateNavigation(this, t, count, LIMIT, offset, SPAN);
 *
 *            Collection&lt;Article&gt; articles = manager.listArticles(LIMIT, offset);
 *            for (Article article : articles)
 *            {
 *                t.setBean(article);
 *                t.appendBlock("articles", "article");
 *            }
 *        }
 *
 *        print(t);
 *    }
 *}</pre>
 */
public class PagedNavigation
{
	public static String    PREFIX_NAV = "NAV:";
	
	public static String    SUFFIX_DISABLED = ":DISABLED";
	
	public static String    ID_RANGECOUNT = PREFIX_NAV+"RANGECOUNT";
	
	public static String    ID_ABSOLUTERANGE_TEXT = "ABSOLUTERANGE_TEXT";

	public static String    ID_FIRSTRANGE = PREFIX_NAV+"FIRSTRANGE";
	public static String    ID_PREVIOUSRANGE = PREFIX_NAV+"PREVIOUSRANGE";
	public static String    ID_ABSOLUTERANGES = PREFIX_NAV+"ABSOLUTERANGES";
	public static String    ID_ABSOLUTERANGE = PREFIX_NAV+"ABSOLUTERANGE";
	public static String    ID_NEXTRANGE = PREFIX_NAV+"NEXTRANGE";
	public static String    ID_LASTRANGE = PREFIX_NAV+"LASTRANGE";
	
	public static String    ID_FIRSTRANGE_DISABLED = PREFIX_NAV+"FIRSTRANGE"+SUFFIX_DISABLED;
	public static String    ID_PREVIOUSRANGE_DISABLED = PREFIX_NAV+"PREVIOUSRANGE"+SUFFIX_DISABLED;
	public static String    ID_ABSOLUTERANGE_DISABLED = PREFIX_NAV+"ABSOLUTERANGE"+SUFFIX_DISABLED;
	public static String    ID_NEXTRANGE_DISABLED = PREFIX_NAV+"NEXTRANGE"+SUFFIX_DISABLED;
	public static String    ID_LASTRANGE_DISABLED = PREFIX_NAV+"LASTRANGE"+SUFFIX_DISABLED;
	
	public static String    DEFAULT_EXIT = "change_offset";
	public static String    DEFAULT_OUTPUT = "offset";

	/**
	 * Generates the paged navigation for the given element, template and
	 * range configuration. The default exit <code>change_offset</code> and
	 * the default output <code>offset</code> will be used when generating the
	 * links.
	 *
	 * @param element The element that is populating the template. Its exit
	 * will be triggered and its output will be set.
	 * @param template The template that will be used for the generation of
	 * the navigation.
	 * @param count The total number of items that are being paged.
	 * @param limit The maximum of items that will be shown in a range on a
	 * page.
	 * @param offset The starting offset of the range that is currently
	 * visible.
	 * @param span The maximum number of ranges that will be shown as
	 * immediately accesible absolute ranges.
	 */
	public static void generateNavigation(ElementSupport element, Template template, long count, int limit, long offset, int span)
	{
		generateNavigation(element, template, count, limit, offset, span, DEFAULT_EXIT, DEFAULT_OUTPUT);
	}
	
	/**
	 * Generates the paged navigation for the given element, template and
	 * range configuration. This version allows you to provide your own names
	 * for the exit and the output that will be used when generating the
	 * links.
	 *
	 * @param element The element that is populating the template, whose exit
	 * will be triggered and whose output will be set.
	 * @param template The template that will be used for the generation of
	 * the navigation.
	 * @param count The total number of items that are being paged.
	 * @param limit The maximum of items that will be shown in a range on a
	 * page.
	 * @param offset The starting offset of the range that is currently
	 * visible.
	 * @param span The maximum number of ranges that will be shown as
	 * immediately accesible absolute ranges.
	 * @param exit The name of the exit that has to be used to trigger an
	 * offset change.
	 * @param output The name of the output that will contain the value of the
	 * new range offset when the exit is triggered.
	 */
	public static void generateNavigation(ElementSupport element, Template template, long count, int limit, long offset, int span, String exit, String output)
	{
		generateNavigation(element, template, count, limit, offset, span, exit, output, null);
	}

	/**
	 * Generates the paged navigation for the given element, template and
	 * range configuration. This version allows you to provide your own names
	 * for the exit and the output that will be used when generating the
	 * links.
	 *
	 * @param element The element that is populating the template, whose exit
	 * will be triggered and whose output will be set.
	 * @param template The template that will be used for the generation of
	 * the navigation.
	 * @param count The total number of items that are being paged.
	 * @param limit The maximum of items that will be shown in a range on a
	 * page.
	 * @param offset The starting offset of the range that is currently
	 * visible.
	 * @param span The maximum number of ranges that will be shown as
	 * immediately accesible absolute ranges.
	 * @param exit The name of the exit that has to be used to trigger an
	 * offset change.
	 * @param output The name of the output that will contain the value of the
	 * new range offset when the exit is triggered.
	 * @param pathInfo The pathinfo to be applied to the exit used to trigger an offset change
	 */
	public static void generateNavigation(ElementSupport element, Template template, long count, int limit, long offset, int span, String exit, String output, String pathInfo)
	{
		long range_count = (long)Math.ceil(((double)count)/limit);
		if (range_count < 0)
		{
			range_count = 0;
		}
		long max_offset = (range_count-1)*limit;
		if (max_offset < 0)
		{
			max_offset = 0;
		}
		if (template.hasValueId(ID_RANGECOUNT))
		{
			template.setValue(ID_RANGECOUNT, range_count);
		}
		
		if (offset < 0)
		{
			offset = 0;
		}
		else if (offset > max_offset)
		{
			offset = max_offset;
		}
		else
		{
			offset = (long)(floor(offset/limit)*limit);
		}

		String first_offset = "0";
		String previous_offset = String.valueOf(offset - limit);
		String next_offset = String.valueOf(offset + limit);
		String last_offset = String.valueOf((long)floor((count-1)/limit)*limit);
		
		if (offset <= 0)
		{
			// turn first and prev off
			template.setBlock(ID_FIRSTRANGE, ID_FIRSTRANGE_DISABLED);
			template.setBlock(ID_PREVIOUSRANGE, ID_PREVIOUSRANGE_DISABLED);
		}
		else
		{
			element.setExitQuery(template, exit, pathInfo, new String[] { output, first_offset });
			template.setBlock(ID_FIRSTRANGE, ID_FIRSTRANGE);
			
			element.setExitQuery(template, exit, pathInfo, new String[] { output, previous_offset });
			template.setBlock(ID_PREVIOUSRANGE, ID_PREVIOUSRANGE);
		}
		
		if (offset + limit >= count)
		{
			// turn next and last off
			template.setBlock(ID_NEXTRANGE, ID_NEXTRANGE_DISABLED);
			template.setBlock(ID_LASTRANGE, ID_LASTRANGE_DISABLED);
		}
		else
		{
			element.setExitQuery(template, exit, pathInfo, new String[] { output, next_offset });
			template.setBlock(ID_NEXTRANGE, ID_NEXTRANGE);
			
			element.setExitQuery(template, exit, pathInfo, new String[] { output, last_offset });
			template.setBlock(ID_LASTRANGE, ID_LASTRANGE);
		}
		
		long absolute_range_end = (long)(floor(offset/limit)+span+1);
		long absolute_range_page = (long)((floor(offset/limit)+1)-span);
		if (absolute_range_page < 1) { absolute_range_page = 1; }
		long absolute_range_offset = (absolute_range_page-1)*limit;
		
		template.setValue(ID_ABSOLUTERANGES, "");
		
		if (absolute_range_page > 1)
		{
			template.setValue(ID_ABSOLUTERANGE_TEXT, "...");
			template.setBlock(ID_ABSOLUTERANGES, ID_ABSOLUTERANGE_DISABLED);
		}
		
		while (absolute_range_offset < count &&
			   absolute_range_page <= absolute_range_end)
		{
			template.setValue(ID_ABSOLUTERANGE_TEXT, absolute_range_page);
			if (offset >= absolute_range_offset &&
				offset < absolute_range_offset + limit)
			{
				template.appendBlock(ID_ABSOLUTERANGES, ID_ABSOLUTERANGE_DISABLED);
			}
			else
			{
				String[] outputs = new String[] { output, String.valueOf((int)absolute_range_offset) };
				
				element.setExitQuery(template, exit, pathInfo, outputs);
				
				template.appendBlock(ID_ABSOLUTERANGES, ID_ABSOLUTERANGE);
			}
			absolute_range_offset += limit;
			absolute_range_page++;
		}
		
		if (absolute_range_end < ceil((double)count/limit))
		{
			template.setValue(ID_ABSOLUTERANGE_TEXT, "...");
			template.appendBlock(ID_ABSOLUTERANGES, ID_ABSOLUTERANGE_DISABLED);
		}
		
		template.removeValue(ID_ABSOLUTERANGE_TEXT);
	}
}

