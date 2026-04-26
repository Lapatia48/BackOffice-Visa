<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="framework.visa.entity.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>

<%
    Demandeur d = (Demandeur) request.getAttribute("demandeur");
    Passeport p = (Passeport) request.getAttribute("passeport");
    Visa v = (Visa) request.getAttribute("visa");
    CarteResident c = (CarteResident) request.getAttribute("carte");
    Demande demande = (Demande) request.getAttribute("demande");
    List<DemandeDossier> demandeDossiers = (List<DemandeDossier>) request.getAttribute("demandeDossiers");
    if (demandeDossiers == null) {
        demandeDossiers = Collections.emptyList();
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    long duree = 0;
    if (c != null && c.getDateDonnation() != null && c.getDateExpiration() != null) {
        duree = java.time.temporal.ChronoUnit.DAYS.between(
            c.getDateDonnation(),
            c.getDateExpiration()
        );
    }
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Duplicata - Infos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
</head>
<body>

<h1>Carte résident - Détails</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
    <a href="${pageContext.request.contextPath}/duplicata">Duplicata</a>
</div>

<% if (d == null) { %>
    <p class="error">Aucun demandeur trouvé</p>
    <div class="bloc" style="max-width:700px;">
        <h2 class="section-title">Entrer de nouvelles donnees</h2>
        <a href="${pageContext.request.contextPath}/nouveau-titre">
            <button type="button">Entrer de nouvelles donnees</button>
        </a>
    </div>
<% } else { %>

    <div class="bloc" style="max-width:960px;">
    <h2 class="section-title">Informations demandeur</h2>

    <p><strong>Nom</strong> <%= d.getNom() == null ? "N/A" : d.getNom() %></p>
    <p><strong>Prenom</strong> <%= d.getPrenom() == null ? "N/A" : d.getPrenom() %></p>
    <p><strong>Date de naissance</strong>
        <%= d.getDateNaissance() == null ? "N/A" : d.getDateNaissance().format(formatter) %>
    </p>
    <p><strong>Lieu de naissance</strong> <%= d.getLieuNaissance() == null ? "N/A" : d.getLieuNaissance() %></p>
    <p><strong>Telephone</strong> <%= d.getTelephone() == null ? "N/A" : d.getTelephone() %></p>
    <p><strong>Email</strong> <%= d.getEmail() == null ? "N/A" : d.getEmail() %></p>
    <p><strong>Adresse</strong> <%= d.getAdresse() == null ? "N/A" : d.getAdresse() %></p>
    <p><strong>Situation familiale</strong>
        <%= d.getSituationFamiliale() == null || d.getSituationFamiliale().getLibelle() == null
            ? "N/A"
            : d.getSituationFamiliale().getLibelle() %>
    </p>
    <p><strong>Nationalite</strong>
        <%= d.getNationalite() == null || d.getNationalite().getLibelle() == null
            ? "N/A"
            : d.getNationalite().getLibelle() %>
    </p>
    <hr>
    <h2 class="section-title">Demande et dossier</h2>

    <p><strong>ID demande</strong> <%= demande == null || demande.getId() == null ? "N/A" : demande.getId() %></p>
    <p><strong>Type demande</strong>
        <%= demande == null || demande.getTypeDemande() == null || demande.getTypeDemande().getLibelle() == null
            ? "N/A"
            : demande.getTypeDemande().getLibelle() %>
    </p>
    <p><strong>Statut dossier</strong>
        <%= demande == null || demande.getStatut() == null || demande.getStatut().getLibelle() == null
            ? "N/A"
            : demande.getStatut().getLibelle() %>
    </p>
    <p><strong>Date demande</strong>
        <%= demande == null || demande.getDateDemande() == null ? "N/A" : demande.getDateDemande().format(formatter) %>
    </p>
    <hr>
    <h2 class="section-title">Visa / carte resident / passeport</h2>

    <p><strong>Numero carte resident :</strong> 
        <%= (c != null && c.getNumero() != null) ? c.getNumero() : "N/A" %>
    </p>

    <p><strong>Reference visa :</strong> 
        <%= (v != null && v.getReference() != null) ? v.getReference() : "N/A" %>
    </p>

    <p><strong>Categorie :</strong> 
        <%= (v != null && v.getCategorieVisa() != null) 
            ? v.getCategorieVisa().getLibelle() 
            : "N/A" %>
    </p>

    <p><strong>Date donnation :</strong> 
        <%= (c != null && c.getDateDonnation() != null) 
            ? c.getDateDonnation().format(formatter) 
            : "N/A" %>
    </p>

    <p><strong>Date expiration :</strong> 
        <%= (c != null && c.getDateExpiration() != null) 
            ? c.getDateExpiration().format(formatter) 
            : "N/A" %>
    </p>

    <p><strong>Duree de validite :</strong> 
        <%= duree > 0 ? duree + " jour(s)" : "N/A" %>
    </p>

    <p><strong>Numero passeport :</strong> 
        <%= (p != null && p.getNumeroPasseport() != null) 
            ? p.getNumeroPasseport() 
            : "N/A" %>
    </p>

    <hr>

    <!-- BOUTON DUPLICATA -->
    <form action="${pageContext.request.contextPath}/creerDuplicata" method="post">
        <input type="hidden" name="idDemandeur" value="<%= d.getId() %>">
        <input type="submit" value="Créer le duplicata">
    </form>
    </div>

<% } %>

<br>

</body>
</html>