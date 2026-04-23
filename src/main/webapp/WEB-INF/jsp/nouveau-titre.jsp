<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Collections" %>
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

    String error = (String) request.getAttribute("error");
%>

<h1>Formulaire - Nouveau titre</h1>

<div class="horizontal-sidebar">
    <a href="/">Retour accueil</a>
    <a href="#">Dossiers en cours</a>
</div>

<% if (error != null && !error.isEmpty()) { %>
<p class="error"><%= error %></p>
<% } %>

<form method="post" action="/nouveau-titre">

    <div class="gauche">
        <div class="bloc">
            <h2 class="section-title">Type visa</h2>
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
