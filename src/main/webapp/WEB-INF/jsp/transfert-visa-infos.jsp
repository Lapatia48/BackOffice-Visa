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
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Transfert Visa - Infos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
</head>
<body>

<h1>Transfert visa - Donnees trouvees</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
    <a href="${pageContext.request.contextPath}/duplicata">Duplicata</a>
    <a href="${pageContext.request.contextPath}/transfert-visa">Transfert visa</a>
</div>

<% if (d == null) { %>
    <p class="error">Aucun demandeur trouve</p>
<% } else { %>
<div class="bloc" style="max-width:960px;">
    <h2 class="section-title">Informations demandeur</h2>
    <p><strong>Nom</strong> <%= d.getNom() == null ? "N/A" : d.getNom() %></p>
    <p><strong>Prenom</strong> <%= d.getPrenom() == null ? "N/A" : d.getPrenom() %></p>
    <p><strong>Date de naissance</strong> <%= d.getDateNaissance() == null ? "N/A" : d.getDateNaissance().format(formatter) %></p>
    <p><strong>Lieu de naissance</strong> <%= d.getLieuNaissance() == null ? "N/A" : d.getLieuNaissance() %></p>
    <p><strong>Telephone</strong> <%= d.getTelephone() == null ? "N/A" : d.getTelephone() %></p>
    <p><strong>Email</strong> <%= d.getEmail() == null ? "N/A" : d.getEmail() %></p>
    <p><strong>Adresse</strong> <%= d.getAdresse() == null ? "N/A" : d.getAdresse() %></p>

    <h2 class="section-title">Demande et pieces</h2>
    <p><strong>ID demande</strong> <%= demande == null || demande.getId() == null ? "N/A" : demande.getId() %></p>
    <p><strong>Type demande</strong> <%= demande == null || demande.getTypeDemande() == null ? "N/A" : demande.getTypeDemande().getLibelle() %></p>
    <p><strong>Statut dossier</strong> <%= demande == null || demande.getStatut() == null ? "N/A" : demande.getStatut().getLibelle() %></p>

    <table>
        <thead>
            <tr>
                <th>Piece</th>
                <th>Obligatoire</th>
                <th>Fournie</th>
                <th>Commentaire</th>
            </tr>
        </thead>
        <tbody>
        <% for (DemandeDossier dd : demandeDossiers) { %>
            <tr>
                <td><%= dd.getDossier() == null ? "N/A" : dd.getDossier().getLibelle() %></td>
                <td><%= dd.getDossier() != null && dd.getDossier().isObligatoire() ? "Oui" : "Non" %></td>
                <td><%= dd.isEstFourni() ? "Oui" : "Non" %></td>
                <td><%= dd.getCommentaire() == null ? "-" : dd.getCommentaire() %></td>
            </tr>
        <% } %>
        </tbody>
    </table>

    <h2 class="section-title">Visa / carte resident / passeport</h2>
    <p><strong>Numero carte resident</strong> <%= c == null ? "N/A" : c.getNumero() %></p>
    <p><strong>Reference visa</strong> <%= v == null ? "N/A" : v.getReference() %></p>
    <p><strong>Categorie</strong> <%= v == null || v.getCategorieVisa() == null ? "N/A" : v.getCategorieVisa().getLibelle() %></p>
    <p><strong>Date donnation</strong> <%= c == null || c.getDateDonnation() == null ? "N/A" : c.getDateDonnation().format(formatter) %></p>
    <p><strong>Date expiration</strong> <%= c == null || c.getDateExpiration() == null ? "N/A" : c.getDateExpiration().format(formatter) %></p>
    <p><strong>Numero passeport actuel</strong> <%= p == null ? "N/A" : p.getNumeroPasseport() %></p>

    <form method="get" action="${pageContext.request.contextPath}/transfert-visa/formulaire">
        <input type="hidden" name="idDemandeur" value="<%= d.getId() %>">
        <input type="submit" value="Faire un transfert de visa">
    </form>
</div>
<% } %>

</body>
</html>
