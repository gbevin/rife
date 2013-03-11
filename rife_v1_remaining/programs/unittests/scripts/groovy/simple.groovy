import com.uwyn.rife.engine.Element

class simple extends Element
{
	void processElement()
	{
		if (hasSubmission("login"))
		{
			print(getParameter("login")+","+getParameter("password"))
		}
		else
		{
			switch (getInput("input1"))
			{
				case "form":
					print("""
<html><body>
<form action="${getSubmissionQueryUrl("login")}" method="post">
<input name="login" type="text">
<input name="password" type="password">
<input type="submit">
</form>
</body></html>
""")
					break
				default:
					print(getInput("input1")+","+getInput("input2"))
					break
			}
		}
	}
}
