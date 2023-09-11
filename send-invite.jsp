<%@ page import="ICD.Jogador" language="java"
    contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html>
<html>
<head>
    <title>Send an Invite to a Player</title>
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
        <h1>Send an Invite to a Player</h1>
        <form method="post">
            <input type="text" name="query" placeholder="Enter the Player you want to invite">
            <button type="submit">Search</button>
            <%
            Jogador JW = (Jogador) session.getAttribute("Jogador");
            if (request.getMethod().equalsIgnoreCase("post")) {

                String query = request.getParameter("query");

                JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<sendInvite nickname=\"" + query
                        + "\"/></protocol>");

                String yn = JW.ReedMessage();
                if (yn != null) {
                    if (yn.equals("y")) {
                        JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<toggleConnect/></protocol>");
                        response.sendRedirect("play.jsp");
                    } else if (yn.equals("n")) {

                        System.out.println("Received answer: " + yn);
                    }
                }
            }
            %>
        </form>
    </div>
</body>
</html>
