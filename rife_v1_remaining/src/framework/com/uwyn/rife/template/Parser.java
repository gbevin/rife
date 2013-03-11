/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Parser.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.template;

import com.uwyn.rife.template.exceptions.*;
import java.util.*;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.datastructures.DocumentPosition;
import com.uwyn.rife.datastructures.collections.primitives.ArrayIntList;
import com.uwyn.rife.resources.ResourceFinder;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.tools.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser implements Cloneable
{
	public enum PartType {CONTENT, TAG_START, TAG_END, TAG_TERM, TAG_SHORT_TERM, STRING_DELIMITER_BEGIN, STRING_DELIMITER_END, VALUE, BLOCK, BLOCKVALUE, BLOCKAPPEND, COMMENT, INCLUDE, UNESCAPE_START}
	
	public final static String	DEFAULT_TEMPLATES_PATH = "templates/";
	
	public final static String	TEMPLATE_PACKAGE = "com.uwyn.rife.template.";
	
	private TemplateFactory	mTemplateFactory = null;
	
	private String			mIdentifier = null;
	private String			mPackageName = null;
	
	private Config[]		mConfigs = null;
	
	private String			mAmbiguousName = null;
	private String			mExtension = null;
	private int				mExtensionLength = -1;
	
	private Pattern[]		mBlockFilters = null;
	private Pattern[]		mValueFilters = null;
	
	Parser(TemplateFactory templateFactory, String identifier, Config[] configs, String extension, Pattern[] blockFilters, Pattern[] valueFilters)
	{
		init(templateFactory, identifier, configs, extension, blockFilters, valueFilters);
	}
	
	Config[] getConfigs()
	{
		return mConfigs;
	}
	
	String getExtension()
	{
		return mExtension;
	}
	
	Pattern[] getBlockFilters()
	{
		return mBlockFilters;
	}
	
	Pattern[] getValueFilters()
	{
		return mValueFilters;
	}
	
	TemplateFactory getTemplateFactory()
	{
		return mTemplateFactory;
	}
	
	private void init(TemplateFactory templateFactory, String identifier, Config[] configs, String extension, Pattern[] blockFilters, Pattern[] valueFilters)
	{
		assert templateFactory != null;
		assert identifier != null;
		assert configs != null;
		assert configs.length > 0;
		assert extension != null;
		
		mTemplateFactory = templateFactory;
		
		mIdentifier = identifier;
		mPackageName = TEMPLATE_PACKAGE + mIdentifier + ".";
		
		mConfigs = configs;
		
		mExtension = extension;
		mExtensionLength = mExtension.length();
		mAmbiguousName = (mExtension + mExtension).substring(1);
		
		mBlockFilters = blockFilters;
		mValueFilters = valueFilters;
		
		assert mExtensionLength > 0;
	}
	
	public Parser clone()
	{
		Parser new_parser = null;
		try
		{
			new_parser = (Parser)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			new_parser = null;
		}
		
		Pattern[] new_blockfilters = null;
		if (mBlockFilters != null)
		{
			new_blockfilters = mBlockFilters.clone();
		}
		Pattern[] new_valuefilters = null;
		if (mValueFilters != null)
		{
			new_valuefilters = mValueFilters.clone();
		}
		new_parser.init(mTemplateFactory, mIdentifier, mConfigs, mExtension, new_blockfilters, new_valuefilters);
		
		return new_parser;
	}
	
	public boolean equals(Object object)
	{
		if (object == this)
		{
			return true;
		}
		
		if (null == object)
		{
			return false;
		}
		
		if (!(object instanceof Parser))
		{
			return false;
		}
		
		Parser other_parser = (Parser)object;
		if (!Arrays.equals(other_parser.mConfigs, this.mConfigs))
		{
			return false;
		}
		if (!other_parser.mIdentifier.equals(this.mIdentifier))
		{
			return false;
		}
		if (!other_parser.mExtension.equals(this.mExtension))
		{
			return false;
		}
		
		if (null == other_parser.mBlockFilters && this.mBlockFilters != null ||
			other_parser.mBlockFilters != null && null == this.mBlockFilters)
		{
			return false;
		}
		if (other_parser.mBlockFilters != null && this.mBlockFilters != null)
		{
			if (other_parser.mBlockFilters.length != this.mBlockFilters.length)
			{
				return false;
			}
			
			for (int i = 0; i < other_parser.mBlockFilters.length; i++)
			{
				if (!other_parser.mBlockFilters[i].pattern().equals(this.mBlockFilters[i].pattern()))
				{
					return false;
				}
			}
		}
		
		if (null == other_parser.mValueFilters && this.mValueFilters != null ||
			other_parser.mValueFilters != null && null == this.mValueFilters)
		{
			return false;
		}
		if (other_parser.mValueFilters != null && this.mValueFilters != null)
		{
			if (other_parser.mValueFilters.length != this.mValueFilters.length)
			{
				return false;
			}
			
			for (int i = 0; i < other_parser.mValueFilters.length; i++)
			{
				if (!other_parser.mValueFilters[i].pattern().equals(this.mValueFilters[i].pattern()))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public Parsed parse(String name, String encoding, TemplateTransformer transformer)
	throws TemplateException
	{
		if (null == name)	throw new IllegalArgumentException("name can't be null.");
		
		URL resource = resolve(name);
		if (null == resource)
		{
			throw new TemplateNotFoundException(name, null);
		}
		
		return parse(prepare(name, resource), encoding, transformer);
	}
	
	public URL resolve(String name)
	{
		if (null == name)	throw new IllegalArgumentException("name can't be null.");
		
		if (0 == name.indexOf(getPackage()))
		{
			name = name.substring(getPackage().length());
		}
		name = name.replace('.', '/') + mExtension;
		
		URL resource = mTemplateFactory.getResourceFinder().getResource(name);
		if (null == resource)
		{
			resource = mTemplateFactory.getResourceFinder().getResource(DEFAULT_TEMPLATES_PATH + name);
		}
		
		return resource;
	}
	
	public String getPackage()
	{
		return mPackageName;
	}
	
	String escapeClassname(String name)
	{
		assert name != null;
		
		if (name.equals(mAmbiguousName))
		{
			throw new AmbiguousTemplateNameException(name);
		}
		
		if (name.endsWith(mExtension))
		{
			name = name.substring(0, name.length() - mExtensionLength);
		}
		
		char[]	name_chars = name.toCharArray();
		int		char_code;
		for (int i = 0; i < name_chars.length; i++)
		{
			char_code = name_chars[i];
			if ((char_code >= 48 && char_code <= 57) ||
				(char_code >= 65 && char_code <= 90) ||
				(char_code >= 97 && char_code <= 122) ||
				char_code == 46)
			{
				continue;
			}
			
			if (char_code == '/' || char_code == '\\')
			{
				name_chars[i] = '.';
			}
			else
			{
				name_chars[i] = '_';
			}
		}
		
		return new String(name_chars);
	}
	
	public Parsed prepare(String name, URL resource)
	{
		if (null == name)		throw new IllegalArgumentException("name can't be null.");
		if (null == resource)	throw new IllegalArgumentException("resource can't be null.");
		
		String template_name = name;
		Parsed template_parsed = new Parsed(this);
		if (0 == template_name.indexOf(getPackage()))
		{
			template_name = name.substring(getPackage().length());
		}
		
		String class_name = template_name;
		String subpackage = "";
		int package_seperator = template_name.lastIndexOf(".");
		if (package_seperator != -1)
		{
			subpackage = "." + template_name.substring(0, package_seperator);
			class_name = template_name.substring(package_seperator + 1);
		}
		template_parsed.setTemplateName(template_name);
		template_parsed.setPackage(getPackage().substring(0, getPackage().length() - 1) + subpackage);
		template_parsed.setClassName(escapeClassname(class_name));
		template_parsed.setResource(resource);
		
		return template_parsed;
	}
	
	Parsed parse(Parsed parsed, String encoding, TemplateTransformer transformer)
	throws TemplateException
	{
		assert parsed != null;
		
		if (null == encoding)
		{
			encoding = RifeConfig.Template.getDefaultEncoding();
		}
		
		// get the resource of the template file
		URL	resource = parsed.getResource();
		
		// obtain the content of the template file
		StringBuilder content_buffer = getContent(parsed.getTemplateName(), parsed, resource, encoding, transformer);
		
		// replace the included templates
		Stack<String> previous_includes = new Stack<String>();
		previous_includes.push(parsed.getFullClassName());
		replaceIncludeTags(parsed, content_buffer, previous_includes, encoding, transformer);
		previous_includes.pop();
		
		// process the blocks and values
		String content = content_buffer.toString();
		parseBlocks(parsed, content);
		parsed.setFilteredBlocks(filterTags(mBlockFilters, parsed.getBlockIds()));
		parsed.setFilteredValues(filterTags(mValueFilters, parsed.getValueIds()));
		
		assert parsed.getBlocks().size() >= 1;
		
		return parsed;
	}
	
	private StringBuilder getContent(String templateName, Parsed parsed, URL resource, String encoding, TemplateTransformer transformer)
	throws TemplateException
	{
		if (null == transformer)
		{
			return getFileContent(resource, encoding);
		}
		else
		{
			return getTransformedContent(templateName, parsed, resource, encoding, transformer);
		}
	}
	
	private StringBuilder getFileContent(URL resource, String encoding)
	throws TemplateException
	{
		assert resource != null;
		
		String	content = null;
		
		try
		{
			content = mTemplateFactory.getResourceFinder().getContent(resource, encoding);
		}
		catch (ResourceFinderErrorException e)
		{
			throw new GetContentErrorException(resource.toExternalForm(), e);
		}
		
		return new StringBuilder(content);
	}
	
	private StringBuilder getTransformedContent(String templateName, Parsed parsed, URL resource, String encoding, TemplateTransformer transformer)
	throws TemplateException
	{
		assert resource != null;
		
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		
		// transform the content
		transformer.setResourceFinder(mTemplateFactory.getResourceFinder());
		Collection<URL> dependencies = transformer.transform(templateName, resource, result, encoding);
		// get the dependencies and their modification times
		if (dependencies != null &&
			dependencies.size() > 0)
		{
			long	modification_time = 0;
			for (URL dependency_resource : dependencies)
			{
				try
				{
					modification_time = transformer.getResourceFinder().getModificationTime(dependency_resource);
				}
				catch (ResourceFinderErrorException e)
				{
					// if there was trouble in obtaining the modification time, just set
					// it to 0 so that the dependency will always be outdated
					modification_time = 0;
				}
				
				if (modification_time > 0)
				{
					parsed.addDependency(dependency_resource, new Long(modification_time));
				}
			}
		}
		// set the modification state so that different filter configurations
		// will reload the template, not only modifications to the dependencies
		parsed.setModificationState(transformer.getState());
		
		// convert the result to a StringBuilder
		try
		{
			if (null == encoding)
			{
				if (null == transformer.getEncoding())
				{
					return new StringBuilder(result.toString());
				}
				else
				{
					return new StringBuilder(result.toString(transformer.getEncoding()));
				}
			}
			else
			{
				return new StringBuilder(result.toString(encoding));
			}
		}
		catch (UnsupportedEncodingException e)
		{
			throw new TransformedResultConversionException(resource.toExternalForm(), e);
		}
	}
	
	public static long getModificationTime(ResourceFinder resourceFinder, URL resource)
	throws TemplateException
	{
		if (null == resource)	throw new IllegalArgumentException("resource can't be null.");
		
		long modification_time = -1;
		
		try
		{
			modification_time = resourceFinder.getModificationTime(resource);
		}
		catch (ResourceFinderErrorException e)
		{
			throw new ModificationTimeErrorException(resource.toExternalForm(), e);
		}
		
		return modification_time;
	}
	
	private int forwardToNextWhitespace(int start, String content)
	{
		// forward until the first non whitespace character
		int i = start;
		for (; i < content.length(); i++)
		{
			if (!Character.isWhitespace(content.charAt(i)))
			{
				break;
			}
		}
		
		return i;
	}
	
	/**
	 * Get the next indices of a search string in the content by taking
	 * backslash escaping into account and skipping over those search
	 * strings that are escaped by it. If fixed parts are provided it will also
	 * ensure that the search string is followed by them and the text in between
	 * is only whitespace.
	 */
	private TagMatch getEscapedIndex(String content, String search, int start, ConfigPart... fixedParts)
	{
		TagMatch match = new TagMatch();
		
		int begin_index = content.indexOf(search, start);
		
		if (begin_index != -1)
		{
			int ending_index = -1;
			int last_match = begin_index;
			int last_ending_index = -1;
			while (begin_index != -1)
			{
				match.clear();
				
				match.addMatch(begin_index);
				
				if (begin_index > 0 &&
					'\\' == content.charAt(begin_index - 1) &&						// check for a first escaping backslash
					(begin_index < 2 || content.charAt(begin_index - 2) != '\\'))		// check if that one hasn't been escaped itself
				{
					begin_index = -1;
				}
				// if fixed parts have been provided, ensure that they are available
				// after the last match and that the text between them is only
				// whitespace
				else if (fixedParts != null && fixedParts.length > 0)
				{
					last_ending_index = begin_index + search.length();
					for (ConfigPart fixed_part : fixedParts)
					{
						if (0 == fixed_part.length())
						{
							match.addMatch(forwardToNextWhitespace(last_ending_index, content));
							match.addFixedPartMatched(true);
							continue;
						}
						
						ending_index = content.indexOf(fixed_part.getText(), last_ending_index);
						String seperating_text = null;
						String seperating_text_trim = null;
						if (ending_index != -1)
						{
							seperating_text = content.substring(last_ending_index, ending_index);
							seperating_text_trim = seperating_text.trim();
						}
						if (-1 == ending_index ||
							seperating_text_trim.length() != 0)
						{
							if (fixed_part.isOptional() &&
								(null == seperating_text || seperating_text.indexOf(seperating_text_trim) > 0))
							{
								match.addMatch(forwardToNextWhitespace(last_ending_index, content));
								match.addFixedPartMatched(false);
								continue;
							}
							else
							{
								begin_index = -1;
								break;
							}
						}
						
						last_ending_index = ending_index + fixed_part.length();
						match.addMatch(last_ending_index);
						match.addFixedPartMatched(true);
					}
				}
				
				// continue searching if the match wasn't successful
				if (-1 == begin_index)
				{
					begin_index = content.indexOf(search, last_match + 1);
					last_match = begin_index;
					continue;
				}
				
				return match;
			}
		}
		
		// return negative result
		return null;
	}
	
	/**
	 * Construct an array with only the configurations that need to be unescaped
	 */
	private Parser.Config[] getUnescapeConfigs(String content)
	{
		// iterate over the supported parser configurations to find the ones
		// that are unused
		List<Parser.Config> configs_list = new ArrayList<Parser.Config>();
		for (Parser.Config config : mConfigs)
		{
			if (content.indexOf(config.mUnescapeStart.getText()) != -1)
			{
				configs_list.add(config);
			}
		}
		
		Parser.Config[]	configs = null;
		if (configs_list.size() > 0)
		{
			// make a config array, containing only the used configs
			configs = new Parser.Config[configs_list.size()];
			configs_list.toArray(configs);
		}
		
		return configs;
	}
	
	/**
	 * Removes the single escape backslash character from content
	 */
	private String unescapePart(Parser.Config[] configs, String part)
	{
		if (configs != null)
		{
			for (Parser.Config config : configs)
			{
				// minor optimization to only do regexp unescape matching when a
				// matching possibility is present through a quick lookup
				if (part.indexOf(config.mUnescapeStart.getText()) != -1)
				{
					part = config.mUnescapePattern.matcher(part).replaceAll("$1");
				}
			}
		}
		
		part = removeTrailingDoubleEscape(part, 0, part.length());
		
		return part;
	}
	
	/**
	 * Replaces the double escaping backslashes from the end of block content
	 * by a single backslash
	 */
	private String removeTrailingDoubleEscape(String content, int begin, int end)
	{
		if (end >= 2 &&
			'\\' == content.charAt(end - 1) &&
			'\\' == content.charAt(end - 2))
		{
			return content.substring(begin, end - 1);
		}
		else
		{
			return content.substring(begin, end);
		}
	}
	
	/**
	 * Looks for the first match of a config part text in the text content. If
	 * a part doesn't match and is optional, the next one will be tried. If the
	 * part is empty, the next one will always be tried immediately.
	 **/
	private ConfigPartMatch getFirstFoundPartIndex(String content, int startIndex, ConfigPart... parts)
	{
		for (ConfigPart part : parts)
		{
			if (part.length() != 0)
			{
				int result = content.indexOf(part.getText(), startIndex);
				if (result != -1)
				{
					return new ConfigPartMatch(result, part);
				}
				else if (!part.isOptional())
				{
					return ConfigPartMatch.NO_MATCH;
				}
			}
		}
		
		return ConfigPartMatch.NO_MATCH;
	}
	
	private void replaceIncludeTags(Parsed parsed, StringBuilder content, Stack<String> previousIncludes, String encoding, TemplateTransformer transformer)
	throws TemplateException
	{
		assert parsed != null;
		assert content != null;
		assert previousIncludes != null;
		
		TagMatch tag_match = null;
		ConfigPartMatch part_match = null;
		
		int previous_index = 0;
		int begin_start_index = 0;
		int includename_end_index = 0;
		int term_start_index = 0;
		int tag_end_index = 0;
		int includename_begin_index = 0;
		
		boolean begin_isquoted = false;
		
		String			included_template_name = null;
		URL				included_template_resource = null;
		Parsed			included_template_parsed = null;
		StringBuilder	included_template_content = null;
		
		// process the included files
		// and iterate over the support parser configurations
		for (Parser.Config config : mConfigs)
		{
			do
			{
				// find the begin position of the include tag
				tag_match = getEscapedIndex(content.toString(), config.mTagStart.getText(), previous_index, config.mIncludeTag, config.mStringDelimiterBegin);
				
				if (tag_match != null)
				{
					begin_start_index = tag_match.getMatch(0);
					
					// find the begin of the filename
					includename_begin_index = tag_match.getMatch(2);
					begin_isquoted = tag_match.didFixedPartMatch(1);
					part_match = null;
					includename_end_index = -1;
					
					// find the string delimiter
					// get the string delimiter that marks the end of the value id
					int includename_end_offset = 0;
					if (begin_isquoted)
					{
						part_match = getFirstFoundPartIndex(content.toString(), includename_begin_index, config.mStringDelimiterEnd);
					}
					
					if (part_match != null &&
						part_match.mPart != null)
					{
						includename_end_index = part_match.mIndex;
						includename_end_offset = part_match.mPart.length();
					}
					// ensure that a name that started out quoted is already terminated with a delimiter
					else if (begin_isquoted && null == part_match.mPart)
					{
						throw new AttributeNotEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content.toString(), includename_begin_index), config.mIncludeTag.getText(), "name");
					}
					else
					{
						int short_index = content.indexOf(config.mTagShortTerm.getText(), includename_begin_index);
						includename_end_offset = 0;
						includename_end_index = backtrackTillFirstNonWhitespace(content.toString(), short_index);
					}
					
					// check if the include name was ended
					if (-1 == includename_end_index) break;
					
					// obtain the filename
					included_template_name = content.substring(includename_begin_index, includename_end_index);
					
					// ensure that an end delimiter corresponds to a start delimiter
					if (!begin_isquoted &&
						included_template_name.endsWith(config.mStringDelimiterEnd.getText()))
					{
						throw new AttributeWronglyEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content.toString(), includename_end_index + includename_end_offset - config.mStringDelimiterEnd.length()), config.mIncludeTag.getText(), "name");
					}
					
					// get the first tag ending
					term_start_index = content.indexOf(config.mTagShortTerm.getText(), includename_end_index + includename_end_offset);
					
					// ensure that the tag was propertly ended
					if (-1 == term_start_index) break;
					
					// check that there's only whitespace between the delimiter and the start of the termination tag
					if (content.substring(includename_end_index + includename_end_offset, term_start_index).trim().length() > 0)
					{
						throw new TagBadlyTerminatedException(parsed.getClassName(), StringUtils.getDocumentPosition(content.toString(), term_start_index), config.mIncludeTag.getText(), included_template_name);
					}
					
					// calculate the index of the end of the complete tag
					tag_end_index = term_start_index+config.mTagShortTermLength;
					
					// obtain the parser that will be used to get the included content
					Parser include_parser = this;
					// check if the included template references another template type
					int doublecolon_index = included_template_name.indexOf(':');
					if (doublecolon_index != -1)
					{
						String template_type = included_template_name.substring(0, doublecolon_index);
						if (!template_type.equals(mTemplateFactory.toString()))
						{
							TemplateFactory factory = TemplateFactory.getFactory(template_type);
							include_parser = factory.getParser();
							included_template_name = included_template_name.substring(doublecolon_index + 1);
						}
					}
					
					included_template_resource = include_parser.resolve(included_template_name);
					if (null == included_template_resource)
					{
						throw new IncludeNotFoundException(parsed.getClassName(), StringUtils.getDocumentPosition(content.toString(), begin_start_index), included_template_name);
					}
					included_template_parsed = include_parser.prepare(included_template_name, included_template_resource);
					
					// check for circular references
					if (previousIncludes.contains(included_template_parsed.getFullClassName()))
					{
						throw new CircularIncludesException(parsed.getClassName(), StringUtils.getDocumentPosition(content.toString(), begin_start_index), included_template_name, previousIncludes);
					}
					
					// parse the included template's include tags too
					included_template_content = include_parser.getContent(included_template_name, parsed, included_template_parsed.getResource(), encoding, transformer);
					previousIncludes.push(included_template_parsed.getFullClassName());
					replaceIncludeTags(included_template_parsed, included_template_content, previousIncludes, encoding, transformer);
					previousIncludes.pop();
					
					// replace the tag with the included file's content
					// if there's a double escaping backslash before the tag, it
					// will also be unescaped
					if (begin_start_index >= 2 &&
						'\\' == content.charAt(begin_start_index - 1) &&
						'\\' == content.charAt(begin_start_index - 2))
					{
						content.replace(begin_start_index - 1, tag_end_index, included_template_content.toString());
					}
					else
					{
						content.replace(begin_start_index, tag_end_index, included_template_content.toString());
					}
					
					// retain the link to this include file for optional later modification time checking
					parsed.addDependency(included_template_parsed);
					
					// add the dependencies of the included template too
					Map<URL, Long>	included_dependencies = included_template_parsed.getDependencies();
					for (Map.Entry<URL, Long> included_dependency : included_dependencies.entrySet())
					{
						parsed.addDependency(included_dependency.getKey(), included_dependency.getValue());
					}
					
					// continue the search after this tag
					previous_index = begin_start_index;
				}
			}
			while (tag_match != null);
		}
	}
	
	private TagMatch getFirstMatch(TagMatch... matches)
	{
		if (null == matches || 0 == matches.length)
		{
			return null;
		}
		
		TagMatch first_match = null;
		for (TagMatch match : matches)
		{
			if (match != null)
			{
				int candidate = match.getMatch(0);
				if (candidate != -1)
				{
					if (null == first_match)
					{
						first_match = match;
					}
					else
					{
						if (candidate <= first_match.getMatch(0))
						{
							first_match = match;
						}
					}
				}
			}
		}
		
		return first_match;
	}
	
	private int getFirstMatch(int... candidates)
	{
		if (null == candidates || 0 == candidates.length)
		{
			return -1;
		}
		
		int first_candidate = -1;
		for (int candidate : candidates)
		{
			if (candidate != -1)
			{
				if (-1 == first_candidate)
				{
					first_candidate = candidate;
				}
				else
				{
					if (candidate <= first_candidate)
					{
						first_candidate = candidate;
					}
				}
			}
		}
		
		return first_candidate;
	}
	
	private int backtrackTillFirstNonWhitespace(String content, int index)
	{
		if (-1 == index)
		{
			return -1;
		}
		else
		{
			do
			{
				index--;
				
				if (!Character.isWhitespace(content.charAt(index)))
				{
					break;
				}
			}
			while (index >= 0);
			
			return index + 1;
		}
	}
	
	private void parseBlocks(Parsed parsed, String content)
	throws TemplateException
	{
		assert parsed != null;
		assert content != null;
		
		LinkedHashMap<String, StringBuilder> blocks = new LinkedHashMap<String, StringBuilder>();
		blocks.put("", new StringBuilder(""));
		
		// iterate over the supported parser configurations to find the ones
		// that are unused
		List<Parser.Config> block_configs_list = new ArrayList<Parser.Config>();
		for (Parser.Config config : mConfigs)
		{
			if (getEscapedIndex(content, config.mTagStart.getText(), 0, config.mBlockTag, config.mStringDelimiterBegin) != null ||
				getEscapedIndex(content, config.mTagStart.getText(), 0, config.mBlockvalueTag, config.mStringDelimiterBegin) != null ||
				getEscapedIndex(content, config.mTagStart.getText(), 0, config.mBlockappendTag, config.mStringDelimiterBegin) != null ||
				getEscapedIndex(content, config.mTagTerm.getText(), 0, config.mBlockTag, config.mTagEnd) != null ||
				getEscapedIndex(content, config.mTagTerm.getText(), 0, config.mBlockvalueTag, config.mTagEnd) != null ||
				getEscapedIndex(content, config.mTagTerm.getText(), 0, config.mBlockappendTag, config.mTagEnd) != null ||
				getEscapedIndex(content, config.mTagStart.getText(), 0, config.mCommentTag, config.mTagEnd) != null ||
				getEscapedIndex(content, config.mTagTerm.getText(), 0, config.mCommentTag, config.mTagEnd) != null)
			{
				block_configs_list.add(config);
			}
		}
		
		Parser.Config[]	configs = null;
		int				previous_index = 0;
		if (block_configs_list.size() > 0)
		{
			// make a config array, containing only the used configs
			configs = new Parser.Config[block_configs_list.size()];
			block_configs_list.toArray(configs);
			
			// setup the parser variables
			Stack<String> block_ids = new Stack<String>();
			Stack<ConfigPart> block_types = new Stack<ConfigPart>();
			Stack<Parser.Config> block_configs = new Stack<Parser.Config>();
			
			// create the root block which is the anonymous main content
			block_ids.push("");
			block_types.push(new ConfigPart(PartType.CONTENT, "CONTENT", false));
			block_configs.push(null);
			
			TagMatch match1 = null;
			TagMatch match2 = null;
			TagMatch match3 = null;
			TagMatch match4 = null;
			TagMatch first_match = null;
			
			int	begin_start_index = 0;
			int	delimiter_end_index = 0;
			int begin_end_index = 0;
			int term_start_index = 0;
			int	term_end_index = 0;
			int blockid_start_index = 0;
			int blockid_end_index = 0;
			
			int[]		begin_start_indices = new int[configs.length];
			int[]		delimiter_end_indices = new int[configs.length];
			boolean[]	begin_isblockvalue_flags = new boolean[configs.length];
			boolean[]	begin_isblockappend_flags = new boolean[configs.length];
			boolean[]	begin_iscomment_flags = new boolean[configs.length];
			boolean[]	begin_isquoted_flags = new boolean[configs.length];
			int[]		term_start_indices = new int[configs.length];
			int[]		term_end_indices = new int[configs.length];
			boolean[]	term_isblockvalue_flags = new boolean[configs.length];
			boolean[]	term_isblockappend_flags = new boolean[configs.length];
			boolean[]	term_iscomment_flags = new boolean[configs.length];
			
			String	leftover_content = null;
			String	blockid = null;
			
			Parser.Config	config_begin = null;
			Parser.Config	config_term = null;
			
			boolean begin_isblockvalue = false;
			boolean begin_isblockappend = false;
			boolean begin_iscomment = false;
			boolean begin_isquoted = false;
			boolean term_isblockvalue = false;
			boolean term_isblockappend = false;
			boolean term_iscomment = false;
			
			// process the block and content
			do
			{
				{
					String block_id_peek = block_ids.peek();
					
					// iterate over the supported parser configurations to find all
					// start tag beginnings
					for (int i = 0; i < configs.length; i++)
					{
						if (null == block_id_peek)
						{
							first_match = null;
						}
						else
						{
							match1 = getEscapedIndex(content, configs[i].mTagStart.getText(), previous_index, configs[i].mBlockTag, configs[i].mStringDelimiterBegin);
							match2 = getEscapedIndex(content, configs[i].mTagStart.getText(), previous_index, configs[i].mBlockvalueTag, configs[i].mStringDelimiterBegin);
							match3 = getEscapedIndex(content, configs[i].mTagStart.getText(), previous_index, configs[i].mBlockappendTag, configs[i].mStringDelimiterBegin);
							match4 = getEscapedIndex(content, configs[i].mTagStart.getText(), previous_index, configs[i].mCommentTag, configs[i].mTagEnd);
							first_match = getFirstMatch(match1, match2, match3, match4);
						}
						
						if (null == first_match)
						{
							begin_start_indices[i] = -1;
							begin_isblockvalue_flags[i] = false;
							begin_isblockappend_flags[i] = false;
							begin_iscomment_flags[i] = false;
							begin_isquoted_flags[i] = false;
							delimiter_end_indices[i] = -1;
						}
						else
						{
							begin_start_indices[i] = first_match.getMatch(0);
							begin_isblockvalue_flags[i] = (first_match == match2);
							begin_isblockappend_flags[i] = (first_match == match3);
							begin_iscomment_flags[i] = (first_match == match4);
							begin_isquoted_flags[i] = first_match.didFixedPartMatch(1);
							delimiter_end_indices[i] = first_match.getMatch(2);
						}
					}
					
					// iterate over the supported parser configurations all term tag
					// beginnings
					for (int i = 0; i < configs.length; i++)
					{
						if (null == block_id_peek)
						{
							match1 = null;
							match2 = null;
							match3 = null;
						}
						else
						{
							match1 = getEscapedIndex(content, configs[i].mTagTerm.getText(), previous_index, configs[i].mBlockTag, configs[i].mTagEnd);
							match2 = getEscapedIndex(content, configs[i].mTagTerm.getText(), previous_index, configs[i].mBlockvalueTag, configs[i].mTagEnd);
							match3 = getEscapedIndex(content, configs[i].mTagTerm.getText(), previous_index, configs[i].mBlockappendTag, configs[i].mTagEnd);
						}
						match4 = getEscapedIndex(content, configs[i].mTagTerm.getText(), previous_index, configs[i].mCommentTag, configs[i].mTagEnd);
						first_match = getFirstMatch(match1, match2, match3, match4);
						
						if (null == first_match)
						{
							term_start_indices[i] = -1;
							term_isblockvalue_flags[i] = false;
							term_isblockappend_flags[i] = false;
							term_iscomment_flags[i] = false;
							term_end_indices[i] = -1;
						}
						else
						{
							term_start_indices[i] = first_match.getMatch(0);
							term_isblockvalue_flags[i] = (first_match == match2);
							term_isblockappend_flags[i] = (first_match == match3);
							term_iscomment_flags[i] = (first_match == match4);
							term_end_indices[i] = first_match.getMatch(2);
						}
					}
				}
				
				// find the start position of the next block begin tags by comparing
				// which is the earliest start tag beginning
				config_begin = null;
				begin_start_index = -1;
				for (int i = 0; i < begin_start_indices.length; i++)
				{
					if (begin_start_indices[i] != -1 &&
						(-1 == begin_start_index ||
						begin_start_indices[i] < begin_start_index))
					{
						begin_start_index = begin_start_indices[i];
						begin_isblockvalue = begin_isblockvalue_flags[i];
						begin_isblockappend = begin_isblockappend_flags[i];
						begin_iscomment = begin_iscomment_flags[i];
						begin_isquoted = begin_isquoted_flags[i];
						delimiter_end_index = delimiter_end_indices[i];
						config_begin = configs[i];
					}
				}
				
				// find the start position of the next block term tags by comparing
				// which is the earliest term tag beginning
				config_term = null;
				term_start_index = -1;
				term_end_index = -1;
				for (int i = 0; i < term_start_indices.length; i++)
				{
					if (term_start_indices[i] != -1 &&
						(-1 == term_start_index ||
						term_start_indices[i] < term_start_index))
					{
						term_start_index = term_start_indices[i];
						term_isblockvalue = term_isblockvalue_flags[i];
						term_isblockappend = term_isblockappend_flags[i];
						term_iscomment = term_iscomment_flags[i];
						term_end_index = term_end_indices[i];
						config_term = configs[i];
					}
				}
				
				// check if a begin tag was found and if the end tag comes after it
				// or the end tag has been omitted which means that the blocks are nested
				// start the new block that corresponds to the new begin tag
				if (begin_start_index != -1 &&
					(begin_start_index < term_start_index || -1 == term_start_index))
				{
					// the text upto the beginning of this block has to be extracted
					// check if the next begin tag has been double-escaped
					leftover_content = removeTrailingDoubleEscape(content, previous_index, begin_start_index);
					
					// and added to the preceeding block
					blockid = block_ids.peek();
					if (blockid != null)
					{			
						blocks.get(blockid).append(leftover_content);
					}
					
					// check if this is a comment tag and don't extract an id in that case
					if (begin_iscomment)
					{
						blockid_start_index = -1;
						blockid_end_index = -1;
						begin_end_index = delimiter_end_index;
						blockid = null;
						
						// add this comment block to the stack of active blocks
						block_ids.push(null);
						block_types.push(config_begin.mCommentTag);
						block_configs.push(config_begin);
					}
					else
					{
						// get the begin and end position of the block's id
						blockid_start_index = delimiter_end_index;
						
						// get the string delimiter that marks the end of the block id
						ConfigPartMatch delimiter_end_match = null;
						if (begin_isquoted)
						{
							delimiter_end_match = getFirstFoundPartIndex(content, blockid_start_index, config_begin.mStringDelimiterEnd);
						}
						
						if (delimiter_end_match != null &&
							delimiter_end_match.mPart != null)
						{
							blockid_end_index = delimiter_end_match.mIndex;
						}
						// ensure that a name that started out quoted is already terminated with a delimiter
						else if (begin_isquoted && null == delimiter_end_match.mPart)
						{
							String tag_type;
							if (begin_isblockvalue)			tag_type = config_begin.mBlockvalueTag.getText();
							else if (begin_isblockappend)	tag_type = config_begin.mBlockappendTag.getText();
							else							tag_type = config_begin.mBlockTag.getText();
							throw new AttributeNotEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, blockid_start_index),  tag_type, "name");
						}
						else
						{
							int long_index = content.indexOf(config_begin.mTagEnd.getText(), blockid_start_index);
							int short_index = content.indexOf(config_begin.mTagShortTerm.getText(), blockid_start_index);
							
							// backtrack until the first non whitespace character
							blockid_end_index = backtrackTillFirstNonWhitespace(content, getFirstMatch(long_index, short_index));
						}
						
						if (-1 == blockid_end_index)
						{
							String tag_type;
							if (begin_isblockvalue)			tag_type = config_begin.mBlockvalueTag.getText();
							else if (begin_isblockappend)	tag_type = config_begin.mBlockappendTag.getText();
							else							tag_type = config_begin.mBlockTag.getText();
							throw new AttributeNotEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, blockid_start_index), tag_type, "name");
						}
						else
						{
							// extract the id of the block
							blockid = content.substring(blockid_start_index, blockid_end_index);
							
							// ensure that an end delimiter corresponds to a start delimiter
							if (!begin_isquoted &&
								blockid.endsWith(config_begin.mStringDelimiterEnd.getText()))
							{
								String tag_type;
								if (begin_isblockvalue)			tag_type = config_begin.mBlockvalueTag.getText();
								else if (begin_isblockappend)	tag_type = config_begin.mBlockappendTag.getText();
								else							tag_type = config_begin.mBlockTag.getText();
								throw new AttributeWronglyEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, blockid_end_index - config_begin.mStringDelimiterEnd.length()), tag_type, "name");
							}
							
							// get the index of the end of the block begin tag
							begin_end_index = content.indexOf(config_begin.mTagEnd.getText(), blockid_end_index);
							if (-1 == begin_end_index)
							{
								String tag_type;
								if (begin_isblockvalue)			tag_type = config_begin.mBlockvalueTag.getText();
								else if (begin_isblockappend)	tag_type = config_begin.mBlockappendTag.getText();
								else							tag_type = config_begin.mBlockTag.getText();
								throw new BeginTagNotEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, blockid_end_index), tag_type, blockid);
							}
							else
							{
								// check that the begin tag termination is valid
								if (delimiter_end_match != null &&
									delimiter_end_match.mPart != null &&
									content.substring(blockid_end_index + delimiter_end_match.mPart.length(), begin_end_index).trim().length() > 0)
								{
									String tag_type;
									if (begin_isblockvalue)			tag_type = config_begin.mBlockvalueTag.getText();
									else if (begin_isblockappend)	tag_type = config_begin.mBlockappendTag.getText();
									else							tag_type = config_begin.mBlockTag.getText();
									throw new BeginTagBadlyTerminatedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, blockid_end_index + config_begin.mStringDelimiterEnd.length()), tag_type, blockid);
								}
								
								// the start tag was correctly ended, add this block to the stack of active block ids
								block_ids.push(blockid);
								block_configs.push(config_begin);
								
								if (begin_isblockvalue)
								{
									blocks.put(blockid, new StringBuilder(""));
									block_types.push(config_begin.mBlockvalueTag);
									parsed.setBlockvalue(blockid);
								}
								else if (begin_isblockappend)
								{
									StringBuilder current_block = blocks.get(blockid);
									if (null == current_block)
									{
										current_block = new StringBuilder("");
										blocks.put(blockid, current_block);
									}
									block_types.push(config_begin.mBlockappendTag);
									parsed.setBlockvalue(blockid);
								}
								else
								{
									blocks.put(blockid, new StringBuilder(""));
									block_types.push(config_begin.mBlockTag);
								}
							}
						}
					}
					
					// continue the search after this tag
					previous_index = begin_end_index + config_begin.mTagEndLength;
				}
				// the termination tag came before the end tag, this means that the current block first has
				// to be closed correctly
				else if (term_start_index > -1)
				{
					// the text upto the termination tag of the current block has to be extracted
					// check if the next termination tag has been double-escaped
					leftover_content = removeTrailingDoubleEscape(content, previous_index, term_start_index);
					
					// check if a tag is actually open
					if (1 == block_ids.size())
					{
						String class_name = parsed.getClassName();
						DocumentPosition doc_position = StringUtils.getDocumentPosition(content, term_start_index);
						if (term_isblockvalue)			throw new TerminatingUnopenedTagException(class_name, doc_position, config_term.mBlockvalueTag.getText());
						else if (term_isblockappend)	throw new TerminatingUnopenedTagException(class_name, doc_position, config_term.mBlockappendTag.getText());
						else if (term_iscomment)		throw new TerminatingUnopenedTagException(class_name, doc_position, config_term.mCommentTag.getText());
						else							throw new TerminatingUnopenedTagException(class_name, doc_position, config_term.mBlockTag.getText());
					}
					
					// and added to the content of the current block
					blockid = block_ids.peek();
					if (blockid != null)
					{			
						blocks.get(blockid).append(leftover_content);
						
						PartType block_type = block_types.peek().getType();
						// ensure that blockvalue tags are closed with the appropriate termination tag
						if (PartType.BLOCKVALUE == block_type)
						{
							if (term_iscomment)
							{
								throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mBlockvalueTag.getText(), config_term.mCommentTag.getText());
							}
							else if (term_isblockappend)
							{
								throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mBlockvalueTag.getText(), config_term.mBlockappendTag.getText());
							}
							else if (!term_isblockvalue)
							{
								throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mBlockvalueTag.getText(), config_term.mBlockTag.getText());
							}
						}
						// ensure that blockappend tags are closed with the appropriate termination tag
						else if (PartType.BLOCKAPPEND == block_type)
						{
							if (term_iscomment)
							{
								throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mBlockappendTag.getText(), config_term.mCommentTag.getText());
							}
							else if (term_isblockvalue)
							{
								throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mBlockappendTag.getText(), config_term.mBlockvalueTag.getText());
							}
							else if (!term_isblockappend)
							{
								throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mBlockappendTag.getText(), config_term.mBlockTag.getText());
							}
						}
						// ensure that the block tags are closed with the appropriate termination tag
						else
						{			
							if (term_iscomment)
							{
								throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mBlockTag.getText(), config_term.mCommentTag.getText());
							}
							else if (term_isblockvalue)
							{
								throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mBlockTag.getText(), config_term.mBlockvalueTag.getText());
							}
							else if (term_isblockappend)
							{
								throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mBlockTag.getText(), config_term.mBlockappendTag.getText());
							}
						}
					}
					// ensure that the comments tags are closed with the appropriate termination tag
					else if (!term_iscomment)
					{
						if (term_isblockvalue)
						{
							throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mCommentTag.getText(), config_term.mBlockvalueTag.getText());
						}
						else if (term_isblockappend)
						{
							throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mCommentTag.getText(), config_term.mBlockappendTag.getText());
						}
						else
						{
							throw new MismatchedTerminationTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), blockid, config_term.mCommentTag.getText(), config_term.mBlockTag.getText());
						}
					}
					
					
					// the block has been terminated, remove its id from the stack
					block_ids.pop();
					block_types.pop();
					block_configs.pop();
					
					// continue the search after this tag
					previous_index = term_end_index;
				}
			}
			// continue until no start and end tags are found anymore
			while (begin_start_index > -1 || term_start_index > -1);
			
			// check if all tags were correctly closed
			if (block_ids.size() > 1)
			{
				throw new MissingTerminationTagsException(parsed.getClassName(), StringUtils.getDocumentPosition(content, content.length()), block_types.peek().getText());
			}
		}
		
		// append everything that's after the last block to the general content
		blocks.get("").append(content.substring(previous_index));
		
		// iterate over the supported parser configurations to find the ones
		// that are unused for value tags
		List<Parser.Config> value_configs_list = new ArrayList<Parser.Config>();
		for (Parser.Config config : mConfigs)
		{
			if (getEscapedIndex(content, config.mTagStart.getText(), 0, config.mValueTag, config.mStringDelimiterBegin) != null ||
				getEscapedIndex(content, config.mTagTerm.getText(), 0, config.mValueTag, config.mTagEnd) != null)
			{
				value_configs_list.add(config);
			}
		}
		Parser.Config[] value_configs = null;
		if (value_configs_list.size() > 0)
		{
			// make a config array, containing only the used configs
			value_configs = new Parser.Config[value_configs_list.size()];
			value_configs_list.toArray(value_configs);
		}
		
		Parser.Config[] unescape_configs = getUnescapeConfigs(content);
		
		// chop the blocks into block parts according to the value tags that are found in each block
		for (String block_key : blocks.keySet())
		{
			parsed.setBlock(block_key, parseBlockParts(value_configs, unescape_configs, parsed, blocks.get(block_key).toString()));
		}
	}
	
	private ParsedBlockData parseBlockParts(Parser.Config[] configs, Parser.Config[] unescapeConfigs, Parsed parsed, String content)
	throws TemplateException
	{
		assert parsed != null;
		assert content != null;
		
		ParsedBlockData block_data = new ParsedBlockData();
		
		int	previous_index = 0;
		if (configs != null)
		{
			// setup the parser variables
			TagMatch match = null;
			
			int		begin_start_index = 0;
			boolean	begin_isquoted = false;
			int		term_start_index = 0;
			int		term_end_index = 0;
			int		valueid_start_index = 0;
			int		valueid_end_index = 0;
			int		begin_term_index = 0;
			
			int[]		begin_start_indices = new int[configs.length];
			boolean[]	begin_isquoted_flags = new boolean[configs.length];
			int[]		delimiter_end_indices = new int[configs.length];
			int[]		term_start_indices = new int[configs.length];
			int[]		term_end_indices = new int[configs.length];
			
			String	valueid = null;
			String	valuetag_start = null;
			
			Parser.Config	config_begin = null;
			Parser.Config	config_term = null;
			
			// extracts all the parts that make up a block
			do
			{
				// iterate over the supported parser configurations to find all
				// start tag beginnings
				for (int i = 0; i < configs.length; i++)
				{
					match = getEscapedIndex(content, configs[i].mTagStart.getText(), previous_index, configs[i].mValueTag, configs[i].mStringDelimiterBegin);
					if (null == match)
					{
						begin_start_indices[i] = -1;
						begin_isquoted_flags[i] = false;
						delimiter_end_indices[i] = -1;
					}
					else
					{
						begin_start_indices[i] = match.getMatch(0);
						begin_isquoted_flags[i] = match.didFixedPartMatch(1);
						delimiter_end_indices[i] = match.getMatch(2);
					}
				}
				
				// find the start position of the next value begin tags by comparing
				// which is the earliest start tag beginning
				config_begin = null;
				begin_start_index = -1;
				begin_isquoted = false;
				for (int i = 0; i < begin_start_indices.length; i++)
				{
					if (begin_start_indices[i] != -1 &&
						(-1 == begin_start_index ||
						begin_start_indices[i] < begin_start_index))
					{
						begin_start_index = begin_start_indices[i];
						begin_isquoted = begin_isquoted_flags[i];
						valueid_start_index = delimiter_end_indices[i];
						config_begin = configs[i];
					}
				}
				
				// check if a begin tag was found
				if (-1 != begin_start_index)
				{
					// add the text up to the beginning of this value tag to the list of parts
					if (previous_index < begin_start_index)
					{
						String part = unescapePart(unescapeConfigs, content.substring(previous_index, begin_start_index));
						block_data.addPart(new ParsedBlockText(part));
					}
					
					// get the string delimiter that marks the end of the value id
					ConfigPartMatch delimiter_end_match = null;
					int valueid_end_offset = 0;
					if (begin_isquoted)
					{
						delimiter_end_match = getFirstFoundPartIndex(content, valueid_start_index, config_begin.mStringDelimiterEnd);
					}
					
					if (delimiter_end_match != null &&
						delimiter_end_match.mPart != null)
					{
						valueid_end_index = delimiter_end_match.mIndex;
						valueid_end_offset = delimiter_end_match.mPart.length();
					}
					// ensure that a name that started out quoted is already terminated with a delimiter
					else if (begin_isquoted && null == delimiter_end_match.mPart)
					{
						throw new AttributeNotEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, valueid_start_index), config_begin.mValueTag.getText(), "name");
					}
					else
					{
						int long_index = content.indexOf(config_begin.mTagEnd.getText(), valueid_start_index);
						int short_index = content.indexOf(config_begin.mTagShortTerm.getText(), valueid_start_index);
						int first_index = getFirstMatch(long_index, short_index);
						valueid_end_offset = 0;
						valueid_end_index = backtrackTillFirstNonWhitespace(content, first_index);
					}
					
					// check if the value id was ended
					if (-1 == valueid_end_index)
					{
						throw new AttributeNotEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, valueid_start_index), config_begin.mValueTag.getText(), "name");
					}
					
					// extract the appearance of the value tag start so that it can be reused later
					// when the value hasn't been set in the template
					valuetag_start = content.substring(begin_start_index, valueid_start_index);
					
					// extract the id of the value and store it
					valueid = content.substring(valueid_start_index, valueid_end_index);
					
					// ensure that an end delimiter corresponds to a start delimiter
					if (!begin_isquoted &&
						valueid.endsWith(config_begin.mStringDelimiterEnd.getText()))
					{
						throw new AttributeWronglyEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, valueid_end_index + valueid_end_offset - config_begin.mStringDelimiterEnd.length()), config_begin.mValueTag.getText(), "name");
					}
					
					// add the value ID to the list of parsed values
					parsed.addValue(valueid);
					
					// get the first tag ending
					begin_term_index = content.indexOf(config_begin.mTagEnd.getText(), valueid_end_index + valueid_end_offset);
					
					// ensure that the tag was propertly ended
					if (-1 == begin_term_index)
					{
						throw new BeginTagNotEndedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, valueid_end_index + valueid_end_offset), config_begin.mValueTag.getText(), valueid);
					}
					
					// check if it is short value tag
					int begin_short_term_index = begin_term_index - (config_begin.mTagShortTermLength - config_begin.mTagEndLength);
					if (config_begin.mTagShortTerm.equals(content.substring(begin_short_term_index, begin_short_term_index + config_begin.mTagShortTermLength)))
					{
						// check that the begin tag termination is valid
						if (content.substring(valueid_end_index + valueid_end_offset, begin_short_term_index).trim().length() > 0)
						{
							throw new BeginTagBadlyTerminatedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, valueid_end_index + valueid_end_offset), config_begin.mValueTag.getText(), valueid);
						}
						
						term_start_index = begin_short_term_index;
						
						// add the value part
						block_data.addPart(new ParsedBlockValue(valueid, content.substring(begin_start_index, begin_short_term_index + config_begin.mTagShortTermLength)));
						
						// continue the search after this tag
						previous_index = term_start_index + config_begin.mTagShortTermLength;
					}
					// check if it was a long value tag
					else
					{
						// check that the begin tag termination is valid
						if (content.substring(valueid_end_index + valueid_end_offset, begin_term_index).trim().length() > 0)
						{
							throw new BeginTagBadlyTerminatedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, valueid_end_index + valueid_end_offset), config_begin.mValueTag.getText(), valueid);
						}
						
						// iterate over the supported parser configurations all term tag
						// beginnings
						for (int i = 0; i < configs.length; i++)
						{
							match = getEscapedIndex(content, configs[i].mTagTerm.getText(), previous_index, configs[i].mValueTag, configs[i].mTagEnd);
							if (null == match)
							{
								term_start_indices[i] = -1;
								term_end_indices[i] = -1;
							}
							else
							{
								term_start_indices[i] = match.getMatch(0);
								term_end_indices[i] = match.getMatch(2);
							}
						}
						
						// find the start position of the next value term tags by comparing
						// which is the earliest term tag beginning
						config_term = null;
						term_start_index = -1;
						for (int i = 0; i < term_start_indices.length; i++)
						{
							if (term_start_indices[i] != -1 &&
								(-1 == term_start_index ||
								term_start_indices[i] < term_start_index))
							{
								term_start_index = term_start_indices[i];
								term_end_index = term_end_indices[i];
								config_term = configs[i];
							}
						}
						
						// the termination tag always has to be found if a begin tag was found
						if (-1 == term_start_index)
						{
							throw new TagNotTerminatedException(parsed.getClassName(), StringUtils.getDocumentPosition(content, begin_start_index), config_begin.mValueTag.getText(), valueid);
						}
						else
						{
							// iterate over all configuration to check if none introduce a nested value tag
							for (Config config_tmp : configs)
							{
								// get the start of the next begin value tag to check for nested value tags
								match = getEscapedIndex(content, config_tmp.mTagStart.getText(), valueid_start_index, config_tmp.mValueTag, config_tmp.mStringDelimiterBegin);
								
								// nested value tags are not permitted
								if (match != null &&
									match.getMatch(0) < term_start_index)
								{
									throw new UnsupportedNestedTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, match.getMatch(0)), config_tmp.mValueTag.getText(), valueid);
								}
							}
							
							// check if an unopened value tag isn't being closed
							if (begin_term_index > term_start_index)
							{
								throw new TerminatingUnopenedTagException(parsed.getClassName(), StringUtils.getDocumentPosition(content, term_start_index), config_term.mValueTag.getText());
							}
							
							// the termination tag comes after the begin tag and the next begin tag after the termination tag
							parsed.setDefaultValue(valueid, unescapePart(unescapeConfigs, content.substring(begin_term_index + config_begin.mTagEndLength, term_start_index)));
							
							// add the value part
							block_data.addPart(new ParsedBlockValue(valueid, valuetag_start + valueid + config_term.mStringDelimiterEnd + config_term.mTagShortTerm));
							
							// continue the search after this tag
							previous_index = term_end_index;
						}
					}
				}
			}
			// continue until no start and end tags are found anymore
			while (begin_start_index > -1 && term_start_index > -1);
		}
		
		// append everything that's after the last value as a text block part
		if (previous_index < content.length())
		{
			block_data.addPart(new ParsedBlockText(unescapePart(unescapeConfigs, content.substring(previous_index))));
		}
		
		return block_data;
	}
	
	private FilteredTagsMap filterTags(Pattern[] filters, Collection<String> tags)
	{
		FilteredTagsMap result = null;
		
		if (filters != null)
		{
			result = new FilteredTagsMap();
			
			Matcher				filter_matcher = null;
			ArrayList<String>	captured_groups = null;
			String				pattern = null;
			String[]			captured_groups_array = null;
			
			ArrayList<String>	filtered_tags = new ArrayList<String>();
			
			// iterate over the tag filters
			for (Pattern filter_pattern : filters)
			{
				// go over all the tags and try to match them against the current filter
				for (String tag : tags)
				{
					// skip over tags that have already been filtered
					if (filtered_tags.contains(tag))
					{
						continue;
					}
					
					// create the filter matcher
					filter_matcher = filter_pattern.matcher(tag);
					
					// if the filter matches, and it returned capturing groups,
					// add the returned groups to the filtered tag mapping
					while (filter_matcher.find())
					{
						if (null == captured_groups)
						{
							captured_groups = new ArrayList<String>();
							captured_groups.add(tag);
						}
						
						if (filter_matcher.groupCount() > 0)
						{
							// store the captured groups
							for (int j = 1; j <= filter_matcher.groupCount(); j++)
							{
								captured_groups.add(filter_matcher.group(j));
							}
						}
					}
					
					if (captured_groups != null)
					{
						pattern = filter_pattern.pattern();
						
						captured_groups_array = new String[captured_groups.size()];
						captured_groups.toArray(captured_groups_array);
						
						result.addFilteredTag(pattern, captured_groups_array);
						filtered_tags.add(tag);
						
						captured_groups_array = null;
						captured_groups = null;
					}
				}
			}
		}
		
		return result;
	}
	
	static class TagMatch
	{
		private ArrayIntList mMatches = null;
		private ArrayIntList mFixedPartsMatched = null;
		
		void addMatch(int match)
		{
			if (null == mMatches)
			{
				mMatches = new ArrayIntList();
			}
			
			mMatches.add(match);
		}
		
		int getMatch(int index)
		{
			if (null == mMatches ||
				index >= mMatches.size())
			{
				return -1;
			}
			
			return mMatches.get(index);
		}
		
		void addFixedPartMatched(boolean match)
		{
			if (null == mFixedPartsMatched)
			{
				mFixedPartsMatched = new ArrayIntList();
			}
			
			mFixedPartsMatched.add(match ? 1 : 0);
		}
		
		boolean didFixedPartMatch(int index)
		{
			if (null == mFixedPartsMatched ||
				index >= mFixedPartsMatched.size())
			{
				return false;
			}
			
			return 1 == mFixedPartsMatched.get(index);
		}
		
		void clear()
		{
			if (mMatches != null)
			{
				mMatches.clear();
			}
			
			if (mFixedPartsMatched != null)
			{
				mFixedPartsMatched.clear();
			}
		}
	}
	
	static class ConfigPartMatch
	{
		static final ConfigPartMatch	NO_MATCH = new ConfigPartMatch(-1, null);
		
		private	int			mIndex = -1;
		private	ConfigPart	mPart = null;
		
		ConfigPartMatch(int index, ConfigPart part)
		{
			mIndex = index;
			mPart = part;
		}
		
		int length()
		{
			if (null == mPart)
			{
				return 0;
			}
			
			return mPart.length();
		}
	}
	
	public static class MandatoryConfigPart extends ConfigPart
	{
		public MandatoryConfigPart(PartType type, String text)
		{
			super(type, text, false);
		}
	}
	
	public static class OptionalConfigPart extends ConfigPart
	{
		public OptionalConfigPart(PartType type, String text)
		{
			super(type, text, true);
		}
	}
	
	public static class ConfigPart implements CharSequence
	{
		private PartType	mType;
		private String		mText;
		private boolean		mOptional;
		
		private ConfigPart(PartType type, String text, boolean optional)
		{
			mType = type;
			
			if (null == text)
			{
				text = "";
			}
			
			mText = text;
			mOptional = optional;
		}
		
		public PartType getType()
		{
			return mType;
		}
		
		public String getText()
		{
			return mText;
		}
		
		public boolean isOptional()
		{
			return mOptional;
		}
		
		public int length()
		{
			return mText.length();
		}
		
		public char charAt(int index)
		{
			return mText.charAt(index);
		}
		
		public CharSequence subSequence(int start, int end)
		{
			return mText.subSequence(start, end);
		}
		
		public String toString()
		{
			return mText;
		}
		
		public boolean equals(Object other)
		{
			if (null == other)
			{
				return false;
			}
			
			if (other == this)
			{
				return true;
			}
			
			if (other instanceof CharSequence)
			{
				return mText.equals(other);
			}
			
			if (!(other instanceof ConfigPart))
			{
				return false;
			}
			
			ConfigPart other_part = (ConfigPart)other;
			return mType == other_part.mType && mOptional == other_part.mOptional && mText.equals(other_part.mText);
		}
		
		public int hashCode()
		{
			return mType.hashCode() * mText.hashCode() * (mOptional ? 1 : 0);
		}
	}
	
	static class Config implements Cloneable
	{		
		private ConfigPart		mTagStart = null;
		private ConfigPart		mTagEnd = null;
		private ConfigPart		mTagTerm = null;
		private ConfigPart 		mTagShortTerm = null;
		private ConfigPart		mStringDelimiterBegin = null;
		private ConfigPart		mStringDelimiterEnd = null;
		private ConfigPart		mValueTag = null;
		private ConfigPart		mBlockTag = null;
		private ConfigPart		mBlockvalueTag = null;
		private ConfigPart		mBlockappendTag = null;
		private ConfigPart		mIncludeTag = null;
		private ConfigPart		mCommentTag = null;
		private ConfigPart		mUnescapeStart = null;
		
		private Pattern		mUnescapePattern = null;
		
		private int			mTagEndLength = -1;
		private int			mTagTermLength = -1;
		private int			mTagShortTermLength = -1;
		
		Config(String tagStart, String tagEnd, String tagTerm, String tagShortTerm, ConfigPart stringDelimiterBegin, ConfigPart stringDelimiterEnd, String valueTag, String blockTag, String blockvalueTag, String blockappendTag, String includeTag, String commentTag)
		{
			assert tagStart != null;
			assert tagEnd != null;
			assert stringDelimiterBegin != null;
			assert stringDelimiterEnd != null;
			assert tagTerm != null;
			assert tagShortTerm != null;
			assert valueTag != null;
			assert blockTag != null;
			assert blockvalueTag != null;
			assert blockappendTag != null;
			assert includeTag != null;
			assert commentTag != null;
			
			mTagStart = new MandatoryConfigPart(PartType.TAG_START, tagStart);
			mTagEnd = new MandatoryConfigPart(PartType.TAG_END, tagEnd);
			mStringDelimiterBegin = stringDelimiterBegin;
			mStringDelimiterEnd = stringDelimiterEnd;
			mValueTag = new MandatoryConfigPart(PartType.VALUE, valueTag);
			mBlockTag = new MandatoryConfigPart(PartType.BLOCK, blockTag);
			mBlockvalueTag = new MandatoryConfigPart(PartType.BLOCKVALUE, blockvalueTag);
			mBlockappendTag = new MandatoryConfigPart(PartType.BLOCKAPPEND, blockappendTag);
			mIncludeTag = new MandatoryConfigPart(PartType.INCLUDE, includeTag);
			mCommentTag = new MandatoryConfigPart(PartType.COMMENT, commentTag);
			mTagTerm = new MandatoryConfigPart(PartType.TAG_TERM, tagTerm);
			mTagShortTerm = new MandatoryConfigPart(PartType.TAG_SHORT_TERM, tagShortTerm);
			mUnescapeStart = new MandatoryConfigPart(PartType.UNESCAPE_START, "\\" + tagStart);
			mUnescapePattern = Pattern.compile("\\\\((?:\\Q" + tagStart + "\\E|\\Q" + mTagTerm + "\\E)\\s*(?:" + mIncludeTag + "|" + mBlockTag + "|" + mBlockvalueTag + "|" + mBlockappendTag + "|" + mValueTag + "|" + mCommentTag + "))");
			
			mTagEndLength = mTagEnd.length();
			mTagTermLength = mTagTerm.length();
			mTagShortTermLength = mTagShortTerm.length();
			
			assert mTagTerm != null;
			assert mTagShortTerm != null;
			assert mStringDelimiterBegin != null;
			assert mStringDelimiterEnd != null;
			assert mTagEndLength > 0;
			assert mTagTermLength > 0;
			assert mTagShortTermLength > 0;
		}
		
		public Config clone()
		{
			Config new_parserconfig = null;
			try
			{
				new_parserconfig = (Config)super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				new_parserconfig = null;
			}
			
			return new_parserconfig;
		}
		
		public boolean equals(Object object)
		{
			if (object == this)
			{
				return true;
			}
			
			if (null == object)
			{
				return false;
			}
			
			if (!(object instanceof Config))
			{
				return false;
			}
			
			Config other_parserconfig = (Config)object;
			if (other_parserconfig.mTagStart.equals(this.mTagStart) &&
				other_parserconfig.mTagEnd.equals(this.mTagEnd) &&
				other_parserconfig.mTagTerm.equals(this.mTagTerm) &&
				other_parserconfig.mTagShortTerm.equals(this.mTagShortTerm) &&
				other_parserconfig.mStringDelimiterBegin.equals(this.mStringDelimiterBegin) &&
				other_parserconfig.mStringDelimiterEnd.equals(this.mStringDelimiterEnd) &&
				other_parserconfig.mValueTag.equals(this.mValueTag) &&
				other_parserconfig.mBlockTag.equals(this.mBlockTag) &&
				other_parserconfig.mBlockvalueTag.equals(this.mBlockvalueTag) &&
				other_parserconfig.mBlockappendTag.equals(this.mBlockappendTag) &&
				other_parserconfig.mIncludeTag.equals(this.mIncludeTag) &&
				other_parserconfig.mCommentTag.equals(this.mCommentTag))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
