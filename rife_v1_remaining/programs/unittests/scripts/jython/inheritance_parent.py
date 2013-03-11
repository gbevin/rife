def childTriggered(name,value):
	if name == "trigger" and value[0] == "ok":
		return 1
	else:
		return 0
	
if element.hasSubmission("activatechild"):
	element.setOutput("trigger", "ok")

element.print("<html><body><a href=\""+element.getSubmissionQueryUrl("activatechild").toString()+"\">activate child</a></body></html>")
