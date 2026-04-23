<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="framework.visa.entity.Demande" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>BackOffice Visa - Dossiers en cours</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dossiers-en-cours.css">
</head>
<body>
<%
    List<Demande> demandesEnCours = (List<Demande>) request.getAttribute("demandesEnCours");
    if (demandesEnCours == null) {
        demandesEnCours = Collections.emptyList();
    }

    Map<Integer, Long> totalPiecesByDemande = (Map<Integer, Long>) request.getAttribute("totalPiecesByDemande");
    if (totalPiecesByDemande == null) {
        totalPiecesByDemande = Collections.emptyMap();
    }

    Map<Integer, Long> providedPiecesByDemande = (Map<Integer, Long>) request.getAttribute("providedPiecesByDemande");
    if (providedPiecesByDemande == null) {
        providedPiecesByDemande = Collections.emptyMap();
    }

    Map<Integer, Long> remainingPiecesByDemande = (Map<Integer, Long>) request.getAttribute("remainingPiecesByDemande");
    if (remainingPiecesByDemande == null) {
        remainingPiecesByDemande = Collections.emptyMap();
    }

    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>

<h1>Dossiers en cours</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
</div>

<% if (message != null && !message.isEmpty()) { %>
<p class="message"><%= message %></p>
<% } %>

<% if (error != null && !error.isEmpty()) { %>
<p class="error"><%= error %></p>
<% } %>

<div class="bloc filtres">
    <h2 class="section-title">Filtrer les demandes</h2>
    <div class="filters-grid">
        <label>ID demande
            <input id="filterId" type="text" placeholder="ex: 12">
        </label>
        <label>Nom / Prenom
            <input id="filterNom" type="text" placeholder="ex: Rakoto">
        </label>
        <label>Date demande
            <input id="filterDate" type="date">
        </label>
        <label>Type demande
            <input id="filterType" type="text" placeholder="ex: Nouveau titre">
        </label>
    </div>
    <p id="resultCount" class="count"></p>
</div>

<div class="bloc table-wrapper">
    <table id="demandesTable">
        <thead>
        <tr>
            <th>ID</th>
            <th>Demandeur</th>
            <th>Date demande</th>
            <th>Type</th>
            <th>Statut</th>
            <th>Progression</th>
            <th>Pieces restantes</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <% for (Demande demande : demandesEnCours) {
            Integer demandeId = demande.getId();
            long total = totalPiecesByDemande.getOrDefault(demandeId, 0L);
            long provided = providedPiecesByDemande.getOrDefault(demandeId, 0L);
            long remaining = remainingPiecesByDemande.getOrDefault(demandeId, 0L);

            String nom = demande.getDemandeur() == null || demande.getDemandeur().getNom() == null
                ? ""
                : demande.getDemandeur().getNom();
            String prenom = demande.getDemandeur() == null || demande.getDemandeur().getPrenom() == null
                ? ""
                : demande.getDemandeur().getPrenom();
            String typeLibelle = demande.getTypeDemande() == null || demande.getTypeDemande().getLibelle() == null
                ? "-"
                : demande.getTypeDemande().getLibelle();
            String statutLibelle = demande.getStatut() == null || demande.getStatut().getLibelle() == null
                ? "-"
                : demande.getStatut().getLibelle();
            String dateIso = demande.getDateDemande() == null ? "" : demande.getDateDemande().toString();
            String dateFormatted = demande.getDateDemande() == null ? "-" : demande.getDateDemande().format(dateFormatter);
        %>
        <tr
            data-id="<%= demandeId %>"
            data-nom="<%= nom.toLowerCase() %>"
            data-prenom="<%= prenom.toLowerCase() %>"
            data-date="<%= dateIso %>"
            data-type="<%= typeLibelle.toLowerCase() %>">
            <td>#<%= demandeId %></td>
            <td><%= nom %> <%= prenom %></td>
            <td><%= dateFormatted %></td>
            <td><%= typeLibelle %></td>
            <td><%= statutLibelle %></td>
            <td><%= provided %>/<%= total %></td>
            <td><%= remaining %></td>
            <td>
                <a class="btn-action" href="${pageContext.request.contextPath}/dossiers-en-cours/ajout?demandeId=<%= demandeId %>">
                    Ajout dossier
                </a>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <% if (demandesEnCours.isEmpty()) { %>
    <p class="empty">Aucune demande incomplete pour le moment.</p>
    <% } %>
</div>

<script>
    const tableBody = document.querySelector('#demandesTable tbody');
    const rows = tableBody ? Array.from(tableBody.querySelectorAll('tr')) : [];

    const filterId = document.getElementById('filterId');
    const filterNom = document.getElementById('filterNom');
    const filterDate = document.getElementById('filterDate');
    const filterType = document.getElementById('filterType');
    const resultCount = document.getElementById('resultCount');

    function normalize(value) {
        return (value || '').toString().trim().toLowerCase();
    }

    function applyFilters() {
        const idText = normalize(filterId.value);
        const nomText = normalize(filterNom.value);
        const dateValue = filterDate.value;
        const typeText = normalize(filterType.value);

        let visibleCount = 0;

        rows.forEach((row) => {
            const rowId = normalize(row.dataset.id);
            const rowNom = normalize(row.dataset.nom + ' ' + row.dataset.prenom);
            const rowDate = row.dataset.date || '';
            const rowType = normalize(row.dataset.type);

            const matchId = !idText || rowId.includes(idText);
            const matchNom = !nomText || rowNom.includes(nomText);
            const matchDate = !dateValue || rowDate === dateValue;
            const matchType = !typeText || rowType.includes(typeText);

            const visible = matchId && matchNom && matchDate && matchType;
            row.style.display = visible ? '' : 'none';
            if (visible) {
                visibleCount += 1;
            }
        });

        resultCount.textContent = visibleCount + ' demande(s) affichee(s)';
    }

    [filterId, filterNom, filterDate, filterType].forEach((element) => {
        element.addEventListener('input', applyFilters);
    });

    applyFilters();
</script>
</body>
</html>
