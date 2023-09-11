<%@ page import="ICD.Jogador" language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
    <title>Change Profile Picture</title>
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

        .container form label {
            display: block;
            margin-bottom: 10px;
        }

        .container form input[type="file"] {
            margin-bottom: 10px;
        }

        .container form input[type="submit"] {
            padding: 10px 20px;
            background-color: #ffffff;
            border-radius: 3px;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Change Profile Picture</h1>
        <form action="UploadPhotoServlet" method="post" enctype="multipart/form-data">
            <label for="picture">Select a picture to upload:</label>
            <input type="file" id="picture" name="picture"><br><br>
            <input type="submit" value="Upload">
        </form>
    </div>
</body>
</html>
