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
        private static final String STATUS_EN_COURS = "en_cours";
        private static final String STATUS_TERMINEE = "terminee";

    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository passeportRepository;
    private final DemandeRepository demandeRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final DossierTypeVisaRepository dossierTypeVisaRepository;
    private final DemandeDossierRepository demandeDossierRepository;
    private final HistoStatutDemandeRepository histoStatutDemandeRepository;

    public DemandeWorkflowService(
            DemandeurRepository demandeurRepository,
            PasseportRepository passeportRepository,
            DemandeRepository demandeRepository,
            TypeDemandeRepository typeDemandeRepository,
            StatutDemandeRepository statutDemandeRepository,
            DossierTypeVisaRepository dossierTypeVisaRepository,
            DemandeDossierRepository demandeDossierRepository,
            HistoStatutDemandeRepository histoStatutDemandeRepository) {
        this.demandeurRepository = demandeurRepository;
        this.passeportRepository = passeportRepository;
        this.demandeRepository = demandeRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.statutDemandeRepository = statutDemandeRepository;
        this.dossierTypeVisaRepository = dossierTypeVisaRepository;
        this.demandeDossierRepository = demandeDossierRepository;
        this.histoStatutDemandeRepository = histoStatutDemandeRepository;
    }

    @Transactional
    public Integer submitNouveauTitre(
            String nom,
            String prenom,
            LocalDate dateNaissance,
            String lieuNaissance,
            String telephone,
            String email,
            String adresse,
            String numeroPasseport,
            LocalDate dateDelivrance,
            LocalDate dateExpiration,
            String paysDelivrance,
            Integer typeVisaId,
            List<Integer> dossierIds,
            String observations) {

        TypeDemande typeVisa = typeDemandeRepository.findById(typeVisaId)
                .orElseThrow(() -> new IllegalArgumentException("Type de visa introuvable."));

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
                demandeur.setNom(nom);
                demandeur.setPrenom(prenom);
                demandeur.setDateNaissance(dateNaissance);
                demandeur.setLieuNaissance(lieuNaissance);
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

                StatutDemande statut = getOrCreateStatus(hasMissingDossier ? STATUS_EN_COURS : STATUS_TERMINEE);

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

                return demande.getId();
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
