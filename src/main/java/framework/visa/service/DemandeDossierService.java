package framework.visa.service;

import framework.visa.entity.Demande;
import framework.visa.entity.DemandeDossier;
import framework.visa.entity.Demandeur;
import framework.visa.entity.HistoStatutDemande;
import framework.visa.entity.Passeport;
import framework.visa.entity.StatutDemande;
import framework.visa.repository.DemandeRepository;
import framework.visa.repository.DemandeDossierRepository;
import framework.visa.repository.DemandeurRepository;
import framework.visa.repository.HistoStatutDemandeRepository;
import framework.visa.repository.PasseportRepository;
import framework.visa.repository.StatutDemandeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DemandeDossierService {
    private static final String STATUS_TERMINEE = "terminee";

    private final DemandeDossierRepository repository;
    private final DemandeRepository demandeRepository;
    private final DemandeurRepository demandeurRepository;
    private final PasseportRepository passeportRepository;
    private final StatutDemandeRepository statutDemandeRepository;
    private final HistoStatutDemandeRepository histoStatutDemandeRepository;

    public DemandeDossierService(
            DemandeDossierRepository repository,
            DemandeRepository demandeRepository,
            DemandeurRepository demandeurRepository,
            PasseportRepository passeportRepository,
            StatutDemandeRepository statutDemandeRepository,
            HistoStatutDemandeRepository histoStatutDemandeRepository) {
        this.repository = repository;
        this.demandeRepository = demandeRepository;
        this.demandeurRepository = demandeurRepository;
        this.passeportRepository = passeportRepository;
        this.statutDemandeRepository = statutDemandeRepository;
        this.histoStatutDemandeRepository = histoStatutDemandeRepository;
    }

    public List<DemandeDossier> findAll() {
        return repository.findAll();
    }

    public List<Demande> findDemandesen_courses() {
        return demandeRepository.findDemandesen_courses();
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
            String telephone,
            String email,
            String adresse,
            String numeroPasseport,
            LocalDate dateDelivrance,
            LocalDate dateExpiration,
            String paysDelivrance) {
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

        for (DemandeDossier ligne : lignes) {
            if (!ligne.isEstFourni() && selectedIds.contains(ligne.getDossier().getId())) {
                ligne.setEstFourni(true);
                ligne.setCommentaire("Piece ajoutee via ecran dossiers en cours.");
            }
        }

        if (!selectedIds.isEmpty()) {
            repository.saveAll(lignes);
        }

        if (updateInformations) {
            updateDemandeurAndPasseport(
                    demande,
                    nom,
                    prenom,
                    dateNaissance,
                    lieuNaissance,
                    telephone,
                    email,
                    adresse,
                    numeroPasseport,
                    dateDelivrance,
                    dateExpiration,
                    paysDelivrance
            );
        }

        boolean hasMissing = lignes.stream().anyMatch(ligne -> !ligne.isEstFourni());
        boolean alreadyCompleted = demande.getStatut() != null
                && STATUS_TERMINEE.equalsIgnoreCase(demande.getStatut().getLibelle());

        if (!hasMissing && !alreadyCompleted) {
            StatutDemande complet = getOrCreateStatus(STATUS_TERMINEE);
            demande.setStatut(complet);
            demandeRepository.save(demande);

            HistoStatutDemande historique = new HistoStatutDemande();
            historique.setDemande(demande);
            historique.setStatut(complet);
            historique.setDateChangement(LocalDateTime.now());
            historique.setCommentaire("Demande completee apres ajout de pieces manquantes.");
            histoStatutDemandeRepository.save(historique);
        }
    }

    private void updateDemandeurAndPasseport(
            Demande demande,
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
            String paysDelivrance) {
        Demandeur demandeur = demande.getDemandeur();
        if (demandeur == null) {
            throw new IllegalArgumentException("Demandeur introuvable pour cette demande.");
        }

        demandeur.setNom(requireNonBlank(nom, "Nom"));
        demandeur.setPrenom(requireNonBlank(prenom, "Prenom"));
        demandeur.setDateNaissance(requireDate(dateNaissance, "Date de naissance"));
        demandeur.setLieuNaissance(requireNonBlank(lieuNaissance, "Lieu de naissance"));
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
