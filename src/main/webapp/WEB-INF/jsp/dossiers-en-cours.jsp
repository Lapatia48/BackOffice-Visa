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
    <title>BackOffice Visa - Liste des demandes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dossiers-en-cours.css">
    <style>
        .statut-nav {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }
        .statut-nav a {
            padding: 8px 16px;
            text-decoration: none;
            background-color: #f0f0f0;
            border: 1px solid #ccc;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;
        }
        .statut-nav a:hover {
            background-color: #e0e0e0;
        }
        .statut-nav a.active {
            background-color: #4CAF50;
            color: white;
            border-color: #4CAF50;
        }
        .row-actions {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;
        }
        .qr-action {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
            padding: 6px 10px;
            text-decoration: none;
            background: #f4e2c5;
            border: 1px solid #c9ab82;
            border-radius: 6px;
            color: #5b3a20;
            font-size: 0.9rem;
            font-weight: 600;
            white-space: nowrap;
        }
        .qr-action:hover {
            background: #ecd3aa;
        }
        .qr-action img {
            display: block;
            width: 22px;
            height: 22px;
        }
        .qr-modal-backdrop {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0, 0, 0, 0.45);
            align-items: center;
            justify-content: center;
            z-index: 9999;
            padding: 16px;
        }
        .qr-modal-backdrop.open {
            display: flex;
        }
        .qr-modal {
            width: min(360px, 100%);
            background: #fff7ea;
            border: 1px solid #c9ab82;
            border-radius: 14px;
            padding: 18px;
            box-shadow: 0 18px 42px rgba(50, 30, 15, 0.25);
            text-align: center;
        }
        .qr-modal h3 {
            margin: 0 0 10px;
            color: #5b3a20;
        }
        .qr-modal img {
            width: 240px;
            height: 240px;
            max-width: 100%;
            display: block;
            margin: 0 auto;
            background: #fff;
            border-radius: 12px;
        }
        .qr-modal .hint {
            margin: 12px 0 0;
            color: #6a4b31;
            font-size: 0.95rem;
        }
        .qr-modal .close-btn {
            margin-top: 14px;
            border: 1px solid #c9ab82;
            background: #f4e2c5;
            color: #5b3a20;
            border-radius: 8px;
            padding: 8px 12px;
            cursor: pointer;
            font-weight: 600;
        }
    </style>
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

    Map<Integer, Boolean> mediaCompletionByDemandeurId = (Map<Integer, Boolean>) request.getAttribute("mediaCompletionByDemandeurId");
    if (mediaCompletionByDemandeurId == null) {
        mediaCompletionByDemandeurId = Collections.emptyMap();
    }

    String message = (String) request.getAttribute("message");
    String error = (String) request.getAttribute("error");
    String statutFiltre = (String) request.getAttribute("statutFiltre");
    if (statutFiltre == null) {
        statutFiltre = "tous";
    }

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    String vueHost = null;
    try {
        java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
        while (interfaces != null && interfaces.hasMoreElements() && (vueHost == null || vueHost.isBlank())) {
            java.net.NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                continue;
            }

            java.util.Enumeration<java.net.InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                java.net.InetAddress address = addresses.nextElement();
                if (address instanceof java.net.Inet4Address && !address.isLoopbackAddress()) {
                    vueHost = address.getHostAddress();
                    break;
                }
            }
        }

        if (vueHost == null || vueHost.isBlank()) {
            java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
            if (localHost != null) {
                vueHost = localHost.getHostAddress();
            }
        }
    } catch (Exception ignored) {
    }

    if (vueHost == null || vueHost.isBlank()) {
        vueHost = request.getLocalAddr();
    }
    if (vueHost == null || vueHost.isBlank()) {
        vueHost = request.getServerName();
    }
    String vueBaseUrl = "http://" + vueHost + ":5173";
%>

<h1>Liste des demandes</h1>

<div class="horizontal-sidebar">
    <a href="/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Liste des demandes</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
</div>

<% if (message != null && !message.isEmpty()) { %>
<p class="message"><%= message %></p>
<% } %>

<% if (error != null && !error.isEmpty()) { %>
<p class="error"><%= error %></p>
<% } %>

<div class="bloc">
    <h2 class="section-title">Filtrer par statut</h2>
    <div class="statut-nav">
        <a href="${pageContext.request.contextPath}/dossiers-en-cours" <%= "tous".equals(statutFiltre) ? "class=\"active\"" : "" %>>Tous les statuts</a>
        <a href="${pageContext.request.contextPath}/dossiers-en-cours?statut=cree" <%= "cree".equals(statutFiltre) ? "class=\"active\"" : "" %>>Créée</a>
        <a href="${pageContext.request.contextPath}/dossiers-en-cours?statut=photo%20et%20signature%20termines" <%= "photo et signature termines".equals(statutFiltre) ? "class=\"active\"" : "" %>>Photo et signature terminés</a>
        <a href="${pageContext.request.contextPath}/dossiers-en-cours?statut=terminee" <%= "terminee".equals(statutFiltre) ? "class=\"active\"" : "" %>>Terminée</a>
        <a href="${pageContext.request.contextPath}/dossiers-en-cours?statut=scanne" <%= "scanne".equals(statutFiltre) ? "class=\"active\"" : "" %>>Scannée</a>
        <a href="${pageContext.request.contextPath}/dossiers-en-cours?statut=approuve" <%= "approuve".equals(statutFiltre) ? "class=\"active\"" : "" %>>Approuvée</a>
        <a href="${pageContext.request.contextPath}/dossiers-en-cours?statut=rejete" <%= "rejete".equals(statutFiltre) ? "class=\"active\"" : "" %>>Rejetée</a>
    </div>
</div>

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
            Integer demandeurId = demande.getDemandeur() == null ? null : demande.getDemandeur().getId();
            boolean mediaComplete = demandeurId != null && Boolean.TRUE.equals(mediaCompletionByDemandeurId.get(demandeurId));

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
                <div class="row-actions">
                    <a class="btn-action" href="${pageContext.request.contextPath}/dossiers-en-cours/ajout?demandeId=<%= demandeId %>">
                        Ajout dossier
                    </a>
                    <% if (!mediaComplete) { %>
                    <a class="btn-action" href="${pageContext.request.contextPath}/demandeur-media?demandeId=<%= demandeId %>&source=list">
                        Photo / signature
                    </a>
                    <% } %>
                    <a class="btn-action" href="${pageContext.request.contextPath}/demande/<%= demandeId %>">
                        Voir
                    </a>
                    <button
                        type="button"
                        class="qr-action qr-trigger"
                        data-demande-id="<%= demandeId %>"
                        title="Scanner QR code">
                        <img alt="QR code" src="https://api.qrserver.com/v1/create-qr-code/?size=90x90&data=<%= java.net.URLEncoder.encode(vueBaseUrl + "/?demandeId=" + demandeId, java.nio.charset.StandardCharsets.UTF_8) %>">
                        <span>Scanner QR code</span>
                    </button>
                </div>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <% if (demandesEnCours.isEmpty()) { %>
    <p class="empty">Aucune demande avec le statut sélectionné pour le moment.</p>
    <% } %>
</div>

<div id="qrModalBackdrop" class="qr-modal-backdrop" onclick="closeQrModal(event)">
    <div class="qr-modal" role="dialog" aria-modal="true" aria-labelledby="qrModalTitle">
        <h3 id="qrModalTitle">Scanner QR code</h3>
        <img id="qrModalImage" alt="QR code pour la demande">
        <p class="hint">Scannez ce code avec votre téléphone pour ouvrir le détail de la demande dans l'application Vue.</p>
        <button type="button" class="close-btn" onclick="closeQrModal()">Fermer</button>
    </div>
</div>

<script>
    const vueBaseUrl = '<%= vueBaseUrl %>';
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

    function buildQrUrl(demandeId) {
        return 'https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=' + encodeURIComponent(vueBaseUrl + '/?demandeId=' + demandeId);
    }

    function openQrModal(demandeId) {
        const backdrop = document.getElementById('qrModalBackdrop');
        const image = document.getElementById('qrModalImage');
        if (!backdrop || !image) {
            return;
        }

        image.src = buildQrUrl(demandeId);
        image.alt = 'QR code de la demande ' + demandeId;
        backdrop.classList.add('open');
    }

    function closeQrModal(event) {
        if (event && event.target && event.target.id !== 'qrModalBackdrop') {
            return;
        }

        const backdrop = document.getElementById('qrModalBackdrop');
        if (!backdrop) {
            return;
        }

        backdrop.classList.remove('open');
    }

    window.openQrModal = openQrModal;
    window.closeQrModal = closeQrModal;

    document.querySelectorAll('.qr-trigger').forEach((button) => {
        button.addEventListener('click', () => {
            openQrModal(button.dataset.demandeId);
        });
    });

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
