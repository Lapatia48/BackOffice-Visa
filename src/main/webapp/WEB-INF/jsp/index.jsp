
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>BackOffice Visa - Accueil</title>
    <link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:wght@400;500&family=Inter:wght@300;400;450&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
</head>
<body>
<h1>BackOffice Visa</h1>
<p>Selectionnez le type de demande:</p>

<ul>
    <li><a href="/nouveau-titre">Nouveau titre</a></li>
    <li><a href="/duplicata">Duplicata carte resident</a></li>
    <li><a href="/transfert-visa">Transfert visa</a></li>
    <li><a href="/demandes">Liste des demandes (tous statuts)</a></li>
</ul>
</body>
</html>
