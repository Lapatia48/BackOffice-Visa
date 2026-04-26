<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Transfert visa - Aucune donnee</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
</head>
<body>
<h1>Transfert visa - Donnees introuvables</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
    <a href="${pageContext.request.contextPath}/duplicata">Duplicata</a>
    <a href="${pageContext.request.contextPath}/transfert-visa">Transfert visa</a>
</div>

<%
    String error = (String) request.getAttribute("error");
%>

<div class="bloc" style="max-width:760px;">
    <p class="error"><%= (error == null || error.isBlank()) ? "Aucune donnee anterieure trouvee pour ce demandeur." : error %></p>
    <h2 class="section-title">Entrer de nouvelles donnees</h2>
    <p>Aucune information precedente n'a ete retrouvee. Vous pouvez saisir une nouvelle demande.</p>
    <a href="${pageContext.request.contextPath}/nouveau-titre?mode=transfert">
        <button type="button">Entrer de nouvelles donnees</button>
    </a>
</div>

</body>
</html>
