package framework.visa.repository;

import framework.visa.entity.DemandeurPhotoSignature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DemandeurPhotoSignatureRepository extends JpaRepository<DemandeurPhotoSignature, Integer> {
    Optional<DemandeurPhotoSignature> findByDemandeurId(Integer demandeurId);

    List<DemandeurPhotoSignature> findByDemandeurIdIn(Collection<Integer> demandeurIds);
}