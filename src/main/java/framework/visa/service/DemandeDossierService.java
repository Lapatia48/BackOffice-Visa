package framework.visa.service;

import framework.visa.entity.Demande;
import framework.visa.entity.DemandeDossier;
import framework.visa.entity.HistoStatutDemande;
import framework.visa.entity.StatutDemande;
import framework.visa.repository.DemandeRepository;
import framework.visa.repository.DemandeDossierRepository;
import framework.visa.repository.HistoStatutDemandeRepository;
import framework.visa.repository.StatutDemandeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final StatutDemandeRepository statutDemandeRepository;
    private final HistoStatutDemandeRepository histoStatutDemandeRepository;

    public DemandeDossierService(
            DemandeDossierRepository repository,
            DemandeRepository demandeRepository,
            StatutDemandeRepository statutDemandeRepository,
            HistoStatutDemandeRepository histoStatutDemandeRepository) {
        this.repository = repository;
        this.demandeRepository = demandeRepository;
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

    public List<DemandeDossier> findByDemandeId(Integer demandeId) {
        return repository.findByDemandeIdOrderByDossierLibelleAsc(demandeId);
    }

    public List<DemandeDossier> findByDemandeIds(Collection<Integer> demandeIds) {
        return repository.findByDemandeIdIn(demandeIds);
    }

    @Transactional
    public void completeMissingDossiers(Integer demandeId, List<Integer> dossierIdsToProvide) {
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

        if (remainingIds.isEmpty()) {
            throw new IllegalArgumentException("Tous les dossiers sont deja fournis pour cette demande.");
        }

        Set<Integer> selectedIds = dossierIdsToProvide == null
                ? Set.of()
                : new HashSet<>(dossierIdsToProvide);

        if (selectedIds.isEmpty()) {
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

        repository.saveAll(lignes);

        boolean hasMissing = lignes.stream().anyMatch(ligne -> !ligne.isEstFourni());
        if (!hasMissing) {
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

    private StatutDemande getOrCreateStatus(String libelle) {
        return statutDemandeRepository.findFirstByLibelleIgnoreCase(libelle)
                .orElseGet(() -> {
                    StatutDemande statut = new StatutDemande();
                    statut.setLibelle(libelle);
                    return statutDemandeRepository.save(statut);
                });
    }
}
