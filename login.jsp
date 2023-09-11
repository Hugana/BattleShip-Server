<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login Page</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #ffffff;
        }

        .container {
            width: 300px;
            padding: 20px;
            text-align: center;
            background-color: #ffffff;
            border-radius: 5px;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);
        }

        .container label {
            display: block;
            margin-bottom: 10px;
        }

        .container input[type="text"],
        .container input[type="password"] {
            width: 100%;
            padding: 5px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 3px;
        }

        .container input[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #333333;
            color: #ffffff;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }

        .container #username-msg {
            margin-top: 10px;
            color: #ff0000;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Login Page</h1>
        <form method="post">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username">
            <br>
            <div id="username-msg"></div>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password">
            <br>
            <input type="submit" value="Login">
            <%
                String xsdPath = "src\\main\\webapp\\xml\\protocol\\ProtocolFromServerToClient.xsd";
                Jogador JW = (Jogador) session.getAttribute("Jogador");

                if (request.getMethod().equalsIgnoreCase("post")) {
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");

                    JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><protocol><login nickname=\"" + username + "\" password=\"" + password + "\"/></protocol>");
                    String XMLreply = JW.ReedMessage();

                    if (true) {
                        String reply = JW.XPathGetReturn(XMLreply, "//protocol/*/message/text()");

                        session.setAttribute("myNickname", username);

                        if (reply.equals("Login Valid")) {
                            JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<getColor nickname=\"" + username + "\"/></protocol>");
                            String color = JW.ReedMessage();
                            session.setAttribute("color", color);
                            response.sendRedirect("menu.jsp");
                        } else {
                            out.println("<script>document.getElementById('username-msg').innerHTML = '<p>" + reply + "</p>';</script>");
                        }
                    }
                }
            %>
        </form>
    </div>
</body>
</html>
