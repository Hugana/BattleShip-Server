<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Menu</title>
    <% String color = (String) session.getAttribute("color"); %>
    <% String myNick = (String) session.getAttribute("myNickname"); %>
    <style>
        body {
            margin: 0;
            padding: 0;
        }

        .container {
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            background: linear-gradient(to bottom, #ffffff, <%= color %>);
        }

        .container h1 {
            margin-top: 0;
        }

        .container p {
            margin-bottom: 20px;
        }

        .container ol {
            list-style-type: none;
            padding: 0;
            margin: 0;
        }

        .container li {
            margin-bottom: 10px;
        }

        .container li a {
            display: block;
            padding: 10px;
            text-decoration: none;
            color: #333333;
            background-color: #ffffff;
            border-radius: 3px;
            transition: background-color 0.3s ease;
        }

        .container li a:hover {
            background-color: #f0f0f0;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Welcome, <%= myNick %></h1>
        <p>Choose an option:</p>
        <ol>
            <li><a href="antesdoplay.jsp?action=play" target="_blank">Play</a></li>
            <li><a href="change-picture.jsp">Change your profile picture</a></li>
            <li><a href="rankings.jsp">See the Rankings</a></li>
            <li><a href="online-users.jsp">See all online Users</a></li>
            <li><a href="autoList.jsp">Search for a Player</a></li>
            <li><a href="send-invite.jsp">Send an invite to play</a></li>
            <li><a href="checkInvite.jsp">Check invites</a></li>
        </ol>
    </div>
</body>
</html>
