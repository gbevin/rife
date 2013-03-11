if $element.hasSubmission("login")
	$element.print($element.getParameter("login")+","+$element.getParameter("password"));
else
	case $element.getInput("input1")
		when "form"
			$element.print(<<END)
<html><body>
<form action="#{$element.getSubmissionQueryUrl("login")}" method="post">
<input name="login" type="text">
<input name="password" type="password">
<input type="submit">
</form>
</body></html>
END
		else
			$element.print($element.getInput("input1")+","+$element.getInput("input2"))
	end
end
