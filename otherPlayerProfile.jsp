<%@ page import="ICD.Jogador" language="java"
    contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Player Profile</title>
    <% 
    Jogador JW = (Jogador) session.getAttribute("Jogador");
    String username = request.getParameter("username");
    JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<getPlayerInfoJSP nickname=\"" + username
            + "\"/></protocol>");

    String color = JW.ReedMessage();
    String dateOfBirth = JW.ReedMessage();
    String nationality = JW.ReedMessage();
    String wins = JW.ReedMessage();
    String losses = JW.ReedMessage();
    String draws = JW.ReedMessage();
    String timeSpent = JW.ReedMessage();
    String imageBase64 = JW.ReedMessage();
    %>
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

        .container form {
            margin-top: 20px;
        }

        .container form input[type="text"] {
            padding: 10px;
            width: 300px;
            margin-right: 10px;
        }

        .container form button[type="submit"] {
            padding: 10px 20px;
            background-color: #ffffff;
            border-radius: 3px;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Player Profile</h1>
        <img src="<%= imageBase64 %>" alt="Profile Picture">
        <p>Nickname: <%= username %></p>
        <p>Favorite Color: <%= color %></p>
        <p>Date of Birth: <%= dateOfBirth %></p>
        <p>Nationality: <%= nationality %></p>
        <p>Time Spent: <%= timeSpent %></p>
        <p>Wins: <%= wins %></p>
        <p>Draws: <%= draws %></p>
        <p>Losses: <%= losses %></p>
        <form action="menu.jsp">
            <button type="submit">Go to Menu</button>
        </form>
    </div>
</body>
</html>
