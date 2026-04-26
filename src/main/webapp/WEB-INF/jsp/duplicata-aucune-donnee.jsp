<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Duplicata - Aucune donnee</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
</head>
<body>
<h1>Duplicata - Donnees anterieures introuvables</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
    <a href="${pageContext.request.contextPath}/duplicata">Duplicata</a>
</div>

<%
    String error = (String) request.getAttribute("error");
%>

<% if (error != null && !error.isBlank()) { %>
    <p class="error"><%= error %></p>
<% } else { %>
    <p class="error">Aucune donnee anterieure disponible pour creer un duplicata.</p>
<% } %>

<div class="bloc">
    <h2 class="section-title">Entrer de nouvelles donnees</h2>
    <p>Vous pouvez saisir une nouvelle demande avec le meme format que la creation de nouveau titre.</p>
    <a href="${pageContext.request.contextPath}/nouveau-titre">
        <button type="button">Entrer de nouvelles donnees</button>
    </a>
</div>

</body>
</html>
