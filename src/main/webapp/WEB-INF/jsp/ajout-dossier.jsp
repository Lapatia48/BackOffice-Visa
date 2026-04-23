<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="framework.visa.entity.Demande" %>
<%@ page import="framework.visa.entity.DemandeDossier" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>BackOffice Visa - Ajout dossier</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
</head>
<body>
<%
    Demande demande = (Demande) request.getAttribute("demande");

    List<DemandeDossier> dossiersRestants = (List<DemandeDossier>) request.getAttribute("dossiersRestants");
    if (dossiersRestants == null) {
        dossiersRestants = Collections.emptyList();
    }

    String error = (String) request.getAttribute("error");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>

<h1>Ajout dossier manquant</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
</div>

<% if (error != null && !error.isEmpty()) { %>
<p class="error"><%= error %></p>
<% } %>

<% if (demande == null) { %>
<div class="bloc">
    <p class="error">Demande introuvable.</p>
</div>
<% } else { %>
<form method="post" action="${pageContext.request.contextPath}/dossiers-en-cours/ajout">
    <input type="hidden" name="demandeId" value="<%= demande.getId() %>">

    <div class="gauche">
        <div class="bloc">
            <h2 class="section-title">Demande</h2>
            <label>ID demande
                <input type="text" value="<%= demande.getId() %>" readonly>
            </label>
            <label>Date demande
                <input type="text" value="<%= demande.getDateDemande() == null ? "-" : demande.getDateDemande().format(dateFormatter) %>" readonly>
            </label>
            <label>Type demande
                <input type="text" value="<%= demande.getTypeDemande() == null ? "-" : demande.getTypeDemande().getLibelle() %>" readonly>
            </label>
            <label>Statut actuel
                <input type="text" value="<%= demande.getStatut() == null ? "-" : demande.getStatut().getLibelle() %>" readonly>
            </label>
        </div>

        <div class="bloc">
            <h2 class="section-title">Pieces restantes a fournir</h2>
            <% for (DemandeDossier demandeDossier : dossiersRestants) { %>
                <label class="inline-checkbox">
                    <input type="checkbox" name="dossierIds" value="<%= demandeDossier.getDossier().getId() %>">
                    <%= demandeDossier.getDossier().getLibelle() %>
                    <% if (demandeDossier.getDossier().isObligatoire()) { %>
                        <span class="obligatoire">(obligatoire)</span>
                    <% } else { %>
                        <span class="optionnel">(optionnel)</span>
                    <% } %>
                </label>
            <% } %>

            <% if (dossiersRestants.isEmpty()) { %>
                <p>Toutes les pieces sont deja fournies.</p>
            <% } %>
        </div>

        <div class="bloc">
            <button type="submit">Mettre a jour les dossiers</button>
        </div>
    </div>

    <div class="droite">
        <div class="bloc">
            <h2 class="section-title">Demandeur (pre-rempli)</h2>
            <label>Nom
                <input type="text" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getNom() %>" readonly>
            </label>
            <label>Prenom
                <input type="text" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getPrenom() %>" readonly>
            </label>
            <label>Date de naissance
                <input type="text" value="<%= demande.getDemandeur() == null || demande.getDemandeur().getDateNaissance() == null ? "-" : demande.getDemandeur().getDateNaissance().format(dateFormatter) %>" readonly>
            </label>
            <label>Lieu de naissance
                <input type="text" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getLieuNaissance() %>" readonly>
            </label>
            <label>Telephone
                <input type="text" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getTelephone() %>" readonly>
            </label>
            <label>Email
                <input type="text" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getEmail() %>" readonly>
            </label>
            <label>Adresse
                <textarea readonly><%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getAdresse() %></textarea>
            </label>
        </div>
    </div>
</form>
<% } %>
</body>
</html>
