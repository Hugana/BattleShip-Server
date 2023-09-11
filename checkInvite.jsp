<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Check Invites</title>
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

        #inviteResponseForm {
            display: inline-block;
        }

        #inviteResponseForm button {
            margin-right: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Check Invites</h1>
        <ul id="checkInvite"></ul>

        <%
        Jogador JW = (Jogador) session.getAttribute("Jogador");
        JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<checkInvite/></protocol>");

        String XMLreply = JW.ReedMessage();
        if (XMLreply.equals("Received an Invite")) {
        %>	
            <p><%= XMLreply %></p>
            <form id="inviteResponseForm" action="handle-invite-response" method="post">
                <input type="hidden" name="inviteResponse" value="yes">
                <button type="submit">Yes</button>
            </form>
            <form id="inviteResponseForm" action="handle-invite-response" method="post">
                <input type="hidden" name="inviteResponse" value="no">
                <button type="submit">No</button>
            </form>
        <%
        } else {
        %>
            <p><%= XMLreply %></p>
        <%
        }
        %>
    </div>
</body>
</html>
