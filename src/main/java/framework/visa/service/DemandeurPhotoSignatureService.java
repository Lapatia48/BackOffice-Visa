package framework.visa.service;

import framework.visa.entity.Demande;
import framework.visa.entity.Demandeur;
import framework.visa.entity.DemandeurPhotoSignature;
import framework.visa.repository.DemandeRepository;
import framework.visa.repository.DemandeurPhotoSignatureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DemandeurPhotoSignatureService {
    private final DemandeurPhotoSignatureRepository repository;
    private final DemandeRepository demandeRepository;
    private final DemandeDossierService demandeDossierService;

    public DemandeurPhotoSignatureService(
            DemandeurPhotoSignatureRepository repository,
            DemandeRepository demandeRepository,
            DemandeDossierService demandeDossierService) {
        this.repository = repository;
        this.demandeRepository = demandeRepository;
        this.demandeDossierService = demandeDossierService;
    }

    public Optional<DemandeurPhotoSignature> findByDemandeurId(Integer demandeurId) {
        if (demandeurId == null) {
            return Optional.empty();
        }

        return repository.findByDemandeurId(demandeurId);
    }

    public Map<Integer, Boolean> findCompletionByDemandeurIds(Collection<Integer> demandeurIds) {
        if (demandeurIds == null || demandeurIds.isEmpty()) {
            return Map.of();
        }

        Map<Integer, Boolean> completionByDemandeurId = new HashMap<>();
        List<DemandeurPhotoSignature> mediaEntries = repository.findByDemandeurIdIn(demandeurIds);
        for (DemandeurPhotoSignature media : mediaEntries) {
            if (media == null || media.getDemandeur() == null || media.getDemandeur().getId() == null) {
                continue;
            }

            completionByDemandeurId.put(media.getDemandeur().getId(), hasCompleteMedia(media));
        }

        return completionByDemandeurId;
    }

    @Transactional
    public DemandeurPhotoSignature saveMedia(Integer demandeId, String photo, String signature) {
        if (demandeId == null) {
            throw new IllegalArgumentException("Demande introuvable.");
        }
        if (!hasText(photo) || !hasText(signature)) {
            throw new IllegalArgumentException("La photo et la signature sont obligatoires.");
        }

        Demande demande = demandeRepository.findDetailedById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));
        Demandeur demandeur = demande.getDemandeur();
        if (demandeur == null || demandeur.getId() == null) {
            throw new IllegalArgumentException("Demandeur introuvable pour cette demande.");
        }

        DemandeurPhotoSignature media = repository.findByDemandeurId(demandeur.getId())
                .orElseGet(DemandeurPhotoSignature::new);
        media.setDemandeur(demandeur);
        media.setPhoto(photo.trim());
        media.setSignature(signature.trim());
        media = repository.save(media);

        demandeDossierService.markPhotoSignatureCompleted(demandeId);
        return media;
    }

    public boolean hasCompleteMedia(DemandeurPhotoSignature media) {
        return media != null && hasText(media.getPhoto()) && hasText(media.getSignature());
    }

    public boolean hasCompleteMediaForDemandeur(Integer demandeurId) {
        return findByDemandeurId(demandeurId).map(this::hasCompleteMedia).orElse(false);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}