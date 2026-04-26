<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Collections" %>
<%@ page import="framework.visa.entity.Nationalite" %>
<%@ page import="framework.visa.entity.SituationFamiliale" %>
<%@ page import="framework.visa.entity.TypeDemande" %>
<%@ page import="framework.visa.entity.Dossier" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>BackOffice Visa - Nouveau titre</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">

</head>
<body>
<%
    List<TypeDemande> types = (List<TypeDemande>) request.getAttribute("types");
    if (types == null) {
        types = Collections.emptyList();
    }

    List<Dossier> commonDossiers = (List<Dossier>) request.getAttribute("commonDossiers");
    if (commonDossiers == null) {
        commonDossiers = Collections.emptyList();
    }

    Map<Integer, List<Dossier>> typedDossiers = (Map<Integer, List<Dossier>>) request.getAttribute("typedDossiers");
    if (typedDossiers == null) {
        typedDossiers = Collections.emptyMap();
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
    Boolean modeDuplicata = (Boolean) request.getAttribute("modeDuplicata");
    if (modeDuplicata == null) {
        modeDuplicata = false;
    }

    Boolean modeTransfert = (Boolean) request.getAttribute("modeTransfert");
    if (modeTransfert == null) {
        modeTransfert = false;
    }

    String modeOperation = (String) request.getAttribute("modeOperation");
    if (modeOperation == null) {
        modeOperation = "";
    }

    String carteEtatLibelle = (String) request.getAttribute("carteEtatLibelle");
    if (carteEtatLibelle == null || carteEtatLibelle.isBlank()) {
        carteEtatLibelle = "nouveau titre";
    }

    List<Dossier> operationDossiers = (List<Dossier>) request.getAttribute("operationDossiers");
    if (operationDossiers == null) {
        operationDossiers = Collections.emptyList();
    }
%>

<h1>Formulaire - Nouveau titre</h1>

<div class="horizontal-sidebar">
    <a href="/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
</div>

<% if (error != null && !error.isEmpty()) { %>
<p class="error"><%= error %></p>
<% } %>

<% if (modeDuplicata) { %>
<p class="message">Mode duplicata active: la carte resident creee sera en etat "duplicata".</p>
<% } %>

<% if (modeTransfert) { %>
<p class="message">Mode transfert active: une demande transfert visa sera creee en parallele avec statut "cree".</p>
<% } %>

<form method="post" action="/nouveau-titre">
    <input type="hidden" name="carteEtatLibelle" value="<%= carteEtatLibelle %>">
    <input type="hidden" name="modeOperation" value="<%= modeOperation %>">

    <div class="gauche">
        <div class="bloc">
            <h2 class="section-title">Visa transformable</h2>
            <label>Reference visa
                <input type="text" name="referenceVisaTransformable" required>
            </label>
            <label>Date d'arrivee a Madagascar
                <input type="date" name="dateArriveeMadagascar" required>
            </label>
            <label>Lieu d'entree a Madagascar
                <input type="text" name="lieuEntreeMadagascar" required>
            </label>
            <label>Date de donnation
                <input type="date" name="dateDonnationVisaTransformable" required>
            </label>
            <label>Date d'expiration
                <input type="date" name="dateExpirationVisaTransformable" required>
            </label>
        </div>
        <div class="bloc">
            <h2 class="section-title">Type visa demandée</h2>
            <label>Selectionner un type
                <select id="typeVisaId" name="typeVisaId" required>
                    <option value="">-- choisir --</option>
                    <% for (TypeDemande type : types) { %>
                        <option value="<%= type.getId() %>"><%= type.getLibelle() %></option>
                    <% } %>
                </select>
            </label>
        </div>

        <div class="bloc">
            <h2 class="section-title">Pieces communes</h2>
            <% for (Dossier dossier : commonDossiers) { %>
                <label class="inline-checkbox">
                    <input type="checkbox" name="dossierIds" value="<%= dossier.getId() %>">
                    <%= dossier.getLibelle() %>
                    <% if (dossier.isObligatoire()) { %>
                        <span class="obligatoire">(obligatoire)</span>
                    <% } else { %>
                        <span class="optionnel">(optionnel)</span>
                    <% } %>
                </label>
            <% } %>
        </div>

        <div class="bloc">
            <h2 class="section-title">Pieces complementaires</h2>
            <% for (TypeDemande type : types) {
                List<Dossier> typeDossiers = typedDossiers.get(type.getId());
                if (typeDossiers == null) {
                    typeDossiers = Collections.emptyList();
                }
            %>
                <div class="typed-group" data-type-id="<%= type.getId() %>" style="display:none;">
                    <h3><%= type.getLibelle() %></h3>
                    <% for (Dossier dossier : typeDossiers) { %>
                        <label class="inline-checkbox">
                            <input type="checkbox" name="dossierIds" value="<%= dossier.getId() %>">
                            <%= dossier.getLibelle() %>
                            <% if (dossier.isObligatoire()) { %>
                                <span class="obligatoire">(obligatoire)</span>
                            <% } else { %>
                                <span class="optionnel">(optionnel)</span>
                            <% } %>
                        </label>
                    <% } %>
                </div>
            <% } %>
        </div>

        <% if (!operationDossiers.isEmpty()) { %>
        <div class="bloc">
            <h2 class="section-title">Pieces justificatives <%= modeOperation %></h2>
            <% for (Dossier dossier : operationDossiers) { %>
                <label class="inline-checkbox">
                    <input type="checkbox" name="operationDossierIds" value="<%= dossier.getId() %>">
                    <%= dossier.getLibelle() %>
                    <% if (dossier.isObligatoire()) { %>
                        <span class="obligatoire">(obligatoire)</span>
                    <% } else { %>
                        <span class="optionnel">(optionnel)</span>
                    <% } %>
                </label>
            <% } %>
        </div>
        <% } %>

        <div class="bloc">
            <button type="submit">Confirmer</button>
        </div>
    </div>

    <div class="droite">
        <div class="bloc">
            <h2 class="section-title">Demandeur</h2>
            <label>Nom
                <input type="text" name="nom" required>
            </label>
            <label>Prenom
                <input type="text" name="prenom" required>
            </label>
            <label>Date de naissance
                <input type="date" name="dateNaissance" required>
            </label>
            <label>Lieu de naissance
                <input type="text" name="lieuNaissance" required>
            </label>
            <label>Situation familiale
                <select name="situationFamilialeId" required>
                    <option value="">-- choisir --</option>
                    <% for (SituationFamiliale situation : situationsFamiliales) { %>
                        <option value="<%= situation.getId() %>"><%= situation.getLibelle() %></option>
                    <% } %>
                </select>
            </label>
            <label>Nationalite
                <select name="nationaliteId" required>
                    <option value="">-- choisir --</option>
                    <% for (Nationalite nationalite : nationalites) { %>
                        <option value="<%= nationalite.getId() %>"><%= nationalite.getLibelle() %></option>
                    <% } %>
                </select>
            </label>
            <label>Telephone
                <input type="text" name="telephone" required>
            </label>
            <label>Email
                <input type="email" name="email" required>
            </label>
            <label>Adresse
                <textarea name="adresse" required></textarea>
            </label>
        </div>

        <div class="bloc">
            <h2 class="section-title">Passeport</h2>
            <label>Numero passeport
                <input type="text" name="numeroPasseport" required>
            </label>
            <label>Date de delivrance
                <input type="date" name="dateDelivrance" required>
            </label>
            <label>Date d'expiration
                <input type="date" name="dateExpiration" required>
            </label>
            <label>Pays de delivrance
                <input type="text" name="paysDelivrance" required>
            </label>
        </div>
    </div>

</form>

<script>
    const typeSelect = document.getElementById('typeVisaId');
    const groups = document.querySelectorAll('.typed-group');

    function refreshTypedGroups() {
        const selected = typeSelect.value;
        groups.forEach((group) => {
            const visible = group.getAttribute('data-type-id') === selected;
            group.style.display = visible ? 'block' : 'none';
            if (!visible) {
                group.querySelectorAll('input[type="checkbox"]').forEach((cb) => {
                    cb.checked = false;
                });
            }
        });
    }

    typeSelect.addEventListener('change', refreshTypedGroups);
    refreshTypedGroups();
</script>
</body>
</html>
