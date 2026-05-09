package framework.visa.service;

import framework.visa.config.VisaConfig;
import framework.visa.entity.CarteResident;
import framework.visa.entity.CategorieVisa;
import framework.visa.entity.Demande;
import framework.visa.entity.DemandeDossier;
import framework.visa.entity.Demandeur;
import framework.visa.entity.DemandeurVisaCarteResident;
import framework.visa.entity.Dossier;
import framework.visa.entity.Etat;
import framework.visa.entity.HistoStatutDemande;
import framework.visa.entity.Nationalite;
import framework.visa.entity.Passeport;
import framework.visa.entity.SituationFamiliale;
import framework.visa.entity.StatutDemande;
import framework.visa.entity.TypeDemande;
import framework.visa.entity.Visa;
import framework.visa.entity.VisaTransformable;
import framework.visa.repository.CarteResidentRepository;
import framework.visa.repository.CategorieVisaRepository;
import framework.visa.repository.DemandeDossierRepository;
import framework.visa.repository.DemandeRepository;
import framework.visa.repository.DemandeurRepository;
import framework.visa.repository.DemandeurVisaCarteResidentRepository;
import framework.visa.repository.DossierTypeVisaRepository;
import framework.visa.repository.EtatRepository;
import framework.visa.repository.HistoStatutDemandeRepository;
import framework.visa.repository.NationaliteRepository;
import framework.visa.repository.PasseportRepository;
import framework.visa.repository.SituationFamilialeRepository;
import framework.visa.repository.StatutDemandeRepository;
import framework.visa.repository.TypeDemandeRepository;
import framework.visa.repository.VisaRepository;
import framework.visa.repository.VisaTransformableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DemandeWorkflowService {
        private static final String STATUS_CREE = "cree";
        private static final String STATUS_TERMINEE = "terminee";
        private static final String STATUS_VALIDEE = "approuve";
        private static final String ETAT_NOUVEAU_TITRE = "nouveau titre";
        private static final String TYPE_NOUVEAU_TITRE = "nouveau titre";
        private static final String TYPE_DUPLICATA = "duplicata";
        private static final String TYPE_TRANSFERT_VISA = "transfert";
        private static final DateTimeFormatter REFERENCE_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        private static final String CARTE_RESIDENT_PREFIX = "CR";

        private final DemandeurRepository demandeurRepository;
        private final PasseportRepository passeportRepository;
        private final DemandeRepository demandeRepository;
        private final TypeDemandeRepository typeDemandeRepository;
        private final StatutDemandeRepository statutDemandeRepository;
        private final DossierTypeVisaRepository dossierTypeVisaRepository;
        private final EtatRepository etatRepository;
        private final DemandeDossierRepository demandeDossierRepository;
        private final HistoStatutDemandeRepository histoStatutDemandeRepository;
        private final VisaTransformableRepository visaTransformableRepository;
        private final SituationFamilialeRepository situationFamilialeRepository;
        private final NationaliteRepository nationaliteRepository;
        private final VisaRepository visaRepository;
        private final CategorieVisaRepository categorieVisaRepository;
        private final CarteResidentRepository carteResidentRepository;
        private final DemandeurVisaCarteResidentRepository demandeurVisaCarteResidentRepository;
        private final VisaConfig visaConfig;

        public DemandeWorkflowService(
                        DemandeurRepository demandeurRepository,
                        PasseportRepository passeportRepository,
                        DemandeRepository demandeRepository,
                        TypeDemandeRepository typeDemandeRepository,
                        StatutDemandeRepository statutDemandeRepository,
                        DossierTypeVisaRepository dossierTypeVisaRepository,
                        EtatRepository etatRepository,
                        DemandeDossierRepository demandeDossierRepository,
                        HistoStatutDemandeRepository histoStatutDemandeRepository,
                        VisaTransformableRepository visaTransformableRepository,
                        SituationFamilialeRepository situationFamilialeRepository,
                        NationaliteRepository nationaliteRepository,
                        VisaRepository visaRepository,
                        CategorieVisaRepository categorieVisaRepository,
                        CarteResidentRepository carteResidentRepository,
                        DemandeurVisaCarteResidentRepository demandeurVisaCarteResidentRepository,
                        VisaConfig visaConfig) {
                this.demandeurRepository = demandeurRepository;
                this.passeportRepository = passeportRepository;
                this.demandeRepository = demandeRepository;
                this.typeDemandeRepository = typeDemandeRepository;
                this.statutDemandeRepository = statutDemandeRepository;
                this.dossierTypeVisaRepository = dossierTypeVisaRepository;
                this.etatRepository = etatRepository;
                this.demandeDossierRepository = demandeDossierRepository;
                this.histoStatutDemandeRepository = histoStatutDemandeRepository;
                this.visaTransformableRepository = visaTransformableRepository;
                this.situationFamilialeRepository = situationFamilialeRepository;
                this.nationaliteRepository = nationaliteRepository;
                this.visaRepository = visaRepository;
                this.categorieVisaRepository = categorieVisaRepository;
                this.carteResidentRepository = carteResidentRepository;
                this.demandeurVisaCarteResidentRepository = demandeurVisaCarteResidentRepository;
                this.visaConfig = visaConfig;
        }

        public static class SansDonneesCreationResult {
                private final Integer demandeNouveauTitreId;
                private final Integer demandeOperationId;
                private final Integer demandeurId;

                public SansDonneesCreationResult(Integer demandeNouveauTitreId, Integer demandeOperationId, Integer demandeurId) {
                        this.demandeNouveauTitreId = demandeNouveauTitreId;
                        this.demandeOperationId = demandeOperationId;
                        this.demandeurId = demandeurId;
                }

                public Integer getDemandeNouveauTitreId() {
                        return demandeNouveauTitreId;
                }

                public Integer getDemandeOperationId() {
                        return demandeOperationId;
                }

                public Integer getDemandeurId() {
                        return demandeurId;
                }
        }

        @Transactional
        public Integer submitNouveauTitre(
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
                        LocalDate dateExpirationVisaTransformable,
                        Integer categorieVisaId,
                        List<Integer> dossierIds,
                        String observations,
                        String modeOperation,
                        String carteEtatLibelle) {

                CategorieVisa categorieVisa = categorieVisaRepository.findById(categorieVisaId)
                                .orElseThrow(() -> new IllegalArgumentException("Categorie de visa introuvable."));
                TypeDemande typeDemande = resolveTypeDemandeForMode(modeOperation);

                validateVisaTransformableExpiration(dateExpirationVisaTransformable);

                Set<Integer> selectedDossierIds = dossierIds == null
                                ? new HashSet<>()
                                : new HashSet<>(dossierIds);

                List<Dossier> commonDossiers = dossierTypeVisaRepository.findCommonDossiers();
                List<Dossier> typedDossiers = dossierTypeVisaRepository.findDossiersByTypeId(categorieVisaId);

                LinkedHashMap<Integer, Dossier> applicableDossiersById = new LinkedHashMap<>();
                for (Dossier dossier : commonDossiers) {
                        applicableDossiersById.put(dossier.getId(), dossier);
                }
                for (Dossier dossier : typedDossiers) {
                        applicableDossiersById.put(dossier.getId(), dossier);
                }

                boolean hasMissingDossier = false;
                for (Dossier dossier : applicableDossiersById.values()) {
                        if (!selectedDossierIds.contains(dossier.getId())) {
                                hasMissingDossier = true;
                                break;
                        }
                }

                Demandeur demandeur = buildDemandeur(
                                nom,
                                prenom,
                                dateNaissance,
                                lieuNaissance,
                                situationFamilialeId,
                                nationaliteId,
                                telephone,
                                email,
                                adresse
                );

                Passeport passeport = new Passeport();
                passeport.setDemandeur(demandeur);
                passeport.setNumeroPasseport(numeroPasseport);
                passeport.setDateDelivrance(dateDelivrance);
                passeport.setDateExpiration(dateExpiration);
                passeport.setPaysDelivrance(paysDelivrance);
                passeport = passeportRepository.save(passeport);

                VisaTransformable visaTransformable = new VisaTransformable();
                visaTransformable.setDemandeur(demandeur);
                visaTransformable.setReference(requireNonBlank(referenceVisaTransformable, "Reference visa transformable"));
                visaTransformable.setDateArriveeMadagascar(requireDate(dateArriveeMadagascar, "Date d'arrivee a Madagascar"));
                visaTransformable.setLieuEntreeMadagascar(requireNonBlank(lieuEntreeMadagascar, "Lieu d'entree a Madagascar"));
                visaTransformable.setDateDonnation(requireDate(dateDonnationVisaTransformable, "Date de donnation visa transformable"));
                visaTransformable.setDateExpiration(requireDate(dateExpirationVisaTransformable, "Date d'expiration visa transformable"));
                visaTransformableRepository.save(visaTransformable);

                StatutDemande statut = getOrCreateStatus(hasMissingDossier ? STATUS_CREE : STATUS_TERMINEE);

                Demande demande = new Demande();
                demande.setDateDemande(LocalDate.now());
                demande.setStatut(statut);
                demande.setDemandeur(demandeur);
                demande.setTypeDemande(typeDemande);
                demande.setObservations(observations);
                demande = demandeRepository.save(demande);

                if (!hasMissingDossier) {
                        Visa visa = createVisaForNouveauTitre(demande, passeport, categorieVisa);
                        demande.setVisa(visa);
                        demandeRepository.save(demande);

                        String etatCarte = (carteEtatLibelle != null && !carteEtatLibelle.isBlank())
                                        ? carteEtatLibelle.trim()
                                        : ETAT_NOUVEAU_TITRE;
                        CarteResident carteResident = createCarteResidentForVisa(demandeur, visa, etatCarte);
                        appendHistorique(demande, statut, "Visa long sejour cree : " + visa.getReference() + ".");
                        appendHistorique(demande, statut, "Carte resident creee : " + carteResident.getNumero() + ".");
                }

                for (Dossier dossier : applicableDossiersById.values()) {
                        DemandeDossier demandeDossier = new DemandeDossier();
                        demandeDossier.setDemande(demande);
                        demandeDossier.setDossier(dossier);
                        demandeDossier.setEstFourni(selectedDossierIds.contains(dossier.getId()));
                        if (!demandeDossier.isEstFourni() && !dossier.isObligatoire()) {
                                demandeDossier.setCommentaire("Piece non obligatoire non fournie.");
                        }
                        demandeDossierRepository.save(demandeDossier);
                }

                appendHistorique(
                                demande,
                                statut,
                                hasMissingDossier
                                                ? "Demande enregistree avec pieces manquantes."
                                                : "Demande enregistree avec dossier complet."
                );
                appendHistorique(demande, statut, "Visa transformable renseigne et lie au demandeur.");

                return demande.getId();
        }

        @Transactional
        public SansDonneesCreationResult submitSansDonneesAnterieures(
                        String modeOperation,
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
                        LocalDate dateExpirationVisaTransformable,
                        Integer categorieVisaId,
                        List<Integer> dossierIds,
                        List<Integer> operationDossierIds,
                        String observations,
                        String carteEtatLibelle) {

                String normalizedMode = normalizeModeOperation(modeOperation);

                CategorieVisa categorieVisa = categorieVisaRepository.findById(categorieVisaId)
                                .orElseThrow(() -> new IllegalArgumentException("Categorie de visa introuvable."));
                TypeDemande typeNouveauTitre = resolveTypeDemandeForMode(TYPE_NOUVEAU_TITRE);

                validateVisaTransformableExpiration(dateExpirationVisaTransformable);

                Demandeur demandeur = buildDemandeur(
                                nom,
                                prenom,
                                dateNaissance,
                                lieuNaissance,
                                situationFamilialeId,
                                nationaliteId,
                                telephone,
                                email,
                                adresse
                );

                Passeport passeport = new Passeport();
                passeport.setDemandeur(demandeur);
                passeport.setNumeroPasseport(numeroPasseport);
                passeport.setDateDelivrance(dateDelivrance);
                passeport.setDateExpiration(dateExpiration);
                passeport.setPaysDelivrance(paysDelivrance);
                passeport = passeportRepository.save(passeport);

                VisaTransformable visaTransformable = new VisaTransformable();
                visaTransformable.setDemandeur(demandeur);
                visaTransformable.setReference(requireNonBlank(referenceVisaTransformable, "Reference visa transformable"));
                visaTransformable.setDateArriveeMadagascar(requireDate(dateArriveeMadagascar, "Date d'arrivee a Madagascar"));
                visaTransformable.setLieuEntreeMadagascar(requireNonBlank(lieuEntreeMadagascar, "Lieu d'entree a Madagascar"));
                visaTransformable.setDateDonnation(requireDate(dateDonnationVisaTransformable, "Date de donnation visa transformable"));
                visaTransformable.setDateExpiration(requireDate(dateExpirationVisaTransformable, "Date d'expiration visa transformable"));
                visaTransformableRepository.save(visaTransformable);

                StatutDemande statutValidee = getOrCreateStatus(STATUS_VALIDEE);
                Demande demandeNouveauTitre = new Demande();
                demandeNouveauTitre.setDateDemande(LocalDate.now());
                demandeNouveauTitre.setDateTraitement(LocalDate.now());
                demandeNouveauTitre.setStatut(statutValidee);
                demandeNouveauTitre.setDemandeur(demandeur);
                demandeNouveauTitre.setTypeDemande(typeNouveauTitre);
                demandeNouveauTitre.setObservations(observations);
                demandeNouveauTitre = demandeRepository.save(demandeNouveauTitre);

                Visa visa = createVisaForNouveauTitre(demandeNouveauTitre, passeport, categorieVisa);
                demandeNouveauTitre.setVisa(visa);
                demandeRepository.save(demandeNouveauTitre);

                String etatCarte = (carteEtatLibelle != null && !carteEtatLibelle.isBlank())
                                ? carteEtatLibelle.trim()
                                : ETAT_NOUVEAU_TITRE;
                CarteResident carteResident = createCarteResidentForVisa(demandeur, visa, etatCarte);

                createDemandeDossiers(demandeNouveauTitre, collectApplicableDossiers(categorieVisa.getId()), dossierIds);
                appendHistorique(demandeNouveauTitre, statutValidee, "Demande nouveau titre validee automatiquement (cas sans donnees anterieures). ");
                appendHistorique(demandeNouveauTitre, statutValidee, "Visa long sejour cree : " + visa.getReference() + ".");
                appendHistorique(demandeNouveauTitre, statutValidee, "Carte resident creee : " + carteResident.getNumero() + ".");
                appendHistorique(demandeNouveauTitre, statutValidee, "Visa transformable renseigne et lie au demandeur.");

                TypeDemande typeOperation = resolveOperationType(normalizedMode);
                StatutDemande statutOperation = getOrCreateStatus(STATUS_TERMINEE);

                Demande demandeOperation = new Demande();
                demandeOperation.setDateDemande(LocalDate.now());
                demandeOperation.setDateTraitement(LocalDate.now());
                demandeOperation.setStatut(statutOperation);
                demandeOperation.setDemandeur(demandeur);
                demandeOperation.setTypeDemande(typeOperation);
                demandeOperation.setVisa(visa);
                demandeOperation.setObservations(buildOperationObservation(normalizedMode, observations));
                demandeOperation = demandeRepository.save(demandeOperation);

                createDemandeDossiers(demandeOperation, collectApplicableDossiers(typeOperation.getId()), operationDossierIds, true);
                appendHistorique(demandeOperation, statutOperation,
                                "Demande " + typeOperation.getLibelle() + " creee automatiquement a partir des donnees saisies et terminee avec pieces valides.");

                return new SansDonneesCreationResult(demandeNouveauTitre.getId(), demandeOperation.getId(), demandeur.getId());
        }

        private Demandeur buildDemandeur(
                        String nom,
                        String prenom,
                        LocalDate dateNaissance,
                        String lieuNaissance,
                        Integer situationFamilialeId,
                        Integer nationaliteId,
                        String telephone,
                        String email,
                        String adresse) {
                SituationFamiliale situationFamiliale = situationFamilialeRepository.findById(situationFamilialeId)
                                .orElseThrow(() -> new IllegalArgumentException("Situation familiale introuvable."));
                Nationalite nationalite = nationaliteRepository.findById(nationaliteId)
                                .orElseThrow(() -> new IllegalArgumentException("Nationalite introuvable."));

                Demandeur demandeur = new Demandeur();
                demandeur.setNom(nom);
                demandeur.setPrenom(prenom);
                demandeur.setDateNaissance(dateNaissance);
                demandeur.setLieuNaissance(lieuNaissance);
                demandeur.setSituationFamiliale(situationFamiliale);
                demandeur.setNationalite(nationalite);
                demandeur.setTelephone(telephone);
                demandeur.setEmail(email);
                demandeur.setAdresse(adresse);
                demandeur.setCreatedAt(LocalDateTime.now());
                demandeur.setUpdatedAt(LocalDateTime.now());
                return demandeurRepository.save(demandeur);
        }

        private List<Dossier> collectApplicableDossiers(Integer categorieVisaId) {
                LinkedHashMap<Integer, Dossier> applicableDossiersById = new LinkedHashMap<>();
                for (Dossier dossier : dossierTypeVisaRepository.findCommonDossiers()) {
                        applicableDossiersById.put(dossier.getId(), dossier);
                }
                if (categorieVisaId != null) {
                        for (Dossier dossier : dossierTypeVisaRepository.findDossiersByTypeId(categorieVisaId)) {
                                applicableDossiersById.put(dossier.getId(), dossier);
                        }
                }
                return List.copyOf(applicableDossiersById.values());
        }

        private void createDemandeDossiers(Demande demande, List<Dossier> applicableDossiers, List<Integer> selectedDossierIdsInput) {
                createDemandeDossiers(demande, applicableDossiers, selectedDossierIdsInput, false);
        }

        private void createDemandeDossiers(
                        Demande demande,
                        List<Dossier> applicableDossiers,
                        List<Integer> selectedDossierIdsInput,
                        boolean forceAllProvided) {
                Set<Integer> selectedDossierIds = selectedDossierIdsInput == null
                                ? Set.of()
                                : new HashSet<>(selectedDossierIdsInput);

                for (Dossier dossier : applicableDossiers) {
                        DemandeDossier demandeDossier = new DemandeDossier();
                        demandeDossier.setDemande(demande);
                        demandeDossier.setDossier(dossier);
                        boolean fourni = forceAllProvided || selectedDossierIds.contains(dossier.getId());
                        demandeDossier.setEstFourni(fourni);
                        if (!demandeDossier.isEstFourni() && !dossier.isObligatoire()) {
                                demandeDossier.setCommentaire("Piece non obligatoire non fournie.");
                        } else if (forceAllProvided && dossier.isObligatoire()) {
                                demandeDossier.setCommentaire("Piece obligatoire validee automatiquement a la creation.");
                        }
                        demandeDossierRepository.save(demandeDossier);
                }
        }

        private String resolveModeOperation(String modeOperation) {
                if (modeOperation == null || modeOperation.isBlank()) {
                        return "";
                }

                String normalized = modeOperation.trim().toLowerCase();
                if (!TYPE_DUPLICATA.equals(normalized) && !TYPE_TRANSFERT_VISA.equals(normalized)) {
                        return "";
                }

                return normalized;
        }

        private String normalizeModeOperation(String modeOperation) {
                return resolveModeOperation(modeOperation);
        }

        private TypeDemande resolveTypeDemandeForMode(String modeOperation) {
                String normalized = resolveModeOperation(modeOperation);
                String label = normalized.isBlank() ? TYPE_NOUVEAU_TITRE : normalized;
                return findOrCreateTypeDemande(label);
        }

        private TypeDemande resolveOperationType(String normalizedMode) {
                String operationLabel = TYPE_DUPLICATA.equals(normalizedMode)
                                ? TYPE_DUPLICATA
                                : TYPE_TRANSFERT_VISA;

                return findOrCreateTypeDemande(operationLabel);
        }

        private TypeDemande findOrCreateTypeDemande(String requestedLabel) {
                String normalizedLabel = normalizeLabel(requestedLabel);
                return typeDemandeRepository.findAll().stream()
                                .filter(typeDemande -> normalizeLabel(typeDemande.getLibelle()).equals(normalizedLabel))
                                .findFirst()
                                .orElseGet(() -> {
                                        TypeDemande typeDemande = new TypeDemande();
                                        typeDemande.setLibelle(requestedLabel.trim());
                                        return typeDemandeRepository.save(typeDemande);
                                });
        }

        private String normalizeLabel(String value) {
                if (value == null) {
                        return "";
                }

                return value.trim().toLowerCase().replace('_', ' ').replaceAll("\\s+", " ");
        }

        private String buildOperationObservation(String normalizedMode, String observations) {
                String prefix = TYPE_DUPLICATA.equals(normalizedMode)
                                ? "Demande duplicata creee depuis parcours sans donnees anterieures."
                                : "Demande transfert visa creee depuis parcours sans donnees anterieures.";

                if (observations == null || observations.isBlank()) {
                        return prefix;
                }

                return prefix + " " + observations.trim();
        }

        private Visa createVisaForNouveauTitre(Demande demande, Passeport passeport, CategorieVisa categorieVisa) {
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

        //create createCarteResidentIfNeeded
        private CarteResident createCarteResidentForVisa(Demandeur demandeur, Visa visa, String etatCarteLibelle) {
                Optional<DemandeurVisaCarteResident> existingLink = demandeurVisaCarteResidentRepository
                                .findFirstWithDetailsByVisaId(visa.getId());
                if (existingLink.isPresent() && existingLink.get().getCarteResident() != null) {
                        return existingLink.get().getCarteResident();
                }

                CarteResident carteResident = new CarteResident();
                carteResident.setNumero(generateNextCarteResidentNumero());
                carteResident.setDateDonnation(visa.getDateDebut() == null ? LocalDate.now() : visa.getDateDebut());
                carteResident.setDateExpiration(visa.getDateFin() == null ? LocalDate.now() : visa.getDateFin());
                carteResident.setDemandeur(demandeur);
                String etatToUse = (etatCarteLibelle != null && !etatCarteLibelle.isBlank())
                                ? etatCarteLibelle.trim()
                                : ETAT_NOUVEAU_TITRE;
                carteResident.setEtat(resolveEtat(etatToUse));
                carteResident = carteResidentRepository.save(carteResident);

                DemandeurVisaCarteResident link = new DemandeurVisaCarteResident();
                link.setDemandeur(demandeur);
                link.setVisa(visa);
                link.setCarteResident(carteResident);
                demandeurVisaCarteResidentRepository.save(link);

                return carteResident;
        }

        private Etat resolveEtat(String libelle) {
                return etatRepository.findFirstByLibelleIgnoreCase(libelle)
                                .orElseGet(() -> {
                                        Etat etat = new Etat();
                                        etat.setLibelle(libelle);
                                        return etatRepository.save(etat);
                                });
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

        private void appendHistorique(Demande demande, StatutDemande statut, String commentaire) {
                if (commentaire == null || commentaire.isBlank()) {
                        return;
                }

                HistoStatutDemande historique = new HistoStatutDemande();
                historique.setDemande(demande);
                historique.setStatut(statut);
                historique.setDateChangement(LocalDateTime.now());
                historique.setCommentaire(commentaire);
                histoStatutDemandeRepository.save(historique);
        }

        private void validateVisaTransformableExpiration(LocalDate dateExpirationVisaTransformable) {
                LocalDate dateExpiration = requireDate(dateExpirationVisaTransformable, "Date d'expiration visa transformable");
                if (dateExpiration.isBefore(LocalDate.now())) {
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

        private String buildVisaReference(Integer demandeId) {
                String timestamp = LocalDateTime.now().format(REFERENCE_TIMESTAMP_FORMATTER);
                return "VISA-NT-" + demandeId + "-" + timestamp;
        }

        private StatutDemande getOrCreateStatus(String label) {
                return statutDemandeRepository.findFirstByLibelleIgnoreCase(label)
                                .orElseGet(() -> {
                                        StatutDemande statutDemande = new StatutDemande();
                                        statutDemande.setLibelle(label);
                                        return statutDemandeRepository.save(statutDemande);
                                });
        }
}
