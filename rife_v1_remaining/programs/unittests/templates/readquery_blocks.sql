/*B 'query1'*/SELECT name FROM tbltest WHERE name = '/*V 'name'-*/'/*-B*/
[!B 'query2']SELECT name FROM tbltest WHERE name = ?[!/B]
