<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="framework.visa.entity.Demandeur" %>
<%@ page import="framework.visa.entity.Passeport" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Transfert visa - Nouveau passeport</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
</head>
<body>
<%
    Demandeur demandeur = (Demandeur) request.getAttribute("demandeur");
    Passeport ancienPasseport = (Passeport) request.getAttribute("ancienPasseport");
    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");
%>

<h1>Formulaire - Transfert visa</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
    <a href="${pageContext.request.contextPath}/duplicata">Duplicata</a>
    <a href="${pageContext.request.contextPath}/transfert-visa">Transfert visa</a>
</div>

<% if (message != null && !message.isBlank()) { %>
    <p class="message"><%= message %></p>
<% } %>

<% if (error != null && !error.isBlank()) { %>
    <p class="error"><%= error %></p>
<% } %>

<% if (demandeur == null) { %>
    <p class="error">Demandeur introuvable.</p>
<% } else { %>
<form method="post" action="${pageContext.request.contextPath}/transfert-visa/formulaire">
    <input type="hidden" name="idDemandeur" value="<%= demandeur.getId() %>">

    <div class="gauche">
        <div class="bloc">
            <h2 class="section-title">Demandeur</h2>
            <label>Nom
                <input type="text" value="<%= demandeur.getNom() == null ? "" : demandeur.getNom() %>" readonly>
            </label>
            <label>Prenom
                <input type="text" value="<%= demandeur.getPrenom() == null ? "" : demandeur.getPrenom() %>" readonly>
            </label>
        </div>

        <div class="bloc">
            <h2 class="section-title">Ancien passeport</h2>
            <label>Numero passeport actuel
                <input type="text" value="<%= ancienPasseport == null || ancienPasseport.getNumeroPasseport() == null ? "" : ancienPasseport.getNumeroPasseport() %>" readonly>
            </label>
            <label>Date delivrance actuelle
                <input type="date" value="<%= ancienPasseport == null || ancienPasseport.getDateDelivrance() == null ? "" : ancienPasseport.getDateDelivrance() %>" readonly>
            </label>
            <label>Date expiration actuelle
                <input type="date" value="<%= ancienPasseport == null || ancienPasseport.getDateExpiration() == null ? "" : ancienPasseport.getDateExpiration() %>" readonly>
            </label>
            <label>Pays delivrance actuel
                <input type="text" value="<%= ancienPasseport == null || ancienPasseport.getPaysDelivrance() == null ? "" : ancienPasseport.getPaysDelivrance() %>" readonly>
            </label>
        </div>
    </div>

    <div class="droite">
        <div class="bloc">
            <h2 class="section-title">Nouveau passeport</h2>
            <label>Nouveau numero passeport
                <input type="text" name="nouveauNumeroPasseport" required>
            </label>
            <label>Nouvelle date de delivrance
                <input type="date" name="nouvelleDateDelivrance" required>
            </label>
            <label>Nouvelle date d'expiration
                <input type="date" name="nouvelleDateExpiration" required>
            </label>
            <label>Nouveau pays de delivrance
                <input type="text" name="nouveauPaysDelivrance" required>
            </label>
        </div>

        <div class="bloc">
            <button type="submit">Valider le transfert</button>
        </div>
    </div>
</form>
<% } %>

</body>
</html>
