/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileUpload.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.UploadedFile;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;

public class FileUpload extends Element
{
	public void processElement()
	{
		if (hasSubmission("upload"))
		{
			if (hasUploadedFile("doc1"))
			{
				if (isFileEmpty("doc1"))
				{
					if (getUploadedFile("doc1").wasSizeExceeded())
					{
						print("file 1 size exceeded");
					}
					else
					{
						print("empty file 1");
					}
				}
				else
				{
					UploadedFile[] files = getUploadedFiles("doc1");
					for (int i = 0; i < files.length; i++)
					{
						try
						{
							print(FileUtils.readString(files[i].getFile()));
						}
						catch (FileUtilsErrorException e)
						{
							throw new EngineException(e);
						}
						
						if (i < files.length-1)
						{
							print(",");
						}
					}
				}
			}
			else
			{
				print("no file 1");
			}
			print(";");
			if (hasUploadedFile("doc2"))
			{
				if (isFileEmpty("doc2"))
				{
					if (getUploadedFile("doc2").wasSizeExceeded())
					{
						print("file 2 size exceeded");
					}
					else
					{
						print("empty file 2");
					}
				}
				else
				{
					UploadedFile[] files = getUploadedFiles("doc2");
					for (int i = 0; i < files.length; i++)
					{
						try
						{
							print(FileUtils.readString(files[i].getFile()));
						}
						catch (FileUtilsErrorException e)
						{
							throw new EngineException(e);
						}
						
						if (i < files.length-1)
						{
							print(",");
						}
					}
				}
			}
			else
			{
				print("no file 2");
			}
			print(";");
			print(getParameter("purpose"));
			return;
		}
		
		print("<html><body>\n");
		print("<form action=\""+getSubmissionFormUrl()+"\" method=\"post\" enctype=\"multipart/form-data\">\n");
		print(getSubmissionFormParameters("upload")+"\n");
		print("<input name=\"purpose\" type=\"text\">\n");
		print("<input name=\"doc1\" type=\"file\">\n");
		print("<input name=\"doc1\" type=\"file\">\n");
		print("<input name=\"doc2\" type=\"file\">\n");
		print("<input type=\"submit\">\n");
		print("</form>\n");
		print("</body></html>\n");
	}
}

