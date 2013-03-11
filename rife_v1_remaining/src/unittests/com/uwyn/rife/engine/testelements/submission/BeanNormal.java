/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: BeanNormal.java 3918 2008-04-14 17:35:35Z gbevin $
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

public class BeanNormal extends Element
{
	public void processElement()
	{
		print("<form name=\"submissionform\" action=\""+getSubmissionFormUrl()+"\" method=\"post\" enctype=\"multipart/form-data\">");
		print(getSubmissionFormParameters("bean"));
		print("<input type=\"text\" name=\"enum\">");
		print("<input type=\"text\" name=\"string\">");
		print("<input type=\"text\" name=\"boolean\">");
		print("<input type=\"text\" name=\"string\">");
		print("<input type=\"text\" name=\"stringbuffer\">");
		print("<input type=\"text\" name=\"int\">");
		print("<input type=\"text\" name=\"integer\">");
		print("<input type=\"text\" name=\"char\">");
		print("<input type=\"text\" name=\"character\">");
		print("<input type=\"text\" name=\"boolean\">");
		print("<input type=\"text\" name=\"booleanObject\">");
		print("<input type=\"text\" name=\"byte\">");
		print("<input type=\"text\" name=\"byteObject\">");
		print("<input type=\"text\" name=\"double\">");
		print("<input type=\"text\" name=\"doubleObject\">");
		print("<input type=\"text\" name=\"float\">");
		print("<input type=\"text\" name=\"floatObject\">");
		print("<input type=\"text\" name=\"long\">");
		print("<input type=\"text\" name=\"longObject\">");
		print("<input type=\"text\" name=\"short\">");
		print("<input type=\"text\" name=\"shortObject\">");
		print("<input type=\"file\" name=\"stringFile\"/>");
		print("<input type=\"file\" name=\"bytesFile\"/>");
		print("<input type=\"file\" name=\"streamFile\"/>");
		print("<input type=\"text\" name=\"date\">");
		print("<input type=\"text\" name=\"dateFormatted\">");
		print("<input type=\"text\" name=\"datesFormatted\">");
		print("<input type=\"text\" name=\"datesFormatted\">");
		print("<input type=\"text\" name=\"serializableParam\">");
		print("<input type=\"text\" name=\"serializableParams\">");
		print("<input type=\"text\" name=\"serializableParams\">");
		print("</form");
	}
	
	public void doBean()
	{
		BeanImpl	bean = getSubmissionBean("bean", BeanImpl.class);
		
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

