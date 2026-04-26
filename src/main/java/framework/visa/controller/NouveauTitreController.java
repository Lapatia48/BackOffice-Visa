package framework.visa.controller;

import framework.visa.entity.CarteResident;
import framework.visa.entity.Demande;
import framework.visa.entity.DemandeDossier;
import framework.visa.entity.DemandeurVisaCarteResident;
import framework.visa.entity.Dossier;
import framework.visa.entity.HistoStatutDemande;
import framework.visa.entity.Nationalite;
import framework.visa.entity.SituationFamiliale;
import framework.visa.entity.StatutDemande;
import framework.visa.entity.TypeDemande;
import framework.visa.entity.Visa;
import framework.visa.service.DemandeDossierService;
import framework.visa.service.DemandeWorkflowService;
import framework.visa.service.DossierService;
import framework.visa.service.NationaliteService;
import framework.visa.service.SituationFamilialeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class NouveauTitreController {
    private static final DateTimeFormatter RESIDENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DossierService dossierService;
    private final DemandeWorkflowService demandeWorkflowService;
    private final DemandeDossierService demandeDossierService;
    private final SituationFamilialeService situationFamilialeService;
    private final NationaliteService nationaliteService;

    public NouveauTitreController(
            DossierService dossierService,
            DemandeWorkflowService demandeWorkflowService,
            DemandeDossierService demandeDossierService,
            SituationFamilialeService situationFamilialeService,
            NationaliteService nationaliteService) {
        this.dossierService = dossierService;
        this.demandeWorkflowService = demandeWorkflowService;
        this.demandeDossierService = demandeDossierService;
        this.situationFamilialeService = situationFamilialeService;
        this.nationaliteService = nationaliteService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/nouveau-titre")
    public String nouveauTitre(Model model) {
        List<TypeDemande> types = dossierService.findAvailableTypes();
        List<Dossier> commonDossiers = dossierService.findCommonDossiers();
        List<SituationFamiliale> situationsFamiliales = situationFamilialeService.findAll();
        List<Nationalite> nationalites = nationaliteService.findAll();

        Map<Integer, List<Dossier>> typedDossiers = new LinkedHashMap<>();
        for (TypeDemande type : types) {
            typedDossiers.put(type.getId(), dossierService.findDossiersByType(type.getId()));
        }

        model.addAttribute("types", types);
        model.addAttribute("commonDossiers", commonDossiers);
        model.addAttribute("typedDossiers", typedDossiers);
        model.addAttribute("situationsFamiliales", situationsFamiliales);
        model.addAttribute("nationalites", nationalites);
        return "nouveau-titre";
    }

    @PostMapping("/nouveau-titre")
    public String submitNouveauTitre(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam String lieuNaissance,
            @RequestParam Integer situationFamilialeId,
            @RequestParam Integer nationaliteId,
            @RequestParam String telephone,
            @RequestParam String email,
            @RequestParam String adresse,
            @RequestParam String numeroPasseport,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDelivrance,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateExpiration,
            @RequestParam String paysDelivrance,
            @RequestParam String referenceVisaTransformable,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateArriveeMadagascar,
            @RequestParam String lieuEntreeMadagascar,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDonnationVisaTransformable,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateExpirationVisaTransformable,
            @RequestParam Integer typeVisaId,
            @RequestParam(required = false) List<Integer> dossierIds,
            @RequestParam(required = false) String observations,
            RedirectAttributes redirectAttributes) {
        try {
                List<Dossier> requiredDossiers = dossierService.findCommonDossiers().stream()
                    .filter(Dossier::isObligatoire)
                    .toList();
                requiredDossiers = new java.util.ArrayList<>(requiredDossiers);
                requiredDossiers.addAll(
                    dossierService.findDossiersByType(typeVisaId).stream()
                        .filter(Dossier::isObligatoire)
                        .toList()
                );

                Set<Integer> selectedDossierIds = dossierIds == null ? Set.of() : new HashSet<>(dossierIds);
                boolean hasMissingRequiredDossier = requiredDossiers.stream()
                    .anyMatch(dossier -> !selectedDossierIds.contains(dossier.getId()));

                if (hasMissingRequiredDossier) {
                throw new IllegalArgumentException("Tous les dossiers obligatoires doivent etre coches.");
                }

            Integer demandeId = demandeWorkflowService.submitNouveauTitre(
                    nom,
                    prenom,
                    dateNaissance,
                    lieuNaissance,
                    situationFamilialeId,
                    nationaliteId,
                    telephone,
                    email,
                    adresse,
                    numeroPasseport,
                    dateDelivrance,
                    dateExpiration,
                    paysDelivrance,
                    referenceVisaTransformable,
                    dateArriveeMadagascar,
                    lieuEntreeMadagascar,
                    dateDonnationVisaTransformable,
                    dateExpirationVisaTransformable,
                    typeVisaId,
                    dossierIds,
                    observations
            );
            redirectAttributes.addFlashAttribute("message", "Demande #" + demandeId + " enregistree avec succes.");
            return "redirect:/dossiers-en-cours";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/nouveau-titre";
        }
    }

    @GetMapping("/dossiers-en-cours")
    public String dossierEnCours(Model model) {
        List<Demande> demandesEnCours = demandeDossierService.findDemandescreees();

        Map<Integer, Long> totalPiecesByDemande = new HashMap<>();
        Map<Integer, Long> providedPiecesByDemande = new HashMap<>();
        Map<Integer, Long> remainingPiecesByDemande = new HashMap<>();

        if (!demandesEnCours.isEmpty()) {
            List<Integer> demandeIds = demandesEnCours.stream()
                    .map(Demande::getId)
                    .toList();

            List<DemandeDossier> lignes = demandeDossierService.findByDemandeIds(demandeIds);
            for (DemandeDossier ligne : lignes) {
                Integer demandeId = ligne.getDemande().getId();
                totalPiecesByDemande.merge(demandeId, 1L, Long::sum);

                if (ligne.isEstFourni()) {
                    providedPiecesByDemande.merge(demandeId, 1L, Long::sum);
                } else {
                    remainingPiecesByDemande.merge(demandeId, 1L, Long::sum);
                }
            }
        }

        model.addAttribute("demandesEnCours", demandesEnCours);
        model.addAttribute("totalPiecesByDemande", totalPiecesByDemande);
        model.addAttribute("providedPiecesByDemande", providedPiecesByDemande);
        model.addAttribute("remainingPiecesByDemande", remainingPiecesByDemande);
        return "dossiers-en-cours";
    }

    @GetMapping("/dossier-terminee")
    public String dossierTerminee(Model model) {
        List<Demande> dossiersTerminees = demandeDossierService.findDossiersTermineesNouveauTitre();
        List<Integer> demandeurIds = dossiersTerminees.stream()
            .map(Demande::getDemandeur)
            .filter(java.util.Objects::nonNull)
            .map(demandeur -> demandeur.getId())
            .filter(java.util.Objects::nonNull)
            .toList();
        Map<Integer, DemandeurVisaCarteResident> residentLinkByDemandeurId =
            demandeDossierService.findResidentLinksByDemandeurIds(demandeurIds);
        Map<Integer, List<HistoStatutDemande>> historiquesByDemandeurId =
            demandeDossierService.findHistoriquesByDemandeurIds(demandeurIds);

        model.addAttribute("residentDetailsByDemande", buildResidentDetailsByDemande(dossiersTerminees, residentLinkByDemandeurId));
        model.addAttribute("historiquesByDemande", buildHistoriquesByDemande(dossiersTerminees, historiquesByDemandeurId));
        model.addAttribute("dossiersTerminees", dossiersTerminees);
        return "dossier-terminee";
    }

    private Map<Integer, Map<String, String>> buildResidentDetailsByDemande(
            List<Demande> dossiersTerminees,
            Map<Integer, DemandeurVisaCarteResident> residentLinkByDemandeurId) {
        Map<Integer, Map<String, String>> detailsByDemande = new HashMap<>();

        for (Demande demande : dossiersTerminees) {
            if (demande == null || demande.getId() == null) {
                continue;
            }

            Integer demandeurId = demande.getDemandeur() == null ? null : demande.getDemandeur().getId();
            DemandeurVisaCarteResident residentLink = demandeurId == null
                ? null
                : residentLinkByDemandeurId.get(demandeurId);
            Visa visa = residentLink == null ? null : residentLink.getVisa();
            CarteResident carteResident = residentLink == null ? null : residentLink.getCarteResident();
            Map<String, String> details = new LinkedHashMap<>();
            details.put("demandeur", buildDemandeurName(demande));
            details.put("numeroCarteResident", resolveText(carteResident == null ? null : carteResident.getNumero()));
            details.put("referenceVisa", resolveText(visa == null ? null : visa.getReference()));
            details.put("categorie", resolveText(
                    visa == null || visa.getCategorieVisa() == null
                            ? null
                            : visa.getCategorieVisa().getLibelle()
            ));
            details.put("typeDemande", resolveText(
                    demande.getTypeDemande() == null
                            ? null
                            : demande.getTypeDemande().getLibelle()
            ));
            details.put("statut", resolveText(
                    demande.getStatut() == null
                            ? null
                            : demande.getStatut().getLibelle()
            ));
                    details.put("dateDonnation", formatDate(carteResident == null ? null : carteResident.getDateDonnation()));
                    details.put("dateExpiration", formatDate(carteResident == null ? null : carteResident.getDateExpiration()));
                    details.put("duree", formatDuree(
                        carteResident == null ? null : carteResident.getDateDonnation(),
                        carteResident == null ? null : carteResident.getDateExpiration()
                    ));
            details.put("numeroPasseport", resolveText(
                    visa == null || visa.getPasseport() == null
                            ? null
                            : visa.getPasseport().getNumeroPasseport()
            ));

            detailsByDemande.put(demande.getId(), details);
        }

        return detailsByDemande;
    }

    private Map<Integer, List<Map<String, String>>> buildHistoriquesByDemande(
            List<Demande> dossiersTerminees,
            Map<Integer, List<HistoStatutDemande>> historiquesByDemandeurId) {
        Map<Integer, List<Map<String, String>>> detailsByDemande = new HashMap<>();

        for (Demande demande : dossiersTerminees) {
            if (demande == null || demande.getId() == null) {
                continue;
            }

            Integer demandeurId = demande.getDemandeur() == null ? null : demande.getDemandeur().getId();
            List<HistoStatutDemande> historiques = demandeurId == null
                ? List.of()
                : historiquesByDemandeurId.getOrDefault(demandeurId, List.of());

            List<Map<String, String>> items = new java.util.ArrayList<>();
            for (HistoStatutDemande historique : historiques) {
                Map<String, String> item = new LinkedHashMap<>();
                StatutDemande statut = historique.getStatut();
                Integer statutId = statut == null ? null : statut.getId();
                String statutLibelle = statut == null ? null : statut.getLibelle();

                item.put("date", historique.getDateChangement() == null
                    ? "Non renseignee"
                    : historique.getDateChangement().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                item.put("statutId", statutId == null ? "-" : String.valueOf(statutId));
                item.put("statutLabel", resolveStatutLabel(statutLibelle));
                item.put("commentaire", resolveText(historique.getCommentaire()));
                items.add(item);
            }

            detailsByDemande.put(demande.getId(), items);
        }

        return detailsByDemande;
    }

    private String resolveStatutLabel(String statutLibelle) {
        if (statutLibelle == null || statutLibelle.isBlank()) {
            return "Statut non renseigne";
        }

        String normalized = statutLibelle.trim().toLowerCase();
        if ("cree".equals(normalized) || "en_cours".equals(normalized)) {
            return "Dossier cree";
        }
        if ("terminee".equals(normalized)) {
            return "Dossier terminee";
        }

        return statutLibelle.trim();
    }

    private String buildDemandeurName(Demande demande) {
        String nom = demande.getDemandeur() == null ? null : demande.getDemandeur().getNom();
        String prenom = demande.getDemandeur() == null ? null : demande.getDemandeur().getPrenom();

        String fullName = ((nom == null ? "" : nom.trim()) + " " + (prenom == null ? "" : prenom.trim())).trim();
        return fullName.isEmpty() ? "Non renseignee" : fullName;
    }

    private String formatDate(LocalDate date) {
        return date == null ? "Non renseignee" : date.format(RESIDENT_DATE_FORMATTER);
    }

    private String formatDuree(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            return "Non renseignee";
        }
        if (dateFin.isBefore(dateDebut)) {
            return "Incoherente";
        }

        long totalJours = ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        return totalJours + " jour(s)";
    }

    private String resolveText(String value) {
        if (value == null || value.isBlank()) {
            return "Non renseignee";
        }
        return value.trim();
    }

    @GetMapping("/dossiers-en-cours/ajout")
    public String ajoutDossier(@RequestParam Integer demandeId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Demande demande = demandeDossierService.findDemandeById(demandeId)
                    .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));

            List<DemandeDossier> dossiersRestants = demandeDossierService.findByDemandeId(demandeId).stream()
                    .filter(demandeDossier -> !demandeDossier.isEstFourni())
                    .toList();

            if (dossiersRestants.isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Cette demande est deja complete.");
                return "redirect:/dossiers-en-cours";
            }

            model.addAttribute("demande", demande);
            model.addAttribute("passeport", demandeDossierService.findPasseportByDemandeId(demandeId).orElse(null));
            model.addAttribute("visaTransformable", demandeDossierService.findVisaTransformableByDemandeId(demandeId).orElse(null));
            model.addAttribute("dossiersRestants", dossiersRestants);
            model.addAttribute("situationsFamiliales", situationFamilialeService.findAll());
            model.addAttribute("nationalites", nationaliteService.findAll());
            return "ajout-dossier";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/dossiers-en-cours";
        }
    }

    @PostMapping("/dossiers-en-cours/ajout")
    public String submitAjoutDossier(
            @RequestParam Integer demandeId,
            @RequestParam(required = false) List<Integer> dossierIds,
            @RequestParam(defaultValue = "false") boolean editInformations,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateNaissance,
            @RequestParam(required = false) String lieuNaissance,
            @RequestParam(required = false) Integer situationFamilialeId,
            @RequestParam(required = false) Integer nationaliteId,
            @RequestParam(required = false) String telephone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) String numeroPasseport,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDelivrance,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateExpiration,
            @RequestParam(required = false) String paysDelivrance,
            @RequestParam(required = false) String referenceVisaTransformable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateArriveeMadagascar,
            @RequestParam(required = false) String lieuEntreeMadagascar,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDonnationVisaTransformable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateExpirationVisaTransformable,
            RedirectAttributes redirectAttributes) {
        try {
            demandeDossierService.completeMissingDossiers(
                    demandeId,
                    dossierIds,
                    editInformations,
                    nom,
                    prenom,
                    dateNaissance,
                    lieuNaissance,
                    situationFamilialeId,
                    nationaliteId,
                    telephone,
                    email,
                    adresse,
                    numeroPasseport,
                    dateDelivrance,
                    dateExpiration,
                        paysDelivrance,
                        referenceVisaTransformable,
                        dateArriveeMadagascar,
                        lieuEntreeMadagascar,
                        dateDonnationVisaTransformable,
                        dateExpirationVisaTransformable
            );

            if (editInformations) {
                redirectAttributes.addFlashAttribute("message", "Dossiers et informations mis a jour pour la demande #" + demandeId + ".");
            } else {
                redirectAttributes.addFlashAttribute("message", "Dossiers mis a jour pour la demande #" + demandeId + ".");
            }
            return "redirect:/dossiers-en-cours";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/dossiers-en-cours/ajout?demandeId=" + demandeId;
        }
    }

    @GetMapping("/duplicata")
    public String duplicata(Model model) {
        List<Nationalite> nationalites = nationaliteService.findAll();
        model.addAttribute("nationalites", nationalites);

        return "duplicata";
    }

    @GetMapping("/transfert-visa")
    public String transfertVisa(Model model) {
        List<Nationalite> nationalites = nationaliteService.findAll();
        model.addAttribute("nationalites", nationalites);
        return "transfert-visa";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
