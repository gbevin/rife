/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FileUploadRegexp.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.engine.testelements.submission;

import java.util.ArrayList;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.engine.exceptions.EngineException;
import com.uwyn.rife.tools.FileUtils;
import com.uwyn.rife.tools.SortListComparables;
import com.uwyn.rife.tools.exceptions.FileUtilsErrorException;

public class FileUploadRegexp extends Element
{
	public void processElement()
	{
		if (hasSubmission("upload"))
		{
			try
			{
				SortListComparables	sort = new SortListComparables();
				ArrayList<String>	files = new ArrayList<String>(getUploadedFileNames());
				sort.sort(files);
				for (String file : files)
				{
					print(FileUtils.readString(getUploadedFile(file).getFile()));
					print(",");
				}
				print("\n");
					
				files = new ArrayList<String>(getUploadedFileNames(".*doc.*"));
				sort.sort(files);
				for (String file : files)
				{
					print(FileUtils.readString(getUploadedFile(file).getFile()));
					print(",");
				}
			}
			catch (FileUtilsErrorException e)
			{
				throw new EngineException(e);
			}
			return;
		}
		
		print("<html><body>\n");
		print("<form action=\""+getSubmissionFormUrl()+"\" method=\"post\" enctype=\"multipart/form-data\">\n");
		print(getSubmissionFormParameters("upload")+"\n");
		print("<input name=\"somefile\" type=\"file\">\n");
		print("<input name=\"yourdoc1\" type=\"file\">\n");
		print("<input name=\"hisdoc1\" type=\"file\">\n");
		print("<input name=\"thisdoc2\" type=\"file\">\n");
		print("<input type=\"submit\">\n");
		print("</form>\n");
		print("</body></html>\n");
	}
}

