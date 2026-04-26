package framework.visa.service;

import framework.visa.entity.*;
import framework.visa.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DemandeWorkflowService {
        private static final String STATUS_cree = "cree";
        private static final String STATUS_TERMINEE = "terminee";

    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository passeportRepository;
    private final DemandeRepository demandeRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final DossierTypeVisaRepository dossierTypeVisaRepository;
    private final DemandeDossierRepository demandeDossierRepository;
    private final HistoStatutDemandeRepository histoStatutDemandeRepository;
        private final VisaTransformableRepository visaTransformableRepository;
        private final SituationFamilialeRepository situationFamilialeRepository;
        private final NationaliteRepository nationaliteRepository;

    public DemandeWorkflowService(
            DemandeurRepository demandeurRepository,
            PasseportRepository passeportRepository,
            DemandeRepository demandeRepository,
            TypeDemandeRepository typeDemandeRepository,
            StatutDemandeRepository statutDemandeRepository,
            DossierTypeVisaRepository dossierTypeVisaRepository,
                        DemandeDossierRepository demandeDossierRepository,
                        HistoStatutDemandeRepository histoStatutDemandeRepository,
                        VisaTransformableRepository visaTransformableRepository,
                        SituationFamilialeRepository situationFamilialeRepository,
                        NationaliteRepository nationaliteRepository) {
        this.demandeurRepository = demandeurRepository;
        this.passeportRepository = passeportRepository;
        this.demandeRepository = demandeRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.statutDemandeRepository = statutDemandeRepository;
        this.dossierTypeVisaRepository = dossierTypeVisaRepository;
        this.demandeDossierRepository = demandeDossierRepository;
        this.histoStatutDemandeRepository = histoStatutDemandeRepository;
                this.visaTransformableRepository = visaTransformableRepository;
                this.situationFamilialeRepository = situationFamilialeRepository;
                this.nationaliteRepository = nationaliteRepository;
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
            Integer typeVisaId,
            List<Integer> dossierIds,
            String observations) {

        TypeDemande typeVisa = typeDemandeRepository.findById(typeVisaId)
                .orElseThrow(() -> new IllegalArgumentException("Type de visa introuvable."));

        validateVisaTransformableExpiration(dateExpirationVisaTransformable);

        Set<Integer> selectedDossierIds = dossierIds == null
                ? new HashSet<>()
                : new HashSet<>(dossierIds);

        List<Dossier> commonDossiers = dossierTypeVisaRepository.findCommonDossiers();
        List<Dossier> typedDossiers = dossierTypeVisaRepository.findDossiersByTypeId(typeVisaId);

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

        Demandeur demandeur = new Demandeur();
        SituationFamiliale situationFamiliale = situationFamilialeRepository.findById(situationFamilialeId)
                .orElseThrow(() -> new IllegalArgumentException("Situation familiale introuvable."));
        Nationalite nationalite = nationaliteRepository.findById(nationaliteId)
                .orElseThrow(() -> new IllegalArgumentException("Nationalite introuvable."));

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
        demandeur = demandeurRepository.save(demandeur);

        Passeport passeport = new Passeport();
        passeport.setDemandeur(demandeur);
        passeport.setNumeroPasseport(numeroPasseport);
        passeport.setDateDelivrance(dateDelivrance);
        passeport.setDateExpiration(dateExpiration);
        passeport.setPaysDelivrance(paysDelivrance);
        passeportRepository.save(passeport);

        VisaTransformable visaTransformable = new VisaTransformable();
        visaTransformable.setDemandeur(demandeur);
        visaTransformable.setReference(requireNonBlank(referenceVisaTransformable, "Reference visa transformable"));
        visaTransformable.setDateArriveeMadagascar(requireDate(dateArriveeMadagascar, "Date d'arrivee a Madagascar"));
        visaTransformable.setLieuEntreeMadagascar(requireNonBlank(lieuEntreeMadagascar, "Lieu d'entree a Madagascar"));
        visaTransformable.setDateDonnation(requireDate(dateDonnationVisaTransformable, "Date de donnation visa transformable"));
        visaTransformable.setDateExpiration(requireDate(dateExpirationVisaTransformable, "Date d'expiration visa transformable"));
        visaTransformableRepository.save(visaTransformable);

        StatutDemande statut = getOrCreateStatus(hasMissingDossier ? STATUS_cree : STATUS_TERMINEE);

        Demande demande = new Demande();
        demande.setDateDemande(LocalDate.now());
        demande.setStatut(statut);
        demande.setDemandeur(demandeur);
        demande.setTypeDemande(typeVisa);
        demande.setObservations(observations);
        demande = demandeRepository.save(demande);

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

        HistoStatutDemande historique = new HistoStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(statut);
        historique.setDateChangement(LocalDateTime.now());
        historique.setCommentaire(hasMissingDossier
                ? "Demande enregistree avec pieces manquantes."
                : "Demande enregistree avec dossier complet.");
        histoStatutDemandeRepository.save(historique);

        HistoStatutDemande historiqueVisaTransformable = new HistoStatutDemande();
        historiqueVisaTransformable.setDemande(demande);
        historiqueVisaTransformable.setStatut(statut);
        historiqueVisaTransformable.setDateChangement(LocalDateTime.now());
        historiqueVisaTransformable.setCommentaire("Visa transformable renseigne et lie au demandeur.");
        histoStatutDemandeRepository.save(historiqueVisaTransformable);

        return demande.getId();
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

        private StatutDemande getOrCreateStatus(String label) {
                return statutDemandeRepository.findFirstByLibelleIgnoreCase(label)
                                .orElseGet(() -> {
                                        StatutDemande statutDemande = new StatutDemande();
                                        statutDemande.setLibelle(label);
                                        return statutDemandeRepository.save(statutDemande);
                                });
        }
}
