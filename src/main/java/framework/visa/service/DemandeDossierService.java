package framework.visa.service;

import framework.visa.config.VisaConfig;
import framework.visa.entity.CarteResident;
import framework.visa.entity.CategorieVisa;
import framework.visa.entity.Demande;
import framework.visa.entity.DemandeDossier;
import framework.visa.entity.DemandeDossierScan;
import framework.visa.entity.Demandeur;
import framework.visa.entity.DemandeurPhotoSignature;
import framework.visa.entity.DemandeurVisaCarteResident;
import framework.visa.entity.Etat;
import framework.visa.entity.HistoStatutDemande;
import framework.visa.entity.Passeport;
import framework.visa.entity.Sexe;
import framework.visa.entity.StatutDemande;
import framework.visa.entity.Visa;
import framework.visa.entity.VisaTransformable;
import framework.visa.repository.CarteResidentRepository;
import framework.visa.repository.CategorieVisaRepository;
import framework.visa.repository.DemandeRepository;
import framework.visa.repository.DemandeDossierRepository;
import framework.visa.repository.DemandeDossierScanRepository;
import framework.visa.repository.DemandeurRepository;
import framework.visa.repository.DemandeurPhotoSignatureRepository;
import framework.visa.repository.DemandeurVisaCarteResidentRepository;
import framework.visa.repository.EtatRepository;
import framework.visa.repository.HistoStatutDemandeRepository;
import framework.visa.repository.NationaliteRepository;
import framework.visa.repository.PasseportRepository;
import framework.visa.repository.SituationFamilialeRepository;
import framework.visa.repository.SexeRepository;
import framework.visa.repository.StatutDemandeRepository;
import framework.visa.repository.VisaRepository;
import framework.visa.repository.VisaTransformableRepository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    private static final String STATUS_SCANNEE = "scanne";
    private static final String STATUS_PHOTO_SIGNATURE_TERMINEES = "photo et signature termines";
    private static final String CATEGORIE_NOUVEAU_TITRE = "nouveau_titre";
    private static final String ETAT_NOUVEAU_TITRE = "nouveau titre";
    private static final DateTimeFormatter REFERENCE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String CARTE_RESIDENT_PREFIX = "CR";

    private final DemandeDossierRepository repository;
    private final DemandeDossierScanRepository demandeDossierScanRepository;
    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository passeportRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final SexeRepository sexeRepository;
    private final DemandeurPhotoSignatureRepository demandeurPhotoSignatureRepository;
    private final NationaliteRepository nationaliteRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final HistoStatutDemandeRepository histoStatutDemandeRepository;
    private final VisaRepository visaRepository;
    private final VisaTransformableRepository visaTransformableRepository;
    private final CarteResidentRepository carteResidentRepository;
    private final DemandeurVisaCarteResidentRepository demandeurVisaCarteResidentRepository;
    private final EtatRepository etatRepository;
    private final CategorieVisaRepository categorieVisaRepository;
    private final VisaConfig visaConfig;

    public DemandeDossierService(
            DemandeDossierRepository repository,
            DemandeDossierScanRepository demandeDossierScanRepository,
            DemandeRepository demandeRepository,
            DemandeurRepository demandeurRepository,
            PasseportRepository passeportRepository,
            SituationFamilialeRepository situationFamilialeRepository,
            SexeRepository sexeRepository,
            DemandeurPhotoSignatureRepository demandeurPhotoSignatureRepository,
            NationaliteRepository nationaliteRepository,
            StatutDemandeRepository statutDemandeRepository,
            HistoStatutDemandeRepository histoStatutDemandeRepository,
            VisaRepository visaRepository,
            VisaTransformableRepository visaTransformableRepository,
            CarteResidentRepository carteResidentRepository,
            DemandeurVisaCarteResidentRepository demandeurVisaCarteResidentRepository,
            EtatRepository etatRepository,
            CategorieVisaRepository categorieVisaRepository,
            VisaConfig visaConfig) {
        this.repository = repository;
        this.demandeDossierScanRepository = demandeDossierScanRepository;
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
        this.passeportRepository = passeportRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.sexeRepository = sexeRepository;
        this.demandeurPhotoSignatureRepository = demandeurPhotoSignatureRepository;
        this.nationaliteRepository = nationaliteRepository;
        this.statutDemandeRepository = statutDemandeRepository;
        this.histoStatutDemandeRepository = histoStatutDemandeRepository;
        this.visaRepository = visaRepository;
        this.visaTransformableRepository = visaTransformableRepository;
        this.carteResidentRepository = carteResidentRepository;
        this.demandeurVisaCarteResidentRepository = demandeurVisaCarteResidentRepository;
        this.etatRepository = etatRepository;
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

    public List<Demande> findDemandesByStatut(String statutLibelle) {
        return demandeRepository.findByStatutLibelle(statutLibelle);
    }

    public List<Demande> findAllDemandes() {
        return demandeRepository.findAllDemandes();
    }

    public Optional<Demande> findDemandeById(Integer demandeId) {
        return demandeRepository.findDetailedById(demandeId);
    }

    public Optional<Demande> findLatestDemandeByDemandeurId(Integer demandeurId) {
        if (demandeurId == null) {
            return Optional.empty();
        }

        List<Demande> rows = demandeRepository.findDetailedByDemandeurIdOrderByIdDesc(demandeurId);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public List<Demande> findDemandesByDemandeurId(Integer demandeurId) {
        if (demandeurId == null) {
            return List.of();
        }

        return demandeRepository.findDetailedByDemandeurIdOrderByIdDesc(demandeurId);
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

    public Map<Integer, Map<Integer, String>> findScanFileNamesByDemandeIds(Collection<Integer> demandeIds) {
        if (demandeIds == null || demandeIds.isEmpty()) {
            return Map.of();
        }

        Map<Integer, Map<Integer, String>> scanFilesByDemande = new HashMap<>();
        List<DemandeDossierScan> scans = demandeDossierScanRepository.findByDemandeIds(demandeIds);
        for (DemandeDossierScan scan : scans) {
            if (scan == null || scan.getDemandeDossier() == null) {
                continue;
            }
            DemandeDossier ligne = scan.getDemandeDossier();
            Integer demandeId = ligne.getDemande() == null ? null : ligne.getDemande().getId();
            Integer dossierId = ligne.getDossier() == null ? null : ligne.getDossier().getId();
            if (demandeId == null || dossierId == null) {
                continue;
            }

            scanFilesByDemande
                .computeIfAbsent(demandeId, ignored -> new HashMap<>())
                .put(dossierId, scan.getCheminFichierAbsolu());
        }
        return scanFilesByDemande;
    }

    @Transactional
    public void appendActionHistoriqueByDemandeurId(Integer demandeurId, String commentaire) {
        if (demandeurId == null || commentaire == null || commentaire.isBlank()) {
            return;
        }

        Demande demande = findLatestDemandeByDemandeurId(demandeurId).orElse(null);
        if (demande == null) {
            return;
        }

        appendHistorique(demande, commentaire);
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
            Integer sexeId,
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
        String statutActuel = demande.getStatut() == null || demande.getStatut().getLibelle() == null
            ? ""
            : demande.getStatut().getLibelle().trim();

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
                    sexeId,
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
        boolean alreadyScanne = STATUS_SCANNEE.equalsIgnoreCase(statutActuel);

        if (!hasMissing && !alreadyScanne) {
            Integer demandeurId = demande.getDemandeur() == null ? null : demande.getDemandeur().getId();
            boolean hasCompleteMedia = hasCompletePhotoSignature(demandeurId);
            StatutDemande targetStatus = getOrCreateStatus(
                    hasCompleteMedia ? STATUS_PHOTO_SIGNATURE_TERMINEES : STATUS_TERMINEE
            );
            boolean statusChanged = demande.getStatut() == null
                    || demande.getStatut().getLibelle() == null
                    || !targetStatus.getLibelle().equalsIgnoreCase(demande.getStatut().getLibelle());
            if (statusChanged) {
                demande.setStatut(targetStatus);
            }
            issueVisaIfNeeded(demande);
            demandeRepository.save(demande);

            if (statusChanged) {
                HistoStatutDemande historique = new HistoStatutDemande();
                historique.setDemande(demande);
                historique.setStatut(targetStatus);
                historique.setDateChangement(LocalDateTime.now());
                historique.setCommentaire(hasCompleteMedia
                        ? "Demande completee avec photo et signature terminees."
                        : "Demande completee apres ajout de pieces manquantes.");
                histoStatutDemandeRepository.save(historique);
            }
        } else if (!hasMissing) {
            issueVisaIfNeeded(demande);
            demandeRepository.save(demande);
        }
    }

    @Transactional
    public void uploadDossierScan(Integer demandeId, Integer dossierId, MultipartFile scanFile) {
        if (demandeId == null) {
            throw new IllegalArgumentException("Demande introuvable.");
        }
        if (dossierId == null) {
            throw new IllegalArgumentException("Dossier introuvable.");
        }
        if (scanFile == null || scanFile.isEmpty()) {
            throw new IllegalArgumentException("Le fichier PDF est obligatoire.");
        }
        if (!isPdf(scanFile)) {
            throw new IllegalArgumentException("Seuls les fichiers PDF sont autorises.");
        }

        Demande demande = demandeRepository.findDetailedById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));
        String statut = demande.getStatut() == null || demande.getStatut().getLibelle() == null
                ? ""
                : demande.getStatut().getLibelle().trim();
        if (STATUS_SCANNEE.equalsIgnoreCase(statut)) {
            throw new IllegalArgumentException("Cette demande est deja scannee et n'est plus modifiable.");
        }
        if (!STATUS_TERMINEE.equalsIgnoreCase(statut)
                && !STATUS_PHOTO_SIGNATURE_TERMINEES.equalsIgnoreCase(statut)) {
            throw new IllegalArgumentException("Seules les demandes terminees ou photo et signature termines peuvent etre scannees.");
        }

        DemandeDossier ligne = repository.findFirstByDemandeIdAndDossierId(demandeId, dossierId)
                .orElseThrow(() -> new IllegalArgumentException("Le dossier selectionne n'appartient pas a cette demande."));

        DemandeDossierScan scan = demandeDossierScanRepository.findByDemandeDossierId(ligne.getId())
                .orElseGet(DemandeDossierScan::new);
        scan.setDemandeDossier(ligne);
        String nomFichier = sanitizeFileName(scanFile.getOriginalFilename());
        Path cheminFichier = resolveScanFilePath(demandeId, dossierId, nomFichier);
        try {
            Files.createDirectories(cheminFichier.getParent());
            try (InputStream inputStream = scanFile.getInputStream()) {
                Files.copy(inputStream, cheminFichier, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalArgumentException("Impossible d'enregistrer le fichier PDF sur disque.");
        }
        scan.setCheminFichierAbsolu(cheminFichier.toAbsolutePath().toString());
        scan.setNomFichier(nomFichier);
        scan.setTypeMime(scanFile.getContentType());
        scan.setDateScan(LocalDateTime.now());
        demandeDossierScanRepository.save(scan);

        appendHistorique(demande, "Scan de piece justificative : " + ligne.getDossier().getLibelle() + ".");

        List<DemandeDossier> lignes = repository.findByDemandeIdOrderByDossierLibelleAsc(demandeId);
        Set<Integer> scannedDossierIds = new HashSet<>(demandeDossierScanRepository.findScannedDossierIdsByDemandeId(demandeId));
        boolean allScanned = !lignes.isEmpty() && lignes.stream()
            .map(ligneDemande -> ligneDemande.getDossier() == null ? null : ligneDemande.getDossier().getId())
            .filter(java.util.Objects::nonNull)
            .allMatch(scannedDossierIds::contains);
        if (allScanned) {
            Integer demandeurId = demande.getDemandeur() == null ? null : demande.getDemandeur().getId();
            if (!hasCompletePhotoSignature(demandeurId)) {
                throw new IllegalArgumentException("La demande ne peut pas passer au statut scanne sans photo et signature.");
            }

            reconcileScanneStatus(demandeId);
        }
    }

    @Transactional
    public void reconcileScanneStatus(Integer demandeId) {
        if (demandeId == null) {
            throw new IllegalArgumentException("Demande introuvable.");
        }

        Demande demande = demandeRepository.findDetailedById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));
        String statut = demande.getStatut() == null || demande.getStatut().getLibelle() == null
                ? ""
                : demande.getStatut().getLibelle().trim();
        if (STATUS_SCANNEE.equalsIgnoreCase(statut)) {
            return;
        }

        Integer demandeurId = demande.getDemandeur() == null ? null : demande.getDemandeur().getId();
        if (!hasCompletePhotoSignature(demandeurId)) {
            return;
        }

        List<DemandeDossier> lignes = repository.findByDemandeIdOrderByDossierLibelleAsc(demandeId);
        if (lignes.isEmpty()) {
            return;
        }

        Set<Integer> scannedDossierIds = new HashSet<>(demandeDossierScanRepository.findScannedDossierIdsByDemandeId(demandeId));
        boolean allScanned = lignes.stream()
                .map(ligne -> ligne.getDossier() == null ? null : ligne.getDossier().getId())
                .filter(java.util.Objects::nonNull)
                .allMatch(scannedDossierIds::contains);
        if (!allScanned) {
            return;
        }

        StatutDemande scanne = getOrCreateStatus(STATUS_SCANNEE);
        demande.setStatut(scanne);
        demandeRepository.save(demande);

        HistoStatutDemande historique = new HistoStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(scanne);
        historique.setDateChangement(LocalDateTime.now());
        historique.setCommentaire("Tous les scans des pieces justificatives sont completes.");
        histoStatutDemandeRepository.save(historique);
    }

    @Transactional
    public void markPhotoSignatureCompleted(Integer demandeId) {
        if (demandeId == null) {
            throw new IllegalArgumentException("Demande introuvable.");
        }

        Demande demande = demandeRepository.findDetailedById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));

        String statut = demande.getStatut() == null || demande.getStatut().getLibelle() == null
                ? ""
                : demande.getStatut().getLibelle().trim();
        if (STATUS_SCANNEE.equalsIgnoreCase(statut)) {
            return;
        }

        boolean allScanned = areAllDossiersScanned(demandeId);
        if (allScanned) {
            reconcileScanneStatus(demandeId);
            return;
        }

        Integer demandeurId = demande.getDemandeur() == null ? null : demande.getDemandeur().getId();
        if (!hasCompletePhotoSignature(demandeurId)) {
            return;
        }

        StatutDemande photoSignatureTerminees = getOrCreateStatus(STATUS_PHOTO_SIGNATURE_TERMINEES);
        demande.setStatut(photoSignatureTerminees);
        demandeRepository.save(demande);

        HistoStatutDemande historique = new HistoStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(photoSignatureTerminees);
        historique.setDateChangement(LocalDateTime.now());
        historique.setCommentaire("Photo et signature completes.");
        histoStatutDemandeRepository.save(historique);
    }

    private boolean areAllDossiersScanned(Integer demandeId) {
        List<DemandeDossier> lignes = repository.findByDemandeIdOrderByDossierLibelleAsc(demandeId);
        if (lignes.isEmpty()) {
            return false;
        }

        Set<Integer> scannedDossierIds = new HashSet<>(demandeDossierScanRepository.findScannedDossierIdsByDemandeId(demandeId));
        return lignes.stream()
                .map(ligne -> ligne.getDossier() == null ? null : ligne.getDossier().getId())
                .filter(java.util.Objects::nonNull)
                .allMatch(scannedDossierIds::contains);
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
        carteResident.setDemandeur(demandeur);
        carteResident.setEtat(resolveEtat(ETAT_NOUVEAU_TITRE));
        carteResident = carteResidentRepository.save(carteResident);

        DemandeurVisaCarteResident link = new DemandeurVisaCarteResident();
        link.setDemandeur(demandeur);
        link.setVisa(visa);
        link.setCarteResident(carteResident);
        demandeurVisaCarteResidentRepository.save(link);

        appendHistorique(demande, "Carte resident creee : " + carteResident.getNumero() + ".");
    }

    private Etat resolveEtat(String libelle) {
        return etatRepository.findFirstByLibelleIgnoreCase(libelle)
                .orElseGet(() -> {
                    Etat etat = new Etat();
                    etat.setLibelle(libelle);
                    return etatRepository.save(etat);
                });
    }

    public String generateNextCarteResidentNumero() {
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
            Integer sexeId,
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
        Sexe sexe = sexeRepository.findById(requireId(sexeId, "Sexe"))
            .orElseThrow(() -> new IllegalArgumentException("Sexe introuvable."));
        demandeur.setSexe(sexe);
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

    private boolean hasCompletePhotoSignature(Integer demandeurId) {
        if (demandeurId == null) {
            return false;
        }

        return demandeurPhotoSignatureRepository.findByDemandeurId(demandeurId)
                .map(media -> hasText(media.getPhoto()) && hasText(media.getSignature()))
                .orElse(false);
    }

    private boolean isPdf(MultipartFile file) {
        if (file == null) {
            return false;
        }
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        boolean mimePdf = contentType != null && contentType.toLowerCase().contains("pdf");
        boolean extensionPdf = originalFilename != null && originalFilename.toLowerCase().endsWith(".pdf");
        return mimePdf || extensionPdf;
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "scan.pdf";
        }
        return fileName.replace("\\", "_").replace("/", "_").trim();
    }

    private Path resolveScanFilePath(Integer demandeId, Integer dossierId, String nomFichier) {
        String safeNom = sanitizeFileName(nomFichier);
        if (!safeNom.toLowerCase().endsWith(".pdf")) {
            safeNom = safeNom + ".pdf";
        }
        Path baseDir = Path.of(System.getProperty("user.home"), "visa-scans");
        Path demandeDir = baseDir.resolve("demande-" + demandeId);
        return demandeDir.resolve("dossier-" + dossierId + "-" + safeNom);
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
