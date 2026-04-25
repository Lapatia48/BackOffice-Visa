<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="framework.visa.entity.Demande" %>
<%@ page import="framework.visa.entity.DemandeDossier" %>
<%@ page import="framework.visa.entity.Nationalite" %>
<%@ page import="framework.visa.entity.Passeport" %>
<%@ page import="framework.visa.entity.SituationFamiliale" %>
<%@ page import="framework.visa.entity.VisaTransformable" %>
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
    Passeport passeport = (Passeport) request.getAttribute("passeport");
    VisaTransformable visaTransformable = (VisaTransformable) request.getAttribute("visaTransformable");

    List<DemandeDossier> dossiersRestants = (List<DemandeDossier>) request.getAttribute("dossiersRestants");
    if (dossiersRestants == null) {
        dossiersRestants = Collections.emptyList();
    }

    List<SituationFamiliale> situationsFamiliales = (List<SituationFamiliale>) request.getAttribute("situationsFamiliales");
    if (situationsFamiliales == null) {
        situationsFamiliales = Collections.emptyList();
    }

    List<Nationalite> nationalites = (List<Nationalite>) request.getAttribute("nationalites");
    if (nationalites == null) {
        nationalites = Collections.emptyList();
    }

    String error = (String) request.getAttribute("error");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>

<h1>Ajout dossier manquant</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Dossiers termines</a>
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
    <input type="hidden" id="editInformations" name="editInformations" value="false">

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
            <button id="submitButton" type="submit">
                Mettre a jour dossier
                <span id="submitInfosSuffix" class="submit-infos-suffix"> et informations</span>
            </button>
        </div>
    </div>

    <div class="droite">
        <div class="bloc">
            <div class="section-header-with-action">
                <h2 class="section-title">Demandeur (pre-rempli)</h2>
                <button type="button" id="toggleEditInfo" class="secondary-action">Modifier informations</button>
            </div>
            <label>Nom
                <input class="editable-info" type="text" name="nom" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getNom() %>" readonly>
            </label>
            <label>Prenom
                <input class="editable-info" type="text" name="prenom" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getPrenom() %>" readonly>
            </label>
            <label>Date de naissance
                <input class="editable-info" type="date" name="dateNaissance" value="<%= demande.getDemandeur() == null || demande.getDemandeur().getDateNaissance() == null ? "" : demande.getDemandeur().getDateNaissance() %>" readonly>
            </label>
            <label>Lieu de naissance
                <input class="editable-info" type="text" name="lieuNaissance" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getLieuNaissance() %>" readonly>
            </label>
            <label>Situation familiale
                <select class="editable-info" name="situationFamilialeId" disabled required>
                    <option value="">-- choisir --</option>
                    <% for (SituationFamiliale situation : situationsFamiliales) { %>
                        <option
                            value="<%= situation.getId() %>"
                            <%= demande.getDemandeur() != null
                                && demande.getDemandeur().getSituationFamiliale() != null
                                && situation.getId().equals(demande.getDemandeur().getSituationFamiliale().getId())
                                ? "selected"
                                : "" %>>
                            <%= situation.getLibelle() %>
                        </option>
                    <% } %>
                </select>
            </label>
            <label>Nationalite
                <select class="editable-info" name="nationaliteId" disabled required>
                    <option value="">-- choisir --</option>
                    <% for (Nationalite nationalite : nationalites) { %>
                        <option
                            value="<%= nationalite.getId() %>"
                            <%= demande.getDemandeur() != null
                                && demande.getDemandeur().getNationalite() != null
                                && nationalite.getId().equals(demande.getDemandeur().getNationalite().getId())
                                ? "selected"
                                : "" %>>
                            <%= nationalite.getLibelle() %>
                        </option>
                    <% } %>
                </select>
            </label>
            <label>Telephone
                <input class="editable-info" type="text" name="telephone" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getTelephone() %>" readonly>
            </label>
            <label>Email
                <input class="editable-info" type="email" name="email" value="<%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getEmail() %>" readonly>
            </label>
            <label>Adresse
                <textarea class="editable-info" name="adresse" readonly><%= demande.getDemandeur() == null ? "" : demande.getDemandeur().getAdresse() %></textarea>
            </label>
        </div>

        <div class="bloc">
            <h2 class="section-title">Passeport (pre-rempli)</h2>
            <label>Numero passeport
                <input class="editable-info" type="text" name="numeroPasseport" value="<%= passeport == null ? "" : passeport.getNumeroPasseport() %>" readonly>
            </label>
            <label>Date de delivrance
                <input class="editable-info" type="date" name="dateDelivrance" value="<%= passeport == null || passeport.getDateDelivrance() == null ? "" : passeport.getDateDelivrance() %>" readonly>
            </label>
            <label>Date d'expiration
                <input class="editable-info" type="date" name="dateExpiration" value="<%= passeport == null || passeport.getDateExpiration() == null ? "" : passeport.getDateExpiration() %>" readonly>
            </label>
            <label>Pays de delivrance
                <input class="editable-info" type="text" name="paysDelivrance" value="<%= passeport == null ? "" : passeport.getPaysDelivrance() %>" readonly>
            </label>
        </div>

        <div class="bloc">
            <h2 class="section-title">Visa transformable (pre-rempli)</h2>
            <label>Reference visa
                <input class="editable-info" type="text" name="referenceVisaTransformable" value="<%= visaTransformable == null ? "" : visaTransformable.getReference() %>" readonly>
            </label>
            <label>Date d'arrivee a Madagascar
                <input class="editable-info" type="date" name="dateArriveeMadagascar" value="<%= visaTransformable == null || visaTransformable.getDateArriveeMadagascar() == null ? "" : visaTransformable.getDateArriveeMadagascar() %>" readonly>
            </label>
            <label>Lieu d'entree a Madagascar
                <input class="editable-info" type="text" name="lieuEntreeMadagascar" value="<%= visaTransformable == null ? "" : visaTransformable.getLieuEntreeMadagascar() %>" readonly>
            </label>
            <label>Date de donnation
                <input class="editable-info" type="date" name="dateDonnationVisaTransformable" value="<%= visaTransformable == null || visaTransformable.getDateDonnation() == null ? "" : visaTransformable.getDateDonnation() %>" readonly>
            </label>
            <label>Date d'expiration
                <input class="editable-info" type="date" name="dateExpirationVisaTransformable" value="<%= visaTransformable == null || visaTransformable.getDateExpiration() == null ? "" : visaTransformable.getDateExpiration() %>" readonly>
            </label>
        </div>
    </div>
</form>

<script>
    const toggleEditInfoButton = document.getElementById('toggleEditInfo');
    const editInformationsInput = document.getElementById('editInformations');
    const editableInputs = Array.from(document.querySelectorAll('.editable-info'));
    const submitInfosSuffix = document.getElementById('submitInfosSuffix');

    let editEnabled = false;

    function setEditionState(enabled) {
        editEnabled = enabled;
        editInformationsInput.value = enabled ? 'true' : 'false';

        editableInputs.forEach((input) => {
            const isSelect = input.tagName === 'SELECT';
            if (enabled) {
                if (isSelect) {
                    input.removeAttribute('disabled');
                } else {
                    input.removeAttribute('readonly');
                }
                input.classList.add('editable-active');
            } else {
                if (isSelect) {
                    input.setAttribute('disabled', 'disabled');
                } else {
                    input.setAttribute('readonly', 'readonly');
                }
                input.classList.remove('editable-active');
            }
        });

        submitInfosSuffix.style.display = enabled ? 'inline' : 'none';
        toggleEditInfoButton.textContent = enabled
            ? 'Annuler modification informations'
            : 'Modifier informations';
    }

    toggleEditInfoButton.addEventListener('click', () => {
        setEditionState(!editEnabled);
    });

    setEditionState(false);
</script>
<% } %>
</body>
</html>
