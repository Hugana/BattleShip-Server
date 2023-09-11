<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Pesquisa de Jogador</title>
    <% String color = (String) session.getAttribute("color"); %>
    <style>
        body {
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background: linear-gradient(to bottom, #ffffff, <%= color %>);
            font-family: Arial, sans-serif;
        }

        .container {
            max-width: 500px;
            padding: 20px;
            background-color: #ffffff;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .container form {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .container label {
            display: block;
            margin-bottom: 10px;
        }

        .container input[type="search"] {
            width: 350px;
            padding: 10px;
            border: 1px solid #cccccc;
            border-radius: 3px;
            font-size: 16px;
        }

        .container datalist {
            margin-top: 10px;
        }

        .container datalist option {
            padding: 5px;
        }

        .container button {
            margin-top: 10px;
            padding: 10px 20px;
            background-color: #4caf50;
            color: #ffffff;
            border: none;
            border-radius: 3px;
            font-size: 16px;
            cursor: pointer;
        }
    </style>
    <script>
        function getXHR() {
            var invocation = null;
            try {
                // Opera 8.0+, Firefox, Chrome, Safari
                invocation = new XMLHttpRequest();
            } catch (e) {
                // Internet Explorer Browsers
                try {
                    invocation = new ActiveXObject("Msxml2.XMLHTTP");

                } catch (e) {

                    try {
                        invocation = new ActiveXObject("Microsoft.XMLHTTP");
                    } catch (e) {
                        // Something went wrong
                        alert("Your browser broke!");
                        return null;
                    }
                }
            }
            return invocation;
        }

        var xmlHttp = getXHR();

        function showState(str) {
            if (event.key === 'Enter') {
                document.getElementById("country").value = document.getElementById("countries").options[0].value;
                return;
            }
            var url = "GetHTML";
            url += "?query=" + str + "&tag=option&nlt=0&nit=10";
            url = encodeURI(url);
            xmlHttp.onreadystatechange = function () {
                if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
                    document.getElementById("countries").innerHTML = xmlHttp.responseText;
                    document.getElementById("country").placeholder = "Player Name";
                } else
                    document.getElementById("country").placeholder = "Couldn't load datalist options :(";
            }
            xmlHttp.open("GET", url, true);
            xmlHttp.send(null);
        }
    </script>
</head>
<body>
    <div class="container">
        <form method="post">
            <label>Type Player Profile:</label>
            <input autocomplete="off" list="countries" id="country" type="search" name="country" onkeyup="showState(this.value);" placeholder="e.g. datalist">
            <datalist id="countries">
                <option>Loading...</option>
            </datalist>
            <% if (request.getMethod().equalsIgnoreCase("post")) {
                String username = request.getParameter("country");
                response.sendRedirect("otherPlayerProfile.jsp?username=" + username);
            } %>
            <button type="submit">Submit</button>
        </form>
    </div>
</body>
</html>
