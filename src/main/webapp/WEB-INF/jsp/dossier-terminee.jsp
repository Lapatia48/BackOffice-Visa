<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="framework.visa.entity.Demande" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>BackOffice Visa - Dossiers termines</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dossiers-en-cours.css">
</head>
<body>
<%
    List<Demande> dossiersTerminees = (List<Demande>) request.getAttribute("dossiersTerminees");
    if (dossiersTerminees == null) {
        dossiersTerminees = Collections.emptyList();
    }

    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>

<h1>Dossiers termines - Nouveau titre</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Dossiers termines</a>
</div>

<% if (message != null && !message.isEmpty()) { %>
<p class="message"><%= message %></p>
<% } %>

<% if (error != null && !error.isEmpty()) { %>
<p class="error"><%= error %></p>
<% } %>

<div class="bloc filtres">
    <h2 class="section-title">Filtrer les dossiers termines</h2>
    <div class="filters-grid">
        <label>ID demande
            <input id="filterId" type="text" placeholder="ex: 12">
        </label>
        <label>Nom / Prenom
            <input id="filterNom" type="text" placeholder="ex: Rakoto">
        </label>
        <label>Reference visa
            <input id="filterReference" type="text" placeholder="ex: VISA-NT-...">
        </label>
        <label>Date debut visa
            <input id="filterDateDebut" type="date">
        </label>
        <label>Date fin visa
            <input id="filterDateFin" type="date">
        </label>
        <label>Type demande
            <input id="filterType" type="text" placeholder="ex: investisseur">
        </label>
    </div>
    <p id="resultCount" class="count"></p>
</div>

<div class="bloc table-wrapper">
    <table id="dossiersTerminesTable">
        <thead>
        <tr>
            <th>ID</th>
            <th>Demandeur</th>
            <th>Type</th>
            <th>Reference visa</th>
            <th>Date debut visa</th>
            <th>Date fin visa</th>
            <th>Statut</th>
        </tr>
        </thead>
        <tbody>
        <% for (Demande demande : dossiersTerminees) {
            Integer demandeId = demande.getId();

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
            String referenceVisa = demande.getVisa() == null || demande.getVisa().getReference() == null
                ? "-"
                : demande.getVisa().getReference();

            String dateDebutIso = demande.getVisa() == null || demande.getVisa().getDateDebut() == null
                ? ""
                : demande.getVisa().getDateDebut().toString();
            String dateDebutFormatted = demande.getVisa() == null || demande.getVisa().getDateDebut() == null
                ? "-"
                : demande.getVisa().getDateDebut().format(dateFormatter);

            String dateFinIso = demande.getVisa() == null || demande.getVisa().getDateFin() == null
                ? ""
                : demande.getVisa().getDateFin().toString();
            String dateFinFormatted = demande.getVisa() == null || demande.getVisa().getDateFin() == null
                ? "-"
                : demande.getVisa().getDateFin().format(dateFormatter);
        %>
        <tr
            data-id="<%= demandeId %>"
            data-nom="<%= nom.toLowerCase() %>"
            data-prenom="<%= prenom.toLowerCase() %>"
            data-reference="<%= referenceVisa.toLowerCase() %>"
            data-date-debut="<%= dateDebutIso %>"
            data-date-fin="<%= dateFinIso %>"
            data-type="<%= typeLibelle.toLowerCase() %>">
            <td>#<%= demandeId %></td>
            <td><%= nom %> <%= prenom %></td>
            <td><%= typeLibelle %></td>
            <td><%= referenceVisa %></td>
            <td><%= dateDebutFormatted %></td>
            <td><%= dateFinFormatted %></td>
            <td><%= statutLibelle %></td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <% if (dossiersTerminees.isEmpty()) { %>
    <p class="empty">Aucun dossier terminee nouveau titre pour le moment.</p>
    <% } %>
</div>

<script>
    const tableBody = document.querySelector('#dossiersTerminesTable tbody');
    const rows = tableBody ? Array.from(tableBody.querySelectorAll('tr')) : [];

    const filterId = document.getElementById('filterId');
    const filterNom = document.getElementById('filterNom');
    const filterReference = document.getElementById('filterReference');
    const filterDateDebut = document.getElementById('filterDateDebut');
    const filterDateFin = document.getElementById('filterDateFin');
    const filterType = document.getElementById('filterType');
    const resultCount = document.getElementById('resultCount');

    function normalize(value) {
        return (value || '').toString().trim().toLowerCase();
    }

    function applyFilters() {
        const idText = normalize(filterId.value);
        const nomText = normalize(filterNom.value);
        const referenceText = normalize(filterReference.value);
        const dateDebutValue = filterDateDebut.value;
        const dateFinValue = filterDateFin.value;
        const typeText = normalize(filterType.value);

        let visibleCount = 0;

        rows.forEach((row) => {
            const rowId = normalize(row.dataset.id);
            const rowNom = normalize(row.dataset.nom + ' ' + row.dataset.prenom);
            const rowReference = normalize(row.dataset.reference);
            const rowDateDebut = row.dataset.dateDebut || '';
            const rowDateFin = row.dataset.dateFin || '';
            const rowType = normalize(row.dataset.type);

            const matchId = !idText || rowId.includes(idText);
            const matchNom = !nomText || rowNom.includes(nomText);
            const matchReference = !referenceText || rowReference.includes(referenceText);
            const matchDateDebut = !dateDebutValue || rowDateDebut === dateDebutValue;
            const matchDateFin = !dateFinValue || rowDateFin === dateFinValue;
            const matchType = !typeText || rowType.includes(typeText);

            const visible = matchId && matchNom && matchReference && matchDateDebut && matchDateFin && matchType;
            row.style.display = visible ? '' : 'none';
            if (visible) {
                visibleCount += 1;
            }
        });

        resultCount.textContent = visibleCount + ' dossier(s) affiche(s)';
    }

    [filterId, filterNom, filterReference, filterDateDebut, filterDateFin, filterType].forEach((element) => {
        element.addEventListener('input', applyFilters);
    });

    applyFilters();
</script>
</body>
</html>
