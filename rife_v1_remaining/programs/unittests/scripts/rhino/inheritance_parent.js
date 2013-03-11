if (element.hasSubmission("activatechild"))
{
	element.setOutput("trigger", "ok");
}

element.print("<html><body><a href=\""+element.getSubmissionQueryUrl("activatechild")+"\">activate child</a></body></html>");
	
function childTriggered(name, value)
{
	if (name == "trigger" &&
		value[0] == "ok")
	{
		return true;
	}
	else
	{
		return false;
	}
}
