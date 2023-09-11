<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Online Users</title>
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
            text-align: center;
            padding: 20px;
        }

        h1 {
            margin-bottom: 20px;
        }

        #onlineUsersList {
            list-style-type: none;
            padding: 0;
        }

        #onlineUsersList li {
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Online Users</h1>
        <ul id="onlineUsersList"></ul>

        <% 
        Jogador JW = (Jogador) session.getAttribute("Jogador");
        JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<listOnlinePlayers/></protocol>");

        String XMLreply1111 = JW.ReedMessage();
        if (XMLreply1111.equals("pedido concedido")) {
            String onlinePlayers = JW.ReedMessage();
            %>
            <script>
                var onlineUsers = '<%= onlinePlayers %>'.split('<br>');
                var onlineUsersList = document.getElementById('onlineUsersList');
                onlineUsers.forEach(function(user) {
                    var listItem = document.createElement('li');
                    listItem.textContent = user;
                    onlineUsersList.appendChild(listItem);
                });
            </script>
            <% 
        }
        %>
    </div>
</body>
</html>
