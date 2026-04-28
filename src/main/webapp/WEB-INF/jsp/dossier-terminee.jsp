<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="framework.visa.entity.Demande" %>
<%@ page import="framework.visa.entity.Dossier" %>
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

    Map<Integer, Map<String, String>> residentDetailsByDemande =
            (Map<Integer, Map<String, String>>) request.getAttribute("residentDetailsByDemande");
    if (residentDetailsByDemande == null) {
        residentDetailsByDemande = Collections.emptyMap();
    }

    Map<Integer, List<Map<String, String>>> historiquesByDemande =
            (Map<Integer, List<Map<String, String>>>) request.getAttribute("historiquesByDemande");
    if (historiquesByDemande == null) {
        historiquesByDemande = Collections.emptyMap();
    }

    List<Dossier> commonDossiers = (List<Dossier>) request.getAttribute("commonDossiers");
    if (commonDossiers == null) {
        commonDossiers = Collections.emptyList();
    }

    Map<Integer, List<Dossier>> typedDossiers = (Map<Integer, List<Dossier>>) request.getAttribute("typedDossiers");
    if (typedDossiers == null) {
        typedDossiers = Collections.emptyMap();
    }

    Map<Integer, Map<Integer, String>> scanFilesByDemandeAndDossier =
            (Map<Integer, Map<Integer, String>>) request.getAttribute("scanFilesByDemandeAndDossier");
    if (scanFilesByDemandeAndDossier == null) {
        scanFilesByDemandeAndDossier = Collections.emptyMap();
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
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
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
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <% for (Demande demande : dossiersTerminees) {
            Integer demandeId = demande.getId();
            Map<String, String> residentDetails = residentDetailsByDemande.getOrDefault(demandeId, Collections.emptyMap());

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
            <td>
                <button type="button" class="btn-action btn-resident js-open-resident" data-demande-id="<%= demandeId %>">
                    Voir carte resident
                </button>
                <button type="button" class="btn-action btn-history js-open-history" data-demande-id="<%= demandeId %>">
                    Voir historique demande
                </button>
                <button type="button" class="btn-action js-open-justificatifs" data-demande-id="<%= demandeId %>">
                    Scanner pieces justificatives
                </button>
            </td>
        </tr>
        <% } %>
        </tbody>
    </table>

    <% if (dossiersTerminees.isEmpty()) { %>
    <p class="empty">Aucun dossier terminee nouveau titre pour le moment.</p>
    <% } %>
</div>

<div id="residentModal" class="resident-modal" aria-hidden="true">
    <div class="resident-modal-backdrop" data-close-modal="true"></div>
    <div class="resident-modal-card" role="dialog" aria-modal="true" aria-labelledby="residentModalTitle">
        <button type="button" class="resident-modal-close" id="closeResidentModal" aria-label="Fermer la fenetre">x</button>
        <h2 id="residentModalTitle">Carte resident - Details</h2>
        <div id="residentModalBody"></div>
    </div>
</div>

<div id="historyModal" class="resident-modal" aria-hidden="true">
    <div class="resident-modal-backdrop" data-close-history-modal="true"></div>
    <div class="resident-modal-card" role="dialog" aria-modal="true" aria-labelledby="historyModalTitle">
        <button type="button" class="resident-modal-close" id="closeHistoryModal" aria-label="Fermer la fenetre">x</button>
        <h2 id="historyModalTitle">Historique demande</h2>
        <div id="historyModalBody"></div>
    </div>
</div>

<div id="justificatifModal" class="resident-modal" aria-hidden="true">
    <div class="resident-modal-backdrop" data-close-justificatif-modal="true"></div>
    <div class="resident-modal-card" role="dialog" aria-modal="true" aria-labelledby="justificatifModalTitle">
        <button type="button" class="resident-modal-close" id="closeJustificatifModal" aria-label="Fermer la fenetre">x</button>
        <h2 id="justificatifModalTitle">Pieces justificatives necessaires</h2>
        <div id="justificatifModalBody"></div>
    </div>
</div>

<div id="residentDetailTemplates" class="resident-detail-templates">
    <% for (Demande demande : dossiersTerminees) {
        Integer demandeId = demande.getId();
        Map<String, String> residentDetails = residentDetailsByDemande.getOrDefault(demandeId, Collections.emptyMap());
    %>
    <section id="resident-detail-template-<%= demandeId %>">
        <div class="resident-card-header">
            <p class="resident-chip">Demande #<%= demandeId %></p>
            <h3><%= residentDetails.getOrDefault("demandeur", "Non renseignee") %></h3>
        </div>
        <div class="resident-grid">
            <div class="resident-item">
                <span>Numero carte resident</span>
                <strong><%= residentDetails.getOrDefault("numeroCarteResident", "Non renseignee") %></strong>
            </div>
            <div class="resident-item">
                <span>Reference visa</span>
                <strong><%= residentDetails.getOrDefault("referenceVisa", "Non renseignee") %></strong>
            </div>
            <div class="resident-item">
                <span>Categorie</span>
                <strong><%= residentDetails.getOrDefault("categorie", "Non renseignee") %></strong>
            </div>
            <div class="resident-item">
                <span>Type demande</span>
                <strong><%= residentDetails.getOrDefault("typeDemande", "Non renseignee") %></strong>
            </div>
            <div class="resident-item">
                <span>Statut dossier</span>
                <strong><%= residentDetails.getOrDefault("statut", "Non renseignee") %></strong>
            </div>
            <div class="resident-item">
                <span>Date donnation</span>
                <strong><%= residentDetails.getOrDefault("dateDonnation", "Non renseignee") %></strong>
            </div>
            <div class="resident-item">
                <span>Date expiration</span>
                <strong><%= residentDetails.getOrDefault("dateExpiration", "Non renseignee") %></strong>
            </div>
            <div class="resident-item">
                <span>Duree de validite</span>
                <strong><%= residentDetails.getOrDefault("duree", "Non renseignee") %></strong>
            </div>
            <div class="resident-item">
                <span>Numero passeport</span>
                <strong><%= residentDetails.getOrDefault("numeroPasseport", "Non renseignee") %></strong>
            </div>
        </div>
    </section>
    <% } %>
</div>

<div id="historyDetailTemplates" class="resident-detail-templates">
    <% for (Demande demande : dossiersTerminees) {
        Integer demandeId = demande.getId();
        List<Map<String, String>> historiques = historiquesByDemande.getOrDefault(demandeId, Collections.emptyList());
    %>
    <section id="history-detail-template-<%= demandeId %>">
        <div class="resident-card-header">
            <p class="resident-chip">Demande #<%= demandeId %></p>
            <h3>Historique des mouvements</h3>
        </div>
        <% if (historiques.isEmpty()) { %>
        <p class="empty">Aucun mouvement disponible.</p>
        <% } else { %>
        <div class="history-list">
            <% for (Map<String, String> historique : historiques) { %>
            <article class="history-item">
                <p class="history-date"><strong><%= historique.getOrDefault("date", "Non renseignee") %></strong></p>
                <p class="history-status">
                    Statut #<%= historique.getOrDefault("statutId", "-") %> -
                    <%= historique.getOrDefault("statutLabel", "Statut non renseigne") %>
                </p>
                <p class="history-comment"><%= historique.getOrDefault("commentaire", "Non renseignee") %></p>
            </article>
            <% } %>
        </div>
        <% } %>
    </section>
    <% } %>
</div>

<div id="justificatifDetailTemplates" class="resident-detail-templates">
    <% for (Demande demande : dossiersTerminees) {
        Integer demandeId = demande.getId();
        Integer typeDemandeId = demande.getTypeDemande() == null ? null : demande.getTypeDemande().getId();
        String typeDemande = demande.getTypeDemande() == null || demande.getTypeDemande().getLibelle() == null
            ? ""
            : demande.getTypeDemande().getLibelle();
        Map<Integer, String> scanFilesByDossierId = scanFilesByDemandeAndDossier.getOrDefault(demandeId, Collections.emptyMap());
        List<Dossier> dossiersComplementaires = typeDemandeId == null
            ? Collections.emptyList()
            : typedDossiers.getOrDefault(typeDemandeId, Collections.emptyList());
    %>
    <section id="justificatif-detail-template-<%= demandeId %>">
        <div class="resident-card-header">
            <p class="resident-chip">Demande #<%= demandeId %></p>
            <h3>Type : <%= typeDemande.isEmpty() ? "Non renseigne" : typeDemande %></h3>
        </div>

        <% if (commonDossiers.isEmpty() && dossiersComplementaires.isEmpty()) { %>
        <p class="empty">Aucune piece justificative configuree pour ce dossier.</p>
        <% } else { %>
            <% if (!commonDossiers.isEmpty()) { %>
            <h4>Pieces communes</h4>
            <ul class="history-list">
                <% for (Dossier dossier : commonDossiers) { %>
                <% String scanChemin = scanFilesByDossierId.get(dossier.getId()); %>
                <li>
                    <%= dossier.getLibelle() %>
                    <% if (dossier.isObligatoire()) { %>
                    <span class="obligatoire">(obligatoire)</span>
                    <% } else { %>
                    <span class="optionnel">(optionnel)</span>
                    <% } %>
                    <% if (scanChemin != null && !scanChemin.isBlank()) { %>
                    <p class="message">Scanne (chemin): <%= scanChemin %></p>
                    <button type="button" class="btn-action btn-scanned" disabled>OK scanne</button>
                    <% } else { %>
                    <form method="post"
                          action="${pageContext.request.contextPath}/dossier-terminee/scanner"
                          enctype="multipart/form-data">
                        <input type="hidden" name="demandeId" value="<%= demandeId %>">
                        <input type="hidden" name="dossierId" value="<%= dossier.getId() %>">
                        <input type="file" name="scanFile" accept="application/pdf" required>
                        <button type="submit" class="btn-action">Scanner</button>
                    </form>
                    <% } %>
                </li>
                <% } %>
            </ul>
            <% } %>

            <% if (!dossiersComplementaires.isEmpty()) { %>
            <h4>Pieces complementaires</h4>
            <ul class="history-list">
                <% for (Dossier dossier : dossiersComplementaires) { %>
                <% String scanChemin = scanFilesByDossierId.get(dossier.getId()); %>
                <li>
                    <%= dossier.getLibelle() %>
                    <% if (dossier.isObligatoire()) { %>
                    <span class="obligatoire">(obligatoire)</span>
                    <% } else { %>
                    <span class="optionnel">(optionnel)</span>
                    <% } %>
                    <% if (scanChemin != null && !scanChemin.isBlank()) { %>
                    <p class="message">Scanne (chemin): <%= scanChemin %></p>
                    <button type="button" class="btn-action btn-scanned" disabled>OK scanne</button>
                    <% } else { %>
                    <form method="post"
                          action="${pageContext.request.contextPath}/dossier-terminee/scanner"
                          enctype="multipart/form-data">
                        <input type="hidden" name="demandeId" value="<%= demandeId %>">
                        <input type="hidden" name="dossierId" value="<%= dossier.getId() %>">
                        <input type="file" name="scanFile" accept="application/pdf" required>
                        <button type="submit" class="btn-action">Scanner</button>
                    </form>
                    <% } %>
                </li>
                <% } %>
            </ul>
            <% } %>
        <% } %>
    </section>
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
    const openResidentButtons = document.querySelectorAll('.js-open-resident');
    const residentModal = document.getElementById('residentModal');
    const residentModalBody = document.getElementById('residentModalBody');
    const closeResidentModal = document.getElementById('closeResidentModal');
    const openHistoryButtons = document.querySelectorAll('.js-open-history');
    const historyModal = document.getElementById('historyModal');
    const historyModalBody = document.getElementById('historyModalBody');
    const closeHistoryModal = document.getElementById('closeHistoryModal');
    const openJustificatifButtons = document.querySelectorAll('.js-open-justificatifs');
    const justificatifModal = document.getElementById('justificatifModal');
    const justificatifModalBody = document.getElementById('justificatifModalBody');
    const closeJustificatifModal = document.getElementById('closeJustificatifModal');

    function normalize(value) {
        return (value || '').toString().trim().toLowerCase();
    }

    function openModalForDemande(demandeId) {
        const template = document.getElementById('resident-detail-template-' + demandeId);
        if (!template) {
            return;
        }

        residentModalBody.innerHTML = template.innerHTML;
        residentModal.classList.add('is-open');
        residentModal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('modal-open');
    }

    function closeModal() {
        residentModal.classList.remove('is-open');
        residentModal.setAttribute('aria-hidden', 'true');
        residentModalBody.innerHTML = '';
        document.body.classList.remove('modal-open');
    }

    function openHistoryModalForDemande(demandeId) {
        const template = document.getElementById('history-detail-template-' + demandeId);
        if (!template) {
            return;
        }

        historyModalBody.innerHTML = template.innerHTML;
        historyModal.classList.add('is-open');
        historyModal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('modal-open');
    }

    function closeHistoryModalView() {
        historyModal.classList.remove('is-open');
        historyModal.setAttribute('aria-hidden', 'true');
        historyModalBody.innerHTML = '';
        document.body.classList.remove('modal-open');
    }

    function openJustificatifModalForDemande(demandeId) {
        const template = document.getElementById('justificatif-detail-template-' + demandeId);
        if (!template) {
            return;
        }

        justificatifModalBody.innerHTML = template.innerHTML;
        justificatifModal.classList.add('is-open');
        justificatifModal.setAttribute('aria-hidden', 'false');
        document.body.classList.add('modal-open');
    }

    function closeJustificatifModalView() {
        justificatifModal.classList.remove('is-open');
        justificatifModal.setAttribute('aria-hidden', 'true');
        justificatifModalBody.innerHTML = '';
        document.body.classList.remove('modal-open');
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

    openResidentButtons.forEach((button) => {
        button.addEventListener('click', () => {
            openModalForDemande(button.dataset.demandeId);
        });
    });

    openHistoryButtons.forEach((button) => {
        button.addEventListener('click', () => {
            openHistoryModalForDemande(button.dataset.demandeId);
        });
    });

    openJustificatifButtons.forEach((button) => {
        button.addEventListener('click', () => {
            openJustificatifModalForDemande(button.dataset.demandeId);
        });
    });

    closeResidentModal.addEventListener('click', closeModal);

    residentModal.addEventListener('click', (event) => {
        if (event.target && event.target.getAttribute('data-close-modal') === 'true') {
            closeModal();
        }
    });

    closeHistoryModal.addEventListener('click', closeHistoryModalView);

    historyModal.addEventListener('click', (event) => {
        if (event.target && event.target.getAttribute('data-close-history-modal') === 'true') {
            closeHistoryModalView();
        }
    });

    closeJustificatifModal.addEventListener('click', closeJustificatifModalView);

    justificatifModal.addEventListener('click', (event) => {
        if (event.target && event.target.getAttribute('data-close-justificatif-modal') === 'true') {
            closeJustificatifModalView();
        }
    });

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && residentModal.classList.contains('is-open')) {
            closeModal();
        }
        if (event.key === 'Escape' && historyModal.classList.contains('is-open')) {
            closeHistoryModalView();
        }
        if (event.key === 'Escape' && justificatifModal.classList.contains('is-open')) {
            closeJustificatifModalView();
        }
    });

    applyFilters();

    const params = new URLSearchParams(window.location.search);
    const openModal = params.get('openModal');
    const demandeIdToOpen = params.get('demandeId');
    if (openModal === 'justificatif' && demandeIdToOpen) {
        openJustificatifModalForDemande(demandeIdToOpen);
    }
</script>
</body>
</html>
