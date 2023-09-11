<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Antes do Play</title>
</head>
<body>
    <a href="play.jsp" target="_blank">Open Play Page</a>

    <% 
        Jogador JW = (Jogador) session.getAttribute("Jogador");
        JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<requestToPlay/></protocol>");
        out.println(JW.ReedMessage());
        response.sendRedirect("play.jsp");
    %>
</body>
</html>