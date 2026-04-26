<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="framework.visa.entity.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%
    String mode = (String) request.getAttribute("mode");
    Demande demandeNouveauTitre = (Demande) request.getAttribute("demandeNouveauTitre");
    Demande demandeOperation = (Demande) request.getAttribute("demandeOperation");
    Demandeur demandeur = (Demandeur) request.getAttribute("demandeur");
    Passeport passeport = (Passeport) request.getAttribute("passeport");
    VisaTransformable visaTransformable = (VisaTransformable) request.getAttribute("visaTransformable");
    Visa visa = (Visa) request.getAttribute("visa");
    CarteResident carte = (CarteResident) request.getAttribute("carte");

    List<DemandeDossier> demandeNouveauTitreDossiers =
        (List<DemandeDossier>) request.getAttribute("demandeNouveauTitreDossiers");
    if (demandeNouveauTitreDossiers == null) {
        demandeNouveauTitreDossiers = Collections.emptyList();
    }

    List<DemandeDossier> demandeOperationDossiers =
        (List<DemandeDossier>) request.getAttribute("demandeOperationDossiers");
    if (demandeOperationDossiers == null) {
        demandeOperationDossiers = Collections.emptyList();
    }

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    String titreOperation = "duplicata".equals(mode) ? "Duplicata" : "Transfert visa";
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Resultat sans donnees anterieures</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nouveau-titre.css">
</head>
<body>
<h1>Resultat - Sans donnees anterieures</h1>

<div class="horizontal-sidebar">
    <a href="${pageContext.request.contextPath}/">Retour accueil</a>
    <a href="${pageContext.request.contextPath}/nouveau-titre">Nouveau titre</a>
    <a href="${pageContext.request.contextPath}/dossiers-en-cours">Dossiers en cours</a>
    <a href="${pageContext.request.contextPath}/dossier-terminee">Nouveaux titres</a>
    <a href="${pageContext.request.contextPath}/duplicata">Duplicata</a>
    <a href="${pageContext.request.contextPath}/transfert-visa">Transfert visa</a>
</div>

<div class="bloc" style="max-width:960px;">
    <h2 class="section-title">Demandeur</h2>
    <p><strong>Nom</strong> <%= demandeur == null ? "N/A" : demandeur.getNom() %></p>
    <p><strong>Prenom</strong> <%= demandeur == null ? "N/A" : demandeur.getPrenom() %></p>
    <p><strong>Date de naissance</strong>
        <%= demandeur == null || demandeur.getDateNaissance() == null ? "N/A" : demandeur.getDateNaissance().format(dateFormatter) %>
    </p>

    <h2 class="section-title">Demande nouveau titre (validee/acceptee)</h2>
    <p><strong>ID demande</strong> <%= demandeNouveauTitre == null ? "N/A" : demandeNouveauTitre.getId() %></p>
    <p><strong>Statut</strong> <%= demandeNouveauTitre == null || demandeNouveauTitre.getStatut() == null ? "N/A" : demandeNouveauTitre.getStatut().getLibelle() %></p>
    <p><strong>Date demande</strong>
        <%= demandeNouveauTitre == null || demandeNouveauTitre.getDateDemande() == null ? "N/A" : demandeNouveauTitre.getDateDemande().format(dateFormatter) %>
    </p>

    <table>
        <thead>
        <tr>
            <th>Piece nouveau titre</th>
            <th>Obligatoire</th>
            <th>Fournie</th>
        </tr>
        </thead>
        <tbody>
        <% for (DemandeDossier dd : demandeNouveauTitreDossiers) { %>
            <tr>
                <td><%= dd.getDossier() == null ? "N/A" : dd.getDossier().getLibelle() %></td>
                <td><%= dd.getDossier() != null && dd.getDossier().isObligatoire() ? "Oui" : "Non" %></td>
                <td><%= dd.isEstFourni() ? "Oui" : "Non" %></td>
            </tr>
        <% } %>
        </tbody>
    </table>

    <h2 class="section-title">Demande <%= titreOperation %> (cree)</h2>
    <p><strong>ID demande</strong> <%= demandeOperation == null ? "N/A" : demandeOperation.getId() %></p>
    <p><strong>Type demande</strong>
        <%= demandeOperation == null || demandeOperation.getTypeDemande() == null ? "N/A" : demandeOperation.getTypeDemande().getLibelle() %>
    </p>
    <p><strong>Statut</strong>
        <%= demandeOperation == null || demandeOperation.getStatut() == null ? "N/A" : demandeOperation.getStatut().getLibelle() %>
    </p>
    <p><strong>Date demande</strong>
        <%= demandeOperation == null || demandeOperation.getDateDemande() == null ? "N/A" : demandeOperation.getDateDemande().format(dateFormatter) %>
    </p>

    <table>
        <thead>
        <tr>
            <th>Piece <%= titreOperation %></th>
            <th>Obligatoire</th>
            <th>Fournie</th>
        </tr>
        </thead>
        <tbody>
        <% for (DemandeDossier dd : demandeOperationDossiers) { %>
            <tr>
                <td><%= dd.getDossier() == null ? "N/A" : dd.getDossier().getLibelle() %></td>
                <td><%= dd.getDossier() != null && dd.getDossier().isObligatoire() ? "Oui" : "Non" %></td>
                <td><%= dd.isEstFourni() ? "Oui" : "Non" %></td>
            </tr>
        <% } %>
        </tbody>
    </table>

    <h2 class="section-title">Informations creees</h2>
    <p><strong>Numero carte resident</strong> <%= carte == null || carte.getNumero() == null ? "N/A" : carte.getNumero() %></p>
    <p><strong>Etat carte resident</strong>
        <%= carte == null || carte.getEtat() == null || carte.getEtat().getLibelle() == null ? "N/A" : carte.getEtat().getLibelle() %>
    </p>
    <p><strong>Reference visa cree</strong> <%= visa == null || visa.getReference() == null ? "N/A" : visa.getReference() %></p>
    <p><strong>Date debut visa</strong>
        <%= visa == null || visa.getDateDebut() == null ? "N/A" : visa.getDateDebut().format(dateFormatter) %>
    </p>
    <p><strong>Date fin visa</strong>
        <%= visa == null || visa.getDateFin() == null ? "N/A" : visa.getDateFin().format(dateFormatter) %>
    </p>

    <h2 class="section-title">Passeport et visa transformable saisis</h2>
    <p><strong>Numero passeport</strong>
        <%= passeport == null || passeport.getNumeroPasseport() == null ? "N/A" : passeport.getNumeroPasseport() %>
    </p>
    <p><strong>Pays delivrance passeport</strong>
        <%= passeport == null || passeport.getPaysDelivrance() == null ? "N/A" : passeport.getPaysDelivrance() %>
    </p>
    <p><strong>Reference visa transformable</strong>
        <%= visaTransformable == null || visaTransformable.getReference() == null ? "N/A" : visaTransformable.getReference() %>
    </p>
    <p><strong>Date expiration visa transformable</strong>
        <%= visaTransformable == null || visaTransformable.getDateExpiration() == null ? "N/A" : visaTransformable.getDateExpiration().format(dateFormatter) %>
    </p>
</div>

</body>
</html>
