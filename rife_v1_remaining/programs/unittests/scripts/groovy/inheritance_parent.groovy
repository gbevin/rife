import com.uwyn.rife.engine.Element

class inheritance_parent extends Element
{
	void processElement()
	{
		if (hasSubmission("activatechild"))
		{
			setOutput("trigger", "ok")
		}
		
		print("""
<html><body><a href="${getSubmissionQueryUrl("activatechild")}">activate child</a></body></html>
""")
	}
	
	boolean childTriggered(String name, String[] values)
	{
		if (name == "trigger" &&
			values[0] == "ok")
		{
			return true
		}
		
		return false
	}
}
