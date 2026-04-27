<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Collections" %>
<%@ page import="framework.visa.entity.Nationalite" %>
<% 
    List<Nationalite> nationalites=(List<Nationalite>) request.getAttribute("nationalites");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>BackOffice Visa - Duplicata</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
</head>
<body>
<h1>Transfert visa</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
    <a href="${pageContext.request.contextPath}/duplicata">Duplicata</a>
</div>

<form action="${pageContext.request.contextPath}/rechercherDataTransfert" method="post">
    <div class="bloc" style="max-width:720px;">
        <p>Nom : <input type="text" name="nom"></p>
        <p>Prenom : <input type="text" name="prenom"></p>
        <p>
            Nationalite : 
            <select name="nationalite">
                <% for(Nationalite nationalite:nationalites){%>
                    <option value="<%= nationalite.getId()%>"><%= nationalite.getLibelle()%></option>
                <% }%>
            </select>
        </p>
        <p>Date de naissance : <input type="date" name="dateNaissance"></p>
        <p>
            <input type="submit" value="Rechercher les informations precedentes">
        </p>
    </div>
    <br>
</form>
</body>
</html>
