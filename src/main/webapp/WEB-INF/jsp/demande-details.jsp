<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="framework.visa.entity.Demande" %>
<%@ page import="framework.visa.entity.CarteResident" %>
<%@ page import="framework.visa.entity.Passeport" %>
<%@ page import="framework.visa.entity.Visa" %>
<%@ page import="framework.visa.entity.Demandeur" %>
<%@ page import="framework.visa.entity.DemandeurPhotoSignature" %>
<%@ page import="framework.visa.entity.DemandeDossier" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Détails demande</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dossiers-en-cours.css">
    <style>
        .bloc table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        .bloc table tr {
            border-bottom: 1px solid #ddd;
        }
        .bloc table td {
            padding: 10px;
            border: 1px solid #ddd;
        }
        .bloc table td:first-child {
            background-color: #f5f5f5;
            font-weight: bold;
            width: 30%;
        }
        .media-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
            gap: 16px;
            margin-top: 12px;
        }
        .media-card {
            border: 1px solid #ddd;
            border-radius: 12px;
            padding: 12px;
            background: #fff;
        }
        .media-card h3 {
            margin-bottom: 10px;
        }
        .media-card img {
            display: block;
            width: 100%;
            max-height: 320px;
            object-fit: contain;
            border-radius: 10px;
            background: #fafafa;
        }
        .media-placeholder {
            border: 1px dashed #bbb;
            border-radius: 10px;
            padding: 18px;
            text-align: center;
            color: #666;
            background: #fcfcfc;
        }
    </style>
</head>
<body>
<%
    Demande demande = (Demande) request.getAttribute("demande");
    List<Map<String,String>> historiques = (List<Map<String,String>>) request.getAttribute("historiques");
    Map<Integer, String> scanFiles = (Map<Integer, String>) request.getAttribute("scanFiles");
    Passeport passeport = (Passeport) request.getAttribute("passeport");
    CarteResident carte = (CarteResident) request.getAttribute("carte");
    Visa visa = (Visa) request.getAttribute("visa");
    DemandeurPhotoSignature demandeurMedia = (DemandeurPhotoSignature) request.getAttribute("demandeurMedia");
    @SuppressWarnings("unchecked")
    List<DemandeDossier> demandeDossiers = (List<DemandeDossier>) request.getAttribute("demandeDossiers");
    if (demandeDossiers == null) {
        demandeDossiers = new java.util.ArrayList<>();
    }
%>

<h1>Détails de la demande</h1>
<div class="horizontal-sidebar">
    <a href="/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Liste des demandes</a>
</div>

<div class="bloc">
    <h2>Informations demandeur</h2>
    <% if (demande == null || demande.getDemandeur() == null) { %>
        <p>Aucun demandeur trouvé.</p>
    <% } else {
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        framework.visa.entity.Demandeur demandeur = demande.getDemandeur();
    %>
        <table border="1" cellpadding="10" style="width: 100%;">
            <tr><td><strong>ID:</strong></td><td><%= demandeur.getId() %></td></tr>
            <tr><td><strong>Nom:</strong></td><td><%= demandeur.getNom() == null ? "-" : demandeur.getNom() %></td></tr>
            <tr><td><strong>Prénom:</strong></td><td><%= demandeur.getPrenom() == null ? "-" : demandeur.getPrenom() %></td></tr>
            <tr><td><strong>Date de naissance:</strong></td><td><%= demandeur.getDateNaissance() == null ? "-" : demandeur.getDateNaissance().format(df) %></td></tr>
            <tr><td><strong>Lieu de naissance:</strong></td><td><%= demandeur.getLieuNaissance() == null ? "-" : demandeur.getLieuNaissance() %></td></tr>
            <tr><td><strong>Sexe:</strong></td><td><%= demandeur.getSexe() == null ? "-" : demandeur.getSexe().getLibelle() %></td></tr>
            <tr><td><strong>Nationalité:</strong></td><td><%= demandeur.getNationalite() == null ? "-" : demandeur.getNationalite().getLibelle() %></td></tr>
            <tr><td><strong>Situation familiale:</strong></td><td><%= demandeur.getSituationFamiliale() == null ? "-" : demandeur.getSituationFamiliale().getLibelle() %></td></tr>
            <tr><td><strong>Téléphone:</strong></td><td><%= demandeur.getTelephone() == null ? "-" : demandeur.getTelephone() %></td></tr>
            <tr><td><strong>Email:</strong></td><td><%= demandeur.getEmail() == null ? "-" : demandeur.getEmail() %></td></tr>
            <tr><td><strong>Adresse:</strong></td><td><%= demandeur.getAdresse() == null ? "-" : demandeur.getAdresse() %></td></tr>
            <tr><td><strong>Créé le:</strong></td><td><%= demandeur.getCreatedAt() == null ? "-" : demandeur.getCreatedAt().format(dtf) %></td></tr>
            <tr><td><strong>Modifié le:</strong></td><td><%= demandeur.getUpdatedAt() == null ? "-" : demandeur.getUpdatedAt().format(dtf) %></td></tr>
        </table>
    <% } %>
</div>

<div class="bloc">
    <h2>Photo et signature</h2>
    <% boolean mediaComplete = demandeurMedia != null
        && demandeurMedia.getPhoto() != null && !demandeurMedia.getPhoto().isBlank()
        && demandeurMedia.getSignature() != null && !demandeurMedia.getSignature().isBlank(); %>
    <% if (mediaComplete) { %>
        <div class="media-grid">
            <div class="media-card">
                <h3>Photo</h3>
                <img src="<%= demandeurMedia.getPhoto() %>" alt="Photo du demandeur">
            </div>
            <div class="media-card">
                <h3>Signature</h3>
                <img src="<%= demandeurMedia.getSignature() %>" alt="Signature du demandeur">
            </div>
        </div>
    <% } else { %>
        <div class="media-placeholder">
            <p>Aucune photo ni signature n'a encore été enregistrée.</p>
            <% if (demande != null && demande.getId() != null) { %>
                <p style="margin-top: 10px;">
                    <a class="btn-action" href="${pageContext.request.contextPath}/demandeur-media?demandeId=<%= demande.getId() %>&source=details">
                        Ajouter photo / signature
                    </a>
                </p>
            <% } %>
        </div>
    <% } %>
</div>

<div class="bloc">
    <h2>Informations demande</h2>
    <% if (demande == null) { %>
        <p>Aucune demande trouvée.</p>
    <% } else {
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
    %>
        <table border="1" cellpadding="10" style="width: 100%;">
            <tr><td><strong>ID demande:</strong></td><td><%= demande.getId() %></td></tr>
            <tr><td><strong>Type:</strong></td><td><%= demande.getTypeDemande() == null ? "-" : demande.getTypeDemande().getLibelle() %></td></tr>
            <tr><td><strong>Statut:</strong></td><td><%= demande.getStatut() == null ? "-" : demande.getStatut().getLibelle() %></td></tr>
            <tr><td><strong>Date de demande:</strong></td><td><%= demande.getDateDemande() == null ? "-" : demande.getDateDemande().format(df) %></td></tr>
        </table>
    <% } %>
</div>

<div class="bloc">
    <h2>Carte résident</h2>
    <% if (carte == null) { %>
        <p>Aucune carte résident liée.</p>
    <% } else {
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
    %>
        <table border="1" cellpadding="10">
            <tr><td><strong>Numéro carte:</strong></td><td><%= carte.getNumero() %></td></tr>
            <tr><td><strong>Date donation:</strong></td><td><%= carte.getDateDonnation() == null ? "-" : carte.getDateDonnation().format(df) %></td></tr>
            <tr><td><strong>Date expiration:</strong></td><td><%= carte.getDateExpiration() == null ? "-" : carte.getDateExpiration().format(df) %></td></tr>
            <tr><td><strong>État:</strong></td><td><%= carte.getEtat() == null ? "-" : carte.getEtat().getLibelle() %></td></tr>
        </table>
    <% } %>
</div>

<div class="bloc">
    <h2>Passeport</h2>
    <% if (passeport == null) { %>
        <p>Aucun passeport renseigné.</p>
    <% } else {
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
    %>
        <table border="1" cellpadding="10">
            <tr><td><strong>Numéro passeport:</strong></td><td><%= passeport.getNumeroPasseport() %></td></tr>
            <tr><td><strong>Date delivrance:</strong></td><td><%= passeport.getDateDelivrance() == null ? "-" : passeport.getDateDelivrance().format(df) %></td></tr>
            <tr><td><strong>Date expiration:</strong></td><td><%= passeport.getDateExpiration() == null ? "-" : passeport.getDateExpiration().format(df) %></td></tr>
            <tr><td><strong>Pays delivrance:</strong></td><td><%= passeport.getPaysDelivrance() == null ? "-" : passeport.getPaysDelivrance() %></td></tr>
        </table>
    <% } %>
</div>

<div class="bloc">
    <h2>Visa</h2>
    <% if (visa == null) { %>
        <p>Aucun visa renseigné.</p>
    <% } else {
        java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
    %>
        <table border="1" cellpadding="10">
            <tr><td><strong>Référence:</strong></td><td><%= visa.getReference() == null ? "-" : visa.getReference() %></td></tr>
            <tr><td><strong>Date début:</strong></td><td><%= visa.getDateDebut() == null ? "-" : visa.getDateDebut().format(df) %></td></tr>
            <tr><td><strong>Date fin:</strong></td><td><%= visa.getDateFin() == null ? "-" : visa.getDateFin().format(df) %></td></tr>
            <tr><td><strong>Catégorie:</strong></td><td><%= visa.getCategorieVisa() == null ? "-" : visa.getCategorieVisa().getLibelle() %></td></tr>
        </table>
    <% } %>
</div>

<div class="bloc">
    <h2>Historique des actions</h2>
    <% if (historiques == null || historiques.isEmpty()) { %>
        <p>Aucun historique disponible.</p>
    <% } else { %>
        <ul>
            <% for (Map<String,String> h : historiques) { %>
                <li>
                    <strong><%= h.getOrDefault("date", "-") %></strong>
                    &nbsp;-&nbsp; <%= h.getOrDefault("statutLabel", "Statut non renseigne") %>
                    <br/>
                    <em><%= h.getOrDefault("commentaire", "") %></em>
                </li>
            <% } %>
        </ul>
    <% } %>
</div>

<div class="bloc">
    <h2>Dossiers</h2>
    <% if (demandeDossiers == null || demandeDossiers.isEmpty()) { %>
        <p>Aucun dossier pour cette demande.</p>
    <% } else { %>
        <ul class="history-list">
            <% for (DemandeDossier dd : demandeDossiers) {
                boolean estFourni = dd.isEstFourni();
                String libelle = dd.getDossier() == null ? "-" : dd.getDossier().getLibelle();
                Integer dossierId = dd.getDossier() == null ? 0 : dd.getDossier().getId();
                String scanChemin = scanFiles == null ? "" : scanFiles.getOrDefault(dossierId, "");
                boolean obligatoire = dd.getDossier() != null && dd.getDossier().isObligatoire();
            %>
            <li>
                <%= libelle %>
                <% if (obligatoire) { %>
                <span class="obligatoire">(obligatoire)</span>
                <% } else { %>
                <span class="optionnel">(optionnel)</span>
                <% } %>
                <% if (estFourni && !scanChemin.isEmpty()) { %>
                <p class="message">Scanne (chemin): <%= scanChemin %></p>
                <button type="button" class="btn-action btn-scanned" disabled>OK scanne</button>
                <% } else { %>
                <form method="post"
                      action="${pageContext.request.contextPath}/dossier-terminee/scanner"
                      enctype="multipart/form-data">
                    <input type="hidden" name="demandeId" value="<%= demande.getId() %>">
                    <input type="hidden" name="dossierId" value="<%= dossierId %>">
                    <input type="file" name="scanFile" accept="application/pdf" required>
                    <button type="submit" class="btn-action">Scanner</button>
                </form>
                <% } %>
            </li>
            <% } %>
        </ul>
    <% } %>
</div>

</body>
</html>
