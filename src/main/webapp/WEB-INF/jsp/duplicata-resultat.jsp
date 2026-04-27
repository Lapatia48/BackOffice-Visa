<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="framework.visa.entity.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    Demandeur d = (Demandeur) request.getAttribute("demandeur");
    Passeport p = (Passeport) request.getAttribute("passeport");
    Visa v = (Visa) request.getAttribute("visa");
    CarteResident c = (CarteResident) request.getAttribute("carte");
    Demande demande = (Demande) request.getAttribute("demande");

    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    long duree = 0;
    if (c != null && c.getDateDonnation() != null && c.getDateExpiration() != null) {
        duree = java.time.temporal.ChronoUnit.DAYS.between(c.getDateDonnation(), c.getDateExpiration());
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Duplicata - Resultat</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
</head>
<body>

<h1>Duplicata cree</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
    <a href="${pageContext.request.contextPath}/duplicata">Duplicata</a>
</div>

<% if (message != null && !message.isBlank()) { %>
    <p style="color:green;"><strong><%= message %></strong></p>
<% } %>

<% if (error != null && !error.isBlank()) { %>
    <p style="color:red;"><strong><%= error %></strong></p>
<% } %>

<% if (d != null && c != null) { %>
    <div class="bloc" style="max-width:760px;">
    <p><strong>Numero carte resident</strong></p>
    <p><%= c.getNumero() == null ? "N/A" : c.getNumero() %></p>

    <p><strong>Reference visa</strong></p>
    <p><%= (v != null && v.getReference() != null) ? v.getReference() : "N/A" %></p>

    <p><strong>Categorie</strong></p>
    <p><%= (v != null && v.getCategorieVisa() != null && v.getCategorieVisa().getLibelle() != null)
            ? v.getCategorieVisa().getLibelle()
            : "N/A" %></p>

    <p><strong>Type demande</strong></p>
    <p><%= (demande != null && demande.getTypeDemande() != null && demande.getTypeDemande().getLibelle() != null)
            ? demande.getTypeDemande().getLibelle()
            : "N/A" %></p>

    <p><strong>Statut dossier</strong></p>
    <p><%= (demande != null && demande.getStatut() != null && demande.getStatut().getLibelle() != null)
            ? demande.getStatut().getLibelle()
            : "N/A" %></p>

    <p><strong>Date donnation</strong></p>
    <p><%= c.getDateDonnation() == null ? "N/A" : c.getDateDonnation().format(formatter) %></p>

    <p><strong>Date expiration</strong></p>
    <p><%= c.getDateExpiration() == null ? "N/A" : c.getDateExpiration().format(formatter) %></p>

    <p><strong>Duree de validite</strong></p>
    <p><%= duree > 0 ? duree + " jour(s)" : "N/A" %></p>

    <p><strong>Numero passeport</strong></p>
    <p><%= (p != null && p.getNumeroPasseport() != null) ? p.getNumeroPasseport() : "N/A" %></p>
    </div>
<% } %>

</body>
</html>
