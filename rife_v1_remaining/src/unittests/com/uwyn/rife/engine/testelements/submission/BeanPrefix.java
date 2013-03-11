/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanPrefix.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.config.RifeConfig;
import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.resources.ResourceFinderClasspath;
import com.uwyn.rife.resources.exceptions.ResourceFinderErrorException;
import com.uwyn.rife.site.ValidationError;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.InnerClassException;
import com.uwyn.rife.tools.InputStreamUser;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public class BeanPrefix extends Element
{
	public void processElement()
	{
		print("<form name=\"submissionform\" action=\""+getSubmissionFormUrl()+"\" method=\"post\" enctype=\"multipart/form-data\">");
		print(getSubmissionFormParameters("bean"));
		print("<input type=\"text\" name=\"prefix_enum\">");
		print("<input type=\"text\" name=\"prefix_string\">");
		print("<input type=\"text\" name=\"prefix_boolean\">");
		print("<input type=\"text\" name=\"prefix_string\">");
		print("<input type=\"text\" name=\"prefix_stringbuffer\">");
		print("<input type=\"text\" name=\"prefix_int\">");
		print("<input type=\"text\" name=\"prefix_integer\">");
		print("<input type=\"text\" name=\"prefix_char\">");
		print("<input type=\"text\" name=\"prefix_character\">");
		print("<input type=\"text\" name=\"prefix_boolean\">");
		print("<input type=\"text\" name=\"prefix_booleanObject\">");
		print("<input type=\"text\" name=\"prefix_byte\">");
		print("<input type=\"text\" name=\"prefix_byteObject\">");
		print("<input type=\"text\" name=\"prefix_double\">");
		print("<input type=\"text\" name=\"prefix_doubleObject\">");
		print("<input type=\"text\" name=\"prefix_float\">");
		print("<input type=\"text\" name=\"prefix_floatObject\">");
		print("<input type=\"text\" name=\"prefix_long\">");
		print("<input type=\"text\" name=\"prefix_longObject\">");
		print("<input type=\"text\" name=\"prefix_short\">");
		print("<input type=\"text\" name=\"prefix_shortObject\">");
		print("<input type=\"file\" name=\"prefix_stringFile\"/>");
		print("<input type=\"file\" name=\"prefix_bytesFile\"/>");
		print("<input type=\"file\" name=\"prefix_streamFile\"/>");
		print("<input type=\"text\" name=\"prefix_date\">");
		print("<input type=\"text\" name=\"prefix_dateFormatted\">");
		print("<input type=\"text\" name=\"prefix_datesFormatted\">");
		print("<input type=\"text\" name=\"prefix_datesFormatted\">");
		print("<input type=\"text\" name=\"prefix_serializableParam\">");
		print("<input type=\"text\" name=\"prefix_serializableParams\">");
		print("<input type=\"text\" name=\"prefix_serializableParams\">");
		print("</form");
	}
	
	public void doBean()
	{
		BeanImpl	bean = getSubmissionBean("bean", BeanImpl.class, "prefix_");
		
		Set<ValidationError> errors = bean.getValidationErrors();
		for (ValidationError error : errors)
		{
			print(error.getIdentifier()+" : "+error.getSubject()+"\n");
		}
		print(bean.getEnum()+","+bean.getString()+","+bean.getStringbuffer()+","+bean.getInt()+","+bean.getInteger()+","+bean.getChar()+","+bean.getCharacter()+","+bean.isBoolean()+","+bean.getBooleanObject()+","+bean.getByte()+","+bean.getByteObject()+","+bean.getDouble()+","+bean.getDoubleObject()+","+bean.getFloat()+","+bean.getFloatObject()+","+bean.getLong()+","+bean.getLongObject()+","+bean.getShort()+","+bean.getShortObject());
		print(","+bean.getStringFile());
		
		try
		{
			byte[] image_bytes = ResourceFinderClasspath.getInstance().useStream("uwyn.png", new InputStreamUser() {
					public Object useInputStream(InputStream stream) throws InnerClassException
					{
						try
						{
							return FileUtils.readBytes(stream);
						}
						catch (FileUtilsErrorException e)
						{
							throwException(e);
						}
						
						return null;
					}
				});
			
			if (null == bean.getBytesFile())
			{
				print(",null");
			}
			else
			{
				print(","+Arrays.equals(image_bytes, bean.getBytesFile()));
			}
			print(","+bean.getConstrainedProperty("bytesFile").getName());
			
			if (null == bean.getStreamFile())
			{
				print(",null");
			}
			else
			{
				print(","+Arrays.equals(image_bytes, FileUtils.readBytes(bean.getStreamFile())));
			}
			SimpleDateFormat sf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss");
			sf.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
			print(","+(null == bean.getDate() ? null : sf.format(bean.getDate())));
			if (null == bean.getDatesFormatted())
			{
				print(",null");
			}
			else
			{
				for (Date date : bean.getDatesFormatted())
				{
					print(",");
					if (null == date)
					{
						print("null");
					}
					else
					{
						print(sf.format(date));
					}
				}
			}
			print(","+bean.getSerializableParam());
			if (null == bean.getSerializableParams())
			{
				print(",null");
			}
			else
			{
				for (Object param : bean.getSerializableParams())
				{
					print(","+param);
				}
			}
		}
		catch (ResourceFinderErrorException e)
		{
			throw new EngineException(e);
		}
		catch (FileUtilsErrorException e)
		{
			throw new EngineException(e);
		}
	}
}

