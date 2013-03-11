/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FillBeanNormal.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

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
import java.io.StringBufferInputStream;
import java.util.Arrays;
import java.util.Set;

public class FillBeanNormal extends Element
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
		print("</form");
	}
	
	public void doBean()
	{
		BeanImpl	bean = new BeanImpl();
		
		bean.setString("string");
		bean.setStringbuffer(new StringBuffer("stringbuffer"));
		bean.setInt(999);
		bean.setInteger(new Integer(111));
		bean.setChar('a');
		bean.setCharacter(new Character('b'));
		bean.setBoolean(false);
		bean.setBooleanObject(new Boolean(true));
		bean.setByte((byte)22);
		bean.setByteObject(new Byte((byte)33));
		bean.setDouble(123.45d);
		bean.setDoubleObject(new Double(234.56d));
		bean.setFloat(321.54f);
		bean.setFloatObject(new Float(432.65f));
		bean.setLong(44L);
		bean.setLongObject(new Long(55L));
		bean.setShort((short)66);
		bean.setShortObject(new Short((short)77));
		bean.setStringFile("stringFile");
		bean.setBytesFile(new byte[] {1, 2, 3});
		bean.setStreamFile(new StringBufferInputStream("streamFile"));
		
		fillSubmissionBean(bean);

		boolean		notnumeric_int = false;
		boolean		notnumeric_double = false;
		boolean		notnumeric_longobject = false;
		
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

