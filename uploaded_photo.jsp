<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Uploaded Photo</title>
    <% String color = (String) session.getAttribute("color"); %>
    <style>
        body {
            margin: 0;
            padding: 0;
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(to bottom, #ffffff, <%= color %>);
        }

        .container {
            max-width: 100%;
            padding: 20px;
            text-align: center;
        }

        .container h1 {
            margin-top: 0;
        }

        .container p {
            margin-bottom: 20px;
        }

        .container img {
            max-width: 100%;
            margin-bottom: 20px;
        }

        .container form button {
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Uploaded Photo</h1>
        <p>Photo Filename: <%= session.getAttribute("photoFileName") %></p>
        <img src="data:image/jpeg;base64,<%= session.getAttribute("photoBase64") %>" alt="Uploaded Photo">
        <form action="menu.jsp" method="get">
            <button type="submit">Go Back to the Menu</button>
        </form>
    </div>
</body>
</html>
