<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page pageEncoding="UTF-8" %>

<html>
<head>
	<title>BlogHost</title>
</head>
<body>
<h1>
	Информация о пользователе "${user.name}":  
</h1>
<hr>
<br>
<p>User details:<br>
Название блога: ${blog_name}<br>
User login: ${user.login}<br>
User name: ${user.name}<br>
User pass: ${user.pass}<br>
<hr>
<a href="/Blog/">Назад</a>
</body>
</html>