<%@ page import="ICD.Jogador" language="java"
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%><!DOCTYPE html>
<html>
<head>
	<title>Login/Register</title>
	<link rel="stylesheet" type="text/css" href="loginAndRegister.css">
</head>
<body>
	<div class="container">
		<h1>Welcome</h1>
		<p>Choose an option:</p>
		<ol>
			<li><a href="login.jsp">Login</a></li>
			<li><a href="register.jsp">Register</a></li>
			
		</ol>
	</div>
</body>
</html>

<%

if(session.getAttribute("Jogador") == null) {
		Jogador JW = new Jogador();
		System.out.println("Criou o Jogador!");
		session.setAttribute("Jogador", JW); 
	}
%>
