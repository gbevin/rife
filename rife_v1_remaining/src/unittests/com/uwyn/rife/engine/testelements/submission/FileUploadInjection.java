/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileUploadInjection.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.UploadedFile;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;

public class FileUploadInjection extends Element
{
	private UploadedFile doc1;
	public void setDoc1(UploadedFile doc1)
	{
		this.doc1 = doc1;
	}

	public void processElement()
	{
		if (hasSubmission("upload"))
		{
			if (doc1 != null)
			{
				try
				{
					print(FileUtils.readString(doc1.getFile()));
				}
				catch (FileUtilsErrorException e)
				{
					throw new EngineException(e);
				}
			}
			else
			{
				print("no file 1");
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
