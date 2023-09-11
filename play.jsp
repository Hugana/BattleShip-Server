<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Board Game</title>
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

        .board-container {
            max-width: 500px;
            padding: 20px;
            background-color: #ffffff;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        }

        .board {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            align-items: center;
        }

        .board-row {
            display: flex;
            flex-wrap: wrap;
        }

        .board-col {
            width: 40px;
            height: 40px;
            display: flex;
            justify-content: center;
            align-items: center;
            border: 1px solid #cccccc;
        }

        .board-col img {
            max-width: 100%;
            max-height: 100%;
        }

        .row-form {
            display: flex;
            align-items: center;
            margin-top: 10px;
        }

        .row-form label {
            margin-right: 10px;
        }

        .row-form input[type="text"] {
            padding: 5px;
            width: 40px;
        }

        .row-form button {
            padding: 5px 10px;
            background-color: <%= color %>
            color: #ffffff;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="board-container">
        <div id="board" class="board"></div>

        <form id="rowForm" class="row-form" method="post">
            <label for="rowInput">Enter a row (1-8):</label>
            <input type="text" name="rowInput" id="rowInput" min="1" max="8" required>
            <button type="submit">Submit</button>
            <% 
            Jogador JW = (Jogador) session.getAttribute("Jogador");
            
            if (request.getMethod().equalsIgnoreCase("post")) {
                String jogadaString = request.getParameter("rowInput");
                System.out.println("ISTO E A JOGADA" + jogadaString);
                short jogada = Short.parseShort(jogadaString);
                JW.jogar(jogada);
            }
            %>
        </form>

        <% 
        String boardString = JW.ReedMessage();
        
        if (boardString == null){
        	 out.println("<div>" + "The other player left you are the winner!!" + "</div>");
        }
        else if (boardString != null && (boardString.equals("Defeat") || boardString.equals("Victory"))) {
            out.println("<div>" + boardString + "</div>");
        } else {
            char[][] b = JW.parseBoardProtocolJSP(boardString);
        %>

        <script>
            function updateBoard() {
                var boardElement = document.getElementById('board');
                boardElement.innerHTML = '';

                // Print row numbers
                var rowNumbersElement = document.createElement('div');
                rowNumbersElement.className = 'board-row';

                <% for (int i = 1; i <= b[0].length; i++) { %>
                    var rowNumberColElement = document.createElement('div');
                    rowNumberColElement.className = 'board-col';
                    rowNumberColElement.textContent = '<%= i %>';

                    rowNumbersElement.appendChild(rowNumberColElement.cloneNode(true));
                <% } %>

                boardElement.appendChild(rowNumbersElement);

                // Print board content
                <% for (int i = 0; i < b.length; i++) { %>
                    var rowElement_<%= i %> = document.createElement('div');
                    rowElement_<%= i %>.className = 'board-row';

                    <% for (int j = 0; j < b[i].length; j++) { %>
                        var colElement_<%= j %> = document.createElement('div');
                        colElement_<%= j %>.className = 'board-col';

                        <% if (b[i][j] == 'A') { %>
                            var imgElement = document.createElement('img');
                            imgElement.src = 'BLUEPIECE.png'; // Replace with the path to the A player's image
                            colElement_<%= j %>.appendChild(imgElement);
                        <% } else if (b[i][j] == 'B') { %>
                            var imgElement = document.createElement('img');
                            imgElement.src = 'REDPIECE.png'; // Replace with the path to the B player's image
                            colElement_<%= j %>.appendChild(imgElement);
                        <% } else if (b[i][j] == '0') { %>
                            var imgElement = document.createElement('img');
                            imgElement.src = 'path_to_empty_image.jpg'; // Replace with the path to the empty image
                            colElement_<%= j %>.appendChild(imgElement);
                        <% } %>

                        rowElement_<%= i %>.appendChild(colElement_<%= j %>.cloneNode(true));
                    <% } %>

                    boardElement.appendChild(rowElement_<%= i %>);
                <% } %>
            }

            updateBoard();
        </script>

        <% } %>
    </div>
</body>
</html>
