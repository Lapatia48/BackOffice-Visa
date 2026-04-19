
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>BackOffice Visa - Accueil</title>
</head>
<body>
<h1>BackOffice Visa</h1>
<p>Test MVC avec JSP + redirect Spring.</p>

<form action="/submit" method="post">
    <label for="fullName">Nom complet :</label>
    <input id="fullName" name="fullName" type="text" required>
    <button type="submit">Valider</button>
</form>
</body>
</html>
