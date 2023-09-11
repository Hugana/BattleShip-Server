<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Top Rankings</title>
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
    <script>
        function formatLeaderboard(leaderboardString) {
            var lines = leaderboardString.split('<br>');
            var formattedLeaderboard = '<h2>Leaderboard</h2>';

            for (var i = 0; i < lines.length; i++) {
                var line = lines[i].trim();
                if (line.length === 0) continue;

                var parts = line.split('.');
                var rank = parts[0].trim();

                if (parts.length < 2) {
                    formattedLeaderboard += '<p>' + line + '</p>';
                    continue;
                }

                var details = parts[1].trim();
                var name = details.split('(')[0].trim();

                if (!details.includes('(') || !details.includes(')')) {
                    formattedLeaderboard += '<p>' + rank + '. ' + line + '</p>';
                    continue;
                }

                var stats = details.split('(')[1].split(')')[0].trim();
                var time = details.split('[')[1].split(']')[0].trim();

                formattedLeaderboard += '<p>' + rank + '. ' + name + '<br>';
                formattedLeaderboard += '&nbsp;&nbsp;&nbsp;&nbsp;' + stats + ' [' + time + ']</p>';
            }

            document.getElementById('leaderboard').innerHTML = formattedLeaderboard;
            document.querySelector('.container').style.background = 'linear-gradient(to bottom, #ffffff, <%= color %>)';
        }
    </script>
</head>
<body>
    <div class="container">
        <h1>Top Rankings</h1>
        <p>Click the button below to retrieve the top rankings:</p>
        <form method="post">
            <label for="getRankings">Enter the number of rankings you want to see:</label>
            <input type="text" name="getRankings" id="getRankings">
            <button type="submit">Submit</button>
            <div id="leaderboard"></div>
            <%
                Jogador JW = (Jogador) session.getAttribute("Jogador");
                if (request.getMethod().equalsIgnoreCase("post")) {
                    String numberOfRanks = request.getParameter("getRankings");
                    System.out.println("ISTO E O NUMERO DE RANKINGS QUE QUERO VER" + numberOfRanks);
                    JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><protocol><seeRankings/></protocol>");
                    String XMLreply11 = JW.ReedMessage();

                    if (XMLreply11.equals("pedido concedido")) {
                        JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><protocol><leaderBoard top=\"" + numberOfRanks + "\"/></protocol>");
                        XMLreply11 = JW.ReedMessage();
                        String rank = JW.ReedMessage();
                        out.println("<script>formatLeaderboard('" + rank.replaceAll("\7", "<br>") + "');</script>");
                    }
                }
            %>
        </form>
        <form action="menu.jsp">
            <button type="submit">Go Back to the Menu</button>
        </form>
    </div>
</body>
</html>
