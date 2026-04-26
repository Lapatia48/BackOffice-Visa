package framework.visa.service;

import framework.visa.config.VisaConfig;
import framework.visa.entity.CarteResident;
import framework.visa.entity.CategorieVisa;
import framework.visa.entity.Demande;
import framework.visa.entity.DemandeDossier;
import framework.visa.entity.Demandeur;
import framework.visa.entity.DemandeurVisaCarteResident;
import framework.visa.entity.HistoStatutDemande;
import framework.visa.entity.Passeport;
import framework.visa.entity.StatutDemande;
import framework.visa.entity.Visa;
import framework.visa.entity.VisaTransformable;
import framework.visa.repository.CarteResidentRepository;
import framework.visa.repository.CategorieVisaRepository;
import framework.visa.repository.DemandeRepository;
import framework.visa.repository.DemandeDossierRepository;
import framework.visa.repository.DemandeurRepository;
import framework.visa.repository.DemandeurVisaCarteResidentRepository;
import framework.visa.repository.HistoStatutDemandeRepository;
import framework.visa.repository.NationaliteRepository;
import framework.visa.repository.PasseportRepository;
import framework.visa.repository.SituationFamilialeRepository;
import framework.visa.repository.StatutDemandeRepository;
import framework.visa.repository.VisaRepository;
import framework.visa.repository.VisaTransformableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class DemandeDossierService {
    private static final String STATUS_TERMINEE = "terminee";
    private static final String CATEGORIE_NOUVEAU_TITRE = "nouveau_titre";
    private static final DateTimeFormatter REFERENCE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String CARTE_RESIDENT_PREFIX = "CR";

    private final DemandeDossierRepository repository;
    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository passeportRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final NationaliteRepository nationaliteRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final HistoStatutDemandeRepository histoStatutDemandeRepository;
    private final VisaRepository visaRepository;
    private final VisaTransformableRepository visaTransformableRepository;
    private final CarteResidentRepository carteResidentRepository;
    private final DemandeurVisaCarteResidentRepository demandeurVisaCarteResidentRepository;
    private final CategorieVisaRepository categorieVisaRepository;
    private final VisaConfig visaConfig;

    public DemandeDossierService(
            DemandeDossierRepository repository,
            DemandeRepository demandeRepository,
            DemandeurRepository demandeurRepository,
            PasseportRepository passeportRepository,
            SituationFamilialeRepository situationFamilialeRepository,
            NationaliteRepository nationaliteRepository,
            StatutDemandeRepository statutDemandeRepository,
            HistoStatutDemandeRepository histoStatutDemandeRepository,
            VisaRepository visaRepository,
            VisaTransformableRepository visaTransformableRepository,
            CarteResidentRepository carteResidentRepository,
            DemandeurVisaCarteResidentRepository demandeurVisaCarteResidentRepository,
            CategorieVisaRepository categorieVisaRepository,
            VisaConfig visaConfig) {
        this.repository = repository;
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
        this.passeportRepository = passeportRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.statutDemandeRepository = statutDemandeRepository;
        this.histoStatutDemandeRepository = histoStatutDemandeRepository;
        this.visaRepository = visaRepository;
        this.visaTransformableRepository = visaTransformableRepository;
        this.carteResidentRepository = carteResidentRepository;
        this.demandeurVisaCarteResidentRepository = demandeurVisaCarteResidentRepository;
        this.categorieVisaRepository = categorieVisaRepository;
        this.visaConfig = visaConfig;
    }

    public List<DemandeDossier> findAll() {
        return repository.findAll();
    }

    public List<Demande> findDemandescreees() {
        return demandeRepository.findDemandescreees();
    }

    public List<Demande> findDossiersTermineesNouveauTitre() {
        return demandeRepository.findDossiersTermineesNouveauTitre();
    }

    public Optional<Demande> findDemandeById(Integer demandeId) {
        return demandeRepository.findDetailedById(demandeId);
    }

    public Optional<Passeport> findPasseportByDemandeId(Integer demandeId) {
        return demandeRepository.findDetailedById(demandeId)
                .map(Demande::getDemandeur)
                .map(Demandeur::getId)
                .flatMap(passeportRepository::findFirstByDemandeurIdOrderByIdDesc);
    }

    public Optional<VisaTransformable> findVisaTransformableByDemandeId(Integer demandeId) {
        return demandeRepository.findDetailedById(demandeId)
                .map(Demande::getDemandeur)
                .map(Demandeur::getId)
                .flatMap(visaTransformableRepository::findFirstByDemandeurIdOrderByIdDesc);
    }

    public Map<Integer, CarteResident> findCarteResidentsByVisaIds(Collection<Integer> visaIds) {
        if (visaIds == null || visaIds.isEmpty()) {
            return Map.of();
        }

        Map<Integer, CarteResident> cartesByVisaId = new HashMap<>();
        List<DemandeurVisaCarteResident> links = demandeurVisaCarteResidentRepository.findWithDetailsByVisaIdIn(visaIds);
        for (DemandeurVisaCarteResident link : links) {
            if (link.getVisa() == null || link.getVisa().getId() == null || link.getCarteResident() == null) {
                continue;
            }
            cartesByVisaId.put(link.getVisa().getId(), link.getCarteResident());
        }

        return cartesByVisaId;
    }

    public Map<Integer, DemandeurVisaCarteResident> findResidentLinksByDemandeurIds(Collection<Integer> demandeurIds) {
        if (demandeurIds == null || demandeurIds.isEmpty()) {
            return Map.of();
        }

        Map<Integer, DemandeurVisaCarteResident> linkByDemandeurId = new HashMap<>();
        List<DemandeurVisaCarteResident> links = demandeurVisaCarteResidentRepository.findWithDetailsByDemandeurIdIn(demandeurIds);
        for (DemandeurVisaCarteResident link : links) {
            if (link.getDemandeur() == null || link.getDemandeur().getId() == null) {
                continue;
            }
            linkByDemandeurId.putIfAbsent(link.getDemandeur().getId(), link);
        }
        return linkByDemandeurId;
    }

    public Map<Integer, List<HistoStatutDemande>> findHistoriquesByDemandeurIds(Collection<Integer> demandeurIds) {
        if (demandeurIds == null || demandeurIds.isEmpty()) {
            return Map.of();
        }

        Map<Integer, List<HistoStatutDemande>> historiqueByDemandeurId = new HashMap<>();
        List<HistoStatutDemande> historiques = histoStatutDemandeRepository.findByDemandeurIds(demandeurIds);
        for (HistoStatutDemande historique : historiques) {
            Integer demandeurId = historique.getDemande() == null || historique.getDemande().getDemandeur() == null
                    ? null
                    : historique.getDemande().getDemandeur().getId();
            if (demandeurId == null) {
                continue;
            }

            historiqueByDemandeurId.computeIfAbsent(demandeurId, ignored -> new java.util.ArrayList<>())
                    .add(historique);
        }

        return historiqueByDemandeurId;
    }

    public List<DemandeDossier> findByDemandeId(Integer demandeId) {
        return repository.findByDemandeIdOrderByDossierLibelleAsc(demandeId);
    }

    public List<DemandeDossier> findByDemandeIds(Collection<Integer> demandeIds) {
        return repository.findByDemandeIdIn(demandeIds);
    }

    @Transactional
    public void completeMissingDossiers(
            Integer demandeId,
            List<Integer> dossierIdsToProvide,
            boolean updateInformations,
            String nom,
            String prenom,
            LocalDate dateNaissance,
            String lieuNaissance,
            Integer situationFamilialeId,
            Integer nationaliteId,
            String telephone,
            String email,
            String adresse,
            String numeroPasseport,
            LocalDate dateDelivrance,
            LocalDate dateExpiration,
            String paysDelivrance,
            String referenceVisaTransformable,
            LocalDate dateArriveeMadagascar,
            String lieuEntreeMadagascar,
            LocalDate dateDonnationVisaTransformable,
            LocalDate dateExpirationVisaTransformable) {
        Demande demande = demandeRepository.findDetailedById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));

        List<DemandeDossier> lignes = repository.findByDemandeIdOrderByDossierLibelleAsc(demandeId);
        if (lignes.isEmpty()) {
            throw new IllegalArgumentException("Aucun dossier rattache a cette demande.");
        }

        Set<Integer> remainingIds = new HashSet<>();
        for (DemandeDossier ligne : lignes) {
            if (!ligne.isEstFourni()) {
                remainingIds.add(ligne.getDossier().getId());
            }
        }

        if (remainingIds.isEmpty() && !updateInformations) {
            throw new IllegalArgumentException("Tous les dossiers sont deja fournis pour cette demande.");
        }

        Set<Integer> selectedIds = dossierIdsToProvide == null
                ? Set.of()
                : new HashSet<>(dossierIdsToProvide);

        if (selectedIds.isEmpty() && !updateInformations && !remainingIds.isEmpty()) {
            throw new IllegalArgumentException("Selectionnez au moins un dossier a marquer comme fourni.");
        }

        if (!remainingIds.containsAll(selectedIds)) {
            throw new IllegalArgumentException("Selection invalide de dossiers a fournir.");
        }

        List<DemandeDossier> dossiersAjoutes = new java.util.ArrayList<>();
        for (DemandeDossier ligne : lignes) {
            if (!ligne.isEstFourni() && selectedIds.contains(ligne.getDossier().getId())) {
                ligne.setEstFourni(true);
                ligne.setCommentaire("Piece ajoutee via ecran dossiers en cours.");
                dossiersAjoutes.add(ligne);
            }
        }

        if (!selectedIds.isEmpty()) {
            repository.saveAll(lignes);

            for (DemandeDossier dossierAjoute : dossiersAjoutes) {
                String nature = dossierAjoute.getDossier().isObligatoire() ? "obligatoire" : "optionnelle";
                appendHistorique(
                        demande,
                        "Ajout piece " + nature + " : " + dossierAjoute.getDossier().getLibelle() + "."
                );
            }
        }

        if (updateInformations) {
            updateDemandeurAndPasseport(
                    demande,
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
                    appendHistorique(demande, "Informations demandeur/passeport/visa transformable mises a jour.");
        }

        boolean hasMissing = lignes.stream().anyMatch(ligne -> !ligne.isEstFourni());
        boolean alreadyCompleted = demande.getStatut() != null
                && STATUS_TERMINEE.equalsIgnoreCase(demande.getStatut().getLibelle());

        if (!hasMissing && !alreadyCompleted) {
            StatutDemande complet = getOrCreateStatus(STATUS_TERMINEE);
            demande.setStatut(complet);
            issueVisaIfNeeded(demande);
            demandeRepository.save(demande);

            HistoStatutDemande historique = new HistoStatutDemande();
            historique.setDemande(demande);
            historique.setStatut(complet);
            historique.setDateChangement(LocalDateTime.now());
            historique.setCommentaire("Demande completee apres ajout de pieces manquantes.");
            histoStatutDemandeRepository.save(historique);
        } else if (!hasMissing) {
            issueVisaIfNeeded(demande);
            demandeRepository.save(demande);
        }
    }

    private void appendHistorique(Demande demande, String commentaire) {
        if (commentaire == null || commentaire.isBlank()) {
            return;
        }

        HistoStatutDemande historique = new HistoStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(demande.getStatut() != null ? demande.getStatut() : getOrCreateStatus("cree"));
        historique.setDateChangement(LocalDateTime.now());
        historique.setCommentaire(commentaire);
        histoStatutDemandeRepository.save(historique);
    }

    private void issueVisaIfNeeded(Demande demande) {
        Visa visa = demande.getVisa();
        if (visa == null) {
            visa = createVisaForNouveauTitre(demande);
            demande.setVisa(visa);
        }

        createCarteResidentIfNeeded(demande, visa);
    }

    private Visa createVisaForNouveauTitre(Demande demande) {
        Demandeur demandeur = demande.getDemandeur();
        if (demandeur == null || demandeur.getId() == null) {
            throw new IllegalArgumentException("Impossible de creer le visa: demandeur introuvable.");
        }

        Passeport passeport = passeportRepository.findFirstByDemandeurIdOrderByIdDesc(demandeur.getId())
                .orElseThrow(() -> new IllegalArgumentException("Impossible de creer le visa: passeport introuvable."));

        CategorieVisa categorieVisa = resolveNouveauTitreCategorie();

        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = dateDebut.plusMonths(visaConfig.getDureeVisaMois());

        Visa visa = new Visa();
        visa.setReference(buildVisaReference(demande.getId()));
        visa.setDateDebut(dateDebut);
        visa.setDateFin(dateFin);
        visa.setCategorieVisa(categorieVisa);
        visa.setPasseport(passeport);
        return visaRepository.save(visa);
    }

    private CategorieVisa resolveNouveauTitreCategorie() {
        return categorieVisaRepository.findFirstByLibelleIgnoreCase(CATEGORIE_NOUVEAU_TITRE)
                .or(() -> categorieVisaRepository.findFirstByLibelleIgnoreCase("nouveau titre"))
                .orElseGet(() -> {
                    CategorieVisa categorieVisa = new CategorieVisa();
                    categorieVisa.setLibelle(CATEGORIE_NOUVEAU_TITRE);
                    return categorieVisaRepository.save(categorieVisa);
                });
    }

    private String buildVisaReference(Integer demandeId) {
        String timestamp = LocalDateTime.now().format(REFERENCE_TIMESTAMP_FORMATTER);
        return "VISA-NT-" + demandeId + "-" + timestamp;
    }

    private void createCarteResidentIfNeeded(Demande demande, Visa visa) {
        Demandeur demandeur = demande.getDemandeur();
        if (demandeur == null || demandeur.getId() == null || visa == null || visa.getId() == null) {
            return;
        }

        boolean hasCarteResident = demandeurVisaCarteResidentRepository
            .findFirstWithDetailsByVisaId(visa.getId())
                .isPresent();
        if (hasCarteResident) {
            return;
        }

        CarteResident carteResident = new CarteResident();
        carteResident.setNumero(generateNextCarteResidentNumero());
        carteResident.setDateDonnation(visa.getDateDebut() == null ? LocalDate.now() : visa.getDateDebut());
        carteResident.setDateExpiration(visa.getDateFin() == null ? LocalDate.now() : visa.getDateFin());
        carteResident = carteResidentRepository.save(carteResident);

        DemandeurVisaCarteResident link = new DemandeurVisaCarteResident();
        link.setDemandeur(demandeur);
        link.setVisa(visa);
        link.setCarteResident(carteResident);
        demandeurVisaCarteResidentRepository.save(link);

        appendHistorique(demande, "Carte resident creee : " + carteResident.getNumero() + ".");
    }

    private String generateNextCarteResidentNumero() {
        int nextNumber = carteResidentRepository.findFirstByOrderByIdDesc()
                .map(CarteResident::getNumero)
                .map(this::extractNumeroSequence)
                .orElse(0) + 1;
        return CARTE_RESIDENT_PREFIX + String.format("%04d", nextNumber);
    }

    private int extractNumeroSequence(String numero) {
        if (numero == null || numero.isBlank()) {
            return 0;
        }

        String digits = numero.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private void updateDemandeurAndPasseport(
            Demande demande,
            String nom,
            String prenom,
            LocalDate dateNaissance,
            String lieuNaissance,
            Integer situationFamilialeId,
            Integer nationaliteId,
            String telephone,
            String email,
            String adresse,
            String numeroPasseport,
            LocalDate dateDelivrance,
            LocalDate dateExpiration,
            String paysDelivrance,
            String referenceVisaTransformable,
            LocalDate dateArriveeMadagascar,
            String lieuEntreeMadagascar,
            LocalDate dateDonnationVisaTransformable,
            LocalDate dateExpirationVisaTransformable) {
        Demandeur demandeur = demande.getDemandeur();
        if (demandeur == null) {
            throw new IllegalArgumentException("Demandeur introuvable pour cette demande.");
        }

        demandeur.setNom(requireNonBlank(nom, "Nom"));
        demandeur.setPrenom(requireNonBlank(prenom, "Prenom"));
        demandeur.setDateNaissance(requireDate(dateNaissance, "Date de naissance"));
        demandeur.setLieuNaissance(requireNonBlank(lieuNaissance, "Lieu de naissance"));
        demandeur.setSituationFamiliale(situationFamilialeRepository.findById(requireId(situationFamilialeId, "Situation familiale"))
            .orElseThrow(() -> new IllegalArgumentException("Situation familiale introuvable.")));
        demandeur.setNationalite(nationaliteRepository.findById(requireId(nationaliteId, "Nationalite"))
            .orElseThrow(() -> new IllegalArgumentException("Nationalite introuvable.")));
        demandeur.setTelephone(requireNonBlank(telephone, "Telephone"));
        demandeur.setEmail(requireNonBlank(email, "Email"));
        demandeur.setAdresse(requireNonBlank(adresse, "Adresse"));
        demandeur.setUpdatedAt(LocalDateTime.now());
        demandeurRepository.save(demandeur);

        boolean hasPasseportInput = hasText(numeroPasseport)
            || dateDelivrance != null
            || dateExpiration != null
            || hasText(paysDelivrance);

        if (hasPasseportInput) {
            Passeport passeport = passeportRepository.findFirstByDemandeurIdOrderByIdDesc(demandeur.getId())
                .orElseGet(() -> {
                Passeport nouveauPasseport = new Passeport();
                nouveauPasseport.setDemandeur(demandeur);
                return nouveauPasseport;
                });

            passeport.setNumeroPasseport(requireNonBlank(numeroPasseport, "Numero passeport"));
            passeport.setDateDelivrance(requireDate(dateDelivrance, "Date de delivrance"));
            passeport.setDateExpiration(requireDate(dateExpiration, "Date d'expiration"));
            passeport.setPaysDelivrance(requireNonBlank(paysDelivrance, "Pays de delivrance"));
            passeportRepository.save(passeport);
        }

        boolean hasVisaTransformableInput = hasText(referenceVisaTransformable)
            || dateArriveeMadagascar != null
            || hasText(lieuEntreeMadagascar)
            || dateDonnationVisaTransformable != null
            || dateExpirationVisaTransformable != null;

        if (hasVisaTransformableInput) {
            LocalDate expirationVisaTransformable = requireDate(
                    dateExpirationVisaTransformable,
                    "Date d'expiration visa transformable"
            );
            validateVisaTransformableExpiration(expirationVisaTransformable);

            VisaTransformable visaTransformable = visaTransformableRepository
                    .findFirstByDemandeurIdOrderByIdDesc(demandeur.getId())
                    .orElseGet(() -> {
                        VisaTransformable nouveauVisaTransformable = new VisaTransformable();
                        nouveauVisaTransformable.setDemandeur(demandeur);
                        return nouveauVisaTransformable;
                    });

            visaTransformable.setReference(requireNonBlank(referenceVisaTransformable, "Reference visa transformable"));
            visaTransformable.setDateArriveeMadagascar(requireDate(dateArriveeMadagascar, "Date d'arrivee a Madagascar"));
            visaTransformable.setLieuEntreeMadagascar(requireNonBlank(lieuEntreeMadagascar, "Lieu d'entree a Madagascar"));
            visaTransformable.setDateDonnation(requireDate(dateDonnationVisaTransformable, "Date de donnation visa transformable"));
            visaTransformable.setDateExpiration(expirationVisaTransformable);
            visaTransformableRepository.save(visaTransformable);
        }
    }

    private void validateVisaTransformableExpiration(LocalDate dateExpirationVisaTransformable) {
        if (dateExpirationVisaTransformable.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La demande doit etre faite avant la fin de validite du visa transformable.");
        }
    }

    private String requireNonBlank(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " est obligatoire.");
        }
        return value.trim();
    }

    private LocalDate requireDate(LocalDate value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " est obligatoire.");
        }
        return value;
    }

    private Integer requireId(Integer value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " est obligatoire.");
        }
        return value;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private StatutDemande getOrCreateStatus(String libelle) {
        return statutDemandeRepository.findFirstByLibelleIgnoreCase(libelle)
                .orElseGet(() -> {
                    StatutDemande statut = new StatutDemande();
                    statut.setLibelle(libelle);
                    return statutDemandeRepository.save(statut);
                });
    }
}
