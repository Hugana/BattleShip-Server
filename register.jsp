<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>User Registration Form</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #f2f2f2;
        }

        .container {
            width: 300px;
            padding: 20px;
            text-align: center;
            background-color: #ffffff;
            border-radius: 5px;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);
        }

        .container h2 {
            margin-top: 0;
        }

        .container label {
            display: block;
            margin-bottom: 10px;
        }

        .container input[type="text"],
        .container input[type="password"],
        .container input[type="color"],
        .container select,
        .container input[type="date"] {
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

        #color {
            margin-top: 10px;
            color: #ff0000;
        }

        .container button {
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>User Registration</h2>
        <form method="post">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required><br>
            <br>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required><br>
            <br>
            <label for="favoriteColor">Favorite Color:</label>
            <input type="color" id="favoriteColor" name="favoriteColor"><br>
            <br>
            <label for="nationality">Nationality:</label>
            <select id="nationality" name="nationality">
            </select><br>
            <br>
            <label for="dob">Date of Birth:</label>
            <input type="date" id="dob" name="dob" required><br>
            <br>
            <input type="submit" value="Submit">
        </form>
        <form action="login.jsp" method="get">
            <button type="submit">Go to Login</button>
        </form>
        <div id="color"></div>

        <script>
            fetch('nationalities.json')
              .then(response => response.json())
              .then(data => {
                var nationalities = data.map(obj => obj.nationality);
                var nationalityDropdown = document.getElementById("nationality");

                nationalities.forEach(nationality => {
                  var option = document.createElement("option");
                  option.text = nationality;
                  option.value = nationality;
                  nationalityDropdown.appendChild(option);
                });
              })
              .catch(error => {
                console.log('Error fetching nationalities:', error);
              });
        </script>

        <%
            Jogador JW = (Jogador) session.getAttribute("Jogador");
            if (request.getMethod().equalsIgnoreCase("post")) {
                String nickname = request.getParameter("username");
                String password = request.getParameter("password");
                String color = request.getParameter("favoriteColor");
                String nationality = request.getParameter("nationality");
                String dateOfBirth = request.getParameter("dob");

                JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><protocol><register nickname=\"" + nickname + "\" password=\""
                + password + "\" nationality=\"" + nationality + "\" dateOfBirth=\"" + dateOfBirth + "\" color=\"" + color
                + "\"/></protocol>");

                String XMLreply = JW.ReedMessage();

                String reply = JW.XPathGetReturn(XMLreply, "//protocol/*/message/text()");

                out.println("<script>document.getElementById('color').innerHTML = '<p>" + reply + "</p>';</script>");
            }
        %>
    </div>
</body>
</html>
